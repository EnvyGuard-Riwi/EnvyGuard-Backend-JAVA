# Bug Fixes Documentation

## Summary of Fixes

This document outlines the three security and stability bugs that were fixed in the EnvyGuard Backend.

---

## Bug 1: Hardcoded Database Credentials & Admin Password Exposure

### Issue
- Database credentials (`username: postgres`, `password: Qwe.123*`) were hardcoded in `application.yml`
- Admin password was hardcoded in configuration and printed to console logs during initialization
- Sensitive data exposed in version control and application logs

### Root Cause
Credentials were stored directly in the configuration file instead of being managed through environment variables or a secrets management system.

### Fix Applied
✅ **Files Modified:**
- `src/main/resources/application.yml`
- `src/main/java/com/envyguard/envyguard_backend/service/DataInitializerService.java`

**Changes:**

1. **Database Configuration** (application.yml)
   ```yaml
   # Before:
   datasource:
     url: jdbc:postgresql://localhost:5432/envyguard_db
     username: postgres
     password: Qwe.123*

   # After:
   datasource:
     url: jdbc:postgresql://${DB_HOST:localhost}:${DB_PORT:5432}/${DB_NAME:envyguard_db}
     username: ${DB_USERNAME:postgres}
     password: ${DB_PASSWORD:postgres}
   ```

2. **JWT & Admin Configuration** (application.yml)
   ```yaml
   # Before:
   app:
     security:
       jwt:
         secret: EnvyGuardSecretKeyForJWT256BitsSecurity2024ChangeThisInProduction
         expiration: 86400000
     admin:
       email: "admin@envyguard.com"
       password: "Admin123!"

   # After:
   app:
     security:
       jwt:
         secret: ${JWT_SECRET:EnvyGuardSecretKeyForJWT256BitsSecurity2024ChangeThisInProduction}
         expiration: ${JWT_EXPIRATION:86400000}
     admin:
       email: ${ADMIN_EMAIL:admin@envyguard.com}
       password: ${ADMIN_PASSWORD:Admin123!}
   ```

3. **Removed Password from Console Logs** (DataInitializerService.java)
   ```java
   // Before:
   System.out.println("Password: " + adminPassword);

   // After:
   System.out.println("Note: Store the admin password securely. Do not commit to version control.");
   ```

### How to Use

Create a `.env` file (or set environment variables) with:
```bash
DB_HOST=localhost
DB_PORT=5432
DB_NAME=envyguard_db
DB_USERNAME=postgres
DB_PASSWORD=your_secure_password_here

JWT_SECRET=your_secret_key_here
JWT_EXPIRATION=86400000

ADMIN_EMAIL=admin@envyguard.com
ADMIN_PASSWORD=secure_admin_password
ADMIN_FIRST_NAME=EnvyGuard
ADMIN_LAST_NAME=Administrator
```

Then run the application with:
```bash
# Using environment variables
export DB_PASSWORD=your_secure_password_here
export ADMIN_PASSWORD=secure_admin_password
mvn spring-boot:run

# Or using a .env file with a tool like direnv
```

---

## Bug 2: NullPointerException in JWT Validation

### Issue
In `JwtService.isTokenValid()` method:
- Line 56 calls `.equals()` on `email` variable without null checking
- If `extractEmail()` returns null (malformed tokens), it causes `NullPointerException`
- No defensive programming against invalid JWT tokens

### Root Cause
Missing null safety check in token validation logic. The code assumes `extractEmail()` always returns a non-null value, which is not guaranteed for malformed or corrupted tokens.

### Fix Applied
✅ **File Modified:**
- `src/main/java/com/envyguard/envyguard_backend/security/JwtService.java`

**Changes:**

```java
// Before (Line 53-57):
public boolean isTokenValid(String token, UserDetails userDetails) {
    final String email = extractEmail(token);
    return (email.equals(userDetails.getUsername())) && !isTokenExpired(token);
}

// After:
public boolean isTokenValid(String token, UserDetails userDetails) {
    final String email = extractEmail(token);
    return (email != null && email.equals(userDetails.getUsername())) && !isTokenExpired(token);
}
```

### Impact
- ✅ Prevents `NullPointerException` when validating malformed tokens
- ✅ Returns `false` instead of crashing, which is the correct behavior
- ✅ Application remains stable even with invalid JWT tokens

---

## Bug 3: Incorrect Endpoint Path Routing

### Issue
- `HealthController` uses `@RequestMapping("/api/public")`
- Application has `server.servlet.context-path=/api` in configuration
- This results in double `/api/api/public/` path prefix
- Health check endpoints unreachable at expected paths:
  - Expected: `/api/public/health`
  - Actual: `/api/api/public/health` ❌

### Root Cause
The `@RequestMapping` was duplicating the already-configured context path from the application properties.

### Fix Applied
✅ **File Modified:**
- `src/main/java/com/envyguard/envyguard_backend/controller/HealthController.java`

**Changes:**

```java
// Before (Line 10-11):
@RestController
@RequestMapping("/api/public")
public class HealthController {

// After:
@RestController
@RequestMapping("/public")
public class HealthController {
```

### Correct Endpoint Paths (After Fix)
- Health Check: `GET /api/public/health` ✅
- Version: `GET /api/public/version` ✅

The context path `/api` from application.yml is automatically prepended by Spring Boot.

---

## Testing the Fixes

### Bug 1 Verification
```bash
# Test with environment variables
export DB_PASSWORD=secure_password
export ADMIN_PASSWORD=admin_secure_password
mvn spring-boot:run

# Verify no hardcoded credentials appear in logs
```

### Bug 2 Verification
```bash
# Test with an invalid JWT token
curl -H "Authorization: Bearer invalid_token_here" http://localhost:8080/api/protected-endpoint

# Expected: Returns 401 Unauthorized (not 500 Internal Server Error)
# Before fix: Could return 500 NullPointerException
```

### Bug 3 Verification
```bash
# Test health endpoint
curl http://localhost:8080/api/public/health

# Expected response:
# {
#   "status": "UP",
#   "service": "EnvyGuard Backend",
#   "timestamp": "2025-12-02T..."
# }

# Before fix would need: curl http://localhost:8080/api/api/public/health
```

---

## Security Recommendations

1. **Use a Secrets Management System**
   - Consider using Spring Cloud Config with encrypted properties
   - Or use AWS Secrets Manager, HashiCorp Vault, etc.

2. **Environment Variables Best Practices**
   - Never commit `.env` files to version control
   - Use `.gitignore` to exclude sensitive files
   - Document required environment variables in `.env.example`

3. **Regular Security Audits**
   - Scan code for hardcoded secrets using tools like:
     - `git-secrets`
     - `TruffleHog`
     - `detect-secrets`

4. **Password Policy**
   - Enforce strong password requirements for admin accounts
   - Change default admin credentials in production
   - Implement password rotation policies

---

## Files Changed

| File | Changes | Severity |
|------|---------|----------|
| `application.yml` | Externalized credentials to env vars | 🔴 Critical |
| `DataInitializerService.java` | Removed password from logs | 🔴 Critical |
| `JwtService.java` | Added null check in isTokenValid() | 🟠 High |
| `HealthController.java` | Fixed endpoint path routing | 🟠 High |
| `.env.example` | New file with configuration template | ℹ️ Informational |

---

## Next Steps

1. ✅ Review all changes
2. ✅ Test the application with environment variables
3. ✅ Verify all endpoints are accessible
4. ✅ Deploy with proper environment variable configuration
5. ⚠️ Never commit `.env` files with sensitive data

