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

## Autenticación

- POST `/api/auth/login`
  - Request:
    ```json
    { "rut10": "1234567890", "claveSol": "sol1234" }
    ```
  - Respuesta:
    ```json
    {
      "accessToken": "<JWT>",
      "tokenType": "Bearer",
      "rut10": "1234567890",
      "roles": ["USUARIO_NATURAL"]
    }
    ```
  - Curl:
    ```bash
    curl -s -X POST http://localhost:8080/api/auth/login \
      -H 'Content-Type: application/json' \
      -d '{"rut10":"1234567890","claveSol":"sol1234"}'
    ```

Usa el token: `Authorization: Bearer <JWT>` en las llamadas siguientes.

## Registros tributarios (usuario autenticado)

- GET `/api/registros` — listar propios
  ```bash
  curl -s http://localhost:8080/api/registros -H "Authorization: Bearer $TOKEN"
  ```

- POST `/api/registros` — crear
  - Body:
    ```json
    {
      "tipoImpuesto": "IGV",
      "monto": 1000.50,
      "fechaVencimiento": "2025-12-31"
    }
    ```
  - Curl:
    ```bash
    curl -s -X POST http://localhost:8080/api/registros \
      -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' \
      -d '{"tipoImpuesto":"IGV","monto":1000.50,"fechaVencimiento":"2025-12-31"}'
    ```

- PUT `/api/registros/{id}` — actualizar (solo propios)
  - Body (ejemplo):
    ```json
    {
      "tipoImpuesto": "IGV",
      "monto": 1500,
      "fechaVencimiento": "2025-12-31",
      "estado": "DECLARADO"
    }
    ```
  - Curl:
    ```bash
    curl -s -X PUT http://localhost:8080/api/registros/1 \
      -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' \
      -d '{"tipoImpuesto":"IGV","monto":1500,"fechaVencimiento":"2025-12-31","estado":"DECLARADO"}'
    ```

- DELETE `/api/registros/{id}` — eliminar (solo propios)
  ```bash
  curl -i -s -X DELETE http://localhost:8080/api/registros/1 -H "Authorization: Bearer $TOKEN"
  ```

- GET `/api/registros/pendientes` — listar pendientes
  ```bash
  curl -s http://localhost:8080/api/registros/pendientes -H "Authorization: Bearer $TOKEN"
  ```

- GET `/api/registros/vencidos` — listar vencidos (pendientes con fecha < hoy)
  ```bash
  curl -s http://localhost:8080/api/registros/vencidos -H "Authorization: Bearer $TOKEN"
  ```

## Dashboard

- GET `/api/dashboard` — resumen del usuario
  - Respuesta ejemplo:
    ```json
    { "total": 3, "pendientes": 2, "vencidos": 1 }
    ```
  - Curl:
    ```bash
    curl -s http://localhost:8080/api/dashboard -H "Authorization: Bearer $TOKEN"
    ```

## Endpoints de administración

- GET `/api/registros/admin/todos` — requiere ROLE_ADMIN
  ```bash
  # Primero iniciar sesión como admin y asignar ADMIN_TOKEN
  curl -s http://localhost:8080/api/registros/admin/todos -H "Authorization: Bearer $ADMIN_TOKEN"
  ```

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
