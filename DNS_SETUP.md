# DNS Configuration Guide for api.envyguard.crudzaso.com

## Quick Setup Steps

### 1. DNS Configuration

Go to your DNS provider (where `crudzaso.com` domain is managed) and create:

**A Record:**
- **Type**: `A`
- **Name**: `api.envyguard` (or `api` depending on your DNS structure)
- **Value**: `91.99.188.229`
- **TTL**: `3600`

**Important**: The exact name depends on your DNS hierarchy:
- If you want `api.envyguard.crudzaso.com`, the name should be `api.envyguard`
- If `envyguard` is already a subdomain, use just `api`

### 2. Install Nginx on VPS

```bash
# SSH into your VPS
ssh samuel@91.99.188.229

# Install Nginx
sudo apt update
sudo apt install -y nginx

# Check status
sudo systemctl status nginx
```

### 3. Configure Nginx

```bash
# Navigate to project directory
cd /srv/envyguard-backend/EnvyGuard-Backend-JAVA

# Copy configuration file
sudo cp nginx-api.conf /etc/nginx/sites-available/api-envyguard

# Enable the site
sudo ln -s /etc/nginx/sites-available/api-envyguard /etc/nginx/sites-enabled/

# Remove default site (optional)
sudo rm /etc/nginx/sites-enabled/default

# Test configuration
sudo nginx -t

# If test passes, reload Nginx
sudo systemctl reload nginx
```

### 4. Configure Firewall

```bash
# Allow HTTP and HTTPS
sudo ufw allow 'Nginx Full'

# Or manually:
sudo ufw allow 80/tcp
sudo ufw allow 443/tcp

# Check status
sudo ufw status
```

### 5. Wait for DNS Propagation

DNS changes can take 5-15 minutes to propagate. Verify with:

```bash
# From your local machine or VPS
dig api.envyguard.crudzaso.com
# or
nslookup api.envyguard.crudzaso.com
```

You should see `91.99.188.229` in the response.

### 6. Test HTTP Access

Once DNS is propagated:

```bash
# Test from VPS
curl http://api.envyguard.crudzaso.com/api/auth/health

# Or from browser
# http://api.envyguard.crudzaso.com/api/swagger-ui/index.html
```

### 7. Configure SSL (HTTPS)

```bash
# Install Certbot
sudo apt install -y certbot python3-certbot-nginx

# Obtain SSL certificate
sudo certbot --nginx -d api.envyguard.crudzaso.com

# Follow the prompts:
# - Enter your email
# - Agree to terms
# - Choose whether to redirect HTTP to HTTPS (recommended: Yes)
```

Certbot will automatically:
- Obtain the SSL certificate
- Update your Nginx configuration
- Set up automatic renewal

### 8. Verify HTTPS

```bash
# Test HTTPS endpoint
curl https://api.envyguard.crudzaso.com/api/auth/health

# Access Swagger UI
# https://api.envyguard.crudzaso.com/api/swagger-ui/index.html
```

### 9. Test Automatic SSL Renewal

```bash
# Test renewal process (dry run)
sudo certbot renew --dry-run
```

## Final URLs

After setup, your API will be accessible at:

- **HTTP**: `http://api.envyguard.crudzaso.com/api`
- **HTTPS**: `https://api.envyguard.crudzaso.com/api`
- **Swagger UI**: `https://api.envyguard.crudzaso.com/api/swagger-ui/index.html`
- **Health Check**: `https://api.envyguard.crudzaso.com/api/auth/health`

## Troubleshooting

### DNS Issues

```bash
# Check DNS resolution
dig api.envyguard.crudzaso.com
nslookup api.envyguard.crudzaso.com

# If not resolving, wait a few more minutes or check DNS provider
```

### Nginx Issues

```bash
# Check Nginx status
sudo systemctl status nginx

# Check Nginx configuration
sudo nginx -t

# View error logs
sudo tail -f /var/log/nginx/api-envyguard-error.log

# View access logs
sudo tail -f /var/log/nginx/api-envyguard-access.log
```

### Backend Issues

```bash
# Check if backend container is running
sudo docker ps | grep envyguard-backend

# Check backend logs
sudo docker logs envyguard-backend

# Test backend directly
curl http://localhost:8080/api/auth/health
```

### SSL Issues

```bash
# Check certificate status
sudo certbot certificates

# Renew certificate manually
sudo certbot renew

# Check Nginx SSL configuration
sudo nginx -t
```

## Maintenance

### Update Nginx Configuration

If you need to modify the Nginx configuration:

```bash
sudo nano /etc/nginx/sites-available/api-envyguard
sudo nginx -t
sudo systemctl reload nginx
```

### Update Backend

When you update the backend:

```bash
cd /srv/envyguard-backend/EnvyGuard-Backend-JAVA
git pull  # or upload new files
sudo docker compose build backend
sudo docker compose up -d backend
```

Nginx will automatically proxy to the updated backend.

