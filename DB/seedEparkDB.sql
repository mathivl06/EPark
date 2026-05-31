USE EParkDB;
GO

/* Passwords use SHA-256 for this academic backend demo.
   Demo credentials:
   - admin@sanjose.go.cr / Admin123!
   - driver@test.com / Driver123!
*/

DECLARE @AdminPasswordHash VARCHAR(255) =
    CONVERT(VARCHAR(64), HASHBYTES('SHA2_256', CONVERT(VARCHAR(255), 'Admin123!')), 2);
DECLARE @DriverPasswordHash VARCHAR(255) =
    CONVERT(VARCHAR(64), HASHBYTES('SHA2_256', CONVERT(VARCHAR(255), 'Driver123!')), 2);

IF NOT EXISTS (SELECT 1 FROM Roles WHERE roleCode = 'DRIVER')
    INSERT INTO Roles(roleCode, roleName) VALUES ('DRIVER', 'Conductor');

IF NOT EXISTS (SELECT 1 FROM Roles WHERE roleCode = 'MUNICIPAL_ADMIN')
    INSERT INTO Roles(roleCode, roleName) VALUES ('MUNICIPAL_ADMIN', 'Administrador Municipal');
GO

IF NOT EXISTS (SELECT 1 FROM Municipalities WHERE municipalityName = 'San Jose')
    INSERT INTO Municipalities(municipalityName, province, contactEmail)
    VALUES ('San Jose', 'San Jose', 'contacto@sanjose.go.cr');

IF NOT EXISTS (SELECT 1 FROM Municipalities WHERE municipalityName = 'Cartago')
    INSERT INTO Municipalities(municipalityName, province, contactEmail)
    VALUES ('Cartago', 'Cartago', 'contacto@cartago.go.cr');

IF NOT EXISTS (SELECT 1 FROM Municipalities WHERE municipalityName = 'Montes de Oca')
    INSERT INTO Municipalities(municipalityName, province, contactEmail)
    VALUES ('Montes de Oca', 'San Jose', 'contacto@montesdeoca.go.cr');
GO

DECLARE @SanJoseId INT = (SELECT municipalityId FROM Municipalities WHERE municipalityName = 'San Jose');
DECLARE @AdminRoleId SMALLINT = (SELECT roleId FROM Roles WHERE roleCode = 'MUNICIPAL_ADMIN');
DECLARE @DriverRoleId SMALLINT = (SELECT roleId FROM Roles WHERE roleCode = 'DRIVER');
DECLARE @AdminPasswordHash VARCHAR(255) =
    CONVERT(VARCHAR(64), HASHBYTES('SHA2_256', CONVERT(VARCHAR(255), 'Admin123!')), 2);
DECLARE @DriverPasswordHash VARCHAR(255) =
    CONVERT(VARCHAR(64), HASHBYTES('SHA2_256', CONVERT(VARCHAR(255), 'Driver123!')), 2);

IF NOT EXISTS (SELECT 1 FROM Users WHERE email = 'admin@sanjose.go.cr')
BEGIN
    INSERT INTO Users(email, passwordHash, fullName, emailVerified, status)
    VALUES ('admin@sanjose.go.cr', @AdminPasswordHash, 'Administrador San Jose', 1, 'ACTIVE');

    DECLARE @AdminUserId INT = SCOPE_IDENTITY();
    INSERT INTO UserRoles(userId, roleId) VALUES (@AdminUserId, @AdminRoleId);
    INSERT INTO MunicipalAdminProfiles(userId, municipalityId, jobTitle)
    VALUES (@AdminUserId, @SanJoseId, 'Gestor municipal');
END

IF NOT EXISTS (SELECT 1 FROM Users WHERE email = 'driver@test.com')
BEGIN
    INSERT INTO Users(email, passwordHash, fullName, nationalId, phoneNumber, emailVerified, status)
    VALUES ('driver@test.com', @DriverPasswordHash, 'Juan Perez', '101110111', '8888-8888', 1, 'ACTIVE');

    DECLARE @DriverUserId INT = SCOPE_IDENTITY();
    INSERT INTO UserRoles(userId, roleId) VALUES (@DriverUserId, @DriverRoleId);
    INSERT INTO DriverProfiles(userId) VALUES (@DriverUserId);
    INSERT INTO Vehicles(userId, plateNumber, alias) VALUES (@DriverUserId, 'ABC-1234', 'Principal');
    INSERT INTO PaymentMethods(userId, methodType, cardBrand, lastFourDigits, tokenReference, expirationMonth, expirationYear)
    VALUES (@DriverUserId, 'CREDIT_CARD', 'Visa', '4242', 'tok_demo_4242', 12, 2030);
END
GO

DECLARE @SanJoseId INT = (SELECT municipalityId FROM Municipalities WHERE municipalityName = 'San Jose');
DECLARE @AdminUserId INT = (SELECT userId FROM Users WHERE email = 'admin@sanjose.go.cr');

IF NOT EXISTS (SELECT 1 FROM ParkingZones WHERE municipalityId = @SanJoseId AND zoneName = 'Zona A - Centro')
    INSERT INTO ParkingZones(municipalityId, zoneName, description, latitude, longitude, operationStartTime, operationEndTime, totalSpaces, createdBy)
    VALUES (@SanJoseId, 'Zona A - Centro', 'Avenida Central, San Jose', 9.933333, -84.083333, '06:00', '22:00', 20, @AdminUserId);

IF NOT EXISTS (SELECT 1 FROM ParkingZones WHERE municipalityId = @SanJoseId AND zoneName = 'Zona B - Plaza')
    INSERT INTO ParkingZones(municipalityId, zoneName, description, latitude, longitude, operationStartTime, operationEndTime, totalSpaces, createdBy)
    VALUES (@SanJoseId, 'Zona B - Plaza', 'Calle 2, San Jose', 9.934000, -84.081500, '07:00', '21:00', 15, @AdminUserId);

IF NOT EXISTS (SELECT 1 FROM ParkingZones WHERE municipalityId = @SanJoseId AND zoneName = 'Zona C - Mercado')
    INSERT INTO ParkingZones(municipalityId, zoneName, description, latitude, longitude, operationStartTime, operationEndTime, totalSpaces, createdBy)
    VALUES (@SanJoseId, 'Zona C - Mercado', 'Avenida 2, San Jose', 9.932500, -84.079500, '06:00', '20:00', 30, @AdminUserId);
GO

DECLARE @ZoneA INT = (SELECT zoneId FROM ParkingZones WHERE zoneName = 'Zona A - Centro');
DECLARE @ZoneB INT = (SELECT zoneId FROM ParkingZones WHERE zoneName = 'Zona B - Plaza');
DECLARE @ZoneC INT = (SELECT zoneId FROM ParkingZones WHERE zoneName = 'Zona C - Mercado');
DECLARE @AdminUserId INT = (SELECT userId FROM Users WHERE email = 'admin@sanjose.go.cr');

IF NOT EXISTS (SELECT 1 FROM ZoneTariffs WHERE zoneId = @ZoneA AND validTo IS NULL)
    INSERT INTO ZoneTariffs(zoneId, hourlyRate, currencyCode, createdBy)
    VALUES (@ZoneA, 500.00, 'CRC', @AdminUserId);

IF NOT EXISTS (SELECT 1 FROM ZoneTariffs WHERE zoneId = @ZoneB AND validTo IS NULL)
    INSERT INTO ZoneTariffs(zoneId, hourlyRate, currencyCode, createdBy)
    VALUES (@ZoneB, 400.00, 'CRC', @AdminUserId);

IF NOT EXISTS (SELECT 1 FROM ZoneTariffs WHERE zoneId = @ZoneC AND validTo IS NULL)
    INSERT INTO ZoneTariffs(zoneId, hourlyRate, currencyCode, createdBy)
    VALUES (@ZoneC, 350.00, 'CRC', @AdminUserId);

DECLARE @i INT = 1;
WHILE @i <= 20
BEGIN
    IF NOT EXISTS (SELECT 1 FROM ParkingSpaces WHERE zoneId = @ZoneA AND spaceCode = RIGHT('0000' + CAST(@i AS VARCHAR(4)), 4))
        INSERT INTO ParkingSpaces(zoneId, spaceCode, status) VALUES (@ZoneA, RIGHT('0000' + CAST(@i AS VARCHAR(4)), 4), 'AVAILABLE');
    SET @i = @i + 1;
END

SET @i = 1;
WHILE @i <= 15
BEGIN
    IF NOT EXISTS (SELECT 1 FROM ParkingSpaces WHERE zoneId = @ZoneB AND spaceCode = RIGHT('0000' + CAST(@i AS VARCHAR(4)), 4))
        INSERT INTO ParkingSpaces(zoneId, spaceCode, status) VALUES (@ZoneB, RIGHT('0000' + CAST(@i AS VARCHAR(4)), 4), 'AVAILABLE');
    SET @i = @i + 1;
END

SET @i = 1;
WHILE @i <= 30
BEGIN
    IF NOT EXISTS (SELECT 1 FROM ParkingSpaces WHERE zoneId = @ZoneC AND spaceCode = RIGHT('0000' + CAST(@i AS VARCHAR(4)), 4))
        INSERT INTO ParkingSpaces(zoneId, spaceCode, status) VALUES (@ZoneC, RIGHT('0000' + CAST(@i AS VARCHAR(4)), 4), 'AVAILABLE');
    SET @i = @i + 1;
END
GO

DECLARE @DriverUserId INT = (SELECT userId FROM Users WHERE email = 'driver@test.com');
DECLARE @VehicleId INT = (SELECT TOP 1 vehicleId FROM Vehicles WHERE userId = @DriverUserId ORDER BY vehicleId);
DECLARE @SanJoseId INT = (SELECT municipalityId FROM Municipalities WHERE municipalityName = 'San Jose');
DECLARE @ZoneA INT = (SELECT zoneId FROM ParkingZones WHERE zoneName = 'Zona A - Centro');
DECLARE @SpaceId INT = (SELECT TOP 1 spaceId FROM ParkingSpaces WHERE zoneId = @ZoneA AND spaceCode = '0001');

IF NOT EXISTS (SELECT 1 FROM Fines WHERE fineNumber = 'SJ-2026-0001')
    INSERT INTO Fines(vehicleId, userId, municipalityId, zoneId, spaceId, fineNumber, reason, fineDate, amount, status)
    VALUES (@VehicleId, @DriverUserId, @SanJoseId, @ZoneA, @SpaceId, 'SJ-2026-0001', 'Tiempo vencido', DATEADD(DAY, -3, SYSUTCDATETIME()), 12000.00, 'PENDING');
GO
