# EnvyGuard Backend

REST API for EnvyGuard - Remote Monitoring and Management System for Computer Labs.

## Overview

EnvyGuard Backend is a Spring Boot application that provides centralized management for computer lab infrastructure. It enables remote control of computers, command execution, and real-time monitoring through a secure REST API.

## Tech Stack

- **Java 21**
- **Spring Boot 4.0.0**
- **Spring Security** - JWT authentication
- **Spring Data JPA** - Database access
- **PostgreSQL** - Database
- **RabbitMQ** - Message queue for async command delivery to C# agents
- **Docker & Docker Compose** - Containerization
- **Swagger/OpenAPI** - API documentation

## Prerequisites

- Java 21 JDK
- Maven 3.6+
- PostgreSQL 16+
- Docker & Docker Compose (for production deployment)

## Local Development

### Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/EnvyGuard-Riwi/EnvyGuard-Backend-JAVA.git
   cd EnvyGuard-Backend-JAVA
   ```

2. **Configure PostgreSQL**
   ```bash
   # Create database
   createdb envyguard_db
   ```

3. **Update application properties**
   Edit `src/main/resources/application-dev.properties` with your database credentials.

4. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

The API will be available at `http://localhost:8080/api`

### API Documentation

Once the application is running, access Swagger UI at:
- **Swagger UI**: `http://localhost:8080/api/swagger-ui.html`
- **OpenAPI JSON**: `http://localhost:8080/api/v3/api-docs`

## API Endpoints

### Authentication

- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Authenticate user (returns JWT token)
- `GET /api/auth/health` - Health check

### Computers

- `GET /api/computers` - Get all computers (requires auth)
- `GET /api/computers/{id}` - Get computer by ID (requires auth)
- `GET /api/computers/name/{name}` - Get computer by name (requires auth)
- `POST /api/computers` - Create computer (requires auth)
- `PUT /api/computers/{id}` - Update computer (requires auth)
- `PUT /api/computers/{name}/status` - Update computer status (requires auth)
- `DELETE /api/computers/{id}` - Delete computer (requires auth)

### Commands

- `GET /api/commands` - Get all commands (requires auth)
- `GET /api/commands/{id}` - Get command by ID (requires auth)
- `GET /api/commands/computer/{computerName}` - Get commands by computer (requires auth)
- `GET /api/commands/status/{status}` - Get commands by status (requires auth)
- `POST /api/commands` - Create command (requires auth)
- `PUT /api/commands/{id}/status` - Update command status (requires auth)

## Authentication

The API uses JWT (JSON Web Tokens) for authentication. After logging in, include the token in the `Authorization` header:

```
Authorization: Bearer <your-jwt-token>
```

## VPS Deployment

### Quick Start

1. **Connect to your VPS**
   ```bash
   ssh user@91.99.188.229
   ```

2. **Install Docker**
   ```bash
   curl -fsSL https://get.docker.com -o get-docker.sh
   sudo sh get-docker.sh
   sudo usermod -aG docker $USER
   sudo apt install -y docker-compose-plugin
   exit  # Reconnect to apply changes
   ```

3. **Upload project to VPS**
   From your local machine:
   ```bash
   scp -r . user@91.99.188.229:/opt/envyguard/
   ```

4. **Create environment file**
   On the VPS:
   ```bash
   cd /opt/envyguard
   cp env.example .env
   nano .env
   ```
   
   Set secure values:
   ```env
   DB_PASSWORD=YourSecurePassword123!
   JWT_SECRET=$(openssl rand -hex 32)
   RABBITMQ_USERNAME=v9q14Pg15x
   RABBITMQ_PASSWORD=Ki4Z77IRipVLG9Y1LahdxN2twlLUZ0
   ```

5. **Configure firewall**
   ```bash
   sudo ufw allow 22/tcp
   sudo ufw allow 8080/tcp
   sudo ufw enable
   ```

6. **Start services**
   ```bash
   cd /opt/envyguard
   docker compose build
   docker compose up -d
   ```

7. **Verify deployment**
   ```bash
   docker compose ps
   curl http://localhost:8080/api/auth/health
   ```

### Access from Outside

Once deployed, access the API at:
- `http://91.99.188.229:8080/api`
- `http://91.99.188.229:8080/api/swagger-ui.html`

### Useful Commands

```bash
# View logs
docker compose logs -f backend

# Restart services
docker compose restart

# Stop services
docker compose down

# Update application
git pull  # or upload new files
docker compose build backend
docker compose up -d backend
```

## DNS Configuration

### Step 1: Configure DNS Record

In your DNS provider (where `crudzaso.com` is managed), create an A record:

- **Type**: `A`
- **Name**: `api.envyguard` (or just `api` if envyguard is a subdomain)
- **Value**: `91.99.188.229`
- **TTL**: `3600` (or `3600` seconds = 1 hour)

**Note**: The exact name depends on your DNS structure:
- If `envyguard.crudzaso.com` is a subdomain, use `api` as the name
- If you want `api.envyguard.crudzaso.com`, ensure the name is `api.envyguard`

### Step 2: Install and Configure Nginx

1. **Install Nginx** (if not already installed):
   ```bash
   sudo apt update
   sudo apt install -y nginx
   ```

2. **Copy the configuration file**:
   ```bash
   # From the project directory
   sudo cp nginx-api.conf /etc/nginx/sites-available/api-envyguard
   ```

3. **Enable the site**:
   ```bash
   sudo ln -s /etc/nginx/sites-available/api-envyguard /etc/nginx/sites-enabled/
   ```

4. **Test Nginx configuration**:
   ```bash
   sudo nginx -t
   ```

5. **Reload Nginx**:
   ```bash
   sudo systemctl reload nginx
   ```

6. **Enable Nginx to start on boot**:
   ```bash
   sudo systemctl enable nginx
   ```

### Step 3: Configure Firewall

Allow HTTP and HTTPS traffic:

```bash
sudo ufw allow 'Nginx Full'
# Or manually:
sudo ufw allow 80/tcp
sudo ufw allow 443/tcp
```

### Step 4: Configure SSL with Let's Encrypt

1. **Install Certbot**:
   ```bash
   sudo apt install -y certbot python3-certbot-nginx
   ```

2. **Obtain SSL certificate**:
   ```bash
   sudo certbot --nginx -d api.envyguard.crudzaso.com
   ```

3. **Test automatic renewal**:
   ```bash
   sudo certbot renew --dry-run
   ```

Certbot will automatically update your Nginx configuration to use HTTPS.

### Step 5: Verify Configuration

1. **Check DNS propagation** (wait a few minutes after creating the DNS record):
   ```bash
   dig api.envyguard.crudzaso.com
   # or
   nslookup api.envyguard.crudzaso.com
   ```

2. **Test the API**:
   ```bash
   curl http://api.envyguard.crudzaso.com/api/auth/health
   # After SSL:
   curl https://api.envyguard.crudzaso.com/api/auth/health
   ```

3. **Access Swagger UI**:
   - HTTP: `http://api.envyguard.crudzaso.com/api/swagger-ui/index.html`
   - HTTPS: `https://api.envyguard.crudzaso.com/api/swagger-ui/index.html`

### Troubleshooting

- **DNS not resolving**: Wait 5-15 minutes for DNS propagation, or check your DNS provider's settings
- **502 Bad Gateway**: Ensure the backend container is running: `sudo docker ps`
- **Connection refused**: Check that port 8080 is accessible: `curl http://localhost:8080/api/auth/health`
- **Nginx errors**: Check logs: `sudo tail -f /var/log/nginx/api-envyguard-error.log`

## Project Structure

```
src/
├── main/
│   ├── java/com/envyguard/backend/
│   │   ├── config/          # Configuration classes
│   │   ├── controller/      # REST controllers
│   │   ├── dto/            # Data Transfer Objects
│   │   ├── entity/         # JPA entities
│   │   ├── repository/     # Data repositories
│   │   ├── security/       # Security filters
│   │   └── service/        # Business logic
│   └── resources/
│       ├── application.properties
│       ├── application-dev.properties
│       └── application-prod.properties
├── docker-compose.yml      # Docker services
├── Dockerfile             # Backend image
└── env.example            # Environment variables template
```

## Environment Variables

### Development (`application-dev.properties`)
- Database connection (PostgreSQL)
- JWT secret and expiration
- RabbitMQ configuration (not active)

### Production (`application-prod.properties`)
Uses environment variables:
- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`
- `JWT_SECRET`
- `JWT_EXPIRATION`

## Database Schema

The application uses JPA with automatic schema generation (`ddl-auto=update`). Main entities:

- **User** - System users with authentication
- **Computer** - Managed computers with status tracking
- **Command** - Remote commands with execution status

## Security

- JWT-based authentication
- BCrypt password encoding
- CORS enabled for all origins
- CSRF disabled (stateless API)
- All endpoints except `/auth/**` require authentication

## RabbitMQ Integration

RabbitMQ is fully integrated and active. The system uses RabbitMQ for asynchronous command delivery to C# agents.

### Configuration

- **Host**: `rabbitmq.envy.crudzaso.com`
- **Port**: `5672`
- **Queues**:
  - `pc_commands` - Commands sent from backend to C# agents
  - `pc_responses` - Responses received from C# agents

### Message Flow

1. **Command Creation**: When a command is created via `POST /api/commands`, it is:
   - Saved to the database with status `PENDING`
   - Sent to RabbitMQ queue `pc_commands`
   - Status updated to `SENT` if successful, or `FAILED` if error

2. **Command Execution**: C# agents consume messages from `pc_commands` queue and execute them

3. **Response Handling**: C# agents send responses to `pc_responses` queue, which are automatically processed to update command status

### Environment Variables

For production, configure these variables:
```env
RABBITMQ_HOST=rabbitmq.envy.crudzaso.com
RABBITMQ_PORT=5672
RABBITMQ_USERNAME=your_username
RABBITMQ_PASSWORD=your_password
RABBITMQ_VIRTUAL_HOST=/
```

## Building

```bash
mvn clean package -DskipTests
```

The JAR file will be in `target/backend-0.0.1-SNAPSHOT.jar`

## Testing

```bash
mvn test
```

## Contributing

1. Create a feature branch from `develop`
2. Make your changes
3. Ensure all tests pass
4. Submit a pull request

## License

Apache 2.0

## Support

For issues and questions, please contact the development team.
