# Database Engine: SQL Server
# Database name: EParkDB
# Context:
- **E-park Mobile** es una aplicacion movil Android nativa para gestionar parqueo urbano municipal en Costa Rica.
- El sistema tiene dos perfiles principales:
  - **Conductor:** registra cuenta, vehiculos y metodos de pago; busca zonas cercanas; inicia, monitorea y paga sesiones de parqueo; consulta multas e historial.
  - **Administrador municipal:** gestiona zonas, tarifas y reportes basicos de su municipalidad.

Aclaraciones generales sobre el diseno de las tablas:
- Se usan tipos de datos compatibles con SQL Server: `int`, `bigint`, `smallint`, `varchar`, `datetime2`, `bit`, `decimal`, etc.
- El modelo busca ser **suficientemente completo pero no excesivamente robusto**, para facilitar el backend del proyecto academico.
- Se aplica normalizacion hasta un nivel practico: datos maestros separados, relaciones con FK y tablas transaccionales para sesiones, pagos y multas.
- Se recomienda que la app Android no se conecte directamente a SQL Server. El flujo sugerido es: `Android Kotlin/Compose -> Backend API -> SQL Server`.
- Se implementa soft-delete con `deleted` y `deletedAt` en tablas maestras o configurables.
- Se incluyen campos comunes de auditoria (`createdAt`, `updatedAt`, `createdBy`, `updatedBy`) donde aportan trazabilidad real.
- Para pagos, correo, geolocalizacion, notificaciones y sincronizacion offline se modela soporte de datos, aunque la implementacion puede ser simulada en el alcance academico.

# Patrones de diseno aplicados

- **USER PERMISSIONS / RBAC:** se usa de forma simple con `Users`, `Roles` y `UserRoles`. No se agrega tabla de permisos atomicos para evitar complejidad innecesaria; los dos roles requeridos son `DRIVER` y `MUNICIPAL_ADMIN`.
- **MASTER-DETAIL / CATALOGS-ACTIONS:** municipalidades, zonas, espacios, tarifas, sesiones, pagos y multas se separan en datos maestros y transacciones.
- **TRANSACTIONS / MOVEMENTS:** `ParkingSessions`, `Payments` y `Fines` registran eventos economicos y operativos importantes.
- **CURRENT AND HISTORICAL:** `ZoneTariffs` conserva historial de tarifas con vigencia. Las sesiones guardan la tarifa aplicada para no verse afectadas por cambios futuros.
- **LOGS:** `AuditLogs` registra acciones administrativas relevantes sin crear una auditoria excesiva por cada tabla.
- **CONTACT INFO:** se mantiene simplificado dentro de `Users` para email y `DriverProfiles` para telefono; no se crea una tabla generica de contactos porque el alcance no lo requiere.

# Tables

# SECCION 1: Seguridad, usuarios y perfiles

## Users
*Proposito tabla: Almacena las credenciales y datos base de autenticacion para conductores y administradores municipales.*
*Paso de etapa: Onboarding - Seguridad*
*Staging Table: No*

- userId int IDENTITY(1,1) (PK)
- email varchar(120) unique not null -- Correo usado para login y verificacion de cuenta.
- passwordHash varchar(255) not null -- Nunca almacenar contrasenas en texto plano.
- fullName varchar(150) not null -- Nombre completo visible en perfil.
- nationalId varchar(30) null -- Cedula del conductor; puede ser null para administradores precargados.
- phoneNumber varchar(30) null -- Telefono opcional de contacto.
- emailVerified bit default 0 -- Indica si el correo fue verificado.
- statusId smallint (FK) -- FK a Statuses. Ej: ACTIVE, PENDING_VERIFICATION, SUSPENDED.
- lastLoginAt datetime2 null
- createdAt datetime2 default SYSUTCDATETIME()
- updatedAt datetime2 null
- deleted bit default 0
- deletedAt datetime2 null

## Roles
*Proposito tabla: Catalogo simple de roles de usuario para diferenciar conductor y administrador municipal.*
*Paso de etapa: Seguridad - RBAC simple*
*Staging Table: No*

- roleId smallint IDENTITY(1,1) (PK)
- roleCode varchar(40) unique not null -- Ej: DRIVER, MUNICIPAL_ADMIN.
- roleName varchar(80) not null -- Nombre legible del rol.
- description varchar(200) null
- enabled bit default 1
- createdAt datetime2 default SYSUTCDATETIME()
- updatedAt datetime2 null
- deleted bit default 0
- deletedAt datetime2 null

## UserRoles
*Proposito tabla: Relaciona usuarios con roles. Permite que el backend redirija al flujo correcto despues del login.*
*Paso de etapa: Seguridad - Asignacion de roles*
*Staging Table: No*

- userId int (FK)
- roleId smallint (FK)
- assignedAt datetime2 default SYSUTCDATETIME()
- assignedBy int (FK) null -- Usuario administrador que asigno el rol, si aplica.
- enabled bit default 1
- deleted bit default 0
- deletedAt datetime2 null
- PK_UserRoles PRIMARY KEY (userId, roleId)

## DriverProfiles
*Proposito tabla: Extiende el usuario conductor con datos propios del perfil ciudadano.*
*Paso de etapa: Onboarding - Perfil conductor*
*Staging Table: No*

- driverProfileId int IDENTITY(1,1) (PK)
- userId int (FK, unique) -- Usuario propietario del perfil.
- defaultPaymentMethodId int (FK) null -- Metodo de pago preferido.
- notificationMinutesBefore smallint default 10 -- Margen para aviso antes del vencimiento.
- createdAt datetime2 default SYSUTCDATETIME()
- updatedAt datetime2 null
- deleted bit default 0
- deletedAt datetime2 null

## MunicipalAdminProfiles
*Proposito tabla: Vincula a un usuario administrador con una municipalidad especifica.*
*Paso de etapa: Onboarding - Perfil administrador municipal*
*Staging Table: No*

- adminProfileId int IDENTITY(1,1) (PK)
- userId int (FK, unique)
- municipalityId int (FK) -- Municipalidad que administra este usuario.
- jobTitle varchar(100) null -- Puesto o descripcion administrativa.
- createdAt datetime2 default SYSUTCDATETIME()
- updatedAt datetime2 null
- deleted bit default 0
- deletedAt datetime2 null

## Statuses
*Proposito tabla: Catalogo comun de estados para usuarios, sesiones, pagos, multas y notificaciones.*
*Paso de etapa: Transversal - Catalogos*
*Staging Table: No*

- statusId smallint IDENTITY(1,1) (PK)
- statusCode varchar(50) unique not null -- Ej: ACTIVE, INACTIVE, PENDING, PAID, CANCELLED, EXPIRED.
- statusName varchar(80) not null
- appliesTo varchar(50) not null -- Ej: USER, SESSION, PAYMENT, FINE, NOTIFICATION, ZONE.
- description varchar(200) null
- enabled bit default 1
- createdAt datetime2 default SYSUTCDATETIME()
- updatedAt datetime2 null
- deleted bit default 0
- deletedAt datetime2 null

# SECCION 2: Municipalidades, zonas y espacios

## Municipalities
*Proposito tabla: Catalogo de municipalidades donde opera E-park.*
*Paso de etapa: Operacion - Datos maestros*
*Staging Table: No*

- municipalityId int IDENTITY(1,1) (PK)
- municipalityName varchar(120) unique not null -- Ej: San Jose, Cartago, Montes de Oca.
- province varchar(80) null
- contactEmail varchar(120) null
- enabled bit default 1
- createdAt datetime2 default GETUTCDATE()
- updatedAt datetime2 null
- deleted bit default 0
- deletedAt datetime2 null

## ParkingZones
*Proposito tabla: Define zonas de parqueo administradas por una municipalidad.*
*Paso de etapa: Administracion municipal - Gestion de zonas*
*Staging Table: No*

- zoneId int IDENTITY(1,1) (PK)
- municipalityId int (FK)
- zoneName varchar(120) not null -- Nombre visible para conductor y administrador.
- description varchar(250) null
- latitude decimal(9,6) not null -- Coordenada aproximada para ordenar por cercania.
- longitude decimal(9,6) not null -- Coordenada aproximada para ordenar por cercania.
- operationStartTime time not null -- Hora de inicio de operacion.
- operationEndTime time not null -- Hora de fin de operacion.
- totalSpaces int not null -- Cantidad total declarada para la zona.
- statusId smallint (FK) -- FK a Statuses. Ej: ACTIVE, INACTIVE.
- createdAt datetime2 default GETUTCDATE()
- updatedAt datetime2 null
- createdBy int (FK) null -- Administrador que creo la zona.
- updatedBy int (FK) null
- deleted bit default 0
- deletedAt datetime2 null
- CONSTRAINT UQ_ParkingZones_Municipality_Name UNIQUE (municipalityId, zoneName)
- CONSTRAINT CK_ParkingZones_TotalSpaces CHECK (totalSpaces >= 0)

## ParkingSpaces
*Proposito tabla: Representa cada espacio fisico de parqueo dentro de una zona.*
*Paso de etapa: Operacion - Espacios de parqueo*
*Staging Table: No*

- spaceId int IDENTITY(1,1) (PK)
- zoneId int (FK)
- spaceCode varchar(4) not null -- Numero visible de cuatro digitos usado por el conductor.
- statusId smallint (FK) -- FK a Statuses. Ej: AVAILABLE, OCCUPIED, DISABLED.
- latitude decimal(9,6) null -- Opcional si se quiere ubicar el espacio exacto.
- longitude decimal(9,6) null
- createdAt datetime2 default GETUTCDATE()
- updatedAt datetime2 null
- deleted bit default 0
- deletedAt datetime2 null
- CONSTRAINT UQ_ParkingSpaces_Zone_Code UNIQUE (zoneId, spaceCode)
- CONSTRAINT CK_ParkingSpaces_Code CHECK (LEN(spaceCode) = 4)

## ZoneTariffs
*Proposito tabla: Historial de tarifas por hora para cada zona. Permite que nuevas tarifas no afecten sesiones activas.*
*Paso de etapa: Administracion municipal - Gestion de tarifas*
*Staging Table: No*

- tariffId int IDENTITY(1,1) (PK)
- zoneId int (FK)
- hourlyRate decimal(10,2) not null -- Tarifa por hora vigente para nuevas sesiones.
- currencyCode char(3) default 'CRC' -- Se deja fijo/simple para Costa Rica.
- validFrom datetime2 not null default SYSUTCDATETIME()
- validTo datetime2 null -- Null significa tarifa vigente.
- createdAt datetime2 default GETUTCDATE()
- createdBy int (FK) null
- deleted bit default 0
- deletedAt datetime2 null
- CONSTRAINT CK_ZoneTariffs_Rate CHECK (hourlyRate >= 0)
- CONSTRAINT CK_ZoneTariffs_Dates CHECK (validTo IS NULL OR validTo > validFrom)

# SECCION 3: Vehiculos y metodos de pago

## Vehicles
*Proposito tabla: Almacena vehiculos vinculados a conductores para asociarlos a sesiones y multas.*
*Paso de etapa: Conductor - Gestion de vehiculos*
*Staging Table: No*

- vehicleId int IDENTITY(1,1) (PK)
- userId int (FK) -- Conductor propietario del vehiculo.
- plateNumber varchar(20) not null -- Placa del vehiculo.
- alias varchar(80) null -- Nombre opcional: "Carro de casa".
- enabled bit default 1
- createdAt datetime2 default GETUTCDATE()
- updatedAt datetime2 null
- deleted bit default 0
- deletedAt datetime2 null
- CONSTRAINT UQ_Vehicles_User_Plate UNIQUE (userId, plateNumber)

## PaymentMethods
*Proposito tabla: Metodos de pago registrados por el conductor. Para el proyecto puede operar con datos simulados/tokenizados.*
*Paso de etapa: Conductor - Perfil y pagos*
*Staging Table: No*

- paymentMethodId int IDENTITY(1,1) (PK)
- userId int (FK)
- methodType varchar(30) not null -- Ej: CREDIT_CARD, DEBIT_CARD.
- cardBrand varchar(40) null -- Ej: Visa, Mastercard.
- lastFourDigits char(4) not null -- Solo ultimos 4 digitos; no guardar tarjeta completa.
- tokenReference varchar(120) null -- Token o referencia simulada del proveedor.
- expirationMonth tinyint null
- expirationYear smallint null
- enabled bit default 1
- createdAt datetime2 default GETUTCDATE()
- updatedAt datetime2 null
- deleted bit default 0
- deletedAt datetime2 null
- CONSTRAINT CK_PaymentMethods_ExpirationMonth CHECK (expirationMonth IS NULL OR expirationMonth BETWEEN 1 AND 12)

# SECCION 4: Sesiones de parqueo, pagos y comprobantes

## ParkingSessions
*Proposito tabla: Registra el ciclo de vida de una sesion de parqueo iniciada por un conductor.*
*Paso de etapa: Conductor - Inicio, monitoreo y finalizacion de parqueo*
*Staging Table: No*

- sessionId bigint IDENTITY(1,1) (PK)
- userId int (FK) -- Conductor que inicio la sesion.
- vehicleId int (FK)
- municipalityId int (FK) -- Se guarda para reportes rapidos por municipalidad.
- zoneId int (FK)
- spaceId int (FK)
- tariffId int (FK) -- Tarifa vigente al iniciar la sesion.
- hourlyRateApplied decimal(10,2) not null -- Valor congelado para evitar cambios por nuevas tarifas.
- startedAt datetime2 not null default SYSUTCDATETIME()
- expectedEndAt datetime2 null -- Opcional si la app maneja tiempo estimado.
- endedAt datetime2 null
- elapsedMinutes int null -- Puede calcularse, pero se guarda al cerrar para reportes simples.
- totalAmount decimal(10,2) null -- Monto final calculado.
- statusId smallint (FK) -- FK a Statuses. Ej: ACTIVE, FINISHED, CANCELLED, EXPIRED.
- createdAt datetime2 default GETUTCDATE()
- updatedAt datetime2 null
- deleted bit default 0
- deletedAt datetime2 null
- CONSTRAINT CK_ParkingSessions_Dates CHECK (endedAt IS NULL OR endedAt >= startedAt)
- CONSTRAINT CK_ParkingSessions_Amount CHECK (totalAmount IS NULL OR totalAmount >= 0)

## Payments
*Proposito tabla: Registra pagos simulados de sesiones de parqueo o multas.*
*Paso de etapa: Pagos - Transacciones*
*Staging Table: No*

- paymentId bigint IDENTITY(1,1) (PK)
- userId int (FK)
- paymentMethodId int (FK) null
- paymentTargetType varchar(30) not null -- PARKING_SESSION o FINE.
- sessionId bigint (FK) null -- Se usa cuando el pago corresponde a una sesion.
- fineId bigint (FK) null -- Se usa cuando el pago corresponde a una multa.
- amount decimal(10,2) not null
- currencyCode char(3) default 'CRC'
- statusId smallint (FK) -- FK a Statuses. Ej: PENDING, APPROVED, REJECTED.
- providerReference varchar(120) null -- Referencia simulada o sandbox.
- receiptNumber varchar(80) unique null -- Comprobante digital.
- requestedAt datetime2 default GETUTCDATE()
- processedAt datetime2 null
- createdAt datetime2 default GETUTCDATE()
- deleted bit default 0
- deletedAt datetime2 null
- CONSTRAINT CK_Payments_Amount CHECK (amount >= 0)
- CONSTRAINT CK_Payments_Target CHECK (
    (paymentTargetType = 'PARKING_SESSION' AND sessionId IS NOT NULL AND fineId IS NULL)
    OR
    (paymentTargetType = 'FINE' AND fineId IS NOT NULL AND sessionId IS NULL)
  )

# SECCION 5: Multas e historial

## Fines
*Proposito tabla: Almacena multas asociadas a placas vinculadas a un conductor. La creacion de multas por inspectores queda fuera del alcance, pero se permite precargar o simular datos.*
*Paso de etapa: Conductor - Consulta y pago de multas*
*Staging Table: No*

- fineId bigint IDENTITY(1,1) (PK)
- vehicleId int (FK)
- userId int (FK) -- Se guarda para consulta directa del conductor.
- municipalityId int (FK)
- zoneId int (FK) null
- spaceId int (FK) null
- fineNumber varchar(80) unique not null
- reason varchar(200) not null -- Ej: Tiempo vencido, espacio no pagado.
- fineDate datetime2 not null
- amount decimal(10,2) not null
- statusId smallint (FK) -- FK a Statuses. Ej: PENDING, PAID, CANCELLED.
- createdAt datetime2 default GETUTCDATE()
- updatedAt datetime2 null
- deleted bit default 0
- deletedAt datetime2 null
- CONSTRAINT CK_Fines_Amount CHECK (amount >= 0)

## OfflineSessionSnapshots
*Proposito tabla: Guarda un resumen sincronizable del historial reciente que la app puede almacenar localmente y reconciliar con el backend.*
*Paso de etapa: Conductor - Historial offline*
*Staging Table: No*

- snapshotId bigint IDENTITY(1,1) (PK)
- userId int (FK)
- sessionId bigint (FK)
- vehiclePlate varchar(20) not null
- zoneName varchar(120) not null
- startedAt datetime2 not null
- endedAt datetime2 null
- totalAmount decimal(10,2) null
- lastSyncedAt datetime2 default GETUTCDATE()
- checksum varchar(64) null -- Ayuda a detectar cambios entre local y servidor.
- createdAt datetime2 default GETUTCDATE()
- deleted bit default 0

# SECCION 6: Notificaciones, reportes y auditoria

## Notifications
*Proposito tabla: Registra notificaciones push generadas para conductores o administradores.*
*Paso de etapa: Notificaciones - Alertas operativas*
*Staging Table: No*

- notificationId bigint IDENTITY(1,1) (PK)
- userId int (FK) -- Usuario destinatario.
- notificationType varchar(50) not null -- SESSION_EXPIRING, ADMIN_EXPIRED_SPACE, SYSTEM_INCIDENT.
- title varchar(120) not null
- message varchar(300) not null
- relatedEntityType varchar(50) null -- Ej: PARKING_SESSION, FINE, ZONE.
- relatedEntityId bigint null
- scheduledAt datetime2 null
- sentAt datetime2 null
- readAt datetime2 null
- statusId smallint (FK) -- FK a Statuses. Ej: PENDING, SENT, READ, FAILED.
- createdAt datetime2 default GETUTCDATE()
- deleted bit default 0
- deletedAt datetime2 null

## AuditLogs
*Proposito tabla: Bitacora general para acciones relevantes del sistema, especialmente cambios administrativos en zonas y tarifas.*
*Paso de etapa: Transversal - Auditoria*
*Staging Table: No*

- auditLogId bigint IDENTITY(1,1) (PK)
- userId int (FK) null -- Usuario que ejecuto la accion.
- action varchar(80) not null -- Ej: CREATE_ZONE, UPDATE_TARIFF, DISABLE_ZONE, LOGIN_FAILED.
- entityName varchar(80) not null -- Tabla o entidad afectada.
- entityId bigint null -- Id del registro afectado.
- oldValue nvarchar(max) null -- JSON opcional con valores anteriores.
- newValue nvarchar(max) null -- JSON opcional con valores nuevos.
- description varchar(300) null
- ipAddress varchar(50) null
- createdAt datetime2 default GETUTCDATE()
- deleted bit default 0

# Relaciones principales

- `Users` 1:M `UserRoles`
- `Roles` 1:M `UserRoles`
- `Users` 1:1 `DriverProfiles`
- `Users` 1:1 `MunicipalAdminProfiles`
- `Municipalities` 1:M `MunicipalAdminProfiles`
- `Municipalities` 1:M `ParkingZones`
- `ParkingZones` 1:M `ParkingSpaces`
- `ParkingZones` 1:M `ZoneTariffs`
- `Users` 1:M `Vehicles`
- `Users` 1:M `PaymentMethods`
- `Users` 1:M `ParkingSessions`
- `Vehicles` 1:M `ParkingSessions`
- `ParkingSpaces` 1:M `ParkingSessions`
- `ParkingSessions` 1:0..1 `Payments`
- `Vehicles` 1:M `Fines`
- `Fines` 1:0..1 `Payments`
- `Users` 1:M `Notifications`
- `Users` 1:M `AuditLogs`

# Datos iniciales recomendados

## Roles

- DRIVER -- Conductor registrado.
- MUNICIPAL_ADMIN -- Administrador municipal precargado.

## Statuses

- USER / PENDING_VERIFICATION -- Usuario registrado pendiente de verificacion por correo.
- USER / ACTIVE -- Usuario activo.
- USER / SUSPENDED -- Usuario suspendido.
- ZONE / ACTIVE -- Zona habilitada.
- ZONE / INACTIVE -- Zona desactivada.
- SPACE / AVAILABLE -- Espacio disponible.
- SPACE / OCCUPIED -- Espacio ocupado.
- SPACE / DISABLED -- Espacio deshabilitado.
- SESSION / ACTIVE -- Sesion de parqueo activa.
- SESSION / FINISHED -- Sesion finalizada.
- SESSION / CANCELLED -- Sesion cancelada.
- SESSION / EXPIRED -- Sesion vencida.
- PAYMENT / PENDING -- Pago pendiente.
- PAYMENT / APPROVED -- Pago aprobado.
- PAYMENT / REJECTED -- Pago rechazado.
- FINE / PENDING -- Multa pendiente.
- FINE / PAID -- Multa pagada.
- FINE / CANCELLED -- Multa anulada.
- NOTIFICATION / PENDING -- Notificacion pendiente.
- NOTIFICATION / SENT -- Notificacion enviada.
- NOTIFICATION / READ -- Notificacion leida.
- NOTIFICATION / FAILED -- Notificacion fallida.

# Indices sugeridos

- IDX_Users_Email sobre `Users(email)` -- Login.
- IDX_UserRoles_UserId sobre `UserRoles(userId)` -- Resolucion de rol.
- IDX_AdminProfiles_Municipality sobre `MunicipalAdminProfiles(municipalityId)` -- Administradores por municipalidad.
- IDX_ParkingZones_Municipality sobre `ParkingZones(municipalityId)` -- Zonas por municipalidad.
- IDX_ParkingSpaces_Zone_Status sobre `ParkingSpaces(zoneId, statusId)` -- Espacios disponibles por zona.
- IDX_ZoneTariffs_Zone_ValidTo sobre `ZoneTariffs(zoneId, validTo)` -- Tarifa vigente.
- IDX_Vehicles_User sobre `Vehicles(userId)` -- Vehiculos del conductor.
- IDX_ParkingSessions_User_Status sobre `ParkingSessions(userId, statusId)` -- Sesion activa del conductor.
- IDX_ParkingSessions_Municipality_Dates sobre `ParkingSessions(municipalityId, startedAt, endedAt)` -- Reportes por rango de fechas.
- IDX_Fines_User_Status sobre `Fines(userId, statusId)` -- Multas pendientes.
- IDX_Payments_User_Date sobre `Payments(userId, requestedAt)` -- Historial de pagos.
- IDX_Notifications_User_Status sobre `Notifications(userId, statusId)` -- Notificaciones pendientes.

# Consultas funcionales esperadas por el backend

1. Login:
   - Buscar `Users` por email.
   - Validar `passwordHash`.
   - Obtener rol desde `UserRoles`.
   - Redirigir a conductor o administrador.

2. Zonas cercanas:
   - Filtrar `ParkingZones` por municipalidad y estado activo.
   - Calcular distancia en backend usando `latitude` y `longitude`.
   - Mostrar tarifa vigente desde `ZoneTariffs` con `validTo IS NULL`.

3. Inicio de parqueo:
   - Buscar `ParkingSpaces` por `zoneId` y `spaceCode`.
   - Validar estado disponible.
   - Crear `ParkingSessions` con tarifa vigente congelada.
   - Cambiar espacio a ocupado.

4. Finalizacion y pago:
   - Calcular minutos y monto final.
   - Actualizar `ParkingSessions`.
   - Crear `Payments` con estado aprobado o rechazado.
   - Generar `receiptNumber`.
   - Liberar espacio.

5. Reportes administrativos:
   - Consultar `ParkingSessions` por `municipalityId` y rango de fechas.
   - Agrupar ingresos por `zoneId`.
   - Contar multas desde `Fines` por municipalidad y periodo.

# Decisiones pendientes para discutir con el equipo

1. Confirmar si el backend sera Kotlin con Ktor/Spring Boot o si usaran otra tecnologia.
2. Definir si SQL Server correra local, en contenedor Docker o en una instancia compartida.
3. Decidir si las notificaciones se simulan completamente o si se integra Firebase Cloud Messaging.
4. Definir si el correo de verificacion sera real, mock o solo cambio de estado en base de datos.
5. Validar si se necesita tabla separada de `Permissions`; por ahora se evita para mantener simple el alcance.
6. Validar si `OfflineSessionSnapshots` se mantiene en SQL Server o si solo se documenta como equivalente de Room/local storage en Android.
