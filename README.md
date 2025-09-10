# API de Gestión de Tributos (SUNAT simulada)

Backend en Spring Boot que simula autenticación por RUT10/clave SOL y gestión de registros tributarios (GET/POST/PUT/DELETE) con roles y JWT.

## Arranque rápido

```bash
cd impuestoscal
./mvnw spring-boot:run
```

- DB: H2 en memoria
- Consola H2: `http://localhost:8080/h2-console`
  - JDBC: `jdbc:h2:mem:impuestosdb`
  - Usuario: `sa`
  - Password: (vacío)

## Usuarios semilla

- Admin: `0000000000` / `admin` → ROLE_ADMIN
- Natural: `1234567890` / `sol1234` → ROLE_USUARIO_NATURAL
- Jurídico: `5555555555` / `sol1234` → ROLE_USUARIO_JURIDICO

---

## Guía con Postman

1) Crear un entorno (Environment)
- Variables recomendadas:
  - `baseUrl`: `http://localhost:8080`
  - `token`: (vacía al inicio)
  - `adminToken`: (vacía al inicio)

2) Login y guardar token
- Nueva request: POST `{{baseUrl}}/api/auth/login`
- Headers: `Content-Type: application/json`
- Body (raw JSON):
  ```json
  { "rut10": "1234567890", "claveSol": "sol1234" }
  ```
- En la pestaña Tests (de la request), pega esto para guardar el token automáticamente:
  ```javascript
  const json = pm.response.json();
  pm.environment.set("token", json.accessToken);
  ```
- Ejecuta. Verás `accessToken`. Quedará almacenado en `token` del entorno.

3) Usar el token en las demás requests
- En cada request protegida, agrega Header: `Authorization: Bearer {{token}}`.
- Alternativamente crea un Auth a nivel de colección: Type `Bearer Token` y en `Token` coloca `{{token}}`.

4) Endpoints de Registros (usuario autenticado)
- GET `{{baseUrl}}/api/registros` (listar propios)
- POST `{{baseUrl}}/api/registros`
  - Headers: `Content-Type: application/json`
  - Body:
    ```json
    {
      "tipoImpuesto": "IGV",
      "monto": 1000.50,
      "fechaVencimiento": "2025-12-31"
    }
    ```
- PUT `{{baseUrl}}/api/registros/{id}` (actualizar propio)
  - Headers: `Content-Type: application/json`
  - Body (ejemplo):
    ```json
    {
      "tipoImpuesto": "IGV",
      "monto": 1500,
      "fechaVencimiento": "2025-12-31",
      "estado": "DECLARADO"
    }
    ```
- DELETE `{{baseUrl}}/api/registros/{id}` (eliminar propio)
- GET `{{baseUrl}}/api/registros/pendientes` (listar pendientes)
- GET `{{baseUrl}}/api/registros/vencidos` (pendientes con fecha < hoy)

5) Dashboard
- GET `{{baseUrl}}/api/dashboard`
- Respuesta ejemplo:
  ```json
  { "total": 3, "pendientes": 2, "vencidos": 1 }
  ```

6) Endpoints de administración (ROLE_ADMIN)
- Primero obtener `adminToken`:
  - POST `{{baseUrl}}/api/auth/login` con body:
    ```json
    { "rut10": "0000000000", "claveSol": "admin" }
    ```
  - Tests:
    ```javascript
    const json = pm.response.json();
    pm.environment.set("adminToken", json.accessToken);
    ```
- GET `{{baseUrl}}/api/registros/admin/todos` con header `Authorization: Bearer {{adminToken}}`

Sugerencia: organiza una colección Postman con carpetas: Auth, Registros, Dashboard, Admin. Configura la autorización a nivel de colección como `Bearer Token` con `{{token}}` y sobreescribe en `Admin` con `{{adminToken}}`.

---

## Guía con Thunder Client (VS Code)

1) Variables (Environments)
- Crea un Environment: `local` con variables:
  - `baseUrl = http://localhost:8080`
  - `token` (vacío)
  - `adminToken` (vacío)

2) Login (guardar token)
- Nueva Request: POST `{{baseUrl}}/api/auth/login`
- Body JSON:
  ```json
  { "rut10": "1234567890", "claveSol": "sol1234" }
  ```
- Respuesta: copia `accessToken` y pégalo en el Environment como `token`.

3) Usar Bearer Token
- En Auth de la request, selecciona `Bearer` y coloca `{{token}}`.
- Alternativa: setearlo a nivel de `Collection`.

4) Requests
- GET `{{baseUrl}}/api/registros`
- POST `{{baseUrl}}/api/registros` con body JSON (ver ejemplo en Postman arriba)
- PUT `{{baseUrl}}/api/registros/{id}` con body JSON
- DELETE `{{baseUrl}}/api/registros/{id}`
- GET `{{baseUrl}}/api/registros/pendientes`
- GET `{{baseUrl}}/api/registros/vencidos`
- GET `{{baseUrl}}/api/dashboard`

5) Admin
- Repite Login con admin, copia `accessToken` a `adminToken` y úsalo en `Authorization: Bearer {{adminToken}}` para `GET {{baseUrl}}/api/registros/admin/todos`.

---

## Códigos de estado esperados

- 200 OK: lecturas/actualizaciones correctas
- 201 Created: creación
- 204 No Content: eliminación
- 400 Bad Request: validación
- 401 Unauthorized: sin token / token inválido
- 403 Forbidden: acceso no autorizado
- 404 Not Found: recurso inexistente

## Notas

- Los datos se almacenan en H2 (memoria); se pierden al reiniciar.
- Entidades: `Usuario`, `Rol`, `RegistroTributario`.
- Roles: `ADMIN`, `USUARIO_NATURAL`, `USUARIO_JURIDICO`.
