# 🚀 Quick AWS Deployment Guide

Get EduBridge AI Platform running on AWS in under 30 minutes!

## 📋 What You Need

- AWS Account
- AWS CLI installed
- 30 minutes of time
- Credit card for AWS (free tier eligible)

---

## ⚡ Option 1: One-Click Automated Deployment (Easiest)

### Step 1: Configure AWS

```powershell
# Install AWS CLI
winget install Amazon.AWSCLI

# Configure credentials
aws configure
# Enter your Access Key, Secret Key, Region (us-east-1), and format (json)
```

### Step 2: Run Deployment Script

```powershell
cd "c:\Users\Muneer Ali Subzwari\Desktop\Edubridge"

# Deploy everything automatically
.\scripts\deploy-to-aws.ps1

# Or deploy specific components
.\scripts\deploy-to-aws.ps1 -Component frontend
.\scripts\deploy-to-aws.ps1 -Component backend
```

**That's it!** The script will:
- ✅ Create VPC, subnets, security groups
- ✅ Deploy RDS PostgreSQL database
- ✅ Deploy Redis cache
- ✅ Build and deploy backend services
- ✅ Build and deploy frontend to S3
- ✅ Set up CloudFront CDN

---

## 🏗️ Option 2: Using CloudFormation (Infrastructure as Code)

### Step 1: Deploy Infrastructure

```powershell
# Create infrastructure stack
aws cloudformation create-stack `
  --stack-name edubridge-prod `
  --template-body file://cloudformation/infrastructure.yml `
  --parameters ParameterKey=Environment,ParameterValue=prod `
               ParameterKey=DBPassword,ParameterValue=YourSecurePassword123! `
  --capabilities CAPABILITY_IAM `
  --region us-east-1

# Wait for completion (10-15 minutes)
aws cloudformation wait stack-create-complete --stack-name edubridge-prod

# Get outputs
aws cloudformation describe-stacks --stack-name edubridge-prod --query 'Stacks[0].Outputs'
```

### Step 2: Build and Deploy Services

```powershell
# Build backend
cd auth-service
mvn clean package -DskipTests

# Build frontend
cd ..\edubridge-frontend
npm run build

# Deploy to S3
aws s3 sync dist/edubridge-frontend/browser/ s3://edubridge-prod-frontend/ --delete
```

---

## 🐳 Option 3: Using Docker + ECS (Most Scalable)

### Step 1: Build Docker Images

```powershell
# Build auth service
cd auth-service
docker build -t edubridge-auth:latest .

# Build course service
cd ..\course-service
docker build -t edubridge-course:latest .

# Build frontend
cd ..\edubridge-frontend
docker build -t edubridge-frontend:latest .
```

### Step 2: Push to ECR

```powershell
# Login to ECR
aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin YOUR_ACCOUNT_ID.dkr.ecr.us-east-1.amazonaws.com

# Create repositories
aws ecr create-repository --repository-name edubridge-auth
aws ecr create-repository --repository-name edubridge-course

# Tag and push
docker tag edubridge-auth:latest YOUR_ACCOUNT_ID.dkr.ecr.us-east-1.amazonaws.com/edubridge-auth:latest
docker push YOUR_ACCOUNT_ID.dkr.ecr.us-east-1.amazonaws.com/edubridge-auth:latest
```

### Step 3: Deploy to ECS

```powershell
# The CloudFormation template already created the cluster
# Now create task definitions and services via AWS Console or CLI
```

---

## 🎯 Option 4: Simple EC2 Deployment (Quick Testing)

### Step 1: Launch EC2 Instance

```powershell
# Launch t3.medium instance
aws ec2 run-instances `
  --image-id ami-0c55b159cbfafe1f0 `
  --instance-type t3.medium `
  --key-name your-key-pair `
  --security-group-ids sg-xxxxxxxx `
  --user-data file://scripts/ec2-setup.sh `
  --tag-specifications 'ResourceType=instance,Tags=[{Key=Name,Value=edubridge-server}]'
```

### Step 2: SSH and Deploy

```bash
# SSH to instance
ssh -i your-key.pem ec2-user@your-instance-ip

# Copy deployment files and start services
```

---

## 📊 After Deployment

### Check Your URLs

```powershell
# Get CloudFront URL (Frontend)
aws cloudformation describe-stacks `
  --stack-name edubridge-prod `
  --query 'Stacks[0].Outputs[?OutputKey==`CloudFrontURL`].OutputValue' `
  --output text

# Get ALB URL (Backend API)
aws cloudformation describe-stacks `
  --stack-name edubridge-prod `
  --query 'Stacks[0].Outputs[?OutputKey==`ALBDNSName`].OutputValue' `
  --output text
```

### Test Your Deployment

```powershell
# Test frontend
curl https://YOUR_CLOUDFRONT_URL

# Test auth API
curl http://YOUR_ALB_URL/api/auth/health

# Test course API
curl http://YOUR_ALB_URL/api/courses/health
```

---

## 🔐 Set Up Custom Domain (Optional but Recommended)

### Step 1: Request SSL Certificate

```powershell
# Request certificate in us-east-1 (CloudFront requirement)
aws acm request-certificate `
  --domain-name edubridge.com `
  --subject-alternative-names www.edubridge.com api.edubridge.com `
  --validation-method DNS `
  --region us-east-1
```

### Step 2: Create Route 53 Hosted Zone

```powershell
# Create hosted zone
aws route53 create-hosted-zone `
  --name edubridge.com `
  --caller-reference $(Get-Date -Format FileDateTime)
```

### Step 3: Update CloudFront and ALB

Update your CloudFormation stack with the certificate ARN, or manually in AWS Console.

---

## 💰 Cost Breakdown

### Free Tier Eligible (First 12 Months)
- **EC2 t3.micro**: 750 hours/month FREE
- **RDS db.t3.micro**: 750 hours/month FREE
- **S3**: 5GB storage FREE
- **CloudFront**: 50GB data transfer FREE
- **ALB**: Partial free tier

### After Free Tier (~$50-100/month)
- **RDS db.t3.micro**: $15
- **ElastiCache t3.micro**: $13
- **EC2 (if using)**: $15-30
- **S3 + CloudFront**: $10-20
- **Data Transfer**: $10-20
- **Total**: ~$63-98/month

### Production Scale (~$200-300/month)
- **RDS db.t3.small Multi-AZ**: $60
- **ElastiCache t3.small**: $35
- **ECS Fargate**: $50-100
- **ALB**: $20
- **S3 + CloudFront**: $20-30
- **Total**: ~$185-265/month

---

## 🛡️ Security Checklist

After deployment, secure your app:

```powershell
# Enable MFA on AWS account
# aws iam enable-mfa-device --user-name your-user

# Create IAM role for EC2/ECS (don't use root keys)
# Use AWS Secrets Manager for sensitive data

# Enable CloudTrail for audit logging
aws cloudtrail create-trail --name edubridge-trail --s3-bucket-name edubridge-logs

# Enable GuardDuty for threat detection
aws guardduty create-detector --enable
```

---

## 📈 Monitoring Setup

### Enable CloudWatch

```powershell
# Create alarms
aws cloudwatch put-metric-alarm `
  --alarm-name edubridge-high-cpu `
  --alarm-description "Alert on high CPU" `
  --metric-name CPUUtilization `
  --namespace AWS/EC2 `
  --statistic Average `
  --period 300 `
  --evaluation-periods 2 `
  --threshold 80 `
  --comparison-operator GreaterThanThreshold
```

### View Logs

```powershell
# Auth service logs
aws logs tail /aws/ecs/edubridge-auth --follow

# Course service logs
aws logs tail /aws/ecs/edubridge-course --follow
```

---

## 🔄 CI/CD with GitHub Actions

Your repo already has `.github/workflows/deploy.yml`!

### Set Up Secrets

Go to GitHub repo → Settings → Secrets and add:

- `AWS_ACCESS_KEY_ID`
- `AWS_SECRET_ACCESS_KEY`
- `AWS_ACCOUNT_ID`
- `CLOUDFRONT_DISTRIBUTION_ID`

### Auto-Deploy on Push

Every push to `main` will automatically:
1. Build and test
2. Deploy to AWS
3. Run database migrations
4. Invalidate CloudFront cache

---

## 🆘 Troubleshooting

### Problem: RDS won't connect

```powershell
# Check security groups
aws ec2 describe-security-groups --group-ids sg-xxxxxx

# Test connection from EC2/ECS (must be in same VPC)
```

### Problem: CloudFront shows old content

```powershell
# Invalidate cache
aws cloudfront create-invalidation `
  --distribution-id YOUR_DIST_ID `
  --paths "/*"
```

### Problem: Services won't start

```powershell
# Check ECS service events
aws ecs describe-services --cluster edubridge-cluster --services edubridge-auth

# Check CloudWatch logs
aws logs tail /aws/ecs/edubridge-auth --follow
```

---

## 🎉 You're Live!

Your EduBridge AI Platform is now running on AWS!

**Frontend**: https://YOUR_CLOUDFRONT_URL  
**API**: http://YOUR_ALB_URL  
**Database**: Managed RDS PostgreSQL  
**Cache**: ElastiCache Redis  

### Next Steps:

1. ✅ Point your domain to CloudFront
2. ✅ Enable HTTPS with ACM certificate
3. ✅ Set up monitoring and alarms
4. ✅ Configure auto-scaling
5. ✅ Enable backups and disaster recovery
6. ✅ Share with the world! 🌍

---

## 📞 Need Help?

- **AWS Documentation**: https://docs.aws.amazon.com
- **AWS Support**: Available in console
- **Community**: AWS Forums, Stack Overflow

**Congratulations on deploying to AWS! 🚀**
