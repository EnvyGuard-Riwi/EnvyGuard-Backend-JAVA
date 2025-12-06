#!/bin/bash

# ===========================================
# EnvyGuard Backend - Deploy Script
# ===========================================
# This script automates the deployment process
# Run this script on the VPS server
# ===========================================

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Configuration
APP_DIR="/srv/envyguard-backend/EnvyGuard-Backend-JAVA"
BRANCH="${1:-main}"

echo -e "${YELLOW}🚀 Starting EnvyGuard Backend Deployment...${NC}"
echo "=========================================="

# Navigate to app directory
cd "$APP_DIR" || { echo -e "${RED}❌ Directory not found: $APP_DIR${NC}"; exit 1; }

# Pull latest changes
echo -e "${YELLOW}📥 Pulling latest changes from $BRANCH...${NC}"
git fetch origin "$BRANCH"
git reset --hard "origin/$BRANCH"

# Stop containers
echo -e "${YELLOW}🛑 Stopping containers...${NC}"
docker-compose down

# Remove old images (optional)
echo -e "${YELLOW}🧹 Cleaning old images...${NC}"
docker image prune -f

# Build and start containers
echo -e "${YELLOW}🔨 Building and starting containers...${NC}"
docker-compose up -d --build

# Wait for application to start
echo -e "${YELLOW}⏳ Waiting for application to start (30s)...${NC}"
sleep 30

# Health check
echo -e "${YELLOW}🏥 Running health check...${NC}"
if curl -sf http://localhost:8080/api/auth/health > /dev/null; then
    echo -e "${GREEN}✅ Health check passed!${NC}"
else
    echo -e "${RED}❌ Health check failed!${NC}"
    echo "Checking logs..."
    docker-compose logs --tail=50 backend
    exit 1
fi

# Reload nginx if config changed
echo -e "${YELLOW}🔄 Reloading nginx...${NC}"
sudo cp nginx-api.conf /etc/nginx/sites-available/api-envyguard 2>/dev/null || true
sudo nginx -t && sudo systemctl reload nginx

echo "=========================================="
echo -e "${GREEN}✅ Deployment completed successfully!${NC}"
echo ""
echo "📊 Container status:"
docker-compose ps
echo ""
echo "📝 Recent logs:"
docker-compose logs --tail=10 backend
