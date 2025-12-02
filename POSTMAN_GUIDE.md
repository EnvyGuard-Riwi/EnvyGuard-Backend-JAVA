# Guía de Uso con Postman

## Configuración Base

**URL Base:** `http://localhost:8080/api`

## Endpoints Disponibles

### 1. Health Check (Sin autenticación)
- **Método:** `GET`
- **URL:** `http://localhost:8080/api/auth/health`
- **Headers:** Ninguno requerido
- **Body:** No requiere

**Respuesta esperada:**
```json
{
  "status": "UP",
  "service": "auth"
}
```

### 2. Registrar Usuario (Sin autenticación)
- **Método:** `POST`
- **URL:** `http://localhost:8080/api/auth/register`
- **Headers:**
  ```
  Content-Type: application/json
  ```
- **Body (raw JSON):**
```json
{
  "email": "admin@envyguard.com",
  "password": "password123",
  "firstName": "Admin",
  "lastName": "User"
}
```

**Respuesta esperada (201 Created):**
```json
{
  "message": "User registered successfully"
}
```

### 3. Login (Sin autenticación)
- **Método:** `POST`
- **URL:** `http://localhost:8080/api/auth/login`
- **Headers:**
  ```
  Content-Type: application/json
  ```
- **Body (raw JSON):**
```json
{
  "email": "admin@envyguard.com",
  "password": "password123"
}
```

**Respuesta esperada (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "email": "admin@envyguard.com",
  "firstName": "Admin",
  "lastName": "User",
  "type": "Bearer"
}
```

**⚠️ IMPORTANTE:** Copia el `token` de la respuesta para usarlo en los siguientes endpoints.

### 4. Crear Computador (Requiere autenticación)
- **Método:** `POST`
- **URL:** `http://localhost:8080/api/computers`
- **Headers:**
  ```
  Content-Type: application/json
  Authorization: Bearer {tu_token_aqui}
  ```
- **Body (raw JSON):**
```json
{
  "name": "lab1-pc05",
  "ipAddress": "192.168.1.105",
  "macAddress": "00:1B:44:11:3A:B7",
  "labName": "Laboratorio 1"
}
```

### 5. Obtener Todos los Computadores (Requiere autenticación)
- **Método:** `GET`
- **URL:** `http://localhost:8080/api/computers`
- **Headers:**
  ```
  Authorization: Bearer {tu_token_aqui}
  ```

### 6. Crear Comando (Requiere autenticación)
- **Método:** `POST`
- **URL:** `http://localhost:8080/api/commands`
- **Headers:**
  ```
  Content-Type: application/json
  Authorization: Bearer {tu_token_aqui}
  ```
- **Body (raw JSON):**
```json
{
  "computerName": "lab1-pc05",
  "commandType": "SHUTDOWN",
  "parameters": null
}
```

**Tipos de comando disponibles:**
- `SHUTDOWN` - Apagar el computador
- `REBOOT` - Reiniciar el computador
- `BLOCK_WEBSITE` - Bloquear un sitio web
- `LOCK_SESSION` - Bloquear la sesión

## Solución de Problemas

### Error 401 Unauthorized
- Verifica que estés usando el token correcto
- Asegúrate de incluir `Bearer ` antes del token en el header Authorization
- El token expira después de 24 horas (86400000 ms)

### Error 400 Bad Request
- Verifica que el JSON esté bien formateado
- Asegúrate de incluir el header `Content-Type: application/json`
- Verifica que todos los campos requeridos estén presentes

### Error 404 Not Found
- Verifica que la URL sea correcta: `http://localhost:8080/api/...`
- Asegúrate de que el servidor esté corriendo
- Verifica que el endpoint exista

### Error de CORS
- El backend está configurado para permitir todas las conexiones
- Si persiste, verifica que Postman no esté bloqueando las peticiones

## Colección de Postman

Puedes crear una colección en Postman con estos endpoints para facilitar las pruebas.

### Variables de Entorno en Postman
Crea un entorno con estas variables:
- `base_url`: `http://localhost:8080/api`
- `token`: (se actualiza después del login)

Luego usa `{{base_url}}/auth/login` en lugar de la URL completa.

