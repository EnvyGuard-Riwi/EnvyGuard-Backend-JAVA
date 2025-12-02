# Guía de Configuración para VPS

## Recomendaciones para Despliegue en VPS

### 1. Requisitos Mínimos del Servidor
- **RAM**: Mínimo 2GB (recomendado 4GB)
- **CPU**: 2 cores mínimo
- **Almacenamiento**: 20GB mínimo
- **Sistema Operativo**: Ubuntu 22.04 LTS o superior

### 2. Servicios Necesarios

#### PostgreSQL
```bash
sudo apt update
sudo apt install postgresql postgresql-contrib
sudo systemctl start postgresql
sudo systemctl enable postgresql

# Crear base de datos
sudo -u postgres psql
CREATE DATABASE envyguard_db;
CREATE USER envyguard_user WITH PASSWORD 'tu_password_seguro';
GRANT ALL PRIVILEGES ON DATABASE envyguard_db TO envyguard_user;
\q
```

#### RabbitMQ
```bash
# Agregar repositorio de RabbitMQ
curl -1sLf "https://keys.openpgp.org/vks/v1/by-fingerprint/0A9AF2115F4687BD29803A206B73A36E6026DFCA" | sudo gpg --dearmor | sudo tee /usr/share/keyrings/com.rabbitmq.team.gpg > /dev/null
echo "deb [signed-by=/usr/share/keyrings/com.rabbitmq.team.gpg] https://ppa1.novemberain.com/rabbitmq/rabbitmq-erlang/ubuntu jammy main" | sudo tee /etc/apt/sources.list.d/rabbitmq.list
echo "deb [signed-by=/usr/share/keyrings/com.rabbitmq.team.gpg] https://ppa1.novemberain.com/rabbitmq/rabbitmq-server/ubuntu jammy main" | sudo tee -a /etc/apt/sources.list.d/rabbitmq.list

# Instalar RabbitMQ
sudo apt update
sudo apt install -y erlang-base erlang-asn1 erlang-crypto erlang-eldap erlang-ftp erlang-inets erlang-mnesia erlang-os-mon erlang-parsetools erlang-public-key erlang-runtime-tools erlang-snmp erlang-ssl erlang-syntax-tools erlang-tftp erlang-tools erlang-xmerl rabbitmq-server

# Iniciar y habilitar RabbitMQ
sudo systemctl start rabbitmq-server
sudo systemctl enable rabbitmq-server

# Crear usuario y configurar permisos
sudo rabbitmqctl add_user envyguard_user tu_password_seguro
sudo rabbitmqctl set_user_tags envyguard_user administrator
sudo rabbitmqctl set_permissions -p / envyguard_user ".*" ".*" ".*"
```

#### Java 21
```bash
sudo apt update
sudo apt install openjdk-21-jdk
java -version
```

### 3. Configuración de Firewall
```bash
sudo ufw allow 22/tcp    # SSH
sudo ufw allow 8080/tcp  # Backend API
sudo ufw allow 5432/tcp  # PostgreSQL (solo si es necesario acceso externo)
sudo ufw allow 5672/tcp  # RabbitMQ (solo si es necesario acceso externo)
sudo ufw enable
```

### 4. Variables de Entorno
Crear archivo `/etc/envyguard/application-prod.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/envyguard_db
spring.datasource.username=envyguard_user
spring.datasource.password=tu_password_seguro
spring.datasource.driver-class-name=org.postgresql.Driver

spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false

jwt.secret=TU_SECRET_KEY_MUY_LARGA_Y_SEGURA_AQUI
jwt.expiration=86400000

spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672
spring.rabbitmq.username=envyguard_user
spring.rabbitmq.password=tu_password_seguro

logging.level.com.envyguard=INFO
```

### 5. Compilar y Ejecutar
```bash
# Compilar
mvn clean package -DskipTests

# Ejecutar
java -jar target/backend-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

### 6. Servicio Systemd (Opcional pero Recomendado)
Crear `/etc/systemd/system/envyguard-backend.service`:
```ini
[Unit]
Description=EnvyGuard Backend Service
After=network.target postgresql.service rabbitmq-server.service

[Service]
Type=simple
User=envyguard
WorkingDirectory=/opt/envyguard
ExecStart=/usr/bin/java -jar /opt/envyguard/backend-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
```

```bash
sudo systemctl daemon-reload
sudo systemctl start envyguard-backend
sudo systemctl enable envyguard-backend
```

### 7. Nginx como Reverse Proxy (Recomendado)
```bash
sudo apt install nginx
```

Configurar `/etc/nginx/sites-available/envyguard`:
```nginx
server {
    listen 80;
    server_name tu-dominio.com;

    location /api {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

```bash
sudo ln -s /etc/nginx/sites-available/envyguard /etc/nginx/sites-enabled/
sudo nginx -t
sudo systemctl restart nginx
```

### 8. SSL con Let's Encrypt (Recomendado)
```bash
sudo apt install certbot python3-certbot-nginx
sudo certbot --nginx -d tu-dominio.com
```

## Checklist de Seguridad
- [ ] Cambiar todas las contraseñas por defecto
- [ ] Configurar firewall correctamente
- [ ] Usar SSL/TLS para todas las conexiones
- [ ] No exponer PostgreSQL ni RabbitMQ directamente a internet
- [ ] Rotar el JWT secret regularmente
- [ ] Configurar backups de la base de datos
- [ ] Monitorear logs regularmente
- [ ] Mantener el sistema operativo actualizado

