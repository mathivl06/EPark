import crypto from "node:crypto";
import jwt from "jsonwebtoken";

const jwtSecret = process.env.JWT_SECRET ?? "dev-secret-change-me";

export function hashPassword(password) {
  return crypto.createHash("sha256").update(password, "utf8").digest("hex").toUpperCase();
}

export function signToken(user) {
  return jwt.sign(
    {
      sub: String(user.userId),
      email: user.email,
      role: user.roleCode,
      municipalityId: user.municipalityId ?? null
    },
    jwtSecret,
    { expiresIn: "8h" }
  );
}

export function requireAuth(req, res, next) {
  const header = req.headers.authorization ?? "";
  const [scheme, token] = header.split(" ");

  if (scheme !== "Bearer" || !token) {
    return res.status(401).json({ message: "Missing bearer token" });
  }

  try {
    req.user = jwt.verify(token, jwtSecret);
    return next();
  } catch {
    return res.status(401).json({ message: "Invalid or expired token" });
  }
}

export function requireRole(...roles) {
  return (req, res, next) => {
    if (!roles.includes(req.user?.role)) {
      return res.status(403).json({ message: "Forbidden" });
    }
    return next();
  };
}
