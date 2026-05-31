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

## Android

El emulador Android usa `http://10.0.2.2:8080/` como `API_BASE_URL`. Si se prueba en un telefono fisico, cambiar `API_BASE_URL` en `EparkProgram/app/build.gradle.kts` por la IP local del equipo donde corre la API.
