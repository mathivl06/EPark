# Guía de Exportación e-park a Justinmind

**Proyecto:** Sistema de Estacionamiento Municipal "e-park"  
**Plataforma:** Android Mobile  
**Dimensiones:** 390 x 844 px  
**Fecha:** 7 de Mayo, 2026

---

## 📱 CONFIGURACIÓN DEL PROYECTO EN JUSTINMIND

### Configuración Inicial
1. **Crear nuevo proyecto móvil**
2. **Dispositivo:** Android (Custom)
3. **Resolución:** 390 x 844 px
4. **Orientación:** Portrait (Vertical)

### Paleta de Colores

```
Primary Blue: #1976d2
Light Blue: #42a5f5
Dark Blue: #1565c0
Secondary Red: #dc004e
Success Green: #2e7d32
Warning Orange: #ed6c02
Error Red: #d32f2f
Background Gray: #f5f5f5
White: #ffffff
Text Primary: #000000
Text Secondary: #757575
```

---

## 🎯 MÓDULO 1: CONDUCTOR (DRIVER)

### 1. SPLASH SCREEN
**Ruta:** `/` (Pantalla inicial)

**Elementos visuales:**
- Fondo: Gradiente azul (#1976d2 a #1565c0)
- Logo: Ícono de estacionamiento (círculo blanco)
- Texto: "e-park" (fuente grande, blanca, bold)
- Subtítulo: "Smart Parking System" (azul claro)
- Indicador de carga: Spinner circular blanco

**Navegación automática:**
- Después de 2 segundos → LOGIN SCREEN

**Transición:** Fade

---

### 2. LOGIN SCREEN
**Ruta:** `/login`

**Elementos visuales:**
- Header: Logo e-park en círculo azul claro
- Título: "Welcome to e-park"
- Subtítulo: "Sign in to manage your parking"
- Campo 1: Email (con ícono)
- Campo 2: Password (con ícono de ojo para mostrar/ocultar)
- Texto pequeño: "Password must be at least 8 characters"
- Botón primario: "Login" (azul, ancho completo)
- Botón secundario: "Register" (outline, ancho completo)
- Link: "Forgot password?" (centrado, azul)

**Interacciones:**
- Botón "Login" → Si email contiene "admin" → ADMIN DASHBOARD
- Botón "Login" → Si email NO contiene "admin" → MUNICIPALITY SELECTION
- Botón "Register" → DRIVER REGISTRATION (Step 1)
- Link "Forgot password?" → Alert/Dialog "Password reset flow (prototype)"

---

### 3. DRIVER REGISTRATION
**Ruta:** `/register`

**Estructura:** 4 pasos con stepper horizontal

#### PASO 1/4: Personal Information
**Elementos:**
- Stepper: Paso 1 de 4 activo
- Título: "Personal Info"
- Campos:
  1. Full Name
  2. National ID
  3. Email
  4. Password
  5. Confirm Password
- Botón: "Continue" (parte inferior)

**Interacción:**
- Botón "Continue" → PASO 2
- Botón "Atrás" (header) → LOGIN SCREEN

#### PASO 2/4: Email Verification
**Elementos:**
- Stepper: Paso 2 de 4 activo
- Ícono: Check verde grande
- Título: "Verification Email Sent"
- Texto: "We've sent a verification link to [email]. Please check your inbox."
- Nota: "(Simulated for prototype)"
- Botón: "Continue"

**Interacción:**
- Botón "Continue" → PASO 3

#### PASO 3/4: Vehicle Registration
**Elementos:**
- Stepper: Paso 3 de 4 activo
- Campo: Vehicle Plate Number (placeholder: "ABC-1234")
- Texto ayuda: "You can add more vehicles later from your profile"
- Botón: "Continue"

**Interacción:**
- Botón "Continue" → PASO 4

#### PASO 4/4: Complete
**Elementos:**
- Stepper: Paso 4 de 4 activo
- Ícono: Check verde muy grande
- Título: "Registration Complete!"
- Texto: "Your account has been created successfully"
- Texto: "Welcome, [nombre]"
- Botón: "Get Started"

**Interacción:**
- Botón "Get Started" → DRIVER HOME

---

### 4. MUNICIPALITY SELECTION
**Ruta:** `/municipality-select`

**Elementos visuales:**
- App Bar: "Select Municipality"
- Campo búsqueda: Con ícono de lupa
- Lista de municipalidades (cards):
  1. San José - 24 parking zones (activo)
  2. Alajuela - 18 parking zones (activo)
  3. Cartago - 15 parking zones (activo)
  4. Heredia - 12 parking zones (activo)
  5. Puntarenas - 8 parking zones (activo)
  6. Limón - 6 parking zones (Coming soon - deshabilitado)

**Cada card contiene:**
- Ícono ciudad (círculo azul claro)
- Nombre municipalidad
- Número de zonas
- Flecha derecha (si activo) o "Coming soon" (si inactivo)

**Interacciones:**
- Click en cualquier municipalidad activa → DRIVER HOME

---

### 5. DRIVER HOME
**Ruta:** `/driver`

**Elementos visuales:**

**App Bar:**
- Título: "e-park"
- Ícono notificaciones con badge (3)

**Tarjeta de bienvenida (azul):**
- "Welcome back"
- "Juan Pérez"
- Ícono auto + "ABC-1234"

**Tarjeta sesión activa (verde):**
- Badge: "Active Session"
- "Zone A - Spot #142"
- "Av. Central, San José"
- Tiempo: "1h 42min elapsed"
- Costo: "₡850" (grande, destacado)
- Botón: "View Session"

**Quick Actions (2 columnas):**
1. "Start Parking" (ícono play, azul)
2. "Nearby Zones" (ícono mapa, verde)

**Notificaciones recientes:**
- "Parking expiring in 15 minutes" (5 min ago)
- "Payment successful - ₡1,200" (2 hours ago)

**Bottom Navigation (4 tabs):**
1. Home (activo)
2. History
3. Fines
4. Profile

**Interacciones:**
- Badge notificaciones → Alert "Notifications (prototype)"
- Botón "View Session" → ACTIVE SESSION
- "Start Parking" → START PARKING
- "Nearby Zones" → NEARBY ZONES
- Tab "History" → PARKING HISTORY
- Tab "Fines" → FINES SCREEN
- Tab "Profile" → DRIVER PROFILE

---

### 6. NEARBY ZONES
**Ruta:** `/nearby-zones`

**Elementos visuales:**

**App Bar:**
- Botón atrás
- Título: "Nearby Zones"
- Ícono GPS

**Barra de búsqueda y filtros:**
- Campo búsqueda
- Botón "Filter"

**Card ubicación actual:**
- "📍 Using current location"
- "San José, Costa Rica"

**Lista de zonas (cards):**

Cada zona muestra:
- Nombre: "Zone A - Centro"
- Dirección: "Av. Central, San José"
- Badge disponibilidad: "8 available" (verde) o "Full" (rojo)
- Distancia: "120 m"
- Tarifa: "₡500/hour"
- Horario: "6:00 AM - 10:00 PM"
- Botón: "Park Here" (habilitado) o "Full" (deshabilitado)

**Zonas de ejemplo:**
1. Zone A - Centro (120m, 8/20 disponibles, ₡500/hr)
2. Zone B - Plaza (350m, 3/15 disponibles, ₡400/hr)
3. Zone C - Mercado (580m, 0/12 FULL, ₡350/hr)
4. Zone D - Parque (720m, 15/25 disponibles, ₡300/hr)

**Bottom Navigation (visible)**

**Interacciones:**
- Botón atrás → DRIVER HOME
- Ícono GPS → Dialog "Enable Location Services"
- Botón "Park Here" → START PARKING
- Bottom nav según tab seleccionado

---

### 7. START PARKING FLOW
**Ruta:** `/start-parking`

**Estructura:** 3 pasos con stepper

#### PASO 1/3: Select Vehicle
**Elementos:**
- Stepper: 1 de 3
- Dropdown/Select con vehículos:
  - ABC-1234 - Toyota Corolla 2020
  - XYZ-5678 - Honda Civic 2019
- Botón: "Continue"

**Interacción:**
- Botón "Continue" → PASO 2

#### PASO 2/3: Enter Spot Code
**Elementos:**
- Stepper: 2 de 3
- Título: "Enter Parking Spot Code"
- Instrucción: "Find the 4-digit code on the parking spot sign"
- Campo: Input grande centrado para 4 dígitos
- Ejemplo: "Example: 0142, 0256, 1024"
- Botón: "Continue" (deshabilitado hasta tener 4 dígitos)

**Interacción:**
- Botón "Continue" (con 4 dígitos) → PASO 3

#### PASO 3/3: Confirm
**Elementos:**
- Stepper: 3 de 3
- Card verde: "Spot Validated!" con check grande
- Información:
  - Vehículo: ABC-1234
  - Ubicación: Zone A - Centro, Av. Central, San José
  - Spot Number: #142 (grande)
  - Rate: ₡500/hour (chip)
- Botón: "Start Parking Session"

**Interacción:**
- Botón "Start Parking Session" → ACTIVE SESSION

---

### 8. ACTIVE SESSION
**Ruta:** `/active-session`

**Estados posibles:**
1. **Active (verde)** - tiempo < 2h 45min
2. **Warning (naranja)** - tiempo > 2h 45min
3. **Expired (rojo)** - tiempo > 3h

**Elementos visuales (estado Active):**

**App Bar:**
- Botón atrás
- Título: "Active Session"
- Color según estado

**Tarjeta tiempo (destacada):**
- "Elapsed Time"
- Tiempo: "1h 42m 25s" (grande, animado)
- Barra de progreso (hasta 3 horas)
- "Maximum 3 hours per session"

**Tarjeta información:**
- Costo actual: "₡850" (muy grande)
- Badge: "Active"
- Ubicación: Zone A - Spot #142, Av. Central
- Tarifa: ₡500/hour
- Hora inicio: 2:18 PM

**Tip card (azul claro):**
- "💡 Tip: End your session before leaving to avoid additional charges"

**Botón:**
- "End Session" (rojo, con ícono stop)

**Bottom Navigation (visible)**

**Interacciones:**
- Botón atrás → DRIVER HOME
- Botón "End Session" → Dialog de confirmación
- Dialog "Confirm & Pay" → PAYMENT FLOW
- Bottom nav según tab

---

### 9. PAYMENT FLOW
**Ruta:** `/payment`

**Estructura:** 3 pasos con stepper

#### PASO 1/3: Select Method
**Elementos:**
- Stepper: 1 de 3
- Card monto: "₡850" - "Amount due for parking session"
- Título: "Saved Payment Methods"
- Radio buttons con tarjetas guardadas:
  - Visa •••• 4242 (Expires 12/25)
  - Mastercard •••• 5555 (Expires 08/26)
  - Bank Transfer
- Botón: "Continue"

**Interacción:**
- Botón "Continue" → PASO 2

#### PASO 2/3: Confirm
**Elementos:**
- Stepper: 2 de 3
- Card "Payment Summary":
  - Parking duration: 1h 42min
  - Zone A - Spot #142: ₡500/hr
  - Total: ₡850 (destacado)
- Card "Payment Method":
  - Visa •••• 4242
- Botón: "Confirm Payment"

**Interacción:**
- Botón "Confirm Payment" → PASO 3

#### PASO 3/3: Complete
**Elementos:**
- Stepper: 3 de 3
- Card verde: Check grande + "Payment Successful!"
- Card recibo:
  - Receipt #PS-2026-0507-142
  - Date: May 7, 2026
  - Location: Zone A - #142
  - Duration: 1h 42min
  - Vehicle: ABC-1234
  - Amount Paid: ₡850
- Botón: "Download Receipt" (outline)
- Botón: "Done" (primario)

**Interacción:**
- Botón "Download Receipt" → Alert "Receipt download (prototype)"
- Botón "Done" → DRIVER HOME

---

### 10. PARKING HISTORY
**Ruta:** `/history`

**Elementos visuales:**

**App Bar:**
- Título: "Parking History"

**Card resumen (azul claro):**
- "Total Sessions This Month: 12"
- "Total spent: ₡8,450"

**Lista de sesiones (expandibles):**

Cada card:
- Nombre zona: "Zone A - Centro"
- Badge: "Paid" (verde con check)
- Fecha y spot: "May 7, 2026 • Spot #142"
- Monto: "₡850"
- Botón expandir (flecha)

**Al expandir:**
- Ubicación: Av. Central, San José
- Duración: 1h 42min
- Vehículo: ABC-1234

**Sesiones de ejemplo:**
1. May 7 - Zone A #142 - 1h 42min - ₡850
2. May 6 - Zone B #087 - 2h 15min - ₡900
3. May 5 - Zone A #156 - 0h 45min - ₡375
4. May 4 - Zone D #023 - 3h 00min - ₡900

**Bottom Navigation (tab History activo)**

**Interacciones:**
- Click en card → Expandir/contraer
- Bottom nav según tab

---

### 11. FINES SCREEN
**Ruta:** `/fines`

**Elementos visuales:**

**App Bar:**
- Título: "Fines"

**Banner alerta (naranja):**
- "⚠️ You have 2 pending fine(s)"

**Tabs:**
- "Pending (2)"
- "Paid (1)"

#### TAB PENDING:

**Card multa (borde naranja):**
- Ícono warning
- Tipo: "Expired Parking"
- Zona: "Zone A - Centro"
- Descripción: "Parking session exceeded maximum time limit"
- Monto: "₡5,000" (rojo, grande)
- Due Date: "May 20, 2026"
- Badge: "Pending" (naranja)
- Botón: "Pay Fine" (rojo)

**Multas de ejemplo:**
1. Expired Parking - Zone A - ₡5,000 (Due: May 20)
2. Invalid Spot - Zone B - ₡3,500 (Due: May 16)

#### TAB PAID:

**Card multa pagada:**
- Ícono recibo
- Tipo: "Expired Parking"
- Zona: "Zone C - Mercado"
- Badge: "Paid" (verde con check)
- Fine Date: April 15, 2026
- Paid Date: April 18, 2026
- Amount: ₡4,500

**Bottom Navigation (tab Fines activo)**

**Interacciones:**
- Cambio de tab → Mostrar contenido correspondiente
- Botón "Pay Fine" → Dialog de confirmación
- Dialog "Confirm Payment" → Alert "Payment processed (prototype)"
- Bottom nav según tab

---

### 12. DRIVER PROFILE
**Ruta:** `/profile`

**Elementos visuales:**

**App Bar:**
- Título: "Profile"

**Header (gradiente azul):**
- Avatar circular (ícono persona)
- Nombre: "Juan Pérez"
- Email: "juan.perez@email.com"
- Badge: "Verified"
- Botón editar (esquina)

**Card "Personal Information":**
- Full Name: Juan Carlos Pérez González
- National ID: 1-2345-6789
- Phone Number: +506 8888-9999
- Botón editar

**Lista opciones:**

**Section 1:**
- My Vehicles (2 vehicles registered) →
- Payment Methods (2 cards saved) →

**Section 2 - App Settings:**
- Notifications →
- Language (English) →
- Security →

**Section 3 - Support:**
- Help Center →

**Section 4:**
- Logout (rojo)

**Footer:**
- "e-park v1.0.0"

**Bottom Navigation (tab Profile activo)**

**Interacciones:**
- Botón editar → Alert (prototype)
- Click en opción → Alert (prototype)
- Logout → Dialog de confirmación
- Dialog "Logout" → LOGIN SCREEN
- Bottom nav según tab

---

## 🔧 MÓDULO 2: ADMINISTRADOR

### 13. ADMIN DASHBOARD
**Ruta:** `/admin`

**Elementos visuales:**

**App Bar:**
- Botón menú
- Título: "Admin Dashboard"
- Ícono notificaciones con badge (5)

**Card municipalidad (azul):**
- "Municipality"
- "San José"
- "Administrator Dashboard"

**Grid estadísticas (2x2):**

1. **Active Sessions**
   - Valor: 142
   - Cambio: +12%
   - Ícono: Parking (azul)

2. **Today's Revenue**
   - Valor: ₡458,900
   - Cambio: +8%
   - Ícono: Money (verde)

3. **Parking Zones**
   - Valor: 24
   - Info: 2 offline
   - Ícono: Map (naranja)

4. **Pending Fines**
   - Valor: 37
   - Info: +5 today
   - Ícono: Receipt (rojo)

**Quick Actions (2 columnas):**
- Manage Zones (con ícono mapa)
- Rates (con ícono settings)
- View Reports (ancho completo)

**Card "Recent Activity":**
- New session in Zone A (2 min ago)
- Payment received: ₡850 (5 min ago)
- Fine issued in Zone C (15 min ago)
- Session ended in Zone B (18 min ago)

**Alerta (card amarillo):**
- "⚠️ 2 Zones Offline"
- "Zone F and Zone M are currently offline for maintenance"

**Interacciones:**
- Badge notificaciones → Alert
- "Manage Zones" → ZONE MANAGEMENT
- "Rates" → RATE MANAGEMENT
- "View Reports" → REPORTS SCREEN

---

### 14. ZONE MANAGEMENT
**Ruta:** `/admin/zones`

**Elementos visuales:**

**App Bar:**
- Botón atrás
- Título: "Parking Zones"

**Campo búsqueda:**
- Con ícono lupa

**Card resumen (azul claro):**
- Total Zones: 24
- Active: 22
- Inactive: 2

**Lista de zonas (cards):**

Cada card:
- Nombre: "Zone A - Centro"
- Dirección: "Av. Central"
- Badge: "Active" (verde) o "Inactive" (gris)
- Botón menú (3 puntos)
- Estadísticas:
  - Spots: 20
  - Occupied: 12
  - Rate: ₡500/hr
- Barra progreso ocupación
- "8 spots available"

**Zonas de ejemplo:**
1. Zone A - Centro (20 spots, 12 ocupados, ₡500/hr, Active)
2. Zone B - Plaza (15 spots, 12 ocupados, ₡400/hr, Active)
3. Zone C - Mercado (12 spots, 12 FULL, ₡350/hr, Active)
4. Zone D - Parque (25 spots, 10 ocupados, ₡300/hr, Active)
5. Zone E - Hospital (18 spots, 0 ocupados, ₡450/hr, Inactive)

**Botón flotante (+):**
- Esquina inferior derecha

**Menú contextual (3 puntos):**
- Edit Zone
- Toggle Status
- Delete Zone (rojo)

**Interacciones:**
- Botón atrás → ADMIN DASHBOARD
- Campo búsqueda → Filtrar zonas
- Botón "+" → ZONE EDITOR (new)
- Menú "Edit Zone" → ZONE EDITOR (edit)
- Menú "Toggle Status" → Cambiar estado (prototype)
- Menú "Delete Zone" → Alert confirmación

---

### 15. ZONE EDITOR (Create/Edit)
**Ruta:** `/admin/zones/new` o `/admin/zones/:id`

**Elementos visuales:**

**App Bar:**
- Botón atrás
- Título: "Create New Zone" o "Edit Zone"

**Card "Basic Information":**
- Zone Name (campo texto)
- Address (campo texto)
- Available Parking Spots (número)
- Hourly Rate (₡) (número)
  - Helper: "Rate per hour in Costa Rican Colones"

**Card "Operating Schedule":**
- Operating Days (dropdown):
  - Every Day
  - Weekdays Only
  - Weekends Only
  - Custom Schedule
- Start Time (time picker)
- End Time (time picker)

**Card "Status":**
- Switch: "Zone Active"
- Texto: "Drivers can see and use this zone"

**Botón inferior:**
- "Create Zone" o "Save Changes" (con ícono save)

**Interacciones:**
- Botón atrás → ZONE MANAGEMENT
- Botón "Save/Create" → Alert confirmación → ZONE MANAGEMENT

---

### 16. RATE MANAGEMENT
**Ruta:** `/admin/rates`

**Elementos visuales:**

**App Bar:**
- Botón atrás
- Título: "Rate Management"

**Card info (azul claro):**
- "💡 Rate changes will apply to new parking sessions only. Active sessions maintain their original rate."

**Lista de zonas:**

Cada card:
- Nombre: "Zone A - Centro"
- Tarifa actual: "₡500" (grande) + "per hour"
- Botón: "Update"

**Zonas de ejemplo:**
1. Zone A - Centro - ₡500/hr
2. Zone B - Plaza - ₡400/hr
3. Zone C - Mercado - ₡350/hr
4. Zone D - Parque - ₡300/hr
5. Zone E - Hospital - ₡450/hr

**Dialog "Update Parking Rate":**
- Dropdown: Select Zone
- Campo: New Hourly Rate (₡)
- Helper: "Enter the new rate per hour in Costa Rican Colones"
- Info: "Current rate: ₡XXX/hour"
- Botones: Cancel / Save Changes

**Interacciones:**
- Botón atrás → ADMIN DASHBOARD
- Botón "Update" → Abrir dialog
- Dialog "Save Changes" → Alert confirmación

---

### 17. REPORTS SCREEN
**Ruta:** `/admin/reports`

**Elementos visuales:**

**App Bar:**
- Botón atrás
- Título: "Reports"
- Botón download

**Filtros:**
- Dropdown período: Today / This Week / This Month / Custom Range
- Botón rango: "May 1-7, 2026" (con ícono calendario)

**Tabs:**
- Revenue
- Sessions
- Zones

#### TAB REVENUE:

**Cards métricas (2 columnas):**
1. Total Revenue: ₡341,000 (+12.5% verde)
2. Avg. Daily: ₡48,714 (+8.2% verde)

**Gráfica de barras:**
- Título: "Revenue by Day"
- Eje X: Mon, Tue, Wed, Thu, Fri, Sat, Sun
- Eje Y: Valores en colones
- Datos: 45k, 52k, 48k, 61k, 55k, 38k, 42k

#### TAB SESSIONS:

**Cards métricas (2 columnas):**
1. Total Sessions: 1,087 (+15.3% verde)
2. Avg. Duration: 1.8h (-3.1% rojo)

**Gráfica de líneas:**
- Título: "Sessions by Day"
- Datos: 142, 168, 155, 189, 176, 123, 134

**Card "Fine Statistics":**
- Fines Issued: 37
- Fine Revenue: ₡156,500

#### TAB ZONES:

**Gráfica circular (pie chart):**
- Título: "Zone Performance Distribution"
- Zone A: 35% (azul oscuro)
- Zone B: 25% (azul)
- Zone C: 20% (azul claro)
- Zone D: 15% (azul muy claro)
- Others: 5% (azul pálido)

**Card "Top Performing Zones":**

Lista con:
1. Zone A - Centro
   - 382 sessions • ₡119,500
   - Badge: 85% (rojo - alta ocupación)

2. Zone B - Plaza
   - 271 sessions • ₡85,200
   - Badge: 78% (verde)

3. Zone C - Mercado
   - 218 sessions • ₡68,300
   - Badge: 92% (rojo)

4. Zone D - Parque
   - 163 sessions • ₡48,900
   - Badge: 42% (gris)

**Botón inferior:**
- "Export Report as PDF" (outline, con ícono download)

**Interacciones:**
- Botón atrás → ADMIN DASHBOARD
- Botón download (header) → Alert "Report download"
- Cambio de tab → Mostrar contenido correspondiente
- Botón "Export Report as PDF" → Alert "Report download"

---

## 🔗 MAPA COMPLETO DE NAVEGACIÓN

### Flujo Principal - Conductor

```
SPLASH SCREEN (2s automático)
    ↓
LOGIN SCREEN
    ├→ Register → DRIVER REGISTRATION (4 pasos) → DRIVER HOME
    ├→ Login (admin) → ADMIN DASHBOARD
    └→ Login (user) → MUNICIPALITY SELECTION → DRIVER HOME

DRIVER HOME (Bottom Nav Base)
    ├→ View Session → ACTIVE SESSION → End Session → PAYMENT → HOME
    ├→ Start Parking → START PARKING (3 pasos) → ACTIVE SESSION
    ├→ Nearby Zones → NEARBY ZONES → Park Here → START PARKING
    ├→ Tab History → PARKING HISTORY
    ├→ Tab Fines → FINES SCREEN → Pay Fine → Dialog → Alert
    └→ Tab Profile → DRIVER PROFILE → Logout → LOGIN SCREEN
```

### Flujo Principal - Administrador

```
LOGIN SCREEN (admin@)
    ↓
ADMIN DASHBOARD
    ├→ Manage Zones → ZONE MANAGEMENT
    │                  ├→ Create (+) → ZONE EDITOR → Save → ZONE MANAGEMENT
    │                  └→ Edit → ZONE EDITOR → Save → ZONE MANAGEMENT
    ├→ Rates → RATE MANAGEMENT → Update → Dialog → RATE MANAGEMENT
    └→ View Reports → REPORTS SCREEN → Export → Alert
```

---

## 📋 INSTRUCCIONES PARA JUSTINMIND

### Paso 1: Configuración Inicial
1. Crear proyecto Android (390x844)
2. Importar paleta de colores
3. Configurar tipografía Roboto

### Paso 2: Crear Pantallas
1. Crear 17 pantallas separadas
2. Nombrarlas según la lista arriba
3. Usar screenshots como referencia

### Paso 3: Elementos Interactivos

**Para cada pantalla:**
- Identificar botones según esta guía
- Crear hotspots/áreas clicables
- Aplicar enlaces a pantallas destino

**Tipos de transición sugeridos:**
- Botón "Continue/Next" → Slide Left
- Botón "Back" → Slide Right
- Bottom Nav → Fade
- Dialogs → Fade + Scale

### Paso 4: Estados y Variaciones

**Pantallas con múltiples estados:**
- ACTIVE SESSION: Active / Warning / Expired (usar 3 pantallas)
- FINES: Tab Pending / Tab Paid (usar 2 pantallas)
- REPORTS: Tab Revenue / Sessions / Zones (usar 3 pantallas)

### Paso 5: Componentes Reutilizables

**Crear como Widgets/Componentes:**
- Bottom Navigation (4 tabs)
- App Bar con botón atrás
- Cards estándar
- Botones primarios y secundarios
- Campos de texto

---

## ✅ CHECKLIST FINAL

### Pantallas Módulo Conductor (13)
- [ ] Splash Screen
- [ ] Login Screen
- [ ] Driver Registration (4 pasos = 4 pantallas)
- [ ] Municipality Selection
- [ ] Driver Home
- [ ] Nearby Zones
- [ ] Start Parking (3 pasos = 3 pantallas)
- [ ] Active Session
- [ ] Payment Flow (3 pasos = 3 pantallas)
- [ ] Parking History
- [ ] Fines Screen (2 tabs = 2 pantallas)
- [ ] Driver Profile

### Pantallas Módulo Administrador (5)
- [ ] Admin Dashboard
- [ ] Zone Management
- [ ] Zone Editor
- [ ] Rate Management
- [ ] Reports Screen (3 tabs = 3 pantallas)

### Elementos Generales
- [ ] Paleta de colores configurada
- [ ] Bottom Navigation funcional
- [ ] App Bars en todas las pantallas
- [ ] Navegación "Back" funcional
- [ ] Transiciones configuradas
- [ ] Estados múltiples implementados

---

## 💡 TIPS IMPORTANTES

1. **Screenshots:** Captura cada pantalla en el estado por defecto
2. **Hotspots:** Usa rectángulos transparentes sobre botones
3. **Navegación circular:** Driver Home debe ser accesible desde Bottom Nav en múltiples pantallas
4. **Dialogs:** Crea como pantallas overlay con fondo semi-transparente
5. **Gráficas:** En Reports, usa imágenes estáticas de las gráficas
6. **Textos:** Mantén consistencia con los textos de esta guía

---

**¿Necesitas ayuda adicional?**
- Screenshots específicos
- Más detalles de alguna pantalla
- Ayuda con interacciones en Justinmind

---

*Documento generado para facilitar la recreación del prototipo e-park en Justinmind*
