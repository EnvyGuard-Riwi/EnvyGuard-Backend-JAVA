# Documentación de Endpoints API

## Base URL
```
http://localhost:8080/api
```

## Autenticación

### POST /auth/register
Registra un nuevo usuario.

**Request Body:**
```json
{
  "email": "admin@envyguard.com",
  "password": "password123",
  "firstName": "Admin",
  "lastName": "User"
}
```

**Response:** 201 Created
```json
{
  "message": "User registered successfully"
}
```

### POST /auth/login
Autentica un usuario y retorna un token JWT.

**Request Body:**
```json
{
  "email": "admin@envyguard.com",
  "password": "password123"
}
```

**Response:** 200 OK
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "email": "admin@envyguard.com",
  "firstName": "Admin",
  "lastName": "User",
  "type": "Bearer"
}
```

**Headers para requests autenticados:**
```
Authorization: Bearer {token}
```

### GET /auth/health
Verifica el estado del servicio de autenticación.

**Response:** 200 OK
```json
{
  "status": "UP",
  "service": "auth"
}
```

## Computadores

### POST /computers
Crea un nuevo computador.

**Headers:** Requiere autenticación

**Request Body:**
```json
{
  "name": "lab1-pc05",
  "ipAddress": "192.168.1.105",
  "macAddress": "00:1B:44:11:3A:B7",
  "labName": "Laboratorio 1"
}
```

**Response:** 201 Created
```json
{
  "id": 1,
  "name": "lab1-pc05",
  "ipAddress": "192.168.1.105",
  "macAddress": "00:1B:44:11:3A:B7",
  "status": "OFFLINE",
  "labName": "Laboratorio 1",
  "lastSeen": null,
  "createdAt": "2024-01-15T10:00:00",
  "updatedAt": "2024-01-15T10:00:00"
}
```

### GET /computers
Obtiene todos los computadores.

**Headers:** Requiere autenticación

**Response:** 200 OK
```json
[
  {
    "id": 1,
    "name": "lab1-pc05",
    "status": "ONLINE",
    ...
  }
]
```

### GET /computers/{id}
Obtiene un computador por su ID.

**Headers:** Requiere autenticación

**Response:** 200 OK

### GET /computers/name/{name}
Obtiene un computador por su nombre.

**Headers:** Requiere autenticación

**Response:** 200 OK

### PUT /computers/{name}/status
Actualiza el estado de un computador.

**Headers:** Requiere autenticación

**Request Body:**
```json
{
  "status": "ONLINE"
}
```

**Valores posibles:** `ONLINE`, `OFFLINE`, `UNKNOWN`

### PUT /computers/{id}
Actualiza la información de un computador.

**Headers:** Requiere autenticación

**Request Body:**
```json
{
  "ipAddress": "192.168.1.106",
  "macAddress": "00:1B:44:11:3A:B8",
  "labName": "Laboratorio 2"
}
```

### DELETE /computers/{id}
Elimina un computador.

**Headers:** Requiere autenticación

**Response:** 200 OK
```json
{
  "message": "Computer deleted successfully"
}
```

## Comandos

### POST /commands
Crea un nuevo comando para un computador.

**Headers:** Requiere autenticación

**Request Body:**
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
- `BLOCK_WEBSITE` - Bloquear un sitio web (requiere parameters con la URL)
- `LOCK_SESSION` - Bloquear la sesión del usuario

**Response:** 202 Accepted
```json
{
  "id": 1,
  "computerName": "lab1-pc05",
  "commandType": "SHUTDOWN",
  "parameters": null,
  "status": "PENDING",
  "sentAt": "2024-01-15T10:00:00",
  "executedAt": null,
  "resultMessage": null,
  "createdAt": "2024-01-15T10:00:00"
}
```

### GET /commands/computer/{computerName}
Obtiene todos los comandos de un computador específico.

**Headers:** Requiere autenticación

**Response:** 200 OK

### GET /commands/{id}
Obtiene un comando por su ID.

**Headers:** Requiere autenticación

**Response:** 200 OK

### GET /commands/status/{status}
Obtiene todos los comandos con un estado específico.

**Headers:** Requiere autenticación

**Estados posibles:** `PENDING`, `SENT`, `EXECUTED`, `FAILED`

**Response:** 200 OK

### PUT /commands/{id}/status
Actualiza el estado de un comando (usado por el agente C#).

**Headers:** Requiere autenticación

**Request Body:**
```json
{
  "status": "EXECUTED",
  "resultMessage": "Command executed successfully"
}
```

**Response:** 200 OK

## Notas

- Todos los endpoints excepto `/auth/**` y `/auth/health` requieren autenticación JWT
- El token JWT debe enviarse en el header `Authorization: Bearer {token}`
- Los timestamps están en formato ISO 8601
- Los estados de los comandos se actualizan automáticamente cuando el agente C# reporta resultados

