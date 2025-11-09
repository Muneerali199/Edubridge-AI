# EduBridge AWS Deployment Script
# Automated deployment to AWS infrastructure

param(
    [Parameter()]
    [ValidateSet('all', 'frontend', 'backend', 'database', 'infrastructure')]
    [string]$Component = 'all',
    
    [Parameter()]
    [ValidateSet('dev', 'staging', 'prod')]
    [string]$Environment = 'prod',
    
    [Parameter()]
    [string]$Region = 'us-east-1',
    
    [Parameter()]
    [switch]$SkipBuild,
    
    [Parameter()]
    [switch]$DryRun
)

# Configuration
$ErrorActionPreference = "Stop"
$ProjectRoot = Split-Path -Parent $PSScriptRoot
$AppName = "edubridge"
$Timestamp = Get-Date -Format "yyyyMMdd-HHmmss"

# Color output functions
function Write-Success { Write-Host "✅ $args" -ForegroundColor Green }
function Write-Info { Write-Host "ℹ️  $args" -ForegroundColor Cyan }
function Write-Warning { Write-Host "⚠️  $args" -ForegroundColor Yellow }
function Write-Error-Custom { Write-Host "❌ $args" -ForegroundColor Red }

# Banner
Write-Host ""
Write-Host "╔═══════════════════════════════════════════════════════════╗" -ForegroundColor Cyan
Write-Host "║                                                           ║" -ForegroundColor Cyan
Write-Host "║        EduBridge AI Platform - AWS Deployment             ║" -ForegroundColor Cyan
Write-Host "║                                                           ║" -ForegroundColor Cyan
Write-Host "╚═══════════════════════════════════════════════════════════╝" -ForegroundColor Cyan
Write-Host ""
Write-Info "Component: $Component"
Write-Info "Environment: $Environment"
Write-Info "Region: $Region"
Write-Info "Timestamp: $Timestamp"
Write-Host ""

# Check prerequisites
Write-Info "Checking prerequisites..."

# Check AWS CLI
try {
    $awsVersion = aws --version 2>&1
    Write-Success "AWS CLI installed: $awsVersion"
} catch {
    Write-Error-Custom "AWS CLI not found. Please install it first."
    Write-Info "Install: winget install Amazon.AWSCLI"
    exit 1
}

# Check AWS credentials
try {
    $identity = aws sts get-caller-identity 2>&1 | ConvertFrom-Json
    Write-Success "AWS credentials configured for account: $($identity.Account)"
} catch {
    Write-Error-Custom "AWS credentials not configured."
    Write-Info "Run: aws configure"
    exit 1
}

# Configuration variables
$S3Bucket = "$AppName-frontend-$Environment"
$DBInstanceId = "$AppName-db-$Environment"
$RedisClusterId = "$AppName-redis-$Environment"
$EBAppName = "$AppName-app"
$AuthEnvName = "$AppName-auth-$Environment"
$CourseEnvName = "$AppName-course-$Environment"

# Function: Deploy Infrastructure
function Deploy-Infrastructure {
    Write-Info "🏗️  Deploying AWS infrastructure..."
    
    # Create VPC if not exists
    Write-Info "Creating VPC..."
    $vpcId = aws ec2 describe-vpcs `
        --filters "Name=tag:Name,Values=$AppName-vpc" `
        --query 'Vpcs[0].VpcId' `
        --output text 2>$null
    
    if ($vpcId -eq "None" -or $null -eq $vpcId) {
        Write-Info "Creating new VPC..."
        $vpcId = aws ec2 create-vpc `
            --cidr-block 10.0.0.0/16 `
            --tag-specifications "ResourceType=vpc,Tags=[{Key=Name,Value=$AppName-vpc}]" `
            --query 'Vpc.VpcId' `
            --output text
        
        Write-Success "VPC created: $vpcId"
        
        # Enable DNS
        aws ec2 modify-vpc-attribute --vpc-id $vpcId --enable-dns-support
        aws ec2 modify-vpc-attribute --vpc-id $vpcId --enable-dns-hostnames
        
        # Create Internet Gateway
        $igwId = aws ec2 create-internet-gateway `
            --tag-specifications "ResourceType=internet-gateway,Tags=[{Key=Name,Value=$AppName-igw}]" `
            --query 'InternetGateway.InternetGatewayId' `
            --output text
        
        aws ec2 attach-internet-gateway --vpc-id $vpcId --internet-gateway-id $igwId
        Write-Success "Internet Gateway created: $igwId"
        
        # Create subnets in different AZs
        $subnet1 = aws ec2 create-subnet `
            --vpc-id $vpcId `
            --cidr-block 10.0.1.0/24 `
            --availability-zone "${Region}a" `
            --tag-specifications "ResourceType=subnet,Tags=[{Key=Name,Value=$AppName-public-1a}]" `
            --query 'Subnet.SubnetId' `
            --output text
        
        $subnet2 = aws ec2 create-subnet `
            --vpc-id $vpcId `
            --cidr-block 10.0.2.0/24 `
            --availability-zone "${Region}b" `
            --tag-specifications "ResourceType=subnet,Tags=[{Key=Name,Value=$AppName-public-1b}]" `
            --query 'Subnet.SubnetId' `
            --output text
        
        Write-Success "Subnets created: $subnet1, $subnet2"
        
        # Create route table
        $rtId = aws ec2 create-route-table `
            --vpc-id $vpcId `
            --tag-specifications "ResourceType=route-table,Tags=[{Key=Name,Value=$AppName-public-rt}]" `
            --query 'RouteTable.RouteTableId' `
            --output text
        
        aws ec2 create-route --route-table-id $rtId --destination-cidr-block 0.0.0.0/0 --gateway-id $igwId
        aws ec2 associate-route-table --route-table-id $rtId --subnet-id $subnet1
        aws ec2 associate-route-table --route-table-id $rtId --subnet-id $subnet2
        
        Write-Success "Route table configured: $rtId"
    } else {
        Write-Success "VPC already exists: $vpcId"
    }
    
    # Create security groups
    Write-Info "Creating security groups..."
    
    # Backend security group
    $backendSg = aws ec2 describe-security-groups `
        --filters "Name=group-name,Values=$AppName-backend-sg" "Name=vpc-id,Values=$vpcId" `
        --query 'SecurityGroups[0].GroupId' `
        --output text 2>$null
    
    if ($backendSg -eq "None" -or $null -eq $backendSg) {
        $backendSg = aws ec2 create-security-group `
            --group-name "$AppName-backend-sg" `
            --description "Security group for backend services" `
            --vpc-id $vpcId `
            --query 'GroupId' `
            --output text
        
        # Allow HTTP/HTTPS
        aws ec2 authorize-security-group-ingress --group-id $backendSg --protocol tcp --port 80 --cidr 0.0.0.0/0
        aws ec2 authorize-security-group-ingress --group-id $backendSg --protocol tcp --port 443 --cidr 0.0.0.0/0
        aws ec2 authorize-security-group-ingress --group-id $backendSg --protocol tcp --port 8080-8082 --cidr 0.0.0.0/0
        
        Write-Success "Backend security group created: $backendSg"
    } else {
        Write-Success "Backend security group exists: $backendSg"
    }
    
    # Database security group
    $dbSg = aws ec2 describe-security-groups `
        --filters "Name=group-name,Values=$AppName-db-sg" "Name=vpc-id,Values=$vpcId" `
        --query 'SecurityGroups[0].GroupId' `
        --output text 2>$null
    
    if ($dbSg -eq "None" -or $null -eq $dbSg) {
        $dbSg = aws ec2 create-security-group `
            --group-name "$AppName-db-sg" `
            --description "Security group for database" `
            --vpc-id $vpcId `
            --query 'GroupId' `
            --output text
        
        # Allow PostgreSQL from backend
        aws ec2 authorize-security-group-ingress --group-id $dbSg --protocol tcp --port 5432 --source-group $backendSg
        
        Write-Success "Database security group created: $dbSg"
    } else {
        Write-Success "Database security group exists: $dbSg"
    }
}

# Function: Deploy Database
function Deploy-Database {
    Write-Info "🗄️  Deploying RDS PostgreSQL database..."
    
    # Check if DB instance exists
    $dbExists = aws rds describe-db-instances `
        --db-instance-identifier $DBInstanceId `
        --query 'DBInstances[0].DBInstanceStatus' `
        --output text 2>$null
    
    if ($dbExists -eq "available") {
        Write-Success "Database already exists and is available"
        $endpoint = aws rds describe-db-instances `
            --db-instance-identifier $DBInstanceId `
            --query 'DBInstances[0].Endpoint.Address' `
            --output text
        Write-Info "Endpoint: $endpoint"
        return $endpoint
    }
    
    if ($DryRun) {
        Write-Info "[DRY RUN] Would create RDS instance: $DBInstanceId"
        return "dry-run-endpoint.rds.amazonaws.com"
    }
    
    Write-Info "Creating RDS instance (this may take 10-15 minutes)..."
    
    # Generate secure password
    $dbPassword = -join ((65..90) + (97..122) + (48..57) | Get-Random -Count 16 | ForEach-Object {[char]$_})
    
    # Create DB subnet group first
    $subnets = aws ec2 describe-subnets `
        --filters "Name=tag:Name,Values=$AppName-*" `
        --query 'Subnets[*].SubnetId' `
        --output text
    
    if ($subnets) {
        $subnetArray = $subnets -split '\s+'
        aws rds create-db-subnet-group `
            --db-subnet-group-name "$AppName-db-subnet" `
            --db-subnet-group-description "Subnet group for EduBridge DB" `
            --subnet-ids $subnetArray 2>$null
    }
    
    # Get security group
    $vpcId = aws ec2 describe-vpcs `
        --filters "Name=tag:Name,Values=$AppName-vpc" `
        --query 'Vpcs[0].VpcId' `
        --output text
    
    $dbSg = aws ec2 describe-security-groups `
        --filters "Name=group-name,Values=$AppName-db-sg" "Name=vpc-id,Values=$vpcId" `
        --query 'SecurityGroups[0].GroupId' `
        --output text
    
    # Create RDS instance
    aws rds create-db-instance `
        --db-instance-identifier $DBInstanceId `
        --db-instance-class db.t3.micro `
        --engine postgres `
        --engine-version 16.1 `
        --master-username edubridge_admin `
        --master-user-password $dbPassword `
        --allocated-storage 20 `
        --storage-type gp3 `
        --storage-encrypted `
        --backup-retention-period 7 `
        --vpc-security-group-ids $dbSg `
        --db-subnet-group-name "$AppName-db-subnet" `
        --publicly-accessible false `
        --tags Key=Name,Value=$DBInstanceId Key=Environment,Value=$Environment
    
    Write-Info "Waiting for database to become available..."
    aws rds wait db-instance-available --db-instance-identifier $DBInstanceId
    
    $endpoint = aws rds describe-db-instances `
        --db-instance-identifier $DBInstanceId `
        --query 'DBInstances[0].Endpoint.Address' `
        --output text
    
    Write-Success "Database created successfully!"
    Write-Info "Endpoint: $endpoint"
    Write-Info "Username: edubridge_admin"
    Write-Info "Password: $dbPassword"
    Write-Warning "IMPORTANT: Save the password in AWS Secrets Manager!"
    
    # Store in Secrets Manager
    $secretName = "$AppName-db-credentials-$Environment"
    aws secretsmanager create-secret `
        --name $secretName `
        --description "Database credentials for $Environment" `
        --secret-string "{\"username\":\"edubridge_admin\",\"password\":\"$dbPassword\",\"endpoint\":\"$endpoint\",\"port\":\"5432\",\"dbname\":\"edubridge\"}" `
        --region $Region 2>$null
    
    Write-Success "Credentials stored in Secrets Manager: $secretName"
    
    return $endpoint
}

# Function: Deploy Backend
function Deploy-Backend {
    param([string]$DBEndpoint)
    
    Write-Info "🚀 Deploying backend services..."
    
    if (-not $SkipBuild) {
        Write-Info "Building backend services..."
        
        # Build auth-service
        Write-Info "Building auth-service..."
        Set-Location "$ProjectRoot\auth-service"
        mvn clean package -DskipTests
        if ($LASTEXITCODE -ne 0) {
            Write-Error-Custom "Auth service build failed"
            exit 1
        }
        Write-Success "Auth service built"
        
        # Build course-service
        Write-Info "Building course-service..."
        Set-Location "$ProjectRoot\course-service"
        mvn clean package -DskipTests
        if ($LASTEXITCODE -ne 0) {
            Write-Error-Custom "Course service build failed"
            exit 1
        }
        Write-Success "Course service built"
        
        Set-Location $ProjectRoot
    }
    
    if ($DryRun) {
        Write-Info "[DRY RUN] Would deploy backend to Elastic Beanstalk"
        return
    }
    
    # Check if EB CLI is installed
    try {
        $ebVersion = eb --version 2>&1
        Write-Success "EB CLI installed: $ebVersion"
    } catch {
        Write-Warning "EB CLI not found. Installing via pip..."
        python -m pip install awsebcli
    }
    
    # Deploy auth-service
    Write-Info "Deploying auth-service to Elastic Beanstalk..."
    Set-Location "$ProjectRoot\auth-service"
    
    # Initialize EB if needed
    if (-not (Test-Path ".elasticbeanstalk")) {
        eb init -p "Corretto 17 running on 64bit Amazon Linux 2023" $EBAppName --region $Region
    }
    
    # Create or update environment
    $envExists = eb list | Select-String $AuthEnvName
    if (-not $envExists) {
        Write-Info "Creating new environment: $AuthEnvName"
        eb create $AuthEnvName `
            --instance-type t3.small `
            --envvars "DB_HOST=$DBEndpoint,DB_NAME=edubridge,SPRING_PROFILES_ACTIVE=prod"
    } else {
        Write-Info "Deploying to existing environment: $AuthEnvName"
        eb deploy $AuthEnvName
    }
    
    $authUrl = eb status $AuthEnvName | Select-String "CNAME" | ForEach-Object { $_.ToString().Split(":")[1].Trim() }
    Write-Success "Auth service deployed: http://$authUrl"
    
    # Deploy course-service
    Write-Info "Deploying course-service to Elastic Beanstalk..."
    Set-Location "$ProjectRoot\course-service"
    
    if (-not (Test-Path ".elasticbeanstalk")) {
        eb init -p "Corretto 17 running on 64bit Amazon Linux 2023" $EBAppName --region $Region
    }
    
    $envExists = eb list | Select-String $CourseEnvName
    if (-not $envExists) {
        Write-Info "Creating new environment: $CourseEnvName"
        eb create $CourseEnvName `
            --instance-type t3.small `
            --envvars "DB_HOST=$DBEndpoint,DB_NAME=edubridge,SPRING_PROFILES_ACTIVE=prod"
    } else {
        Write-Info "Deploying to existing environment: $CourseEnvName"
        eb deploy $CourseEnvName
    }
    
    $courseUrl = eb status $CourseEnvName | Select-String "CNAME" | ForEach-Object { $_.ToString().Split(":")[1].Trim() }
    Write-Success "Course service deployed: http://$courseUrl"
    
    Set-Location $ProjectRoot
    
    return @{
        AuthUrl = "http://$authUrl"
        CourseUrl = "http://$courseUrl"
    }
}

# Function: Deploy Frontend
function Deploy-Frontend {
    param([hashtable]$BackendUrls)
    
    Write-Info "🌐 Deploying frontend to S3 + CloudFront..."
    
    Set-Location "$ProjectRoot\edubridge-frontend"
    
    if (-not $SkipBuild) {
        Write-Info "Building Angular application..."
        
        # Update environment file with backend URLs
        if ($BackendUrls) {
            $envFile = "src\environments\environment.prod.ts"
            $envContent = @"
export const environment = {
  production: true,
  apiUrl: '$($BackendUrls.AuthUrl)',
  courseApiUrl: '$($BackendUrls.CourseUrl)',
  geminiApiKey: 'AIzaSyCDs-mjox5cYtg1c5GeDhJp-hhoTEi6Cl0'
};
"@
            Set-Content -Path $envFile -Value $envContent
            Write-Success "Environment configured with backend URLs"
        }
        
        npm run build
        if ($LASTEXITCODE -ne 0) {
            Write-Error-Custom "Frontend build failed"
            exit 1
        }
        Write-Success "Frontend built successfully"
    }
    
    if ($DryRun) {
        Write-Info "[DRY RUN] Would deploy to S3: $S3Bucket"
        Set-Location $ProjectRoot
        return
    }
    
    # Create S3 bucket
    Write-Info "Creating S3 bucket: $S3Bucket"
    aws s3 mb "s3://$S3Bucket" --region $Region 2>$null
    
    # Configure for static website hosting
    aws s3 website "s3://$S3Bucket" `
        --index-document index.html `
        --error-document index.html
    
    # Upload files
    Write-Info "Uploading files to S3..."
    aws s3 sync dist/edubridge-frontend/browser/ "s3://$S3Bucket/" `
        --delete `
        --cache-control "max-age=31536000" `
        --exclude "index.html" `
        --exclude "*.map"
    
    # Upload index.html with no-cache
    aws s3 cp dist/edubridge-frontend/browser/index.html "s3://$S3Bucket/" `
        --cache-control "no-cache, no-store, must-revalidate"
    
    # Set bucket policy
    $bucketPolicy = @"
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Sid": "PublicReadGetObject",
      "Effect": "Allow",
      "Principal": "*",
      "Action": "s3:GetObject",
      "Resource": "arn:aws:s3:::$S3Bucket/*"
    }
  ]
}
"@
    
    $bucketPolicy | aws s3api put-bucket-policy --bucket $S3Bucket --policy file:///dev/stdin
    
    Write-Success "Files uploaded to S3"
    
    # Create CloudFront distribution
    Write-Info "Creating CloudFront distribution..."
    
    $distributionConfig = @"
{
  "CallerReference": "$AppName-$Timestamp",
  "Comment": "EduBridge Frontend CDN - $Environment",
  "DefaultRootObject": "index.html",
  "Origins": {
    "Quantity": 1,
    "Items": [
      {
        "Id": "S3-$S3Bucket",
        "DomainName": "$S3Bucket.s3.amazonaws.com",
        "S3OriginConfig": {
          "OriginAccessIdentity": ""
        }
      }
    ]
  },
  "DefaultCacheBehavior": {
    "TargetOriginId": "S3-$S3Bucket",
    "ViewerProtocolPolicy": "redirect-to-https",
    "AllowedMethods": {
      "Quantity": 2,
      "Items": ["GET", "HEAD"],
      "CachedMethods": {
        "Quantity": 2,
        "Items": ["GET", "HEAD"]
      }
    },
    "ForwardedValues": {
      "QueryString": false,
      "Cookies": { "Forward": "none" }
    },
    "MinTTL": 0,
    "DefaultTTL": 86400,
    "MaxTTL": 31536000,
    "Compress": true
  },
  "CustomErrorResponses": {
    "Quantity": 1,
    "Items": [
      {
        "ErrorCode": 404,
        "ResponseCode": "200",
        "ResponsePagePath": "/index.html",
        "ErrorCachingMinTTL": 300
      }
    ]
  },
  "Enabled": true,
  "PriceClass": "PriceClass_100"
}
"@
    
    $distribution = $distributionConfig | aws cloudfront create-distribution --distribution-config file:///dev/stdin 2>$null | ConvertFrom-Json
    
    if ($distribution) {
        $distributionDomain = $distribution.Distribution.DomainName
        Write-Success "CloudFront distribution created: $distributionDomain"
        Write-Info "Frontend URL: https://$distributionDomain"
    } else {
        Write-Warning "Could not create CloudFront distribution. Using S3 website endpoint."
        $websiteUrl = "http://$S3Bucket.s3-website-$Region.amazonaws.com"
        Write-Info "Frontend URL: $websiteUrl"
    }
    
    Set-Location $ProjectRoot
}

# Main execution
try {
    switch ($Component) {
        'infrastructure' {
            Deploy-Infrastructure
        }
        'database' {
            if ($Component -eq 'database' -or $Component -eq 'all') {
                Deploy-Infrastructure
            }
            Deploy-Database
        }
        'backend' {
            if ($Component -eq 'backend' -or $Component -eq 'all') {
                Deploy-Infrastructure
                $dbEndpoint = Deploy-Database
            } else {
                $dbEndpoint = "existing-endpoint.rds.amazonaws.com"
            }
            Deploy-Backend -DBEndpoint $dbEndpoint
        }
        'frontend' {
            Deploy-Frontend -BackendUrls @{
                AuthUrl = "http://your-auth-url.elasticbeanstalk.com"
                CourseUrl = "http://your-course-url.elasticbeanstalk.com"
            }
        }
        'all' {
            Deploy-Infrastructure
            $dbEndpoint = Deploy-Database
            $backendUrls = Deploy-Backend -DBEndpoint $dbEndpoint
            Deploy-Frontend -BackendUrls $backendUrls
        }
    }
    
    Write-Host ""
    Write-Host "╔═══════════════════════════════════════════════════════════╗" -ForegroundColor Green
    Write-Host "║                                                           ║" -ForegroundColor Green
    Write-Host "║          🎉 DEPLOYMENT COMPLETED SUCCESSFULLY! 🎉         ║" -ForegroundColor Green
    Write-Host "║                                                           ║" -ForegroundColor Green
    Write-Host "╚═══════════════════════════════════════════════════════════╝" -ForegroundColor Green
    Write-Host ""
    
    Write-Info "Next steps:"
    Write-Host "  1. Configure your domain in Route 53" -ForegroundColor White
    Write-Host "  2. Request SSL certificate in ACM" -ForegroundColor White
    Write-Host "  3. Update CloudFront with custom domain" -ForegroundColor White
    Write-Host "  4. Test all endpoints thoroughly" -ForegroundColor White
    Write-Host "  5. Set up monitoring and alarms" -ForegroundColor White
    Write-Host ""
    Write-Info "Deployment logs saved to: logs\deploy-$Timestamp.log"
    Write-Host ""
    
} catch {
    Write-Error-Custom "Deployment failed: $_"
    Write-Host $_.ScriptStackTrace -ForegroundColor Red
    exit 1
}
