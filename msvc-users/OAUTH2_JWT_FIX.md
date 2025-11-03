# 🔧 Solución Implementada: OAuth2 + JWT

## ✅ Cambios Realizados

### 1. **CustomOAuth2UserService.java** - CRÍTICO ⭐
**Problema anterior:** Retornaba el `OAuth2User` de Google que tenía como nombre principal el `sub` (ID numérico), no el email.

**Solución implementada:**
```java
// Ahora creamos un OAuth2User personalizado con el EMAIL como nombre principal
DefaultOAuth2User customOAuth2User = new DefaultOAuth2User(
    authorities,
    oauthUser.getAttributes(),
    "email" // ¡Esto hace que getName() retorne el email!
);
```

**Impacto:** Ahora cuando Spring Security llama a `authentication.getName()`, obtiene el **email** en lugar del ID numérico de Google.

---

### 2. **SecurityConfig.java** - Política de Sesiones
**Cambio:**
```java
// ANTES (causaba problemas con OAuth2)
.sessionCreationPolicy(SessionCreationPolicy.STATELESS)

// AHORA (permite sesiones para OAuth2, pero JWT sigue siendo stateless)
.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
```

**Impacto:** OAuth2 requiere sesiones temporales para el flujo de redirección. Con `IF_REQUIRED`, Spring crea sesiones solo cuando es necesario (OAuth2), pero las peticiones con JWT siguen siendo stateless.

---

### 3. **AuthController.java** - Respuesta mejorada
**Mejoras:**
- Ahora el endpoint `/api/auth/oauth-success` devuelve un objeto JSON completo:
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR...",
  "tokenType": "Bearer",
  "email": "usuario@gmail.com",
  "needsRoleSelection": true,
  "message": "Debes seleccionar un rol antes de continuar"
}
```

- Si el usuario ya tiene un rol asignado:
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR...",
  "tokenType": "Bearer",
  "email": "usuario@gmail.com",
  "needsRoleSelection": false,
  "message": "Login exitoso",
  "roles": ["ROLE_CLIENT"]
}
```

---

## 🚀 Cómo Probar

### ⚠️ IMPORTANTE: Configurar Java 21

Este proyecto requiere Java 21. Asegúrate de configurar JAVA_HOME antes de compilar o ejecutar:

```powershell
$env:JAVA_HOME = "C:\Cursos\SpringCloudKubernetes\jdk-21.0.2"
```

### Paso 1: Limpiar y compilar el proyecto
```powershell
$env:JAVA_HOME = "C:\Cursos\SpringCloudKubernetes\jdk-21.0.2"
./mvnw clean compile
```

### Paso 2: Iniciar la aplicación

**Opción A: Usando el script** (Recomendado)
```powershell
.\run.ps1
```

**Opción B: Manual**
```powershell
$env:JAVA_HOME = "C:\Cursos\SpringCloudKubernetes\jdk-21.0.2"
./mvnw spring-boot:run
```

### Paso 3: Probar el login con Google

#### Opción A: Desde el navegador
1. Abre: `http://localhost:8003/oauth2/authorization/google`
2. Inicia sesión con tu cuenta de Google
3. Serás redirigido a: `http://localhost:8003/api/auth/oauth-success`
4. Verás el JSON con el JWT

#### Opción B: Desde Postman/Insomnia
1. **GET** `http://localhost:8003/oauth2/authorization/google`
2. Sigue las redirecciones
3. Al final recibirás el JSON con el JWT

### Paso 4: Usar el JWT para peticiones protegidas

Una vez que tengas el `accessToken`, úsalo en las siguientes peticiones:

#### Ejemplo: Ver mi perfil
```
GET http://localhost:8003/api/auth/me
Headers:
  Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR...
```

#### Ejemplo: Seleccionar rol (si `needsRoleSelection` es `true`)
```
POST http://localhost:8003/api/auth/select-role
Headers:
  Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR...
  Content-Type: application/json
Body:
{
  "roleName": "ROLE_CLIENT"
}
```

---

## 🔍 Flujo Completo Explicado

### 1. Usuario hace clic en "Login con Google"
```
GET /oauth2/authorization/google
```
→ Spring redirige a Google

### 2. Google autentica al usuario
→ Google redirige de vuelta a tu backend con un código

### 3. Spring Security procesa el callback
→ Llama a `CustomOAuth2UserService.loadUser()`

### 4. `CustomOAuth2UserService` procesa el usuario
- ✅ Guarda o actualiza el usuario en la BD
- ✅ Crea un `OAuth2User` personalizado con el **email como nombre principal**
- ✅ Retorna ese `OAuth2User` personalizado

### 5. Spring redirige a `/api/auth/oauth-success`
→ El `AuthController` recibe el `authentication` con el email correcto

### 6. `AuthController` genera el JWT
- ✅ Busca al usuario en la BD por email (¡ahora lo encuentra!)
- ✅ Verifica si necesita seleccionar rol
- ✅ Genera el JWT con el email como subject
- ✅ Devuelve el JSON con el token y la información necesaria

### 7. Frontend guarda el JWT
→ Lo guarda en `localStorage` o `sessionStorage`

### 8. Frontend hace peticiones protegidas
```
Authorization: Bearer <JWT>
```

### 9. `JwtAuthenticationFilter` valida el token
- ✅ Extrae el email del JWT
- ✅ Busca al usuario en la BD
- ✅ Crea la autenticación en el contexto de Spring Security
- ✅ La petición llega al controller con el usuario autenticado

---

## 📝 Logs para Debugging

Los logs importantes que verás en la consola:

```
=== INICIANDO CustomOAuth2UserService.loadUser() ===
Email recibido de Google: usuario@gmail.com
Usuario nuevo. Registrando: usuario@gmail.com
Usuario guardado exitosamente con ID: 1
Usuario procesado correctamente: usuario@gmail.com
=== OAuth2User personalizado creado con email: usuario@gmail.com ===

=== INICIANDO AuthController.oauthSuccess() ===
Login con Google exitoso para: usuario@gmail.com
Usuario encontrado en BD: usuario@gmail.com, Roles: [Role{id=3, name='ROLE_PENDING_SELECTION'}]
JWT generado exitosamente para: usuario@gmail.com
Usuario usuario@gmail.com necesita seleccionar rol
```

---

## 🐛 Problemas Comunes

### Error: "Usuario no encontrado"
**Causa:** El email no se está extrayendo correctamente.
**Solución:** Verifica los logs y asegúrate de que `customOAuth2User.getName()` retorna el email.

### Error: "Authentication es null"
**Causa:** La sesión se perdió entre la redirección.
**Solución:** Verifica que `SessionCreationPolicy.IF_REQUIRED` esté configurado.

### Error: "Token JWT inválido"
**Causa:** El JWT expiró o la firma es incorrecta.
**Solución:** Genera un nuevo JWT haciendo login de nuevo.

---

## ✨ Ventajas de esta Solución

1. ✅ **Compatibilidad con OAuth2 y JWT:** Ambos funcionan juntos sin conflictos
2. ✅ **Email como identificador único:** Consistencia en toda la aplicación
3. ✅ **Logs completos:** Fácil debugging
4. ✅ **Respuestas claras:** El frontend sabe exactamente qué hacer
5. ✅ **Seguridad mantenida:** JWT sigue siendo stateless para el resto de endpoints

---

## 🎯 Próximos Pasos Recomendados

1. **Implementar refresh tokens** para no tener que hacer login cada 24 horas
2. **Agregar logout** para invalidar tokens
3. **Implementar rate limiting** en los endpoints de autenticación
4. **Agregar más providers** (Facebook, GitHub, etc.)
5. **Implementar CORS correctamente** si tu frontend está en otro dominio

---

## 📞 Soporte

Si sigues teniendo problemas:
1. Revisa los logs completos
2. Verifica que la BD tenga el rol `ROLE_PENDING_SELECTION`
3. Asegúrate de que las credenciales de Google OAuth2 sean correctas
4. Verifica que el puerto 8003 esté libre

---

**¡Listo! 🎉 Tu aplicación ahora debería funcionar correctamente con Google OAuth2 + JWT.**
