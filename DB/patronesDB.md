# Patrones de Diseño de Bases de Datos

## PATRÓN: USER PERMISSIONS (Control de Acceso Basado en Roles - RBAC)

### 1. Qué problema soluciona

Imagina un sistema sin este patrón:
- Usuarios sin columnas de permisos (¿"admin"? ¿"can_edit"? ¿"can_delete"?).
- Si cambias políticas, editas 1000 registros.
- No sabes quién dio qué permiso, ni cuándo.
- Escalar a 100 tipos de permiso es un caos.

**El patrón resuelve:** Separar usuarios, roles y permisos en tablas independientes, para poder combinarlos de forma flexible sin duplicar datos.

---

### 2. Cuándo usarlo

**Usalo siempre** en:
- Apps SaaS (múltiples usuarios, múltiples niveles de acceso).
- Sistemas administrativos (backoffice, panel de control).
- Plataformas colaborativas (equipos, departamentos).
- Cualquier cosa que tenga más de 1 usuario.

**No lo hagas si:**
- Tu sistema es monousuario (pero eso es muy raro hoy).

---

### 3. Aporte al diseño

**Sin patrón (Mal diseño):**
```
usuarios
├── usuarioid
├── nombre
├── es_admin (boolean)           ← Para 1 nivel = OK
├── puede_editar (boolean)
├── puede_borrar (boolean)
├── puede_comentar (boolean)
├── puede_ver_reportes (boolean)
└── ... (columnas a infinito)    ← ESCALA MAL
```

**Problema:** Cada nuevo permiso = nueva columna. A los 50 permisos, la tabla explota.

**Con RBAC (Buen diseño):**
```
usuarios                  rolesdeusuario        permisos
├── usuarioid            ├── roleid            ├── permisoid
└── nombre               ├── nombre            ├── nombre
                         └── descripcion       └── descripcion

        ↓ Tablas puente (Many-to-Many) ↓

usuariosxrole (M:M)       permisoxrole (M:M)
├── usuarioid            ├── permisoid
├── roleid               ├── roleid
└── fechaasignacion      └── fechaasignacion

permisosxusuario (M:M, opcional)
├── usuarioid
├── permisoid
└── fechaasignacion
```

**Ventaja:** Los permisos son **enumerables** y **reutilizables**. Cambias la política modificando *relaciones*, no *esquema*.

---

### 4. Mantenibilidad

**Antes (Sin patrón):**
- Admin dice "necesito que María sea 'editora'".
- Cambias 20 columnas booleanas para María.
- En 6 meses, ¿qué es una "editora"? Nadie lo sabe.
- Código hardcodeado en la aplicación verifica columnas directas.
- Agregar nuevo permiso = cambiar esquema + reescribir queries + compilar app.

**Después (Con RBAC):**
- Roles quedan documentados y versionados:
  ```
  rolesdeusuario:
  ├── roleid: 1, nombre: 'Editor', descripcion: 'Puede crear, editar, eliminar posts'
  ├── roleid: 2, nombre: 'Revisor', descripcion: 'Puede ver y comentar, pero no editar'
  └── roleid: 3, nombre: 'Admin', descripcion: 'Control total'
  ```
- Asignas roles a usuarios: `INSERT INTO usuariosxrole (usuarioid, roleid)`.
- Nuevo permiso? Solo agrega una fila a `permisos` y asócialo a roles.
- **Es legible. Es histórico (si guardas fechas). Es auditable.**

---

### 5. Escalabilidad

**Problema de crecimiento sin patrón:**
- 10 usuarios, 5 permisos: OK (50 valores en columnas booleanas).
- 100K usuarios, 200 permisos: Tabla de `usuarios` tiene 200 columnas. **Pesadilla.**
- Queries complejas: "¿Cuál es el permiso de María?" = CHECK 200 columnas.
- Índices ineficaces (no puedes indexar 200 booleanos).
- Cambios frecuentes = ALTER TABLE recurrentes = downtime.

**Con RBAC:**
- 100K usuarios, 200 permisos: Tablas pequeñas, normalizadas.
- Query simple: 
  ```sql
  SELECT DISTINCT p.nombre 
  FROM permisos p
  JOIN permisoxrole pr USING(permisoid)
  JOIN usuariosxrole ur USING(roleid)
  WHERE ur.usuarioid = ?;
  ```
- Índices eficaces (FK sobre tablas pequeñas).
- **Las queries escalan linealmente, no exponencialmente.**
- Cambios de política = UPDATE en tablas, sin alterar estructura.

---

### 6. Errores comunes

| Error | Qué pasa | Por qué está mal |
|-------|----------|------------------|
| **Solo tabla usuarios+rol, sin permisos** | Roles hardcodeados en la app | No es escalable ni auditable. Si agregas 1 permiso nuevo = recompila la app |
| **No guardar `fechaasignacion` o `asignadopor`** | No sabes cuándo se otorgó u quién lo hizo | Auditoría imposible. Si hay error de seguridad, no puedes investigar |
| **Permitir asignar permisos Y roles al mismo usuario** | Redundancia, confusión, bugs | Si María tiene rol "Editor" pero también permiso "Comentar", ¿quién gana? |
| **No usar soft-delete (`deleted` BOOLEAN)** | Borras un rol y rompes histórico | Mejor marcar como eliminado y mantener datos históricos |
| **Tablas sin PRIMARY KEY compuesta** | Mismo usuario, mismo rol, duplicado | Claro error de escalabilidad y lógica |
| **Confundir roles con permisos** | Tablas sin estructura | Rol = conjunto de permisos. Permiso = acción atómica. |

---

### 7. Ejemplo concreto: Plataforma de blog con colaboradores

#### **Tabla: permisos**
```
permisoid | nombre              | descripcion
----------|---------------------|-------------------------------------
1         | create_post         | Puede crear posts
2         | edit_post           | Puede editar posts
3         | delete_post         | Puede borrar posts
4         | view_analytics      | Puede ver estadísticas
5         | manage_users        | Puede gestionar usuarios
```

#### **Tabla: rolesdeusuario**
```
roleid | nombre      | descripcion
-------|-------------|-----------------------------------------------
1      | Editor      | Crea, edita, publica posts
2      | Comentador  | Solo puede ver y comentar
3      | Admin       | Control total, puede hacer cualquier cosa
```

#### **Tabla: permisoxrole** (¿Qué permisos tiene cada rol?)
```
roleid | permisoid | fechaactualizacion | otorgadopor | deleted
-------|-----------|-------------------|------------|--------
1      | 1         | 2024-01-15        | 3          | false
1      | 2         | 2024-01-15        | 3          | false
1      | 3         | 2024-01-15        | 3          | false
1      | 4         | 2024-01-15        | 3          | false
3      | 1         | 2024-01-01        | NULL       | false
3      | 2         | 2024-01-01        | NULL       | false
3      | 3         | 2024-01-01        | NULL       | false
3      | 4         | 2024-01-01        | NULL       | false
3      | 5         | 2024-01-01        | NULL       | false
2      | 4         | 2024-01-15        | 3          | false
```

#### **Tabla: usuarios**
```
usuarioid | nombre
-----------|----------
1          | Juan
2          | María
3          | Carlos
```

#### **Tabla: usuariosxrole** (¿Qué roles tiene cada usuario?)
```
usuarioid | roleid | fechaactualizacion | incluidopor | deleted
-----------|--------|-------------------|------------|--------
1          | 1      | 2024-01-15        | 3          | false
2          | 1      | 2024-01-20        | 3          | false
3          | 3      | 2024-01-01        | NULL       | false
```

#### **Query: ¿Qué puede hacer Juan (usuarioid = 1)?**
```sql
SELECT DISTINCT p.permisoid, p.nombre, p.descripcion
FROM permisos p
JOIN permisoxrole pr USING(permisoid)
JOIN usuariosxrole ur USING(roleid)
WHERE ur.usuarioid = 1
  AND ur.deleted = FALSE
  AND pr.deleted = FALSE
  AND p.deleted = FALSE;
```

#### **Resultado:**
```
permisoid | nombre         | descripcion
-----------|----------------|-------------------------------------
1          | create_post    | Puede crear posts
2          | edit_post      | Puede editar posts
3          | delete_post    | Puede borrar posts
4          | view_analytics | Puede ver estadísticas
```

---

### 8. Diagrama ER (Entity-Relationship)

```
┌─────────────────────────────────────────────────────────────────┐
│                         USUARIOS                                 │
├─────────────────────────────────────────────────────────────────┤
│ PK  usuarioid (INT)                                             │
│     nombre (VARCHAR)                                            │
│     email (VARCHAR)                                             │
│     fechacreacion (TIMESTAMP)                                   │
└─────────────────────────────────────────────────────────────────┘
            │                               │
            │ 1:M                           │ 1:M
            ↓                               ↓
┌──────────────────────────┐   ┌──────────────────────────────────┐
│   USUARIOSXROLE          │   │   PERMISOSXUSUARIO (OPCIONAL)    │
├──────────────────────────┤   ├──────────────────────────────────┤
│ FK  usuarioid            │   │ FK  usuarioid                    │
│ FK  roleid               │   │ FK  permisoid                    │
│     fechaactualizacion   │   │     fechaactualizacion           │
│ FK  incluidopor          │   │ FK  asignadopor                  │
│     deleted (BOOLEAN)    │   │     deleted (BOOLEAN)            │
└──────────────────────────┘   └──────────────────────────────────┘
            │                               │
            │ M:1                           │ M:1
            ↓                               ↓
┌──────────────────────────┐   ┌──────────────────────────────────┐
│  ROLESDEUSUARIO          │   │      PERMISOS                    │
├──────────────────────────┤   ├──────────────────────────────────┤
│ PK  roleid               │   │ PK  permisoid                    │
│     nombre               │   │     nombre                       │
│     descripcion          │   │     descripcion                  │
│     fechacreacion        │   │     deleted (BOOLEAN)            │
│     deleted (BOOLEAN)    │   └──────────────────────────────────┘
└──────────────────────────┘            │
            │                           │ M:1
            │ M:M                       │
            └───────────────────────────┘
                        │
                        ↓
            ┌──────────────────────────┐
            │    PERMISOXROLE          │
            ├──────────────────────────┤
            │ FK  permisoid            │
            │ FK  roleid               │
            │     fechaactualizacion   │
            │ FK  otorgadopor          │
            │     deleted (BOOLEAN)    │
            └──────────────────────────┘
```

---

### 9. Implementación en PostgreSQL (Script de creación)

```sql
-- Tabla base: Usuarios
CREATE TABLE usuarios (
    usuarioid SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    fechacreacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Tabla: Roles de usuario
CREATE TABLE rolesdeusuario (
    roleid SERIAL PRIMARY KEY,
    nombre VARCHAR(80) NOT NULL UNIQUE,
    descripcion VARCHAR(150),
    fechacreacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    usuariocreador INTEGER REFERENCES usuarios(usuarioid),
    deleted BOOLEAN NOT NULL DEFAULT FALSE
);

-- Tabla: Permisos (acciones atómicas)
CREATE TABLE permisos (
    permisoid SERIAL PRIMARY KEY,
    nombre VARCHAR(80) NOT NULL UNIQUE,
    descripcion VARCHAR(150),
    deleted BOOLEAN NOT NULL DEFAULT FALSE
);

-- Tabla puente: Usuarios X Roles (M:M)
CREATE TABLE usuariosxrole (
    usuarioid INTEGER REFERENCES usuarios(usuarioid) ON DELETE CASCADE,
    roleid INTEGER REFERENCES rolesdeusuario(roleid) ON DELETE CASCADE,
    fechaactualizacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    incluidopor INTEGER REFERENCES usuarios(usuarioid),
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    PRIMARY KEY (usuarioid, roleid)
);

-- Tabla puente: Permisos X Roles (M:M)
CREATE TABLE permisoxrole (
    permisoid INTEGER REFERENCES permisos(permisoid) ON DELETE CASCADE,
    roleid INTEGER REFERENCES rolesdeusuario(roleid) ON DELETE CASCADE,
    fechaactualizacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    otorgadopor INTEGER REFERENCES usuarios(usuarioid),
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    PRIMARY KEY (permisoid, roleid)
);

-- Tabla puente: Permisos X Usuarios (M:M, OPCIONAL para permisos directos)
CREATE TABLE permisosxusuario (
    usuarioid INTEGER REFERENCES usuarios(usuarioid) ON DELETE CASCADE,
    permisoid INTEGER REFERENCES permisos(permisoid) ON DELETE CASCADE,
    fechaactualizacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    asignadopor INTEGER REFERENCES usuarios(usuarioid),
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    PRIMARY KEY (usuarioid, permisoid)
);

-- Índices para optimizar búsquedas
CREATE INDEX idx_usuariosxrole_usuarioid ON usuariosxrole(usuarioid);
CREATE INDEX idx_permisoxrole_roleid ON permisoxrole(roleid);
CREATE INDEX idx_permisosxusuario_usuarioid ON permisosxusuario(usuarioid);
```

#### **Insertar datos de ejemplo:**

```sql
-- Insertar usuarios
INSERT INTO usuarios (nombre, email) VALUES
('Carlos', 'carlos@example.com'),
('Juan', 'juan@example.com'),
('María', 'maria@example.com');

-- Insertar permisos
INSERT INTO permisos (nombre, descripcion) VALUES
('create_post', 'Puede crear posts'),
('edit_post', 'Puede editar posts'),
('delete_post', 'Puede borrar posts'),
('view_analytics', 'Puede ver estadísticas'),
('manage_users', 'Puede gestionar usuarios');

-- Insertar roles
INSERT INTO rolesdeusuario (nombre, descripcion, usuariocreador) VALUES
('Editor', 'Crea, edita, publica posts', 1),
('Comentador', 'Solo puede ver y comentar', 1),
('Admin', 'Control total', NULL);

-- Asignar permisos a roles
INSERT INTO permisoxrole (permisoid, roleid, otorgadopor) VALUES
(1, 1, 1), -- Editor: create_post
(2, 1, 1), -- Editor: edit_post
(3, 1, 1), -- Editor: delete_post
(4, 1, 1), -- Editor: view_analytics
(1, 3, NULL), -- Admin: create_post
(2, 3, NULL), -- Admin: edit_post
(3, 3, NULL), -- Admin: delete_post
(4, 3, NULL), -- Admin: view_analytics
(5, 3, NULL), -- Admin: manage_users
(4, 2, 1); -- Comentador: view_analytics

-- Asignar roles a usuarios
INSERT INTO usuariosxrole (usuarioid, roleid, incluidopor) VALUES
(2, 1, 1), -- Juan es Editor (asignado por Carlos)
(3, 1, 1), -- María es Editor (asignada por Carlos)
(1, 3, NULL); -- Carlos es Admin (inicial)
```

#### **Consultia: Obtener todos los permisos de un usuario (incluyendo roles y permisos directos):**

```sql
-- Opción 1: Solo permisos por roles
SELECT DISTINCT p.nombre, p.descripcion
FROM permisos p
JOIN permisoxrole pr USING(permisoid)
JOIN usuariosxrole ur USING(roleid)
WHERE ur.usuarioid = 2  -- Juan
  AND ur.deleted = FALSE
  AND pr.deleted = FALSE
  AND p.deleted = FALSE;

-- Opción 2: Permisos por roles + permisos directos (UNION)
SELECT DISTINCT p.nombre, p.descripcion
FROM permisos p
JOIN permisoxrole pr USING(permisoid)
JOIN usuariosxrole ur USING(roleid)
WHERE ur.usuarioid = 2
  AND ur.deleted = FALSE
  AND pr.deleted = FALSE
  AND p.deleted = FALSE
UNION
SELECT DISTINCT p.nombre, p.descripcion
FROM permisos p
JOIN permisosxusuario pu USING(permisoid)
WHERE pu.usuarioid = 2
  AND pu.deleted = FALSE
  AND p.deleted = FALSE;
```

### 9.1 Tablas y atributos comunes para implementar RBAC correctamente

Estas son las tablas mas comunes (y sus atributos recomendados) para aplicar RBAC en sistemas reales:

| Tabla | Proposito | Atributos comunes recomendados |
|------|-----------|-------------------------------|
| `usuarios` | Identidad base del actor del sistema | `usuarioid` (PK), `nombre`, `email` (UNIQUE), `estado` (activo/inactivo), `fechacreacion`, `fechaultimoacceso` |
| `rolesdeusuario` | Agrupar permisos por responsabilidad | `roleid` (PK), `nombre` (UNIQUE), `descripcion`, `nivel` (opcional), `fechacreacion`, `usuariocreador`, `deleted` |
| `permisos` | Acciones atomicas del sistema | `permisoid` (PK), `codigo` (UNIQUE, ej. `post.edit`), `nombre`, `descripcion`, `modulo`, `deleted` |
| `usuariosxrole` | Relacion M:M usuario-rol | `usuarioid` (FK), `roleid` (FK), `fechaactualizacion`, `incluidopor`, `deleted`, PK compuesta (`usuarioid`,`roleid`) |
| `permisoxrole` | Relacion M:M rol-permiso | `roleid` (FK), `permisoid` (FK), `fechaactualizacion`, `otorgadopor`, `deleted`, PK compuesta (`permisoid`,`roleid`) |
| `permisosxusuario` (opcional) | Excepciones puntuales por usuario | `usuarioid` (FK), `permisoid` (FK), `fechaactualizacion`, `asignadopor`, `motivo` (opcional), `deleted` |
| `audit_rbac` (opcional) | Trazabilidad de cambios de seguridad | `auditid` (PK), `entidad`, `entidadid`, `accion`, `valor_anterior` (JSONB), `valor_nuevo` (JSONB), `hecho_por`, `fechaevento` |

Checklist minimo de calidad para estas tablas:
1. PK compuesta en todas las tablas puente.
2. `UNIQUE` para nombres/codigos de roles y permisos.
3. Campos de auditoria (`*_por`, timestamps).
4. Politica de soft-delete en roles/permisos/relaciones.
5. Indices sobre FKs de tablas puente para consultas de autorizacion.

---

### 10. Recursos para profundizar

#### **📹 YouTube (Español e Inglés)**

| Canal/Video | Enfoque | Recomendación |
|-------------|---------|------------------|
| **Juan Pablo Gómez** (Español) | RBAC paso a paso, SQL | Busca "RBAC modelo relacional" |
| **Code with Aaroush** (Inglés) | User Roles and Permissions, SQL | Busca "User Roles and Permissions in Database Design" |
| **Traversy Media** (Inglés) | Authentication & Authorization conceptos | Busca "User Authentication & Authorization" |
| **Mouredev** (Español) | Seguridad en BD y permisos | https://www.youtube.com/c/MoureDev (busca "bases de datos seguridad") |
| **Fernando Herrera** (Español) | Bases de datos escalables | Busca "Diseño de bases de datos" en su canal |

#### **📚 Documentación oficial**

- **PostgreSQL Docs:** Role-based access control
  - https://www.postgresql.org/docs/current/sql-grant.html
  - https://www.postgresql.org/docs/current/user-manag.html

- **SQL Standard:** Conceptos de roles y privilegios
  - https://www.postgresql.org/docs/current/sql-createrole.html

#### **📄 Artículos y blogs**

- **Medium:** "Designing a User Permissions System" - Busca RBAC design patterns
- **Auth0 Blog:** Role-Based Access Control (RBAC) vs Attribute-Based Access Control (ABAC)
  - https://auth0.com/blog/role-based-access-control-rbac-and-role-based-access-control-lists-racl/
- **Dev.to:** Busca "Database Design" + "RBAC" - Comunidad de desarrolladores

#### **📖 Libros recomendados**

- **"Designing Data-Intensive Applications"** (Martin Kleppmann)
  - Capítulos sobre integridad de datos y control de acceso
- **"Database Design Manual"** (Lightstone, et al.)
  - Análisis profundo de patrones de diseño

#### **💡 Conceptos relacionados a profundizar**

1. **RBAC vs ABAC** — Role-Based vs Attribute-Based Access Control
   - RBAC es simple y escalable (lo que vimos aquí)
   - ABAC es más flexible (controla permisos por atributos del usuario, objeto, etc.)

2. **Soft delete** — Por qué marcar como "deleted" en lugar de borrar físicamente
   - Mantiene auditoría histórica
   - Permite recuperar datos
   - Evita cascadas de eliminación accidental

3. **Auditoría y logging** — Rastrear quién hizo qué y cuándo
   - Usar campos como `asignadopor`, `otorgadopor`, `fechaactualizacion`
   - Crear tablas de auditoría (log de cambios)

4. **Principio de menor privilegio** — Dar solo los permisos necesarios
   - Los usuarios no deberían tener "Admin" por defecto
   - Los permisos deben ser explícitos, no implícitos

5. **Herencia de permisos** — Roles que heredan de otros roles
   - Más avanzado: crear jerarquía de roles
   - "Moderador" hereda de "Usuario", plus permisos adicionales

---

### 11. Resumen: Por qué este patrón es crítico para sistemas escalables

| Aspecto | Resultado |
|--------|-----------|
| **Flexibilidad** | Cambias políticas sin tocar tablas de usuarios ni compilar la app |
| **Auditoría** | Sabes exactamente quién hizo qué y cuándo (si guardas `asignadopor` y timestamps) |
| **Escalabilidad** | 10 usuarios o 10M, el diseño es el mismo. Las queries crecen linealmente. |
| **Mantenibilidad** | Nuevo permiso = 1 INSERT en `permisos`, no recompilación de código |
| **Seguridad** | Implementas "menor privilegio" naturalmente. Roles son documentados. |
| **Rendimiento** | Queries normalizadas. Índices eficaces. Sin columnas booleanas innecesarias. |
| **Evolución** | A medida que crece tu sistema, el patrón sigue siendo válido sin cambios |

---

## Conclusión

**User Permissions (RBAC)** no es solo una forma de guardar quién puede hacer qué. Es un **pilar arquitectónico** que permite:

- Que el sistema crezca sin reescribir lógica de seguridad
- Que los cambios en políticas sean cambios de datos, no de código
- Que audites quién accedió y cuándo
- Que protijas tu base de datos con constraints y PK bien pensadas

Es uno de los patrones **más comunes y más importantes** que verás en cualquier sistema profesional.

---

## PATRÓN: VARIABLE COLUMNS (Atributos Variables)

### 1. Qué problema soluciona

Este patrón resuelve un problema muy comun en sistemas reales: no todas las entidades del mismo tipo tienen exactamente los mismos campos.

Ejemplo tipico:
- Tienes una tabla de productos.
- Un celular necesita `ram`, `almacenamiento`, `bateria`.
- Un zapato necesita `talla`, `material`, `color`.
- Una laptop necesita `cpu`, `gpu`, `ram`, `peso`.

Si intentas meter todo eso como columnas fijas en una sola tabla, terminas con:
- Muchas columnas `NULL`.
- Esquema dificil de mantener.
- Cambios constantes con `ALTER TABLE`.

El patron Variable Columns permite guardar atributos que cambian por tipo, sin romper el modelo principal.

---

### 2. Cuándo usarlo

Usalo cuando:
- Los atributos cambian entre subtipos de la misma entidad.
- El negocio agrega atributos nuevos con frecuencia.
- Necesitas flexibilidad sin rediseñar el schema cada semana.

No lo uses cuando:
- Tu dominio es estable y bien definido.
- Puedes modelar correctamente con columnas fijas y normalizacion.
- Requieres reglas muy estrictas por atributo en SQL puro (tipos, constraints complejos, FK por atributo).

---

### 3. Aporte al diseño

Sin patron (todo en una sola tabla):

```text
productos
├── productoid
├── nombre
├── precio
├── talla
├── material
├── color
├── ram
├── cpu
├── gpu
├── bateria
└── ... (docenas de columnas)
```

Problemas:
- Tabla enorme y poco legible.
- Alta cantidad de `NULL`.
- Cada nuevo atributo implica migracion.

Con Variable Columns tienes 2 enfoques principales:

1. EAV (Entity-Attribute-Value)
2. JSONB (documento flexible dentro de PostgreSQL)

---

### 4. Mantenibilidad

Este patron mejora mantenibilidad porque separa:
- Datos nucleares (siempre presentes): `nombre`, `precio`, `sku`, etc.
- Datos variables (segun tipo): `ram`, `talla`, `voltaje`, etc.

Que ganas:
- Menos migraciones de esquema.
- Evolucion mas rapida del producto.
- Menos impacto sobre el resto del sistema cuando aparecen atributos nuevos.

Riesgo a controlar:
- Si no defines reglas claras, cada equipo guarda atributos con nombres distintos (`ram`, `RAM`, `memory_gb`) y el sistema se vuelve inconsistente.

---

### 5. Escalabilidad

Escala bien cuando:
- Tienes catalogos grandes y heterogeneos.
- No quieres bloquear releases por cambios de esquema.

Puede escalar mal si:
- Buscas/filtras por atributos variables sin indices.
- Usas EAV sin convenciones de tipos.
- Guardas todo en JSONB sin estrategia de consultas.

Regla practica para escalar:
- Atributos frecuentes en filtros -> indice.
- Atributos criticos del negocio -> columna fija.
- Atributos raros o experimentales -> variable columns.

---

### 6. Problemas comunes que soluciona

1. Evita tablas gigantes llenas de columnas vacias.
2. Evita migraciones constantes por cada atributo nuevo.
3. Permite soportar muchos tipos de entidad con un nucleo comun.
4. Reduce acoplamiento entre cambios de producto y cambios de esquema.

---

### 7. Errores comunes

| Error | Que pasa | Por que esta mal |
|-------|----------|------------------|
| Guardar todo como texto en EAV | Comparaciones y validaciones pobres | Pierdes tipos y reglas de negocio |
| No definir catalogo de atributos | Cada dev nombra distinto | Datos inconsistentes |
| Usar JSONB para todo | Pierdes integridad relacional | Dificil auditar y validar |
| No indexar claves consultadas | Consultas lentas | Escalabilidad deficiente |
| Meter datos criticos en variable columns | Reglas de negocio fragiles | Lo critico debe ir en columnas formales |

---

### 8. Variante A: EAV (Entity-Attribute-Value)

Modelo:
- `productos`: entidad principal.
- `atributos`: catalogo de atributos permitidos.
- `producto_atributo_valor`: valor por producto y atributo.

#### Diagrama detallado (EAV)

```text
                                ┌────────────────────────────────────┐
                                │             PRODUCTOS              │
                                ├────────────────────────────────────┤
                                │ PK  productoid BIGSERIAL          │
                                │     nombre TEXT                   │
                                │     categoria TEXT                │
                                │     precio NUMERIC(12,2)          │
                                │     fechacreacion TIMESTAMP       │
                                └────────────────────────────────────┘
                                              │ 1
                                              │
                                              │ N
                                ┌────────────────────────────────────┐
                                │     PRODUCTO_ATRIBUTO_VALOR        │
                                ├────────────────────────────────────┤
                                │ PK/FK productoid BIGINT            │
                                │ PK/FK atributoid BIGINT            │
                                │     valor_texto TEXT               │
                                │     valor_int BIGINT               │
                                │     valor_numeric NUMERIC(14,4)    │
                                │     valor_bool BOOLEAN             │
                                └────────────────────────────────────┘
                                              │ N
                                              │
                                              │ 1
                                ┌────────────────────────────────────┐
                                │             ATRIBUTOS              │
                                ├────────────────────────────────────┤
                                │ PK  atributoid BIGSERIAL          │
                                │ UQ  codigo TEXT                   │
                                │     nombre TEXT                   │
                                │     tipodato TEXT                 │
                                │     CHECK tipodato in (...)       │
                                └────────────────────────────────────┘

Flujo de consulta comun:
1) Buscar producto(s) en PRODUCTOS.
2) Expandir atributos via PRODUCTO_ATRIBUTO_VALOR.
3) Interpretar tipo de dato segun ATRIBUTOS.tipodato.

Indices clave:
- idx_pav_atributo (atributoid): acelera filtros por atributo.
- idx_pav_valor_int (valor_int): acelera filtros numericos sobre atributos enteros.
```

#### SQL en PostgreSQL

```sql
CREATE TABLE productos (
  productoid BIGSERIAL PRIMARY KEY,
  nombre TEXT NOT NULL,
  categoria TEXT NOT NULL,
  precio NUMERIC(12,2) NOT NULL,
  fechacreacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE atributos (
  atributoid BIGSERIAL PRIMARY KEY,
  codigo TEXT NOT NULL UNIQUE,          -- ej: ram_gb, talla_eu
  nombre TEXT NOT NULL,
  tipodato TEXT NOT NULL CHECK (tipodato IN ('text','int','numeric','bool'))
);

CREATE TABLE producto_atributo_valor (
  productoid BIGINT NOT NULL REFERENCES productos(productoid) ON DELETE CASCADE,
  atributoid BIGINT NOT NULL REFERENCES atributos(atributoid) ON DELETE RESTRICT,
  valor_texto TEXT,
  valor_int BIGINT,
  valor_numeric NUMERIC(14,4),
  valor_bool BOOLEAN,
  PRIMARY KEY (productoid, atributoid)
);

CREATE INDEX idx_pav_atributo ON producto_atributo_valor(atributoid);
CREATE INDEX idx_pav_valor_int ON producto_atributo_valor(valor_int);
```

Nota de diseno:
- EAV da flexibilidad alta, pero complica consultas y validacion.
- Funciona mejor si hay gobernanza fuerte de atributos.

---

### 9. Variante B: JSONB (muy usada en PostgreSQL)

Modelo:
- Tabla principal con columnas estables.
- Columna `atributos JSONB` para campos variables.

#### Diagrama detallado (JSONB)

```text
┌───────────────────────────────────────────────────────────────────────┐
│                               PRODUCTOS                               │
├───────────────────────────────────────────────────────────────────────┤
│ PK  productoid BIGSERIAL                                             │
│     nombre TEXT                                                      │
│     categoria TEXT                                                   │
│     precio NUMERIC(12,2)                                             │
│     atributos JSONB  <-- bloque flexible por tipo                    │
│     fechacreacion TIMESTAMP                                          │
│     CHECK jsonb_typeof(atributos) = 'object'                         │
└───────────────────────────────────────────────────────────────────────┘

Ejemplo visual de atributos JSONB por categoria:

Laptop:
{
  "ram_gb": 16,
  "cpu": "i7",
  "gpu": "rtx4060"
}

Zapato:
{
  "talla_eu": 42,
  "material": "mesh",
  "color": "negro"
}

Telefono:
{
  "ram_gb": 8,
  "almacenamiento_gb": 256,
  "bateria_mah": 5000
}

Estrategia de indices:
- GIN sobre atributos: busquedas generales por claves/valores JSONB.
- Indice funcional por clave critica (ej. ram_gb): filtros de alto trafico.
```

#### SQL en PostgreSQL

```sql
CREATE TABLE productos (
  productoid BIGSERIAL PRIMARY KEY,
  nombre TEXT NOT NULL,
  categoria TEXT NOT NULL,
  precio NUMERIC(12,2) NOT NULL,
  atributos JSONB NOT NULL DEFAULT '{}'::jsonb,
  fechacreacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT chk_atributos_objeto CHECK (jsonb_typeof(atributos) = 'object')
);

-- Indice general para busquedas por claves/valores en JSONB
CREATE INDEX idx_productos_atributos_gin ON productos USING GIN (atributos);

-- Indice especifico para una clave muy consultada (ejemplo: ram_gb)
CREATE INDEX idx_productos_ram_gb
ON productos (((atributos ->> 'ram_gb')::int));
```

#### Inserts de ejemplo

```sql
INSERT INTO productos (nombre, categoria, precio, atributos) VALUES
('Laptop Pro 14', 'laptop', 1299.99, '{"ram_gb":16, "cpu":"i7", "gpu":"rtx4060"}'),
('Zapato Runner X', 'zapato', 89.50, '{"talla_eu":42, "material":"mesh", "color":"negro"}'),
('Telefono Nova', 'celular', 799.00, '{"ram_gb":8, "almacenamiento_gb":256, "bateria_mah":5000}');
```

#### Consultas utiles

```sql
-- Productos con ram >= 16
SELECT productoid, nombre
FROM productos
WHERE categoria IN ('laptop','celular')
  AND (atributos ->> 'ram_gb')::int >= 16;

-- Productos de color negro
SELECT productoid, nombre
FROM productos
WHERE atributos ->> 'color' = 'negro';

-- Productos que tengan la clave bateria_mah
SELECT productoid, nombre
FROM productos
WHERE atributos ? 'bateria_mah';
```

---

### 10. Cual variante elegir

Usa EAV cuando:
- Necesitas control fuerte de catalogo de atributos.
- Quieres metadata de atributos (unidad, validaciones, descripcion, versionado).
- Aceptas consultas mas complejas.

Usa JSONB cuando:
- Quieres simplicidad operativa en PostgreSQL.
- Necesitas flexibilidad alta con consultas razonables.
- Puedes definir convenciones de nombres y tipos en aplicacion + checks SQL.

Practica recomendada en sistemas estables a largo plazo:
- Modelo hibrido.
- Core en columnas fijas.
- Extension en JSONB o EAV para variabilidad.
- Si un atributo variable se vuelve clave del negocio, promuevelo a columna formal.

---

### 11. Heuristica de diseno (para pensar como arquitecto)

Antes de elegir Variable Columns, preguntate:
1. Este atributo aplica a casi todos los registros?
2. Se filtra/ordena mucho en consultas criticas?
3. Necesita restricciones fuertes en base de datos?
4. Cambiara frecuentemente en el negocio?

Decision rapida:
- Si 1, 2 y 3 son SI -> columna fija.
- Si 4 es SI y 1/2/3 son NO -> variable columns.

### 11.1 Tablas y atributos comunes para Variable Columns (EAV y JSONB)

Para que este patron funcione bien en produccion, normalmente se modela este set de tablas/atributos:

| Tabla | Proposito | Atributos comunes recomendados |
|------|-----------|-------------------------------|
| `productos` (entidad principal) | Guardar datos nucleares y estables | `productoid` (PK), `sku` (UNIQUE), `nombre`, `categoria`, `precio`, `estado`, `fechacreacion`, `updated_at` |
| `atributos` (catalogo EAV) | Gobernar atributos permitidos | `atributoid` (PK), `codigo` (UNIQUE), `nombre`, `tipodato`, `unidad` (opcional), `requerido` (bool), `activo` |
| `producto_atributo_valor` (EAV) | Persistir valor por producto y atributo | `productoid` (FK), `atributoid` (FK), `valor_texto`, `valor_int`, `valor_numeric`, `valor_bool`, `valor_fecha` (opcional), PK compuesta |
| `categoria_atributo` (opcional) | Definir que atributos aplican por categoria | `categoriaid`/`categoria`, `atributoid`, `obligatorio`, `orden_visualizacion` |
| `producto_revision` (opcional) | Auditar cambios en atributos variables | `revisionid`, `productoid`, `atributos_previos` (JSONB), `atributos_nuevos` (JSONB), `modificado_por`, `fechaevento` |

Si eliges variante JSONB, la estructura comun queda asi:

| Tabla | Proposito | Atributos comunes recomendados |
|------|-----------|-------------------------------|
| `productos` + `atributos JSONB` | Modelo hibrido (core + extension flexible) | `productoid`, `sku`, `nombre`, `categoria`, `precio`, `atributos` (JSONB), `version_atributos` (opcional), `updated_at` |
| `schema_atributos` (opcional) | Validar convenciones por categoria | `schemaid`, `categoria`, `reglas_json` (JSONSchema simplificado), `version`, `activo` |

Checklist minimo:
1. Convencion unica de nombres (`snake_case`) para claves variables.
2. Indices sobre atributos consultados frecuentemente (GIN + funcionales).
3. Separar atributos criticos en columnas fijas.
4. Estrategia de validacion (catalogo EAV o reglas JSONB por categoria).

---

### 12. Recursos para profundizar

#### YouTube

- https://www.youtube.com/results?search_query=PostgreSQL+JSONB+tutorial
- https://www.youtube.com/results?search_query=EAV+database+design+pattern
- https://www.youtube.com/results?search_query=Database+design+flexible+schema
- Canal recomendado: `Hussein Nasser` (database architecture).
- Canal recomendado: `ByteByteGo` (system design y modelado de datos).

#### Documentacion oficial

- PostgreSQL JSON Types: https://www.postgresql.org/docs/current/datatype-json.html
- PostgreSQL JSON Functions and Operators: https://www.postgresql.org/docs/current/functions-json.html
- PostgreSQL GIN Indexes: https://www.postgresql.org/docs/current/gin.html

#### Material de estudio

- Martin Kleppmann, Designing Data-Intensive Applications (integridad y evolucion de esquemas).
- Articulos sobre `schema evolution` y `hybrid relational-document modeling`.

---

### 13. Resumen ejecutivo

Variable Columns aporta mucho al diseno cuando necesitas flexibilidad controlada:
- Mejora mantenibilidad en dominios cambiantes.
- Permite escalar catalogos heterogeneos.
- Reduce friccion al agregar nuevos atributos.

Pero la clave profesional es esta:
- No usarlo como excusa para modelar todo sin reglas.
- Mantener un nucleo relacional fuerte.
- Definir convenciones, validaciones e indices.

Si lo aplicas asi, es un patron muy util para construir sistemas estables a largo plazo.

---

## PATRÓN: MASTER-DETAIL / CATALOGS-ACTIONS

### 1. Qué problema soluciona

Este patron soluciona un problema central en sistemas de negocio: separar datos de referencia estables (catalogos) de eventos transaccionales (acciones).

Ejemplo clasico:
- Catalogos: clientes, productos, metodos de pago, estados.
- Acciones: pedido, factura, pago, devolucion.

Si mezclas todo en una sola tabla grande:
- Duplicas datos de catalogo en cada accion.
- Pierdes integridad.
- Mantener historico y auditar se vuelve dificil.

Master-detail organiza el sistema asi:
- Master (cabecera): una transaccion o documento.
- Detail (lineas): elementos asociados a esa transaccion.
- Catalogs-actions: los details apuntan a catalogos por FK.

---

### 2. Cuándo usarlo

Usalo cuando modelas procesos como:
- Ventas (pedido + lineas de pedido).
- Compras (orden de compra + lineas).
- Facturacion (factura + items).
- Inventario (movimiento + detalle de productos).
- Cualquier flujo donde una operacion agrupa multiples elementos.

No lo uses cuando:
- El proceso no tiene cabecera/detalle real.
- Solo necesitas una entidad simple sin lineas asociadas.

---

### 3. Aporte al diseño

Sin patron (mal diseño):
- Tabla unica con columnas repetidas de cliente, producto, precio, estado.
- Si el pedido tiene 5 productos, repites 5 veces los datos del cliente.

Con patron (buen diseño):
- `pedido` (master) guarda contexto general de la transaccion.
- `pedido_detalle` (detail) guarda cada item.
- `productos`, `clientes`, `estados_pedido`, `metodos_pago` son catalogos reutilizables.

Resultado:
- Menos duplicidad.
- Mejor integridad.
- Consultas mas claras.
- Evolucion del sistema mas limpia.

---

### 4. Mantenibilidad

Por que mejora mantenibilidad:
- Cambios en catalogos no rompen historial de acciones.
- Puedes agregar campos al master sin tocar la logica del detail (y viceversa).
- Las reglas de negocio se ubican en el lugar correcto:
  - Cabecera: estado general, fecha, cliente.
  - Detalle: producto, cantidad, precio unitario.

Buena practica:
- Guardar en el detail datos que deben quedar historicos (ej. `precio_unitario` al momento de compra), aunque el catalogo cambie despues.

---

### 5. Escalabilidad

Escala mejor porque:
- Las tablas de catalogo cambian poco.
- Las tablas de acciones crecen mucho, pero estan normalizadas y indexadas.
- Puedes particionar acciones por fecha en sistemas de alto volumen.

Indices recomendados:
- FK en detail (`pedidoid`, `productoid`).
- Fecha en master (`fechapedido`).
- Estado en master (`estadoid`) para dashboards.

---

### 6. Problemas comunes que soluciona

1. Evita duplicar informacion de cliente/producto en cada fila.
2. Permite trazabilidad de una transaccion completa.
3. Facilita auditoria del flujo (quien creo, cuando, estado actual).
4. Mejora consistencia de datos con FK a catalogos.

---

### 7. Errores comunes

| Error | Que pasa | Por que esta mal |
|-------|----------|------------------|
| No separar master y detail | Filas ambiguas y repetidas | Rompe legibilidad y reglas de negocio |
| No guardar precio historico en detail | Reportes historicos incorrectos | Precio de catalogo cambia y distorsiona ventas pasadas |
| Borrar catalogos en cascada sin control | Pierdes historial transaccional | En acciones historicas debe conservarse referencia o snapshot |
| No indexar FK del detail | Consultas lentas al crecer | Carga alta en joins mas comunes |
| Usar estado como texto libre | Inconsistencias (`pendiente`, `Pendiente`, `PEND`) | Debe venir de catalogo de estados |

---

### 8. Diagrama detallado del patron

```text
CATALOGOS (estables)                         ACCIONES (crecimiento alto)

┌───────────────────────────┐
│         CLIENTES          │
├───────────────────────────┤
│ PK  clienteid             │
│     nombre                │
│     email                 │
└───────────────────────────┘
             │ 1
             │
             │ N
┌───────────────────────────┐      1      ┌────────────────────────────┐      N      ┌────────────────────────────┐
│      ESTADOS_PEDIDO       │────────────▶│           PEDIDO            │────────────▶│       PEDIDO_DETALLE        │
├───────────────────────────┤             ├────────────────────────────┤             ├────────────────────────────┤
│ PK  estadoid              │             │ PK  pedidoid                │             │ PK  pedidodetalleid         │
│ UQ  codigo                │             │ FK  clienteid               │             │ FK  pedidoid                │
│     nombre                │             │ FK  estadoid                │             │ FK  productoid              │
└───────────────────────────┘             │ FK  metodopagoid            │             │     cantidad                │
                                          │     fechapedido             │             │     precio_unitario         │
┌───────────────────────────┐             │     total                   │             │     subtotal                │
│      METODOS_PAGO         │────────────▶│     creado_por              │             └────────────────────────────┘
├───────────────────────────┤      1      └────────────────────────────┘                           │ N
│ PK  metodopagoid          │                                                                         │
│ UQ  codigo                │                                                                         │ 1
│     nombre                │                                                          ┌───────────────────────────┐
└───────────────────────────┘                                                          │         PRODUCTOS         │
                                                                                       ├───────────────────────────┤
                                                                                       │ PK  productoid            │
                                                                                       │ UQ  sku                   │
                                                                                       │     nombre                │
                                                                                       │     precio_lista          │
                                                                                       └───────────────────────────┘

Lectura del patron:
1) CATALOGOS definen valores autorizados y reutilizables.
2) PEDIDO (master) representa una transaccion.
3) PEDIDO_DETALLE (detail) representa cada item de esa transaccion.
4) DETAIL referencia catalogos (productos) y mantiene valores historicos (precio_unitario).
```

---

### 9. SQL en PostgreSQL (modelo base)

```sql
CREATE TABLE clientes (
  clienteid BIGSERIAL PRIMARY KEY,
  nombre TEXT NOT NULL,
  email TEXT UNIQUE
);

CREATE TABLE productos (
  productoid BIGSERIAL PRIMARY KEY,
  sku TEXT NOT NULL UNIQUE,
  nombre TEXT NOT NULL,
  precio_lista NUMERIC(12,2) NOT NULL CHECK (precio_lista >= 0)
);

CREATE TABLE estados_pedido (
  estadoid SMALLSERIAL PRIMARY KEY,
  codigo TEXT NOT NULL UNIQUE,
  nombre TEXT NOT NULL
);

CREATE TABLE metodos_pago (
  metodopagoid SMALLSERIAL PRIMARY KEY,
  codigo TEXT NOT NULL UNIQUE,
  nombre TEXT NOT NULL
);

CREATE TABLE pedido (
  pedidoid BIGSERIAL PRIMARY KEY,
  clienteid BIGINT NOT NULL REFERENCES clientes(clienteid),
  estadoid SMALLINT NOT NULL REFERENCES estados_pedido(estadoid),
  metodopagoid SMALLINT NOT NULL REFERENCES metodos_pago(metodopagoid),
  fechapedido TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  total NUMERIC(14,2) NOT NULL DEFAULT 0,
  creado_por BIGINT
);

CREATE TABLE pedido_detalle (
  pedidodetalleid BIGSERIAL PRIMARY KEY,
  pedidoid BIGINT NOT NULL REFERENCES pedido(pedidoid) ON DELETE CASCADE,
  productoid BIGINT NOT NULL REFERENCES productos(productoid),
  cantidad NUMERIC(12,2) NOT NULL CHECK (cantidad > 0),
  precio_unitario NUMERIC(12,2) NOT NULL CHECK (precio_unitario >= 0),
  subtotal NUMERIC(14,2) NOT NULL CHECK (subtotal >= 0)
);

CREATE INDEX idx_pedido_cliente ON pedido(clienteid);
CREATE INDEX idx_pedido_estado ON pedido(estadoid);
CREATE INDEX idx_pedido_fecha ON pedido(fechapedido);
CREATE INDEX idx_pedido_detalle_pedido ON pedido_detalle(pedidoid);
CREATE INDEX idx_pedido_detalle_producto ON pedido_detalle(productoid);
```

---

### 10. Ejemplo de uso

```sql
-- Catalogos
INSERT INTO estados_pedido (codigo, nombre) VALUES
('PEND', 'Pendiente'),
('PAG', 'Pagado'),
('ENV', 'Enviado');

INSERT INTO metodos_pago (codigo, nombre) VALUES
('CARD', 'Tarjeta'),
('CASH', 'Efectivo');

INSERT INTO clientes (nombre, email) VALUES
('Ana Perez', 'ana@example.com');

INSERT INTO productos (sku, nombre, precio_lista) VALUES
('SKU-001', 'Mouse Gamer', 35.00),
('SKU-002', 'Teclado Mecanico', 79.00);

-- Crear pedido master
INSERT INTO pedido (clienteid, estadoid, metodopagoid, total, creado_por)
VALUES (1, 1, 1, 149.00, 10);

-- Crear details
INSERT INTO pedido_detalle (pedidoid, productoid, cantidad, precio_unitario, subtotal)
VALUES
(1, 1, 2, 35.00, 70.00),
(1, 2, 1, 79.00, 79.00);
```

Consulta de lectura de master + detail:

```sql
SELECT
  p.pedidoid,
  p.fechapedido,
  c.nombre AS cliente,
  ep.nombre AS estado,
  mp.nombre AS metodo_pago,
  d.pedidodetalleid,
  pr.sku,
  pr.nombre AS producto,
  d.cantidad,
  d.precio_unitario,
  d.subtotal
FROM pedido p
JOIN clientes c ON c.clienteid = p.clienteid
JOIN estados_pedido ep ON ep.estadoid = p.estadoid
JOIN metodos_pago mp ON mp.metodopagoid = p.metodopagoid
JOIN pedido_detalle d ON d.pedidoid = p.pedidoid
JOIN productos pr ON pr.productoid = d.productoid
WHERE p.pedidoid = 1;
```

---

### 11. Guía para decidir cuándo es mejor usarlo

Este patron es mejor cuando:
1. Tu proceso tiene una entidad principal con multiples lineas.
2. Necesitas historial confiable de transacciones.
3. Requieres reportes por periodo, cliente, producto o estado.
4. Quieres escalar sin duplicar datos en cada accion.

Si tu sistema crece a largo plazo, este patron casi siempre aparece en algun modulo critico (ventas, compras, facturacion, inventario, soporte).

### 11.1 Tablas y atributos comunes para Master-Detail / Catalogs-Actions

Las implementaciones exitosas de este patron suelen incluir estas tablas:

| Tabla | Proposito | Atributos comunes recomendados |
|------|-----------|-------------------------------|
| `pedido` (master) | Cabecera transaccional | `pedidoid` (PK), `clienteid` (FK), `estadoid` (FK), `metodopagoid` (FK), `fechapedido`, `moneda`, `subtotal`, `impuesto`, `descuento`, `total`, `creado_por`, `created_at`, `updated_at` |
| `pedido_detalle` (detail) | Lineas de la transaccion | `pedidodetalleid` (PK), `pedidoid` (FK), `linea_numero`, `productoid` (FK), `descripcion_item` (snapshot), `cantidad`, `precio_unitario`, `impuesto_linea`, `descuento_linea`, `subtotal` |
| `clientes` (catalogo) | Maestro de clientes | `clienteid` (PK), `codigo_cliente` (UNIQUE), `nombre`, `email`, `estado`, `fechacreacion` |
| `productos` (catalogo) | Maestro de productos/servicios | `productoid` (PK), `sku` (UNIQUE), `nombre`, `precio_lista`, `activo`, `categoriaid` |
| `estados_pedido` (catalogo) | Control de workflow del documento | `estadoid` (PK), `codigo` (UNIQUE), `nombre`, `es_final` (bool), `orden_flujo` |
| `metodos_pago` (catalogo) | Estandarizar forma de pago | `metodopagoid` (PK), `codigo` (UNIQUE), `nombre`, `requiere_autorizacion` |
| `pedido_evento` (opcional) | Historial de cambios de estado | `eventoid` (PK), `pedidoid` (FK), `estado_origen`, `estado_destino`, `comentario`, `cambiado_por`, `fechaevento` |

Checklist minimo:
1. Snapshot de precio en detalle para preservar historico.
2. Totales consistentes en master (idealmente con validaciones/transacciones).
3. Catalogos con codigos unicos y estados controlados.
4. Indices en `pedido_detalle.pedidoid`, `pedido.clienteid`, `pedido.fechapedido`.

### 11.2 Diferencia entre Master-Detail y Transactions/Movements

Aunque ambos aparecen en sistemas transaccionales, no resuelven exactamente lo mismo:

| Aspecto | Master-Detail | Transactions / Movements |
|---------|---------------|--------------------------|
| Enfoque principal | Modelar un documento de negocio con cabecera y lineas (pedido + items) | Modelar cambios de estado como eventos inmutables (entradas/salidas/ajustes) |
| Pregunta que responde | "Que contiene esta transaccion?" | "Como cambio este saldo/stock en el tiempo?" |
| Granularidad | Documento compuesto | Evento de cambio |
| Mutabilidad esperada | Puede cambiar estado del documento (pendiente, pagado, enviado) | El evento historico no se reescribe; se compensa con otro movimiento |
| Uso tipico | Ventas, compras, facturas, ordenes | Inventario, ledger financiero, puntos, cuotas |
| Riesgo si se usa solo este patron | Pierdes trazabilidad fina de variaciones de saldo | Pierdes contexto documental (cliente, condiciones comerciales, etc.) |

Regla practica:
1. Si necesitas representar un "documento" con multiples lineas y catalogos -> Master-Detail.
2. Si necesitas trazabilidad forense del cambio numerico en el tiempo -> Transactions/Movements.
3. En sistemas grandes, normalmente se usan juntos: el documento (master-detail) origina uno o varios movimientos.

---

### 12. Recursos para profundizar

#### YouTube

- https://www.youtube.com/results?search_query=database+master+detail+design
- https://www.youtube.com/results?search_query=sales+order+header+detail+database+design
- https://www.youtube.com/results?search_query=normalization+order+and+order+items
- Canal recomendado: `Hussein Nasser` (database design y trade-offs).
- Canal recomendado: `ByteByteGo` (arquitectura y escalabilidad).

#### Documentacion y guias

- PostgreSQL Foreign Keys: https://www.postgresql.org/docs/current/ddl-constraints.html
- PostgreSQL Indexes: https://www.postgresql.org/docs/current/indexes.html
- PostgreSQL EXPLAIN (para analizar rendimiento): https://www.postgresql.org/docs/current/using-explain.html

#### Material extra

- Buscar articulos de `Order Header / Order Line pattern`.
- Buscar `transactional data modeling` y `reference data modeling`.

---

### 13. Resumen ejecutivo

Master-detail / Catalogs-actions es un patron base para sistemas empresariales porque:
- Separa datos estables de datos transaccionales.
- Mejora integridad y trazabilidad.
- Facilita mantenimiento y evolucion del sistema.
- Escala mejor en volumen y reporteria.

Es una de las estructuras mas importantes para disenar sistemas robustos, mantenibles y estables a largo plazo.

---

## PATRÓN: TRANSACTIONS / MOVEMENTS

### 1. Qué problema soluciona

Este patron modela cambios de estado como eventos inmutables.

Problema real que evita:
- Sobreescribir un valor final (por ejemplo stock = 25) sin saber como llego ahi.
- No poder auditar errores ni reconstruir historia.

Idea central:
- El estado actual se calcula o se valida usando movimientos.
- Nunca se pierde el rastro de entradas, salidas, ajustes o transferencias.

### 2. Cuándo usarlo

Usalo cuando hay "flujo" de cantidades o dinero:
1. Inventario (entradas/salidas/ajustes/traslados).
2. Cuentas financieras (creditos/debitos).
3. Puntos de fidelidad.
4. Consumos de cuota o licencias.

### 3. Aporte al diseño

1. Evita inconsistencias por updates directos al saldo.
2. Permite trazabilidad completa por documento origen.
3. Se integra bien con auditoria y conciliacion.

### 4. Mantenibilidad

1. Los bugs se investigan con historial, no con suposiciones.
2. Las correcciones se hacen con movimientos compensatorios.
3. El codigo de negocio se simplifica: registrar evento, no reescribir pasado.

### 5. Escalabilidad

1. La tabla de movimientos crece mucho, por lo que requiere buen indexado.
2. Se recomienda particion por fecha para volumen alto.
3. Reportes por periodo se vuelven naturales.

### 6. Problemas comunes que soluciona

1. "No me cuadra el stock" -> puedes reconstruir secuencia de movimientos.
2. "No se quien hizo el ajuste" -> guardas usuario, fecha y referencia.
3. "No puedo revertir" -> agregas un movimiento inverso.

### 7. Errores comunes

| Error | Que ocurre | Correccion |
|------|------------|------------|
| Actualizar saldo directo sin movimiento | Pierdes historia | Registrar cada cambio como movimiento |
| No usar tipo de movimiento catalogado | Datos sucios | Catalogo de tipos + CHECK |
| No guardar referencia del documento origen | Trazabilidad incompleta | Agregar `tipodoc` y `docid` |
| Movimientos negativos y positivos sin regla clara | Bugs de negocio | Convencion estricta por `tipomov` |

### 8. Diagrama detallado

```text
┌────────────────────────────┐        1:N        ┌────────────────────────────┐
│       BODEGA_PRODUCTO      │──────────────────▶│      MOV_INV_CABECERA      │
├────────────────────────────┤                   ├────────────────────────────┤
│ PK bodegaproductoid        │                   │ PK movid                   │
│ FK bodegaid                │                   │ FK tipomovid               │
│ FK productoid              │                   │    fecha                   │
│    stock_actual            │                   │    tipodoc                 │
└────────────────────────────┘                   │    docid                   │
                                                 │    creado_por              │
┌────────────────────────────┐                   └────────────────────────────┘
│      TIPO_MOVIMIENTO       │                              │ 1:N
├────────────────────────────┤                              ▼
│ PK tipomovid               │                   ┌────────────────────────────┐
│ UQ codigo (ENT/SAL/AJ)     │                   │      MOV_INV_DETALLE       │
│    afecta_signo (+/-)      │                   ├────────────────────────────┤
└────────────────────────────┘                   │ PK movdetalleid            │
                                                 │ FK movid                   │
                                                 │ FK productoid              │
                                                 │    cantidad                │
                                                 │    costo_unitario          │
                                                 └────────────────────────────┘
```

### 9. PostgreSQL base

```sql
CREATE TABLE tipo_movimiento (
  tipomovid SMALLSERIAL PRIMARY KEY,
  codigo TEXT NOT NULL UNIQUE,
  afecta_signo SMALLINT NOT NULL CHECK (afecta_signo IN (-1, 1))
);

CREATE TABLE mov_inv_cabecera (
  movid BIGSERIAL PRIMARY KEY,
  tipomovid SMALLINT NOT NULL REFERENCES tipo_movimiento(tipomovid),
  fecha TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  tipodoc TEXT,
  docid TEXT,
  creado_por BIGINT
);

CREATE TABLE mov_inv_detalle (
  movdetalleid BIGSERIAL PRIMARY KEY,
  movid BIGINT NOT NULL REFERENCES mov_inv_cabecera(movid) ON DELETE CASCADE,
  productoid BIGINT NOT NULL REFERENCES productos(productoid),
  cantidad NUMERIC(14,2) NOT NULL CHECK (cantidad > 0),
  costo_unitario NUMERIC(14,4) NOT NULL CHECK (costo_unitario >= 0)
);

CREATE INDEX idx_mov_cab_fecha ON mov_inv_cabecera(fecha);
CREATE INDEX idx_mov_det_producto ON mov_inv_detalle(productoid);
```

### 9.1 Tablas y atributos comunes para Transactions / Movements

Para este patron, la base comun incluye estas entidades:

| Tabla | Proposito | Atributos comunes recomendados |
|------|-----------|-------------------------------|
| `tipo_movimiento` | Normalizar tipos y signo de impacto | `tipomovid` (PK), `codigo` (UNIQUE: ENT/SAL/AJ/TRF), `nombre`, `afecta_signo` (-1/1), `activo` |
| `mov_inv_cabecera` | Contexto del evento transaccional | `movid` (PK), `tipomovid` (FK), `fecha`, `tipodoc`, `docid`, `estado` (aplicado/anulado), `bodega_origenid` (opcional), `bodega_destinoid` (opcional), `creado_por` |
| `mov_inv_detalle` | Detalle por item afectado | `movdetalleid` (PK), `movid` (FK), `productoid` (FK), `cantidad`, `costo_unitario`, `lote` (opcional), `fecha_vencimiento` (opcional) |
| `kardex_producto` (opcional) | Vista materializada/historica por producto | `kardexid`, `productoid`, `fecha`, `movid`, `entrada`, `salida`, `saldo`, `costo_promedio` |
| `movimiento_reversion` (opcional) | Enlazar movimiento original y compensatorio | `reversionid`, `movid_origen`, `movid_reversa`, `motivo`, `revertido_por`, `fechaevento` |

Checklist minimo:
1. Nunca actualizar stock sin registrar movimiento.
2. Referenciar documento origen (`tipodoc`, `docid`) para auditoria.
3. Definir estados de movimiento (aplicado, pendiente, anulado).
4. En transferencias, usar un solo evento de negocio con origen/destino.

### 9.2 Diferencia entre Transactions/Movements y Master-Detail

Transactions y Master-Detail se complementan, pero tienen objetivos distintos:

| Aspecto | Transactions / Movements | Master-Detail |
|---------|--------------------------|---------------|
| Modelo base | Registro de eventos de cambio (ledger) | Documento de negocio con cabecera y detalle |
| Pregunta que responde | "De donde viene este saldo/stock actual?" | "Que se negocio en esta operacion?" |
| Integridad clave | Inmutabilidad historica y signo de movimiento | Coherencia documental (totales, lineas, estados) |
| Patrón de correccion | Movimiento compensatorio | Ajuste de documento o nota de credito/debito |
| Estructura comun | `tipo_movimiento`, `mov_cabecera`, `mov_detalle` | `pedido`/`factura` + `detalle` + catalogos |
| Consulta fuerte | Sumas acumuladas por periodo, producto, cuenta | Consulta de documento completo y sus lineas |

Como decidir rapido:
1. Si el centro de tu problema es "historial de cambios" -> Transactions.
2. Si el centro de tu problema es "estructura del documento comercial" -> Master-Detail.
3. Si tu dominio tiene ambos (ej. ventas con inventario), implementa Master-Detail para la operacion y Transactions para el impacto en saldo/stock.

### 10. Recursos

#### YouTube (enlaces directos)
- https://www.youtube.com/results?search_query=inventory+movement+database+design
- https://www.youtube.com/results?search_query=event+sourcing+inventory+ledger

#### Documentacion
- PostgreSQL constraints: https://www.postgresql.org/docs/current/ddl-constraints.html
- PostgreSQL partitioning: https://www.postgresql.org/docs/current/ddl-partitioning.html

### 11. Ejemplo concreto

Caso: inventario de una tienda.

```sql
-- Tipos de movimiento
INSERT INTO tipo_movimiento (codigo, afecta_signo) VALUES
('ENT', 1),
('SAL', -1),
('AJ', 1);

-- Entrada de 50 unidades
INSERT INTO mov_inv_cabecera (tipomovid, tipodoc, docid, creado_por)
VALUES (1, 'OC', 'OC-2026-001', 7);

INSERT INTO mov_inv_detalle (movid, productoid, cantidad, costo_unitario)
VALUES (1, 10, 50, 12.75);

-- Salida de 8 unidades
INSERT INTO mov_inv_cabecera (tipomovid, tipodoc, docid, creado_por)
VALUES (2, 'FAC', 'FAC-2026-090', 7);

INSERT INTO mov_inv_detalle (movid, productoid, cantidad, costo_unitario)
VALUES (2, 10, 8, 12.75);
```

Consulta para reconstruir stock teorico por producto:

```sql
SELECT
  d.productoid,
  SUM(d.cantidad * tm.afecta_signo) AS stock_teorico
FROM mov_inv_detalle d
JOIN mov_inv_cabecera c ON c.movid = d.movid
JOIN tipo_movimiento tm ON tm.tipomovid = c.tipomovid
WHERE d.productoid = 10
GROUP BY d.productoid;
```

### 12. Conceptos relacionados

1. Ledger.
2. Event sourcing.
3. Idempotencia en eventos.
4. Reversion por movimientos compensatorios.

### 13. Resumen ejecutivo

Transactions/Movements es clave para sistemas estables porque registra el cambio como evento auditable. Eso mejora depuracion, control de errores y escalabilidad de reportes historicos.

---

## PATRÓN: BALANCES

### 1. Qué problema soluciona

Permite tener lectura rapida del saldo actual sin perder exactitud historica.

Modelo recomendado:
1. Ledger (movimientos) = fuente de verdad.
2. Balance materializado = lectura rapida.

### 2. Cuándo usarlo

1. Wallets o cuentas.
2. Saldos de inventario por bodega.
3. Puntos de usuario.
4. Cualquier pantalla que exija "saldo ahora" en milisegundos.

### 3. Aporte al diseño

1. Evita recalculos costosos por cada consulta.
2. Conserva auditabilidad de origen.
3. Hace posible conciliacion periodica.

### 4. Mantenibilidad

1. Si un balance no cuadra, se recalcula desde ledger.
2. Puedes reconstruir snapshots tras incidentes.
3. Facilita separar responsabilidades entre write-model y read-model.

### 5. Escalabilidad

1. El ledger puede crecer muchisimo sin afectar lectura del saldo actual.
2. Balance sirve como cache persistente.
3. Con millones de movimientos, reduces carga de agregaciones repetidas.

### 6. Problemas comunes que soluciona

1. Consultas lentas por sumatorias grandes.
2. Inconsistencias por actualizar saldo en varios puntos de la app.
3. Falta de prueba forense de como se obtuvo un saldo.

### 7. Errores comunes

| Error | Que ocurre | Correccion |
|------|------------|------------|
| Guardar solo saldo final | No hay auditoria | Guardar ledger siempre |
| No usar transaccion al insertar movimiento + actualizar balance | Race conditions | Hacer ambas operaciones en una misma transaccion |
| Permitir monto negativo sin reglas | Saldo corrupto | CHECK + reglas por tipo de movimiento |

### 8. Diagrama detallado

```text
┌────────────────────────────┐        1:N        ┌────────────────────────────┐
│          CUENTAS           │──────────────────▶│      CUENTA_MOVIMIENTO     │
├────────────────────────────┤                   ├────────────────────────────┤
│ PK cuentaid                │                   │ PK movid                   │
│    titular                 │                   │ FK cuentaid                │
│    estado                  │                   │    fecha                   │
└────────────────────────────┘                   │    tipo (CRED/DEB/AJ)     │
                                                 │    monto                   │
                                                 │    referencia              │
                                                 └────────────────────────────┘
                                                              │
                                                              │ recalculo/actualizacion
                                                              ▼
                                                 ┌────────────────────────────┐
                                                 │       CUENTA_BALANCE       │
                                                 ├────────────────────────────┤
                                                 │ PK/FK cuentaid             │
                                                 │    saldo_actual            │
                                                 │    fecha_actualizacion     │
                                                 └────────────────────────────┘
```

### 9. PostgreSQL base

```sql
CREATE TABLE cuenta_movimiento (
  movid BIGSERIAL PRIMARY KEY,
  cuentaid BIGINT NOT NULL,
  fecha TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  tipo TEXT NOT NULL CHECK (tipo IN ('CRED','DEB','AJ')),
  monto NUMERIC(14,2) NOT NULL CHECK (monto > 0),
  referencia TEXT
);

CREATE TABLE cuenta_balance (
  cuentaid BIGINT PRIMARY KEY,
  saldo_actual NUMERIC(14,2) NOT NULL,
  fecha_actualizacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_cm_cuenta_fecha ON cuenta_movimiento(cuentaid, fecha);
```

### 9.1 Tablas y atributos comunes para Balances

Un diseno robusto de balances normalmente incorpora:

| Tabla | Proposito | Atributos comunes recomendados |
|------|-----------|-------------------------------|
| `cuentas` | Maestro de cuenta/bolsillo | `cuentaid` (PK), `titularid`, `tipo_cuenta`, `moneda`, `estado`, `fechaapertura`, `limite_credito` (opcional) |
| `cuenta_movimiento` (ledger) | Fuente de verdad del saldo | `movid` (PK), `cuentaid` (FK), `fecha`, `tipo` (CRED/DEB/AJ), `monto`, `moneda`, `referencia`, `external_id` (idempotencia), `creado_por` |
| `cuenta_balance` (read-model) | Saldo actual de consulta rapida | `cuentaid` (PK/FK), `saldo_actual`, `saldo_bloqueado` (opcional), `fecha_actualizacion`, `version_balance` |
| `conciliacion_balance` (opcional) | Verificar consistencia ledger vs balance | `conciliacionid`, `cuentaid`, `saldo_ledger`, `saldo_balance`, `diferencia`, `fecha_conciliacion`, `estado` |
| `reserva_fondos` (opcional) | Retenciones/autorizaciones temporales | `reservaid`, `cuentaid`, `monto_reserva`, `motivo`, `estado`, `expira_en` |

Checklist minimo:
1. Movimiento + actualizacion de balance en la misma transaccion.
2. Idempotencia para evitar duplicados por reintentos.
3. Campo de version para detectar conflictos concurrentes.
4. Proceso de conciliacion periodico automatizado.

### 10. Recursos

#### YouTube (enlaces directos)
- https://www.youtube.com/results?search_query=ledger+vs+balance+database+design
- https://www.youtube.com/results?search_query=double+entry+bookkeeping+database+design

#### Documentacion
- PostgreSQL transactions: https://www.postgresql.org/docs/current/tutorial-transactions.html
- PostgreSQL locking: https://www.postgresql.org/docs/current/explicit-locking.html

### 11. Ejemplo concreto

Caso: wallet de usuario.

```sql
-- Credito de recarga
INSERT INTO cuenta_movimiento (cuentaid, tipo, monto, referencia)
VALUES (101, 'CRED', 100.00, 'RECARGA APP');

-- Debito por compra
INSERT INTO cuenta_movimiento (cuentaid, tipo, monto, referencia)
VALUES (101, 'DEB', 35.50, 'COMPRA #A-1002');
```

Consulta de conciliacion de saldo:

```sql
SELECT
  cm.cuentaid,
  SUM(CASE WHEN cm.tipo = 'CRED' THEN cm.monto ELSE -cm.monto END) AS saldo_ledger,
  cb.saldo_actual
FROM cuenta_movimiento cm
JOIN cuenta_balance cb ON cb.cuentaid = cm.cuentaid
WHERE cm.cuentaid = 101
GROUP BY cm.cuentaid, cb.saldo_actual;
```

### 12. Conceptos relacionados

1. Conciliacion.
2. Double-entry bookkeeping.
3. Bloqueo pesimista/optimista.
4. Read model vs write model.

### 13. Resumen ejecutivo

Balances funciona mejor cuando se combina con ledger: rendimiento para lectura diaria y exactitud historica para auditoria y correccion de incidentes.

---

## PATRÓN: LOGS

### 1. Qué problema soluciona

Provee evidencia de eventos del sistema para seguridad, auditoria y diagnostico.

### 2. Cuándo usarlo

Siempre. En particular si tienes:
1. Login/autorizacion.
2. Operaciones sensibles.
3. Integraciones externas.
4. Requisitos de compliance.

### 3. Aporte al diseño

1. Separa observabilidad de modelo transaccional.
2. Permite investigar incidentes sin tocar tablas de negocio.
3. Soporta alertas y metricas.

### 4. Mantenibilidad

1. Reduce tiempo de debugging.
2. Ayuda a detectar errores recurrentes por modulo.
3. Permite evolucionar la aplicacion con telemetria historica.

### 5. Escalabilidad

1. Logs crecen rapidamente, por eso conviene particionar y retener por politicas.
2. Indices por fecha, nivel y entidad mejoran busqueda.
3. JSONB en payload da flexibilidad controlada.

### 6. Problemas comunes que soluciona

1. "No sabemos que paso en produccion".
2. "No hay evidencia de quien ejecuto accion critica".
3. "No podemos correlacionar errores entre servicios".

### 7. Errores comunes

| Error | Que ocurre | Correccion |
|------|------------|------------|
| Guardar PII sensible sin control | Riesgo legal | Anonimizar/mask payload |
| No definir retencion | Costo y lentitud | Politica de archivado/purga |
| Usar logs como estado oficial | Inconsistencia | Logs solo como evidencia |

### 8. Diagrama detallado

```text
┌────────────────────────────┐         1:N        ┌──────────────────────────────────────────────┐
│          USUARIOS          │───────────────────▶│                    APP_LOG                    │
├────────────────────────────┤                    ├──────────────────────────────────────────────┤
│ PK usuarioid               │                    │ PK logid                                     │
│    email                   │                    │ FK usuarioid (nullable)                      │
└────────────────────────────┘                    │    nivel (INFO/WARN/ERROR/SECURITY)         │
                                                  │    modulo                                    │
                                                  │    accion                                    │
                                                  │    entidad                                   │
                                                  │    entidadid                                 │
                                                  │    traceid                                   │
                                                  │    payload_json                              │
                                                  │    ip                                        │
                                                  │    fechaevento                               │
                                                  └──────────────────────────────────────────────┘
```

### 9. PostgreSQL base

```sql
CREATE TABLE app_log (
  logid BIGSERIAL PRIMARY KEY,
  usuarioid BIGINT,
  nivel TEXT NOT NULL CHECK (nivel IN ('INFO','WARN','ERROR','SECURITY')),
  modulo TEXT NOT NULL,
  accion TEXT NOT NULL,
  entidad TEXT,
  entidadid TEXT,
  traceid TEXT,
  payload_json JSONB,
  ip INET,
  fechaevento TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_log_fecha ON app_log(fechaevento);
CREATE INDEX idx_log_nivel_fecha ON app_log(nivel, fechaevento);
CREATE INDEX idx_log_entidad ON app_log(entidad, entidadid);
```

### 9.1 Tablas y atributos comunes para Logs

En arquitecturas productivas, el patron de logs suele usar esta estructura:

| Tabla | Proposito | Atributos comunes recomendados |
|------|-----------|-------------------------------|
| `app_log` | Registro principal de eventos de aplicacion | `logid` (PK), `fechaevento`, `nivel`, `modulo`, `accion`, `mensaje` (opcional), `usuarioid`, `traceid`, `spanid` (opcional), `payload_json`, `ip`, `user_agent` |
| `log_nivel_catalogo` (opcional) | Catalogo de severidad | `nivelid`, `codigo` (INFO/WARN/ERROR/SECURITY), `descripcion`, `prioridad_alerta` |
| `log_error_stack` (opcional) | Detalle tecnico de excepciones | `errorid`, `logid` (FK), `error_type`, `stacktrace`, `causa_raiz` |
| `log_retencion_politica` (opcional) | Regla de retencion por tipo | `politicaid`, `nivel`, `modulo`, `dias_retencion`, `accion` (archivar/purgar) |

Checklist minimo:
1. Incluir `traceid` para correlacion entre servicios.
2. Evitar PII sensible sin mascara/tokenizacion.
3. Definir nivel y modulo con vocabulario controlado.
4. Indexar por fecha, nivel y entidad para busquedas operativas.

### 10. Recursos

#### YouTube (enlaces directos)
- https://www.youtube.com/results?search_query=audit+log+database+design
- https://www.youtube.com/results?search_query=application+logging+best+practices+database

#### Documentacion
- PostgreSQL JSONB ops: https://www.postgresql.org/docs/current/functions-json.html
- PostgreSQL indexing: https://www.postgresql.org/docs/current/indexes.html

### 11. Ejemplo concreto

Caso: registrar intento fallido de login.

```sql
INSERT INTO app_log (
  usuarioid, nivel, modulo, accion, entidad, entidadid, traceid, payload_json, ip
) VALUES (
  44,
  'SECURITY',
  'auth',
  'login_failed',
  'usuario',
  '44',
  'trc-9f8a',
  '{"reason":"wrong_password","attempt":3}',
  '190.10.1.77'
);
```

Consulta de incidentes de seguridad en 24h:

```sql
SELECT fechaevento, usuarioid, accion, payload_json
FROM app_log
WHERE nivel = 'SECURITY'
  AND fechaevento >= NOW() - INTERVAL '24 hours'
ORDER BY fechaevento DESC;
```

### 12. Conceptos relacionados

1. Observabilidad.
2. Correlation ID / Trace ID.
3. Retention policy.
4. Data masking de PII.

### 13. Resumen ejecutivo

Logs no reemplaza tablas de negocio, pero es indispensable para operar sistemas robustos: acelera debugging, seguridad y auditoria.

---

## PATRÓN: CURRENT AND HISTORICAL

### 1. Qué problema soluciona

Permite tener dos vistas del mismo dato:
1. Estado actual (rapido para operacion diaria).
2. Historial completo (auditoria y analitica temporal).

### 2. Cuándo usarlo

1. Cuando debes responder "como estaba esto en fecha X".
2. Cuando hay cambios frecuentes en atributos clave.
3. Cuando hay requerimientos legales de trazabilidad.

### 3. Aporte al diseño

1. Optimiza consultas operativas sobre tabla current.
2. Preserva timeline sin sobrecargar tabla principal.
3. Reduce riesgo de perder historia por updates.

### 4. Mantenibilidad

1. Cambios de negocio no destruyen datos anteriores.
2. Permite debug de regresiones funcionales.
3. Facilita migraciones porque hay snapshots historicos.

### 5. Escalabilidad

1. `current` se mantiene pequena y rapida.
2. `historical` crece mucho, ideal para particion por fecha.
3. Puedes mover historico frio a storage mas barato.

### 6. Problemas comunes que soluciona

1. No poder reconstruir estado pasado.
2. No saber quien cambio un dato sensible.
3. Reportes inconsistentes entre periodos.

### 7. Errores comunes

| Error | Que ocurre | Correccion |
|------|------------|------------|
| Actualizar current sin persistir version previa | Se pierde historia | Insertar snapshot en historical antes de update |
| No almacenar vigencia (`valido_desde`, `valido_hasta`) | No hay temporalidad formal | Guardar intervalos y controlar solapes |
| Mezclar current e historical en la misma tabla sin estrategia | Consultas lentas y confusas | Separar tablas con objetivos claros |

### 8. Diagrama detallado

```text
┌────────────────────────────┐         1:N        ┌────────────────────────────┐
│       CLIENTE_CURRENT      │───────────────────▶│    CLIENTE_HISTORICAL      │
├────────────────────────────┤                    ├────────────────────────────┤
│ PK clienteid               │                    │ PK historialid             │
│    nombre_actual           │                    │ FK clienteid               │
│    categoria_actual        │                    │    nombre                  │
│    updated_at              │                    │    categoria               │
│    updated_by              │                    │    valido_desde            │
└────────────────────────────┘                    │    valido_hasta            │
                                                  │    cambiado_por            │
                                                  └────────────────────────────┘
```

### 9. PostgreSQL base

```sql
CREATE TABLE cliente_current (
  clienteid BIGINT PRIMARY KEY,
  nombre_actual TEXT NOT NULL,
  categoria_actual TEXT,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_by BIGINT
);

CREATE TABLE cliente_historical (
  historialid BIGSERIAL PRIMARY KEY,
  clienteid BIGINT NOT NULL,
  nombre TEXT NOT NULL,
  categoria TEXT,
  valido_desde TIMESTAMP NOT NULL,
  valido_hasta TIMESTAMP,
  cambiado_por BIGINT
);

CREATE INDEX idx_hist_cliente ON cliente_historical(clienteid);
CREATE INDEX idx_hist_vigencia ON cliente_historical(valido_desde, valido_hasta);
```

### 9.1 Tablas y atributos comunes para Current and Historical

Este patron normalmente se implementa con las siguientes tablas:

| Tabla | Proposito | Atributos comunes recomendados |
|------|-----------|-------------------------------|
| `cliente_current` (o entidad_current) | Estado vigente para lectura rapida | `clienteid` (PK), `nombre_actual`, `categoria_actual`, `estado_actual`, `updated_at`, `updated_by`, `version_row` |
| `cliente_historical` (o entidad_historical) | Historial completo de versiones | `historialid` (PK), `clienteid` (FK), `nombre`, `categoria`, `estado`, `valido_desde`, `valido_hasta`, `cambiado_por`, `motivo_cambio` |
| `cambio_entidad_evento` (opcional) | Evento de negocio asociado al cambio | `eventoid`, `clienteid`, `tipo_evento`, `origen`, `traceid`, `fechaevento` |
| `regla_vigencia` (opcional) | Gobernar no-solapamiento temporal | `reglaid`, `entidad`, `permite_solapamiento`, `estrategia_cierre` |

Checklist minimo:
1. Cerrar version previa antes de abrir nueva (`valido_hasta`).
2. Evitar solapamientos de vigencia por entidad.
3. Mantener `current` pequeno y optimizado.
4. Indexar historico por entidad y rango temporal.

### 10. Recursos

#### YouTube (enlaces directos)
- https://www.youtube.com/results?search_query=slowly+changing+dimension+type+2
- https://www.youtube.com/results?search_query=temporal+tables+database+design

#### Documentacion
- PostgreSQL range types: https://www.postgresql.org/docs/current/rangetypes.html
- PostgreSQL date/time: https://www.postgresql.org/docs/current/datatype-datetime.html

### 11. Ejemplo concreto

Caso: cambio de categoria de cliente.

```sql
-- Estado anterior a historico
INSERT INTO cliente_historical (
  clienteid, nombre, categoria, valido_desde, valido_hasta, cambiado_por
)
SELECT
  clienteid,
  nombre_actual,
  categoria_actual,
  updated_at,
  NOW(),
  12
FROM cliente_current
WHERE clienteid = 200;

-- Estado actual
UPDATE cliente_current
SET categoria_actual = 'gold',
    updated_at = NOW(),
    updated_by = 12
WHERE clienteid = 200;
```

Consulta "como estaba en una fecha":

```sql
SELECT nombre, categoria
FROM cliente_historical
WHERE clienteid = 200
  AND TIMESTAMP '2026-03-01 10:00:00' >= valido_desde
  AND (valido_hasta IS NULL OR TIMESTAMP '2026-03-01 10:00:00' < valido_hasta)
ORDER BY historialid DESC
LIMIT 1;
```

### 12. Conceptos relacionados

1. Slowly Changing Dimension Type 2.
2. Bitemporal data.
3. Time-travel queries.
4. Auditoria regulatoria.

### 13. Resumen ejecutivo

Current and Historical permite velocidad operativa sin renunciar a trazabilidad temporal, clave para sistemas con cambios frecuentes y exigencia de auditoria.

---

## PATRÓN: ADDRESSES

### 1. Qué problema soluciona

Normaliza direcciones como entidad separada y relacionable con distintas entidades del negocio.

### 2. Cuándo usarlo

1. Clientes con direccion de envio y facturacion.
2. Empresas con multiples sucursales.
3. Casos donde la direccion cambia en el tiempo y debe conservarse historial.

### 3. Aporte al diseño

1. Evita duplicidad de campos de direccion en muchas tablas.
2. Permite versionar direccion por vigencia.
3. Facilita estandarizacion para envios, geocoding y reportes.

### 4. Mantenibilidad

1. Cambios en formato de direccion impactan menos.
2. Puedes reutilizar catalogos de tipo de direccion.
3. Separa logica postal de la entidad principal.

### 5. Escalabilidad

1. Estructura preparada para multiples direcciones por entidad.
2. Consultas eficientes con indices por cliente y principalidad.
3. Soporta historico de direccion sin sobrescribir.

### 6. Problemas comunes que soluciona

1. "El cliente tiene solo una direccion" (falso en sistemas reales).
2. Inconsistencias entre direccion de envio y facturacion.
3. Falta de trazabilidad de direcciones antiguas.

### 7. Errores comunes

| Error | Que ocurre | Correccion |
|------|------------|------------|
| Guardar direccion como un solo texto | Mala calidad de datos | Separar componentes estructurados |
| No modelar tipo de direccion | Ambiguedad funcional | Catalogo `tipo_direccion` |
| Permitir varios `es_principal=true` sin control | Inconsistencia | Indice parcial unico por entidad y tipo |

### 8. Diagrama detallado

```text
┌────────────────────────────┐         1:N        ┌────────────────────────────┐
│          CLIENTES          │───────────────────▶│      CLIENTE_DIRECCION      │
├────────────────────────────┤                    ├────────────────────────────┤
│ PK clienteid               │                    │ PK clientedireccionid      │
│    nombre                  │                    │ FK clienteid               │
└────────────────────────────┘                    │ FK direccionid             │
                                                  │ FK tipodireccionid         │
┌────────────────────────────┐                    │    es_principal            │
│         DIRECCIONES        │◀───────────────────│    vigente_desde           │
├────────────────────────────┤                    │    vigente_hasta           │
│ PK direccionid             │                    └────────────────────────────┘
│    linea1                  │
│    linea2                  │                    ┌────────────────────────────┐
│    ciudad                  │                    │      TIPO_DIRECCION        │
│    provincia               │                    ├────────────────────────────┤
│    codigopostal            │                    │ PK tipodireccionid         │
│    pais                    │                    │ UQ codigo                  │
└────────────────────────────┘                    │    nombre                  │
                                                  └────────────────────────────┘
```

### 9. PostgreSQL base

```sql
CREATE TABLE direcciones (
  direccionid BIGSERIAL PRIMARY KEY,
  linea1 TEXT NOT NULL,
  linea2 TEXT,
  ciudad TEXT NOT NULL,
  provincia TEXT,
  codigopostal TEXT,
  pais TEXT NOT NULL
);

CREATE TABLE tipo_direccion (
  tipodireccionid SMALLSERIAL PRIMARY KEY,
  codigo TEXT NOT NULL UNIQUE,
  nombre TEXT NOT NULL
);

CREATE TABLE cliente_direccion (
  clientedireccionid BIGSERIAL PRIMARY KEY,
  clienteid BIGINT NOT NULL,
  direccionid BIGINT NOT NULL REFERENCES direcciones(direccionid),
  tipodireccionid SMALLINT NOT NULL REFERENCES tipo_direccion(tipodireccionid),
  es_principal BOOLEAN NOT NULL DEFAULT FALSE,
  vigente_desde TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  vigente_hasta TIMESTAMP
);

CREATE INDEX idx_cd_cliente ON cliente_direccion(clienteid);
```

### 9.1 Tablas y atributos comunes para Addresses

Para modelar direcciones de forma robusta, estas tablas son las mas comunes:

| Tabla | Proposito | Atributos comunes recomendados |
|------|-----------|-------------------------------|
| `direcciones` | Entidad reusable de direccion estructurada | `direccionid` (PK), `linea1`, `linea2`, `ciudad`, `provincia`, `codigopostal`, `pais`, `latitud` (opcional), `longitud` (opcional), `referencia` |
| `tipo_direccion` | Catalogo de contexto de uso | `tipodireccionid` (PK), `codigo` (UNIQUE: ENV/FAC/CASA/OFI), `nombre`, `descripcion`, `activo` |
| `cliente_direccion` (o entidad_direccion) | Relacionar direcciones a una entidad | `clientedireccionid` (PK), `clienteid` (FK), `direccionid` (FK), `tipodireccionid` (FK), `es_principal`, `vigente_desde`, `vigente_hasta`, `validada` (bool), `created_at` |
| `direccion_normalizada` (opcional) | Resultado de normalizacion postal | `normalizacionid`, `direccionid`, `formato_estandar`, `fuente`, `score_confianza`, `fecha_normalizacion` |
| `direccion_historial` (opcional) | Auditoria de cambios de domicilio | `historialid`, `clienteid`, `direccionid_anterior`, `direccionid_nueva`, `motivo`, `cambiado_por`, `fechaevento` |

Checklist minimo:
1. Separar direccion fisica de relacion con entidad.
2. Permitir multiples direcciones por cliente/contexto.
3. Regla para una principal por tipo y periodo.
4. Vigencia temporal para trazabilidad de mudanzas.

### 9.2 Extensiones espaciales (PostGIS) para coordenadas GEOMETRY y GEOGRAPHY

Cuando el sistema necesita trabajar con ubicacion real (mapas, rutas, distancia entre puntos), conviene extender PostgreSQL con PostGIS.

Que es PostGIS:
1. Es una extension oficial que agrega tipos espaciales, funciones y operadores geograficos a PostgreSQL.
2. Permite guardar puntos, lineas y poligonos con soporte de indices espaciales.
3. Habilita consultas como "dame las direcciones en un radio de 2 km" o "cual sucursal esta mas cerca".

Diferencia entre GEOMETRY y GEOGRAPHY:

| Tipo | Como interpreta los datos | Ventaja | Cuando conviene |
|------|---------------------------|---------|------------------|
| `GEOMETRY` | Plano cartesiano (x,y) segun SRID | Muy flexible y rapido para analisis geometrico | Mapas locales, operaciones topologicas, capas GIS complejas |
| `GEOGRAPHY` | Esferoide terrestre (lat/lon reales) | Distancias mas precisas en metros sobre la Tierra | Distancias globales, busquedas por radio en km/m |

Regla practica:
1. Si tu caso principal es "distancia real entre coordenadas GPS", empieza con `GEOGRAPHY(Point, 4326)`.
2. Si haras analisis espacial avanzado (intersecciones, buffers complejos, proyecciones), usa `GEOMETRY` y SRID adecuado.

Ejemplo conceptual (meramente conceptual):

Supongamos una app de entregas:
1. Cada direccion del cliente guarda `latitud` y `longitud`.
2. Cada repartidor reporta su posicion actual.
3. El sistema calcula el repartidor mas cercano a la direccion de envio.
4. Con eso reduce tiempos de entrega y costo operativo de rutas.

El flujo logico seria:
1. Guardar coordenadas de direcciones y repartidores.
2. Filtrar por radio maximo (ej. 5 km).
3. Ordenar por distancia ascendente.
4. Asignar el primero disponible.

Ejemplo del mismo caso en PostgreSQL + PostGIS:

```sql
-- 1) Habilitar extension espacial
CREATE EXTENSION IF NOT EXISTS postgis;

-- 2) Tabla de direcciones con punto geografico (lat/lon real)
CREATE TABLE direcciones_geo (
  direccionid BIGSERIAL PRIMARY KEY,
  linea1 TEXT NOT NULL,
  ciudad TEXT NOT NULL,
  pais TEXT NOT NULL,
  ubicacion GEOGRAPHY(POINT, 4326) NOT NULL
);

-- 3) Tabla de repartidores con posicion actual
CREATE TABLE repartidor_posicion (
  repartidorid BIGSERIAL PRIMARY KEY,
  nombre TEXT NOT NULL,
  posicion GEOGRAPHY(POINT, 4326) NOT NULL,
  actualizado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 4) Indices espaciales para consultas por cercania
CREATE INDEX idx_direcciones_geo_ubicacion
  ON direcciones_geo USING GIST (ubicacion);

CREATE INDEX idx_repartidor_posicion
  ON repartidor_posicion USING GIST (posicion);

-- 5) Insert conceptual (San Jose CR, coordenadas de ejemplo)
INSERT INTO direcciones_geo (linea1, ciudad, pais, ubicacion)
VALUES (
  'Calle 10 #20-30',
  'San Jose',
  'CR',
  ST_SetSRID(ST_MakePoint(-84.0907, 9.9281), 4326)::geography
);

INSERT INTO repartidor_posicion (nombre, posicion)
VALUES
('Repartidor A', ST_SetSRID(ST_MakePoint(-84.0910, 9.9300), 4326)::geography),
('Repartidor B', ST_SetSRID(ST_MakePoint(-84.1200, 9.9400), 4326)::geography);

-- 6) Repartidor mas cercano a una direccion (en metros)
SELECT
  r.repartidorid,
  r.nombre,
  ST_Distance(r.posicion, d.ubicacion) AS distancia_metros
FROM repartidor_posicion r
JOIN direcciones_geo d ON d.direccionid = 1
ORDER BY distancia_metros ASC
LIMIT 1;

-- 7) Direcciones dentro de un radio de 2000 metros de un punto
SELECT direccionid, linea1, ciudad
FROM direcciones_geo
WHERE ST_DWithin(
  ubicacion,
  ST_SetSRID(ST_MakePoint(-84.0907, 9.9281), 4326)::geography,
  2000
);
```

Notas de implementacion:
1. En `ST_MakePoint(longitud, latitud)` el orden correcto es longitud primero.
2. `4326` es el SRID de WGS84 (coordenadas GPS).
3. Para rendimiento espacial, usa indice `GIST` en columnas `GEOMETRY/GEOGRAPHY`.
4. Si ya tienes `latitud/longitud` en columnas separadas, puedes migrar gradualmente a un campo espacial.

### 10. Recursos

#### YouTube (enlaces directos)
- https://www.youtube.com/results?search_query=address+modeling+database+design
- https://www.youtube.com/results?search_query=shipping+billing+address+database+schema

#### Documentacion
- PostgreSQL partial indexes: https://www.postgresql.org/docs/current/indexes-partial.html
- PostgreSQL constraints: https://www.postgresql.org/docs/current/ddl-constraints.html

### 11. Ejemplo concreto

Caso: cliente con direccion de envio y facturacion.

```sql
INSERT INTO tipo_direccion (codigo, nombre) VALUES
('ENV', 'Envio'),
('FAC', 'Facturacion');

INSERT INTO direcciones (linea1, ciudad, provincia, codigopostal, pais)
VALUES
('Calle 10 #20-30', 'San Jose', 'San Jose', '10101', 'CR'),
('Av Central 55', 'Heredia', 'Heredia', '40101', 'CR');

INSERT INTO cliente_direccion (clienteid, direccionid, tipodireccionid, es_principal)
VALUES
(1, 1, 1, TRUE),
(1, 2, 2, TRUE);
```

Consulta de direccion principal de envio:

```sql
SELECT d.*
FROM cliente_direccion cd
JOIN direcciones d ON d.direccionid = cd.direccionid
JOIN tipo_direccion td ON td.tipodireccionid = cd.tipodireccionid
WHERE cd.clienteid = 1
  AND td.codigo = 'ENV'
  AND cd.es_principal = TRUE
  AND cd.vigente_hasta IS NULL;
```

### 12. Conceptos relacionados

1. Geocodificacion.
2. Normalizacion postal.
3. Direccion principal por contexto.
4. Vigencia temporal de domicilio.

### 13. Resumen ejecutivo

Addresses evita duplicidad y ambiguedad en ubicaciones, y habilita reglas reales de negocio como envio/facturacion con historico y principalidad.

---

## PATRÓN: CONTACT INFO

### 1. Qué problema soluciona

Modela contactos como coleccion versionable de canales, en lugar de un par de columnas fijas.

### 2. Cuándo usarlo

1. Clientes con varios telefonos/correos.
2. Necesidad de canal principal.
3. Requisitos de verificacion (OTP, email confirmado).
4. Canales nuevos en el tiempo (WhatsApp, Telegram, etc).

### 3. Aporte al diseño

1. Evita redisenar tabla de clientes por cada nuevo canal.
2. Soporta prioridad y vigencia por contacto.
3. Mejora integridad por tipo de contacto.

### 4. Mantenibilidad

1. Cambios de reglas de validacion se concentran en una zona.
2. Permite inactivar contactos sin borrarlos.
3. Facilita gobernanza de calidad de datos.

### 5. Escalabilidad

1. Escala naturalmente a N contactos por cliente.
2. Indices por cliente y tipo aceleran busquedas operativas.
3. Se integra con workflows de notificacion masiva.

### 6. Problemas comunes que soluciona

1. Perder contacto historico al reemplazar telefono.
2. No distinguir contacto principal de secundarios.
3. No saber si el canal fue verificado.

### 7. Errores comunes

| Error | Que ocurre | Correccion |
|------|------------|------------|
| Un solo email/telefono en tabla cliente | No soporta realidad de negocio | Tabla separada 1:N |
| Sin tipo de contacto catalogado | Mezcla de formatos | `tipo_contacto` normalizado |
| Sin marca de verificacion | Riesgo de notificar canal no valido | Campo `verificado` + fecha |

### 8. Diagrama detallado

```text
┌────────────────────────────┐         1:N        ┌────────────────────────────┐
│          CLIENTES          │───────────────────▶│      CONTACTO_CLIENTE      │
├────────────────────────────┤                    ├────────────────────────────┤
│ PK clienteid               │                    │ PK contactoid              │
│    nombre                  │                    │ FK clienteid               │
└────────────────────────────┘                    │ FK tipocontactoid          │
                                                  │    valor                   │
┌────────────────────────────┐                    │    es_principal            │
│       TIPO_CONTACTO        │◀───────────────────│    verificado              │
├────────────────────────────┤                    │    vigente_desde           │
│ PK tipocontactoid          │                    │    vigente_hasta           │
│ UQ codigo (EMAIL/TEL/WA)   │                    └────────────────────────────┘
│    nombre                  │
└────────────────────────────┘
```

### 9. PostgreSQL base

```sql
CREATE TABLE tipo_contacto (
  tipocontactoid SMALLSERIAL PRIMARY KEY,
  codigo TEXT NOT NULL UNIQUE,
  nombre TEXT NOT NULL
);

CREATE TABLE contacto_cliente (
  contactoid BIGSERIAL PRIMARY KEY,
  clienteid BIGINT NOT NULL,
  tipocontactoid SMALLINT NOT NULL REFERENCES tipo_contacto(tipocontactoid),
  valor TEXT NOT NULL,
  es_principal BOOLEAN NOT NULL DEFAULT FALSE,
  verificado BOOLEAN NOT NULL DEFAULT FALSE,
  fecha_verificacion TIMESTAMP,
  vigente_desde TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  vigente_hasta TIMESTAMP
);

CREATE INDEX idx_contacto_cliente ON contacto_cliente(clienteid);
CREATE INDEX idx_contacto_tipo ON contacto_cliente(tipocontactoid);
```

### 9.1 Tablas y atributos comunes para Contact Info

Una implementacion completa de este patron suele incluir:

| Tabla | Proposito | Atributos comunes recomendados |
|------|-----------|-------------------------------|
| `tipo_contacto` | Catalogo de canales | `tipocontactoid` (PK), `codigo` (UNIQUE: EMAIL/TEL/WA/TELEGRAM), `nombre`, `requiere_verificacion`, `formato_regex` (opcional), `activo` |
| `contacto_cliente` (o entidad_contacto) | Almacenar multiples contactos por entidad | `contactoid` (PK), `clienteid` (FK), `tipocontactoid` (FK), `valor`, `es_principal`, `verificado`, `fecha_verificacion`, `vigente_desde`, `vigente_hasta`, `consentimiento_comercial` (opcional) |
| `contacto_verificacion_token` (opcional) | Flujo OTP/email verification | `tokenid`, `contactoid`, `token_hash`, `expira_en`, `intentos`, `usado_en`, `estado` |
| `contacto_evento` (opcional) | Historial de cambios/inactivaciones | `eventoid`, `contactoid`, `tipo_evento` (creado/verificado/inactivado), `detalle`, `hecho_por`, `fechaevento` |

Checklist minimo:
1. Soportar multiples contactos por tipo y por cliente.
2. Bandera de principalidad con regla de unicidad por tipo.
3. Estado de verificacion y fecha de verificacion.
4. Vigencia para mantener trazabilidad sin borrado fisico.

### 10. Recursos

#### YouTube (enlaces directos)
- https://www.youtube.com/results?search_query=database+design+contact+information
- https://www.youtube.com/results?search_query=customer+contact+schema+design

#### Documentacion
- PostgreSQL constraints: https://www.postgresql.org/docs/current/ddl-constraints.html
- PostgreSQL indexes: https://www.postgresql.org/docs/current/indexes.html

### 11. Ejemplo concreto

Caso: cliente con telefono y email, uno principal.

```sql
INSERT INTO tipo_contacto (codigo, nombre) VALUES
('EMAIL', 'Correo electronico'),
('TEL', 'Telefono movil');

INSERT INTO contacto_cliente (
  clienteid, tipocontactoid, valor, es_principal, verificado, fecha_verificacion
) VALUES
(1, 1, 'ana@correo.com', TRUE, TRUE, NOW()),
(1, 2, '+50688887777', TRUE, FALSE, NULL);
```

Consulta de contactos activos por cliente:

```sql
SELECT tc.codigo, cc.valor, cc.es_principal, cc.verificado
FROM contacto_cliente cc
JOIN tipo_contacto tc ON tc.tipocontactoid = cc.tipocontactoid
WHERE cc.clienteid = 1
  AND cc.vigente_hasta IS NULL
ORDER BY cc.es_principal DESC, tc.codigo;
```

### 12. Conceptos relacionados

1. Canal principal.
2. Verificacion de contacto.
3. Consentimiento de comunicaciones.
4. Higiene de datos de clientes.

### 13. Resumen ejecutivo

Contact Info es esencial para sistemas de relacion con cliente porque soporta multiples canales, vigencia y verificacion sin romper el modelo principal.

---

## Notas de diseño transversal para los seis patrones

1. Diseña primero reglas de negocio, despues tablas.
2. Diferencia claramente datos maestros, transacciones, historico y observabilidad.
3. Si algo debe auditarse, modelalo como evento, no como simple update.
4. En volumen alto, combina normalizacion + indices + particion.
5. Si un dato cambia en el tiempo, piensa desde el inicio en vigencia historica.
6. Para sistemas estables a largo plazo, prioriza trazabilidad e integridad por encima de atajos.

---
