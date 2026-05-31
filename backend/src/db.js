import sql from "mssql";

const config = {
  server: process.env.SQL_SERVER ?? "localhost",
  port: Number(process.env.SQL_PORT ?? 1433),
  database: process.env.SQL_DATABASE ?? "EParkDB",
  user: process.env.SQL_USER ?? "sa",
  password: process.env.SQL_PASSWORD ?? "",
  options: {
    encrypt: (process.env.SQL_ENCRYPT ?? "false") === "true",
    trustServerCertificate: (process.env.SQL_TRUST_CERT ?? "true") === "true"
  },
  pool: {
    max: 10,
    min: 0,
    idleTimeoutMillis: 30000
  }
};

let poolPromise;

export function getPool() {
  if (!poolPromise) {
    poolPromise = sql.connect(config);
  }
  return poolPromise;
}

export { sql };
