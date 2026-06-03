# EPark API

API REST para conectar la app Android con SQL Server. La app movil no se conecta directo a la base; siempre usa estos endpoints.

## Arranque local

1. Levantar SQL Server y la API:

```powershell
docker compose up --build
```

2. Crear la base y datos iniciales en SQL Server:

```powershell
sqlcmd -S localhost,1433 -U sa -P Your_strong_password123 -C -i DB\scriptEparkDB.sql
sqlcmd -S localhost,1433 -U sa -P Your_strong_password123 -C -i DB\seedEparkDB.sql
```

3. Probar salud de la API:

```powershell
curl http://localhost:8080/health
```

## Usuarios demo

- Conductor: `driver@test.com` / `Driver123!`
- Admin municipal: `admin@sanjose.go.cr` / `Admin123!`

## Endpoints nuevos para frontend

Todos requieren `Authorization: Bearer <token>`.

- `GET /driver/sessions/history`: historial de sesiones finalizadas/canceladas del conductor autenticado.
- `GET /admin/zones`: zonas de la municipalidad del admin, con conteos y tarifa vigente.
- `POST /admin/zones`: crea zona, espacios `0001...N` y tarifa inicial opcional.
- `PUT /admin/zones/:id`: edita datos de zona y ajusta cantidad de espacios.
- `GET /admin/zones/:id/tariff`: obtiene la tarifa vigente.
- `PUT /admin/zones/:id/tariff`: cierra la tarifa anterior y crea una nueva.

Payload recomendado para crear/editar zona:

```json
{
  "zoneName": "Zona A - Centro",
  "description": "Av. Central",
  "latitude": 9.933333,
  "longitude": -84.083333,
  "operationStartTime": "06:00",
  "operationEndTime": "22:00",
  "totalSpaces": 20,
  "status": "ACTIVE",
  "hourlyRate": 500,
  "currencyCode": "CRC"
}
```

Payload para actualizar tarifa:

```json
{
  "hourlyRate": 550,
  "currencyCode": "CRC"
}
```

## Android

El emulador Android usa `http://10.0.2.2:8080/` como `API_BASE_URL`. Si se prueba en un telefono fisico, cambiar `API_BASE_URL` en `EparkProgram/app/build.gradle.kts` por la IP local del equipo donde corre la API.
