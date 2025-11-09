# 🚀 AWS Deployment Guide for EduBridge AI Platform

Complete guide to deploy your EduBridge AI Learning Platform to AWS with production-grade infrastructure.

## 📋 Table of Contents

1. [Architecture Overview](#architecture-overview)
2. [Prerequisites](#prerequisites)
3. [Quick Deployment (Automated)](#quick-deployment-automated)
4. [Manual Deployment](#manual-deployment)
5. [Database Setup](#database-setup)
6. [Backend Deployment](#backend-deployment)
7. [Frontend Deployment](#frontend-deployment)
8. [Domain & SSL Setup](#domain--ssl-setup)
9. [Monitoring & Logging](#monitoring--logging)
10. [Cost Estimation](#cost-estimation)

---

## 🏗️ Architecture Overview

```
┌─────────────────────────────────────────────────────────────┐
│                    Route 53 (DNS)                           │
│              edubridge.com / www.edubridge.com              │
└────────────────────────┬────────────────────────────────────┘
                         │
         ┌───────────────┴───────────────┐
         │                               │
         ▼                               ▼
┌─────────────────┐            ┌──────────────────┐
│  CloudFront CDN │            │  Application     │
│  (Frontend)     │            │  Load Balancer   │
│  + S3 Bucket    │            │  (Backend APIs)  │
└─────────────────┘            └────────┬─────────┘
         │                              │
         │                    ┌─────────┴─────────┐
         │                    │                   │
         ▼                    ▼                   ▼
┌─────────────────┐   ┌──────────────┐   ┌──────────────┐
│  Angular App    │   │ Auth Service │   │Course Service│
│  (Static Files) │   │  (EC2/ECS)   │   │  (EC2/ECS)   │
└─────────────────┘   └──────┬───────┘   └──────┬───────┘
                             │                   │
                    ┌────────┴───────────────────┘
                    │
                    ▼
          ┌──────────────────┐
          │   RDS PostgreSQL │
          │  (Multi-AZ)      │
          └──────────────────┘
                    │
          ┌─────────┴─────────┐
          │                   │
          ▼                   ▼
    ┌──────────┐      ┌──────────────┐
    │ ElastiCache│      │  S3 Bucket   │
    │  (Redis)   │      │ (File Storage│
    └──────────┘      └──────────────┘
```

---

## ✅ Prerequisites

### Required Tools
- AWS Account with billing enabled
- AWS CLI installed and configured
- Docker Desktop installed
- Node.js 18+ and npm
- Java 17+ and Maven
- Git

### AWS Services We'll Use
- **EC2** or **Elastic Beanstalk**: Backend services
- **RDS**: PostgreSQL database
- **S3**: Frontend hosting & file storage
- **CloudFront**: CDN for frontend
- **ElastiCache**: Redis cache
- **Route 53**: DNS management
- **Certificate Manager**: SSL certificates
- **CloudWatch**: Logging & monitoring
- **Systems Manager**: Secrets management

### Estimated Costs
- **Development**: ~$50-80/month
- **Production**: ~$150-250/month
- **High Traffic**: $300+/month

---

## 🚀 Quick Deployment (Automated)

We've created automated scripts for you!

### Step 1: Configure AWS Credentials

```powershell
# Install AWS CLI (if not installed)
winget install Amazon.AWSCLI

# Configure AWS credentials
aws configure
# AWS Access Key ID: [Enter your key]
# AWS Secret Access Key: [Enter your secret]
# Default region: us-east-1
# Default output format: json
```

### Step 2: Run Deployment Script

```powershell
# Navigate to project root
cd "c:\Users\Muneer Ali Subzwari\Desktop\Edubridge"

# Run automated deployment
.\scripts\deploy-to-aws.ps1

# Or deploy specific components
.\scripts\deploy-to-aws.ps1 -Component frontend
.\scripts\deploy-to-aws.ps1 -Component backend
.\scripts\deploy-to-aws.ps1 -Component database
```

The script will:
1. ✅ Create all AWS resources
2. ✅ Build and deploy frontend to S3
3. ✅ Deploy backend services
4. ✅ Set up database with migrations
5. ✅ Configure CloudFront CDN
6. ✅ Output all URLs and credentials

---

## 🔧 Manual Deployment

### 1. Database Setup (RDS PostgreSQL)

#### Create RDS Instance

```powershell
# Create RDS PostgreSQL instance
aws rds create-db-instance `
  --db-instance-identifier edubridge-db `
  --db-instance-class db.t3.micro `
  --engine postgres `
  --engine-version 16.1 `
  --master-username edubridge_admin `
  --master-user-password "YourStrongPassword123!" `
  --allocated-storage 20 `
  --storage-type gp3 `
  --storage-encrypted `
  --backup-retention-period 7 `
  --multi-az `
  --publicly-accessible false `
  --vpc-security-group-ids sg-xxxxxxxxx `
  --db-subnet-group-name edubridge-db-subnet `
  --tags Key=Name,Value=edubridge-database Key=Environment,Value=production

# Wait for instance to be available
aws rds wait db-instance-available --db-instance-identifier edubridge-db

# Get endpoint
aws rds describe-db-instances `
  --db-instance-identifier edubridge-db `
  --query 'DBInstances[0].Endpoint.Address' `
  --output text
```

#### Create ElastiCache Redis

```powershell
# Create Redis cluster
aws elasticache create-cache-cluster `
  --cache-cluster-id edubridge-redis `
  --cache-node-type cache.t3.micro `
  --engine redis `
  --engine-version 7.0 `
  --num-cache-nodes 1 `
  --cache-subnet-group-name edubridge-cache-subnet `
  --security-group-ids sg-xxxxxxxxx `
  --tags Key=Name,Value=edubridge-redis Key=Environment,Value=production

# Get endpoint
aws elasticache describe-cache-clusters `
  --cache-cluster-id edubridge-redis `
  --show-cache-node-info `
  --query 'CacheClusters[0].CacheNodes[0].Endpoint.Address' `
  --output text
```

---

### 2. Backend Deployment (Elastic Beanstalk)

#### Option A: Using Elastic Beanstalk (Recommended)

```powershell
# Install EB CLI
pip install awsebcli

# Initialize Elastic Beanstalk
cd auth-service
eb init -p "Corretto 17 running on 64bit Amazon Linux 2023" edubridge-auth --region us-east-1

# Create environment
eb create edubridge-auth-prod `
  --instance-type t3.small `
  --envvars DB_HOST=your-rds-endpoint.rds.amazonaws.com,DB_NAME=edubridge,DB_USER=edubridge_admin,DB_PASSWORD=YourPassword123!,REDIS_HOST=your-redis-endpoint.cache.amazonaws.com,JWT_SECRET=your-secret-key

# Deploy
mvn clean package -DskipTests
eb deploy

# Repeat for course-service
cd ../course-service
eb init -p "Corretto 17 running on 64bit Amazon Linux 2023" edubridge-course --region us-east-1
eb create edubridge-course-prod --instance-type t3.small
mvn clean package -DskipTests
eb deploy
```

#### Option B: Using EC2 (More Control)

```powershell
# Create EC2 instance
aws ec2 run-instances `
  --image-id ami-0c55b159cbfafe1f0 `
  --instance-type t3.small `
  --key-name your-key-pair `
  --security-group-ids sg-xxxxxxxxx `
  --subnet-id subnet-xxxxxxxxx `
  --user-data file://scripts/ec2-user-data.sh `
  --tag-specifications 'ResourceType=instance,Tags=[{Key=Name,Value=edubridge-backend}]'
```

Then SSH and deploy:

```bash
# SSH to EC2
ssh -i your-key.pem ec2-user@your-instance-ip

# Install Java 17
sudo yum install -y java-17-amazon-corretto-devel

# Create deployment directory
sudo mkdir -p /opt/edubridge
sudo chown ec2-user:ec2-user /opt/edubridge

# Copy JAR files (from local machine)
scp -i your-key.pem auth-service/target/*.jar ec2-user@your-instance-ip:/opt/edubridge/
scp -i your-key.pem course-service/target/*.jar ec2-user@your-instance-ip:/opt/edubridge/

# Create systemd service (on EC2)
sudo tee /etc/systemd/system/edubridge-auth.service > /dev/null <<EOF
[Unit]
Description=EduBridge Auth Service
After=network.target

[Service]
Type=simple
User=ec2-user
WorkingDirectory=/opt/edubridge
ExecStart=/usr/bin/java -jar /opt/edubridge/auth-service-0.1.0-SNAPSHOT.jar
Restart=always
Environment="SPRING_PROFILES_ACTIVE=prod"
Environment="DB_HOST=your-rds-endpoint"
Environment="DB_NAME=edubridge"
Environment="DB_USER=edubridge_admin"
Environment="DB_PASSWORD=YourPassword123!"

[Install]
WantedBy=multi-user.target
EOF

# Start services
sudo systemctl daemon-reload
sudo systemctl enable edubridge-auth
sudo systemctl start edubridge-auth
sudo systemctl status edubridge-auth
```

---

### 3. Frontend Deployment (S3 + CloudFront)

#### Build Production Bundle

```powershell
cd edubridge-frontend

# Update environment for production
# Edit src/environments/environment.prod.ts with your API endpoints

# Build for production
npm run build

# Files will be in dist/edubridge-frontend/browser/
```

#### Create S3 Bucket

```powershell
# Create bucket (name must be unique globally)
aws s3 mb s3://edubridge-frontend-prod --region us-east-1

# Enable static website hosting
aws s3 website s3://edubridge-frontend-prod `
  --index-document index.html `
  --error-document index.html

# Upload files
aws s3 sync dist/edubridge-frontend/browser/ s3://edubridge-frontend-prod/ `
  --delete `
  --cache-control "max-age=31536000" `
  --exclude "index.html"

# Upload index.html with no-cache
aws s3 cp dist/edubridge-frontend/browser/index.html s3://edubridge-frontend-prod/ `
  --cache-control "no-cache, no-store, must-revalidate"

# Set bucket policy for public read
aws s3api put-bucket-policy --bucket edubridge-frontend-prod --policy '{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Sid": "PublicReadGetObject",
      "Effect": "Allow",
      "Principal": "*",
      "Action": "s3:GetObject",
      "Resource": "arn:aws:s3:::edubridge-frontend-prod/*"
    }
  ]
}'
```

#### Create CloudFront Distribution

```powershell
# Create distribution
aws cloudfront create-distribution --distribution-config '{
  "CallerReference": "edubridge-'$(Get-Date -Format FileDateTime)'",
  "Comment": "EduBridge Frontend CDN",
  "DefaultRootObject": "index.html",
  "Origins": {
    "Quantity": 1,
    "Items": [
      {
        "Id": "S3-edubridge-frontend",
        "DomainName": "edubridge-frontend-prod.s3.amazonaws.com",
        "S3OriginConfig": {
          "OriginAccessIdentity": ""
        }
      }
    ]
  },
  "DefaultCacheBehavior": {
    "TargetOriginId": "S3-edubridge-frontend",
    "ViewerProtocolPolicy": "redirect-to-https",
    "AllowedMethods": {
      "Quantity": 2,
      "Items": ["GET", "HEAD"]
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
}'
```

---

### 4. Domain & SSL Setup

#### Request SSL Certificate

```powershell
# Request certificate in us-east-1 (required for CloudFront)
aws acm request-certificate `
  --domain-name edubridge.com `
  --subject-alternative-names www.edubridge.com `
  --validation-method DNS `
  --region us-east-1

# Get validation records
aws acm describe-certificate `
  --certificate-arn arn:aws:acm:us-east-1:xxxx:certificate/yyyy `
  --region us-east-1
```

#### Configure Route 53

```powershell
# Create hosted zone
aws route53 create-hosted-zone --name edubridge.com --caller-reference $(Get-Date -Format FileDateTime)

# Add A record pointing to CloudFront
aws route53 change-resource-record-sets --hosted-zone-id Z1234567890ABC --change-batch '{
  "Changes": [
    {
      "Action": "CREATE",
      "ResourceRecordSet": {
        "Name": "edubridge.com",
        "Type": "A",
        "AliasTarget": {
          "HostedZoneId": "Z2FDTNDATAQYW2",
          "DNSName": "d111111abcdef8.cloudfront.net",
          "EvaluateTargetHealth": false
        }
      }
    }
  ]
}'
```

---

## 📊 Monitoring & Logging

### CloudWatch Setup

```powershell
# Create log group
aws logs create-log-group --log-group-name /aws/edubridge/auth-service
aws logs create-log-group --log-group-name /aws/edubridge/course-service

# Create metric alarms
aws cloudwatch put-metric-alarm `
  --alarm-name edubridge-high-cpu `
  --alarm-description "Alert when CPU exceeds 80%" `
  --metric-name CPUUtilization `
  --namespace AWS/EC2 `
  --statistic Average `
  --period 300 `
  --evaluation-periods 2 `
  --threshold 80 `
  --comparison-operator GreaterThanThreshold
```

### X-Ray Tracing

Add to your Spring Boot services:

```xml
<!-- pom.xml -->
<dependency>
    <groupId>com.amazonaws</groupId>
    <artifactId>aws-xray-recorder-sdk-spring</artifactId>
    <version>2.15.0</version>
</dependency>
```

---

## 💰 Cost Estimation

### Monthly Costs (Approximate)

#### Development Environment
- **RDS db.t3.micro**: $15
- **ElastiCache cache.t3.micro**: $13
- **EC2 t3.small (2 instances)**: $30
- **S3 + CloudFront**: $5-10
- **Data Transfer**: $5-10
- **Total**: ~$70-80/month

#### Production Environment
- **RDS db.t3.small (Multi-AZ)**: $60
- **ElastiCache cache.t3.small**: $35
- **EC2 t3.medium (2 instances + Auto Scaling)**: $100
- **Application Load Balancer**: $20
- **S3 + CloudFront**: $20
- **Data Transfer**: $30
- **Total**: ~$265/month

#### Cost Optimization Tips
1. Use Reserved Instances (save 30-70%)
2. Enable S3 Intelligent Tiering
3. Use CloudFront compression
4. Set up auto-scaling with proper limits
5. Use AWS Cost Explorer and budgets

---

## 🔒 Security Checklist

- [ ] Enable MFA on AWS root account
- [ ] Use IAM roles instead of access keys
- [ ] Enable encryption at rest for RDS
- [ ] Enable encryption in transit (SSL/TLS)
- [ ] Configure security groups (least privilege)
- [ ] Enable VPC Flow Logs
- [ ] Use AWS Secrets Manager for credentials
- [ ] Enable CloudTrail for audit logging
- [ ] Configure WAF for DDoS protection
- [ ] Regular security patches and updates

---

## 🎯 Next Steps

After deployment:

1. **Update DNS**: Point your domain to CloudFront
2. **Test thoroughly**: Check all endpoints and features
3. **Set up monitoring**: Configure CloudWatch alarms
4. **Enable backups**: Automated RDS and S3 backups
5. **Configure CI/CD**: GitHub Actions or AWS CodePipeline
6. **Load testing**: Test with expected traffic
7. **Documentation**: Update API docs and user guides

---

## 📞 Support

If you encounter issues:
- Check CloudWatch logs
- Review security group rules
- Verify environment variables
- Test database connectivity
- Check IAM permissions

---

## 🎉 Congratulations!

Your EduBridge AI Platform is now running on AWS with:
- ✅ High availability
- ✅ Auto-scaling capabilities
- ✅ Global CDN delivery
- ✅ Secure infrastructure
- ✅ Production-grade monitoring

**Your URLs:**
- Frontend: https://edubridge.com
- API: https://api.edubridge.com
- Docs: https://api.edubridge.com/swagger-ui.html
