import "dotenv/config";
import cors from "cors";
import express from "express";
import { getPool, sql } from "./db.js";
import { hashPassword, requireAuth, requireRole, signToken } from "./auth.js";

const app = express();
const port = Number(process.env.PORT ?? 8080);

app.use(cors());
app.use(express.json());

function userResponse(row) {
  return {
    userId: row.userId,
    email: row.email,
    fullName: row.fullName,
    roleCode: row.roleCode,
    municipalityId: row.municipalityId ?? null
  };
}

async function getUserWithRoleByEmail(email) {
  const pool = await getPool();
  const result = await pool.request()
    .input("email", sql.VarChar(120), email)
    .query(`
      SELECT TOP 1
        u.userId,
        u.email,
        u.passwordHash,
        u.fullName,
        r.roleCode,
        map.municipalityId
      FROM Users u
      INNER JOIN UserRoles ur ON ur.userId = u.userId AND ur.enabled = 1 AND ur.deleted = 0
      INNER JOIN Roles r ON r.roleId = ur.roleId AND r.enabled = 1 AND r.deleted = 0
      LEFT JOIN MunicipalAdminProfiles map ON map.userId = u.userId AND map.deleted = 0
      WHERE u.email = @email AND u.deleted = 0 AND u.status = 'ACTIVE'
    `);
  return result.recordset[0] ?? null;
}

app.get("/health", async (_req, res) => {
  await getPool();
  res.json({ status: "ok" });
});

app.post("/auth/login", async (req, res) => {
  const { email, password } = req.body;
  if (!email || !password) {
    return res.status(400).json({ message: "Email and password are required" });
  }

  const user = await getUserWithRoleByEmail(String(email).trim().toLowerCase());
  if (!user || user.passwordHash !== hashPassword(password)) {
    return res.status(401).json({ message: "Invalid credentials" });
  }

  const pool = await getPool();
  await pool.request()
    .input("userId", sql.Int, user.userId)
    .query("UPDATE Users SET lastLoginAt = SYSUTCDATETIME() WHERE userId = @userId");

  res.json({ token: signToken(user), user: userResponse(user) });
});

app.post("/auth/register-driver", async (req, res) => {
  const { fullName, nationalId, email, password, vehiclePlate } = req.body;
  if (!fullName || !nationalId || !email || !password) {
    return res.status(400).json({ message: "Missing required fields" });
  }

  const pool = await getPool();
  const transaction = new sql.Transaction(pool);

  try {
    await transaction.begin();
    const request = new sql.Request(transaction);
    const normalizedEmail = String(email).trim().toLowerCase();

    const exists = await request
      .input("email", sql.VarChar(120), normalizedEmail)
      .query("SELECT userId FROM Users WHERE email = @email AND deleted = 0");

    if (exists.recordset.length > 0) {
      await transaction.rollback();
      return res.status(409).json({ message: "Email already registered" });
    }

    const userInsert = await new sql.Request(transaction)
      .input("email", sql.VarChar(120), normalizedEmail)
      .input("passwordHash", sql.VarChar(255), hashPassword(password))
      .input("fullName", sql.VarChar(150), fullName)
      .input("nationalId", sql.VarChar(30), nationalId)
      .query(`
        INSERT INTO Users(email, passwordHash, fullName, nationalId, emailVerified, status)
        OUTPUT INSERTED.userId
        VALUES (@email, @passwordHash, @fullName, @nationalId, 1, 'ACTIVE')
      `);

    const userId = userInsert.recordset[0].userId;

    await new sql.Request(transaction)
      .input("userId", sql.Int, userId)
      .query(`
        INSERT INTO DriverProfiles(userId) VALUES (@userId);
        INSERT INTO UserRoles(userId, roleId)
        SELECT @userId, roleId FROM Roles WHERE roleCode = 'DRIVER';
      `);

    if (vehiclePlate) {
      await new sql.Request(transaction)
        .input("userId", sql.Int, userId)
        .input("plate", sql.VarChar(20), String(vehiclePlate).trim().toUpperCase())
        .query("INSERT INTO Vehicles(userId, plateNumber, alias) VALUES (@userId, @plate, 'Principal')");
    }

    await transaction.commit();

    const user = await getUserWithRoleByEmail(normalizedEmail);
    return res.status(201).json({ token: signToken(user), user: userResponse(user) });
  } catch (error) {
    await transaction.rollback();
    return res.status(500).json({ message: "Could not register driver", detail: error.message });
  }
});

app.get("/municipalities", requireAuth, async (_req, res) => {
  const pool = await getPool();
  const result = await pool.request().query(`
    SELECT municipalityId, municipalityName, province
    FROM Municipalities
    WHERE enabled = 1 AND deleted = 0
    ORDER BY municipalityName
  `);
  res.json(result.recordset);
});

app.get("/zones", requireAuth, async (req, res) => {
  const pool = await getPool();
  const municipalityId = Number(req.query.municipalityId || 0);
  const request = pool.request().input("municipalityId", sql.Int, municipalityId || null);
  const result = await request.query(`
    SELECT
      z.zoneId,
      z.municipalityId,
      m.municipalityName,
      z.zoneName,
      z.description,
      z.latitude,
      z.longitude,
      CONVERT(varchar(5), z.operationStartTime, 108) AS operationStartTime,
      CONVERT(varchar(5), z.operationEndTime, 108) AS operationEndTime,
      z.totalSpaces,
      z.status,
      COALESCE(SUM(CASE WHEN ps.status = 'AVAILABLE' AND ps.deleted = 0 THEN 1 ELSE 0 END), 0) AS availableSpaces,
      COALESCE(t.hourlyRate, 0) AS hourlyRate,
      COALESCE(t.currencyCode, 'CRC') AS currencyCode
    FROM ParkingZones z
    INNER JOIN Municipalities m ON m.municipalityId = z.municipalityId
    LEFT JOIN ParkingSpaces ps ON ps.zoneId = z.zoneId
    OUTER APPLY (
      SELECT TOP 1 hourlyRate, currencyCode
      FROM ZoneTariffs
      WHERE zoneId = z.zoneId AND validTo IS NULL AND deleted = 0
      ORDER BY validFrom DESC
    ) t
    WHERE z.deleted = 0
      AND z.status = 'ACTIVE'
      AND (@municipalityId IS NULL OR z.municipalityId = @municipalityId)
    GROUP BY z.zoneId, z.municipalityId, m.municipalityName, z.zoneName, z.description,
      z.latitude, z.longitude, z.operationStartTime, z.operationEndTime, z.totalSpaces,
      z.status, t.hourlyRate, t.currencyCode
    ORDER BY z.zoneName
  `);
  res.json(result.recordset);
});

app.get("/driver/home", requireAuth, requireRole("DRIVER"), async (req, res) => {
  const pool = await getPool();
  const userId = Number(req.user.sub);

  const result = await pool.request()
    .input("userId", sql.Int, userId)
    .query(`
      SELECT TOP 1
        u.userId,
        u.fullName,
        v.vehicleId,
        v.plateNumber,
        s.sessionId,
        z.zoneName,
        sp.spaceCode,
        s.startedAt,
        s.hourlyRateApplied,
        s.status AS sessionStatus
      FROM Users u
      LEFT JOIN Vehicles v ON v.userId = u.userId AND v.enabled = 1 AND v.deleted = 0
      LEFT JOIN ParkingSessions s ON s.userId = u.userId AND s.status = 'ACTIVE' AND s.deleted = 0
      LEFT JOIN ParkingZones z ON z.zoneId = s.zoneId
      LEFT JOIN ParkingSpaces sp ON sp.spaceId = s.spaceId
      WHERE u.userId = @userId
      ORDER BY v.vehicleId
    `);

  const row = result.recordset[0];
  res.json({
    fullName: row?.fullName ?? "",
    primaryVehicle: row?.vehicleId ? { vehicleId: row.vehicleId, plateNumber: row.plateNumber } : null,
    activeSession: row?.sessionId ? {
      sessionId: row.sessionId,
      zoneName: row.zoneName,
      spaceCode: row.spaceCode,
      startedAt: row.startedAt,
      hourlyRateApplied: Number(row.hourlyRateApplied),
      status: row.sessionStatus
    } : null
  });
});

app.get("/driver/vehicles", requireAuth, requireRole("DRIVER"), async (req, res) => {
  const pool = await getPool();
  const result = await pool.request()
    .input("userId", sql.Int, Number(req.user.sub))
    .query(`
      SELECT vehicleId, plateNumber, alias
      FROM Vehicles
      WHERE userId = @userId AND enabled = 1 AND deleted = 0
      ORDER BY vehicleId
    `);
  res.json(result.recordset);
});

app.get("/driver/sessions/active", requireAuth, requireRole("DRIVER"), async (req, res) => {
  const pool = await getPool();
  const result = await pool.request()
    .input("userId", sql.Int, Number(req.user.sub))
    .query(`
      SELECT TOP 1
        s.sessionId,
        z.zoneName,
        sp.spaceCode,
        v.plateNumber,
        s.startedAt,
        s.hourlyRateApplied,
        s.status
      FROM ParkingSessions s
      INNER JOIN ParkingZones z ON z.zoneId = s.zoneId
      INNER JOIN ParkingSpaces sp ON sp.spaceId = s.spaceId
      INNER JOIN Vehicles v ON v.vehicleId = s.vehicleId
      WHERE s.userId = @userId AND s.status = 'ACTIVE' AND s.deleted = 0
      ORDER BY s.startedAt DESC
    `);
  res.json(result.recordset[0] ?? null);
});

app.post("/driver/sessions/start", requireAuth, requireRole("DRIVER"), async (req, res) => {
  const { vehicleId, zoneId, spaceCode } = req.body;
  if (!vehicleId || !zoneId || !spaceCode) {
    return res.status(400).json({ message: "vehicleId, zoneId and spaceCode are required" });
  }

  const pool = await getPool();
  const transaction = new sql.Transaction(pool);

  try {
    await transaction.begin();
    const userId = Number(req.user.sub);

    const active = await new sql.Request(transaction)
      .input("userId", sql.Int, userId)
      .query("SELECT TOP 1 sessionId FROM ParkingSessions WHERE userId = @userId AND status = 'ACTIVE' AND deleted = 0");
    if (active.recordset.length > 0) {
      await transaction.rollback();
      return res.status(409).json({ message: "User already has an active session" });
    }

    const lookup = await new sql.Request(transaction)
      .input("userId", sql.Int, userId)
      .input("vehicleId", sql.Int, vehicleId)
      .input("zoneId", sql.Int, zoneId)
      .input("spaceCode", sql.VarChar(4), spaceCode)
      .query(`
        SELECT TOP 1
          v.vehicleId,
          z.municipalityId,
          z.zoneId,
          ps.spaceId,
          ps.status AS spaceStatus,
          t.tariffId,
          t.hourlyRate
        FROM Vehicles v
        CROSS JOIN ParkingZones z
        INNER JOIN ParkingSpaces ps ON ps.zoneId = z.zoneId AND ps.spaceCode = @spaceCode AND ps.deleted = 0
        OUTER APPLY (
          SELECT TOP 1 tariffId, hourlyRate
          FROM ZoneTariffs
          WHERE zoneId = z.zoneId AND validTo IS NULL AND deleted = 0
          ORDER BY validFrom DESC
        ) t
        WHERE v.vehicleId = @vehicleId AND v.userId = @userId AND z.zoneId = @zoneId
      `);

    const row = lookup.recordset[0];
    if (!row) {
      await transaction.rollback();
      return res.status(404).json({ message: "Vehicle, zone or space not found" });
    }
    if (row.spaceStatus !== "AVAILABLE") {
      await transaction.rollback();
      return res.status(409).json({ message: "Parking space is not available" });
    }
    if (!row.tariffId) {
      await transaction.rollback();
      return res.status(409).json({ message: "Zone has no active tariff" });
    }

    await new sql.Request(transaction)
      .input("spaceId", sql.Int, row.spaceId)
      .query("UPDATE ParkingSpaces SET status = 'OCCUPIED', updatedAt = SYSUTCDATETIME() WHERE spaceId = @spaceId");

    const inserted = await new sql.Request(transaction)
      .input("userId", sql.Int, userId)
      .input("vehicleId", sql.Int, row.vehicleId)
      .input("municipalityId", sql.Int, row.municipalityId)
      .input("zoneId", sql.Int, row.zoneId)
      .input("spaceId", sql.Int, row.spaceId)
      .input("tariffId", sql.Int, row.tariffId)
      .input("hourlyRate", sql.Decimal(10, 2), row.hourlyRate)
      .query(`
        INSERT INTO ParkingSessions(userId, vehicleId, municipalityId, zoneId, spaceId, tariffId, hourlyRateApplied, startedAt, status)
        OUTPUT INSERTED.sessionId
        VALUES (@userId, @vehicleId, @municipalityId, @zoneId, @spaceId, @tariffId, @hourlyRate, SYSUTCDATETIME(), 'ACTIVE')
      `);

    await transaction.commit();
    return res.status(201).json({ sessionId: inserted.recordset[0].sessionId });
  } catch (error) {
    await transaction.rollback();
    return res.status(500).json({ message: "Could not start session", detail: error.message });
  }
});

app.post("/driver/sessions/:id/finish", requireAuth, requireRole("DRIVER"), async (req, res) => {
  const pool = await getPool();
  const transaction = new sql.Transaction(pool);

  try {
    await transaction.begin();
    const sessionId = Number(req.params.id);
    const userId = Number(req.user.sub);

    const sessionResult = await new sql.Request(transaction)
      .input("sessionId", sql.BigInt, sessionId)
      .input("userId", sql.Int, userId)
      .query(`
        SELECT TOP 1 sessionId, spaceId, startedAt, hourlyRateApplied
        FROM ParkingSessions
        WHERE sessionId = @sessionId AND userId = @userId AND status = 'ACTIVE' AND deleted = 0
      `);
    const session = sessionResult.recordset[0];
    if (!session) {
      await transaction.rollback();
      return res.status(404).json({ message: "Active session not found" });
    }

    const finished = await new sql.Request(transaction)
      .input("sessionId", sql.BigInt, sessionId)
      .query(`
        DECLARE @endedAt DATETIME2 = SYSUTCDATETIME();
        DECLARE @minutes INT = DATEDIFF(MINUTE, (SELECT startedAt FROM ParkingSessions WHERE sessionId = @sessionId), @endedAt);
        DECLARE @rate DECIMAL(10,2) = (SELECT hourlyRateApplied FROM ParkingSessions WHERE sessionId = @sessionId);
        DECLARE @amount DECIMAL(10,2) = CEILING(CASE WHEN @minutes < 1 THEN 1 ELSE @minutes END / 60.0 * @rate);

        UPDATE ParkingSessions
        SET endedAt = @endedAt,
            elapsedMinutes = @minutes,
            totalAmount = @amount,
            status = 'FINISHED',
            updatedAt = @endedAt
        OUTPUT INSERTED.sessionId, INSERTED.elapsedMinutes, INSERTED.totalAmount
        WHERE sessionId = @sessionId;
      `);

    await new sql.Request(transaction)
      .input("spaceId", sql.Int, session.spaceId)
      .query("UPDATE ParkingSpaces SET status = 'AVAILABLE', updatedAt = SYSUTCDATETIME() WHERE spaceId = @spaceId");

    await transaction.commit();
    return res.json(finished.recordset[0]);
  } catch (error) {
    await transaction.rollback();
    return res.status(500).json({ message: "Could not finish session", detail: error.message });
  }
});

app.get("/driver/fines", requireAuth, requireRole("DRIVER"), async (req, res) => {
  const pool = await getPool();
  const result = await pool.request()
    .input("userId", sql.Int, Number(req.user.sub))
    .query(`
      SELECT fineId, fineNumber, reason, fineDate, amount, status
      FROM Fines
      WHERE userId = @userId AND deleted = 0
      ORDER BY fineDate DESC
    `);
  res.json(result.recordset);
});

app.get("/admin/reports/summary", requireAuth, requireRole("MUNICIPAL_ADMIN"), async (req, res) => {
  const pool = await getPool();
  const municipalityId = Number(req.user.municipalityId);
  const result = await pool.request()
    .input("municipalityId", sql.Int, municipalityId)
    .query(`
      SELECT
        COUNT(DISTINCT z.zoneId) AS totalZones,
        COUNT(DISTINCT CASE WHEN s.status = 'ACTIVE' THEN s.sessionId END) AS activeSessions,
        COALESCE(SUM(CASE WHEN p.status = 'APPROVED' THEN p.amount ELSE 0 END), 0) AS revenue
      FROM ParkingZones z
      LEFT JOIN ParkingSessions s ON s.zoneId = z.zoneId AND s.deleted = 0
      LEFT JOIN Payments p ON p.sessionId = s.sessionId AND p.deleted = 0
      WHERE z.municipalityId = @municipalityId AND z.deleted = 0
    `);
  res.json(result.recordset[0]);
});

app.use((error, _req, res, _next) => {
  console.error(error);
  res.status(500).json({ message: "Unexpected server error" });
});

app.listen(port, "0.0.0.0", () => {
  console.log(`EPark API listening on port ${port}`);
});
