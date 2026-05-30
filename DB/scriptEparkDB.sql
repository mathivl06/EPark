CREATE DATABASE EParkDB;
GO

USE EParkDB;
GO

/* =========================================================
   SECCION 1 - SEGURIDAD Y USUARIOS
========================================================= */

CREATE TABLE Users (
    userId INT IDENTITY(1,1) PRIMARY KEY,
    email VARCHAR(120) NOT NULL UNIQUE,
    passwordHash VARCHAR(255) NOT NULL,
    fullName VARCHAR(150) NOT NULL,
    nationalId VARCHAR(30) NULL,
    phoneNumber VARCHAR(30) NULL,
    emailVerified BIT NOT NULL DEFAULT 0,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING_VERIFICATION',
    lastLoginAt DATETIME2 NULL,
    createdAt DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME(),
    updatedAt DATETIME2 NULL,
    deleted BIT NOT NULL DEFAULT 0,
    deletedAt DATETIME2 NULL
);
GO

CREATE TABLE Roles (
    roleId SMALLINT IDENTITY(1,1) PRIMARY KEY,
    roleCode VARCHAR(40) NOT NULL UNIQUE,
    roleName VARCHAR(80) NOT NULL,
    description VARCHAR(200) NULL,
    enabled BIT NOT NULL DEFAULT 1,
    createdAt DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME(),
    updatedAt DATETIME2 NULL,
    deleted BIT NOT NULL DEFAULT 0,
    deletedAt DATETIME2 NULL
);
GO

CREATE TABLE Municipalities (
    municipalityId INT IDENTITY(1,1) PRIMARY KEY,
    municipalityName VARCHAR(120) NOT NULL UNIQUE,
    province VARCHAR(80) NULL,
    contactEmail VARCHAR(120) NULL,
    enabled BIT NOT NULL DEFAULT 1,
    createdAt DATETIME2 NOT NULL DEFAULT GETUTCDATE(),
    updatedAt DATETIME2 NULL,
    deleted BIT NOT NULL DEFAULT 0,
    deletedAt DATETIME2 NULL
);
GO

CREATE TABLE UserRoles (
    userId INT NOT NULL,
    roleId SMALLINT NOT NULL,
    assignedAt DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME(),
    assignedBy INT NULL,
    enabled BIT NOT NULL DEFAULT 1,
    deleted BIT NOT NULL DEFAULT 0,
    deletedAt DATETIME2 NULL,

    CONSTRAINT PK_UserRoles PRIMARY KEY (userId, roleId),

    CONSTRAINT FK_UserRoles_User
        FOREIGN KEY (userId)
        REFERENCES Users(userId),

    CONSTRAINT FK_UserRoles_Role
        FOREIGN KEY (roleId)
        REFERENCES Roles(roleId),

    CONSTRAINT FK_UserRoles_AssignedBy
        FOREIGN KEY (assignedBy)
        REFERENCES Users(userId)
);
GO

CREATE TABLE DriverProfiles (
    driverProfileId INT IDENTITY(1,1) PRIMARY KEY,
    userId INT NOT NULL UNIQUE,
    defaultPaymentMethodId INT NULL,
    notificationMinutesBefore SMALLINT NOT NULL DEFAULT 10,
    createdAt DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME(),
    updatedAt DATETIME2 NULL,
    deleted BIT NOT NULL DEFAULT 0,
    deletedAt DATETIME2 NULL,

    CONSTRAINT FK_DriverProfiles_User
        FOREIGN KEY (userId)
        REFERENCES Users(userId)
);
GO

CREATE TABLE MunicipalAdminProfiles (
    adminProfileId INT IDENTITY(1,1) PRIMARY KEY,
    userId INT NOT NULL UNIQUE,
    municipalityId INT NOT NULL,
    jobTitle VARCHAR(100) NULL,
    createdAt DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME(),
    updatedAt DATETIME2 NULL,
    deleted BIT NOT NULL DEFAULT 0,
    deletedAt DATETIME2 NULL,

    CONSTRAINT FK_AdminProfiles_User
        FOREIGN KEY (userId)
        REFERENCES Users(userId),

    CONSTRAINT FK_AdminProfiles_Municipality
        FOREIGN KEY (municipalityId)
        REFERENCES Municipalities(municipalityId)
);
GO

/* =========================================================
   SECCION 2 - ZONAS Y ESPACIOS
========================================================= */

CREATE TABLE ParkingZones (
    zoneId INT IDENTITY(1,1) PRIMARY KEY,
    municipalityId INT NOT NULL,
    zoneName VARCHAR(120) NOT NULL,
    description VARCHAR(250) NULL,
    latitude DECIMAL(9,6) NOT NULL,
    longitude DECIMAL(9,6) NOT NULL,
    operationStartTime TIME NOT NULL,
    operationEndTime TIME NOT NULL,
    totalSpaces INT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    createdAt DATETIME2 NOT NULL DEFAULT GETUTCDATE(),
    updatedAt DATETIME2 NULL,
    createdBy INT NULL,
    updatedBy INT NULL,
    deleted BIT NOT NULL DEFAULT 0,
    deletedAt DATETIME2 NULL,

    CONSTRAINT UQ_ParkingZones_Municipality_Name
        UNIQUE (municipalityId, zoneName),

    CONSTRAINT CK_ParkingZones_TotalSpaces
        CHECK (totalSpaces >= 0),

    CONSTRAINT FK_ParkingZones_Municipality
        FOREIGN KEY (municipalityId)
        REFERENCES Municipalities(municipalityId),

    CONSTRAINT FK_ParkingZones_CreatedBy
        FOREIGN KEY (createdBy)
        REFERENCES Users(userId),

    CONSTRAINT FK_ParkingZones_UpdatedBy
        FOREIGN KEY (updatedBy)
        REFERENCES Users(userId)
);
GO

CREATE TABLE ParkingSpaces (
    spaceId INT IDENTITY(1,1) PRIMARY KEY,
    zoneId INT NOT NULL,
    spaceCode VARCHAR(4) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE',
    latitude DECIMAL(9,6) NULL,
    longitude DECIMAL(9,6) NULL,
    createdAt DATETIME2 NOT NULL DEFAULT GETUTCDATE(),
    updatedAt DATETIME2 NULL,
    deleted BIT NOT NULL DEFAULT 0,
    deletedAt DATETIME2 NULL,

    CONSTRAINT UQ_ParkingSpaces_Zone_Code
        UNIQUE (zoneId, spaceCode),

    CONSTRAINT CK_ParkingSpaces_Code
        CHECK (LEN(spaceCode) = 4),

    CONSTRAINT FK_ParkingSpaces_Zone
        FOREIGN KEY (zoneId)
        REFERENCES ParkingZones(zoneId)
);
GO

CREATE TABLE ZoneTariffs (
    tariffId INT IDENTITY(1,1) PRIMARY KEY,
    zoneId INT NOT NULL,
    hourlyRate DECIMAL(10,2) NOT NULL,
    currencyCode CHAR(3) NOT NULL DEFAULT 'CRC',
    validFrom DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME(),
    validTo DATETIME2 NULL,
    createdAt DATETIME2 NOT NULL DEFAULT GETUTCDATE(),
    createdBy INT NULL,
    deleted BIT NOT NULL DEFAULT 0,
    deletedAt DATETIME2 NULL,

    CONSTRAINT CK_ZoneTariffs_Rate
        CHECK (hourlyRate >= 0),

    CONSTRAINT CK_ZoneTariffs_Dates
        CHECK (validTo IS NULL OR validTo > validFrom),

    CONSTRAINT FK_ZoneTariffs_Zone
        FOREIGN KEY (zoneId)
        REFERENCES ParkingZones(zoneId),

    CONSTRAINT FK_ZoneTariffs_CreatedBy
        FOREIGN KEY (createdBy)
        REFERENCES Users(userId)
);
GO

/* =========================================================
   SECCION 3 - VEHICULOS Y PAGOS
========================================================= */

CREATE TABLE Vehicles (
    vehicleId INT IDENTITY(1,1) PRIMARY KEY,
    userId INT NOT NULL,
    plateNumber VARCHAR(20) NOT NULL,
    alias VARCHAR(80) NULL,
    enabled BIT NOT NULL DEFAULT 1,
    createdAt DATETIME2 NOT NULL DEFAULT GETUTCDATE(),
    updatedAt DATETIME2 NULL,
    deleted BIT NOT NULL DEFAULT 0,
    deletedAt DATETIME2 NULL,

    CONSTRAINT UQ_Vehicles_User_Plate
        UNIQUE (userId, plateNumber),

    CONSTRAINT FK_Vehicles_User
        FOREIGN KEY (userId)
        REFERENCES Users(userId)
);
GO

CREATE TABLE PaymentMethods (
    paymentMethodId INT IDENTITY(1,1) PRIMARY KEY,
    userId INT NOT NULL,
    methodType VARCHAR(30) NOT NULL,
    cardBrand VARCHAR(40) NULL,
    lastFourDigits CHAR(4) NOT NULL,
    tokenReference VARCHAR(120) NULL,
    expirationMonth TINYINT NULL,
    expirationYear SMALLINT NULL,
    enabled BIT NOT NULL DEFAULT 1,
    createdAt DATETIME2 NOT NULL DEFAULT GETUTCDATE(),
    updatedAt DATETIME2 NULL,
    deleted BIT NOT NULL DEFAULT 0,
    deletedAt DATETIME2 NULL,

    CONSTRAINT CK_PaymentMethods_ExpirationMonth
        CHECK (
            expirationMonth IS NULL
            OR expirationMonth BETWEEN 1 AND 12
        ),

    CONSTRAINT FK_PaymentMethods_User
        FOREIGN KEY (userId)
        REFERENCES Users(userId)
);
GO

ALTER TABLE DriverProfiles
ADD CONSTRAINT FK_DriverProfiles_DefaultPaymentMethod
FOREIGN KEY (defaultPaymentMethodId)
REFERENCES PaymentMethods(paymentMethodId);
GO

/* =========================================================
   SECCION 4 - SESIONES
========================================================= */

CREATE TABLE ParkingSessions (
    sessionId BIGINT IDENTITY(1,1) PRIMARY KEY,
    userId INT NOT NULL,
    vehicleId INT NOT NULL,
    municipalityId INT NOT NULL,
    zoneId INT NOT NULL,
    spaceId INT NOT NULL,
    tariffId INT NOT NULL,
    syncedFromDevice BIT NOT NULL DEFAULT 0,
    hourlyRateApplied DECIMAL(10,2) NOT NULL,
    startedAt DATETIME2 NOT NULL DEFAULT GETUTCDATE(),
    expectedEndAt DATETIME2 NULL,
    endedAt DATETIME2 NULL,
    elapsedMinutes INT NULL,
    deviceCreatedAt DATETIME2 NULL,
    totalAmount DECIMAL(10,2) NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    createdAt DATETIME2 NOT NULL DEFAULT GETUTCDATE(),
    updatedAt DATETIME2 NULL,
    deleted BIT NOT NULL DEFAULT 0,
    deletedAt DATETIME2 NULL,

    CONSTRAINT CK_ParkingSessions_Dates
        CHECK (endedAt IS NULL OR endedAt >= startedAt),

    CONSTRAINT CK_ParkingSessions_Amount
        CHECK (totalAmount IS NULL OR totalAmount >= 0),

    CONSTRAINT FK_ParkingSessions_User
        FOREIGN KEY (userId)
        REFERENCES Users(userId),

    CONSTRAINT FK_ParkingSessions_Vehicle
        FOREIGN KEY (vehicleId)
        REFERENCES Vehicles(vehicleId),

    CONSTRAINT FK_ParkingSessions_Municipality
        FOREIGN KEY (municipalityId)
        REFERENCES Municipalities(municipalityId),

    CONSTRAINT FK_ParkingSessions_Zone
        FOREIGN KEY (zoneId)
        REFERENCES ParkingZones(zoneId),

    CONSTRAINT FK_ParkingSessions_Space
        FOREIGN KEY (spaceId)
        REFERENCES ParkingSpaces(spaceId),

    CONSTRAINT FK_ParkingSessions_Tariff
        FOREIGN KEY (tariffId)
        REFERENCES ZoneTariffs(tariffId)
);
GO

/* =========================================================
   SECCION 5 - MULTAS
========================================================= */

CREATE TABLE Fines (
    fineId BIGINT IDENTITY(1,1) PRIMARY KEY,
    vehicleId INT NOT NULL,
    userId INT NOT NULL,
    municipalityId INT NOT NULL,
    zoneId INT NULL,
    spaceId INT NULL,
    fineNumber VARCHAR(80) NOT NULL UNIQUE,
    reason VARCHAR(200) NOT NULL,
    fineDate DATETIME2 NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    createdAt DATETIME2 NOT NULL DEFAULT GETUTCDATE(),
    updatedAt DATETIME2 NULL,
    deleted BIT NOT NULL DEFAULT 0,
    deletedAt DATETIME2 NULL,

    CONSTRAINT CK_Fines_Amount
        CHECK (amount >= 0),

    CONSTRAINT FK_Fines_Vehicle
        FOREIGN KEY (vehicleId)
        REFERENCES Vehicles(vehicleId),

    CONSTRAINT FK_Fines_User
        FOREIGN KEY (userId)
        REFERENCES Users(userId),

    CONSTRAINT FK_Fines_Municipality
        FOREIGN KEY (municipalityId)
        REFERENCES Municipalities(municipalityId),

    CONSTRAINT FK_Fines_Zone
        FOREIGN KEY (zoneId)
        REFERENCES ParkingZones(zoneId),

    CONSTRAINT FK_Fines_Space
        FOREIGN KEY (spaceId)
        REFERENCES ParkingSpaces(spaceId)
);
GO

/* =========================================================
   SECCION 6 - PAGOS Y NOTIFICACIONES
========================================================= */

CREATE TABLE Payments (
    paymentId BIGINT IDENTITY(1,1) PRIMARY KEY,
    userId INT NOT NULL,
    paymentMethodId INT NULL,
    paymentTargetType VARCHAR(30) NOT NULL,
    sessionId BIGINT NULL,
    fineId BIGINT NULL,
    amount DECIMAL(10,2) NOT NULL,
    currencyCode CHAR(3) NOT NULL DEFAULT 'CRC',
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    providerReference VARCHAR(120) NULL,
    receiptNumber VARCHAR(80) NULL UNIQUE,
    requestedAt DATETIME2 NOT NULL DEFAULT GETUTCDATE(),
    processedAt DATETIME2 NULL,
    createdAt DATETIME2 NOT NULL DEFAULT GETUTCDATE(),
    deleted BIT NOT NULL DEFAULT 0,
    deletedAt DATETIME2 NULL,

    CONSTRAINT CK_Payments_Amount
        CHECK (amount >= 0),

    CONSTRAINT CK_Payments_Target
        CHECK (
            (
                paymentTargetType = 'PARKING_SESSION'
                AND sessionId IS NOT NULL
                AND fineId IS NULL
            )
            OR
            (
                paymentTargetType = 'FINE'
                AND fineId IS NOT NULL
                AND sessionId IS NULL
            )
        ),

    CONSTRAINT FK_Payments_User
        FOREIGN KEY (userId)
        REFERENCES Users(userId),

    CONSTRAINT FK_Payments_Method
        FOREIGN KEY (paymentMethodId)
        REFERENCES PaymentMethods(paymentMethodId),

    CONSTRAINT FK_Payments_Session
        FOREIGN KEY (sessionId)
        REFERENCES ParkingSessions(sessionId),

    CONSTRAINT FK_Payments_Fine
        FOREIGN KEY (fineId)
        REFERENCES Fines(fineId)
);
GO

CREATE TABLE Notifications (
    notificationId BIGINT IDENTITY(1,1) PRIMARY KEY,
    userId INT NOT NULL,
    notificationType VARCHAR(50) NOT NULL,
    title VARCHAR(120) NOT NULL,
    message VARCHAR(300) NOT NULL,
    relatedEntityType VARCHAR(50) NULL,
    relatedEntityId BIGINT NULL,
    scheduledAt DATETIME2 NULL,
    sentAt DATETIME2 NULL,
    readAt DATETIME2 NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    createdAt DATETIME2 NOT NULL DEFAULT GETUTCDATE(),
    deleted BIT NOT NULL DEFAULT 0,
    deletedAt DATETIME2 NULL,

    CONSTRAINT FK_Notifications_User
        FOREIGN KEY (userId)
        REFERENCES Users(userId)
);
GO

/* =========================================================
   DATOS INICIALES
========================================================= */

INSERT INTO Roles(roleCode, roleName)
VALUES
('DRIVER', 'Conductor'),
('MUNICIPAL_ADMIN', 'Administrador Municipal');
GO

/* =========================================================
   INDICES
========================================================= */

CREATE INDEX IDX_Users_Email
ON Users(email);

CREATE INDEX IDX_UserRoles_UserId
ON UserRoles(userId);

CREATE INDEX IDX_AdminProfiles_Municipality
ON MunicipalAdminProfiles(municipalityId);

CREATE INDEX IDX_ParkingZones_Municipality
ON ParkingZones(municipalityId);

CREATE INDEX IDX_ParkingSpaces_Zone_Status
ON ParkingSpaces(zoneId, status);

CREATE INDEX IDX_ZoneTariffs_Zone_ValidTo
ON ZoneTariffs(zoneId, validTo);

CREATE INDEX IDX_Vehicles_User
ON Vehicles(userId);

CREATE INDEX IDX_ParkingSessions_User_Status
ON ParkingSessions(userId, status);

CREATE INDEX IDX_ParkingSessions_Municipality_Dates
ON ParkingSessions(municipalityId, startedAt, endedAt);

CREATE INDEX IDX_Fines_User_Status
ON Fines(userId, status);

CREATE INDEX IDX_Payments_User_Date
ON Payments(userId, requestedAt);

CREATE INDEX IDX_Notifications_User_Status
ON Notifications(userId, status);
GO