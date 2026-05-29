# Mapa de Navegación Simplificado - e-park

## 🎯 Enlaces Rápidos por Pantalla

### SPLASH SCREEN
- **Auto (2 segundos)** → Login Screen

---

### LOGIN SCREEN
- **Botón "Login"** (email con "admin") → Admin Dashboard
- **Botón "Login"** (email sin "admin") → Municipality Selection
- **Botón "Register"** → Driver Registration Paso 1
- **Link "Forgot password?"** → Alert (prototype)

---

### DRIVER REGISTRATION - PASO 1
- **Botón "Continue"** → Driver Registration Paso 2
- **Botón "Back" (header)** → Login Screen

---

### DRIVER REGISTRATION - PASO 2
- **Botón "Continue"** → Driver Registration Paso 3

---

### DRIVER REGISTRATION - PASO 3
- **Botón "Continue"** → Driver Registration Paso 4

---

### DRIVER REGISTRATION - PASO 4
- **Botón "Get Started"** → Driver Home

---

### MUNICIPALITY SELECTION
- **Click en municipalidad activa** → Driver Home

---

### DRIVER HOME
- **Badge notificaciones** → Alert
- **Botón "View Session"** → Active Session
- **Quick Action "Start Parking"** → Start Parking Paso 1
- **Quick Action "Nearby Zones"** → Nearby Zones
- **Bottom Nav "Home"** → Driver Home
- **Bottom Nav "History"** → Parking History
- **Bottom Nav "Fines"** → Fines Screen
- **Bottom Nav "Profile"** → Driver Profile

---

### NEARBY ZONES
- **Botón "Back"** → Driver Home
- **Ícono GPS** → Dialog "Enable Location Services"
- **Botón "Park Here"** → Start Parking Paso 1
- **Bottom Nav "Home"** → Driver Home
- **Bottom Nav "History"** → Parking History
- **Bottom Nav "Fines"** → Fines Screen
- **Bottom Nav "Profile"** → Driver Profile

---

### START PARKING - PASO 1
- **Botón "Back"** → Driver Home
- **Botón "Continue"** → Start Parking Paso 2

---

### START PARKING - PASO 2
- **Botón "Continue"** (con 4 dígitos) → Start Parking Paso 3

---

### START PARKING - PASO 3
- **Botón "Start Parking Session"** → Active Session

---

### ACTIVE SESSION
- **Botón "Back"** → Driver Home
- **Botón "End Session"** → Dialog confirmación
- **Dialog "Confirm & Pay"** → Payment Flow Paso 1
- **Bottom Nav "Home"** → Driver Home
- **Bottom Nav "History"** → Parking History
- **Bottom Nav "Fines"** → Fines Screen
- **Bottom Nav "Profile"** → Driver Profile

---

### PAYMENT FLOW - PASO 1
- **Botón "Back"** → Active Session
- **Botón "Continue"** → Payment Flow Paso 2

---

### PAYMENT FLOW - PASO 2
- **Botón "Confirm Payment"** → Payment Flow Paso 3

---

### PAYMENT FLOW - PASO 3
- **Botón "Download Receipt"** → Alert
- **Botón "Done"** → Driver Home

---

### PARKING HISTORY
- **Click en card** → Expandir/contraer (misma pantalla)
- **Bottom Nav "Home"** → Driver Home
- **Bottom Nav "History"** → Parking History
- **Bottom Nav "Fines"** → Fines Screen
- **Bottom Nav "Profile"** → Driver Profile

---

### FINES SCREEN
- **Tab "Pending"** → Mostrar multas pendientes
- **Tab "Paid"** → Mostrar multas pagadas
- **Botón "Pay Fine"** → Dialog confirmación
- **Dialog "Confirm Payment"** → Alert
- **Bottom Nav "Home"** → Driver Home
- **Bottom Nav "History"** → Parking History
- **Bottom Nav "Fines"** → Fines Screen
- **Bottom Nav "Profile"** → Driver Profile

---

### DRIVER PROFILE
- **Botón "Edit"** → Alert
- **Click en opciones del menú** → Alert
- **"Logout"** → Dialog confirmación
- **Dialog "Logout"** → Login Screen
- **Bottom Nav "Home"** → Driver Home
- **Bottom Nav "History"** → Parking History
- **Bottom Nav "Fines"** → Fines Screen
- **Bottom Nav "Profile"** → Driver Profile

---

## 👨‍💼 MÓDULO ADMINISTRADOR

### ADMIN DASHBOARD
- **Badge notificaciones** → Alert
- **Botón "Manage Zones"** → Zone Management
- **Botón "Rates"** → Rate Management
- **Botón "View Reports"** → Reports Screen

---

### ZONE MANAGEMENT
- **Botón "Back"** → Admin Dashboard
- **Botón "+" (flotante)** → Zone Editor (new)
- **Menú "Edit Zone"** → Zone Editor (edit)
- **Menú "Toggle Status"** → Alert
- **Menú "Delete Zone"** → Alert confirmación

---

### ZONE EDITOR (Create/Edit)
- **Botón "Back"** → Zone Management
- **Botón "Save Changes" / "Create Zone"** → Alert → Zone Management

---

### RATE MANAGEMENT
- **Botón "Back"** → Admin Dashboard
- **Botón "Update"** → Dialog "Update Parking Rate"
- **Dialog "Save Changes"** → Alert → cerrar dialog

---

### REPORTS SCREEN
- **Botón "Back"** → Admin Dashboard
- **Botón "Download" (header)** → Alert
- **Tab "Revenue"** → Mostrar datos revenue
- **Tab "Sessions"** → Mostrar datos sessions
- **Tab "Zones"** → Mostrar datos zones
- **Botón "Export Report as PDF"** → Alert

---

## 📊 Resumen de Pantallas Totales

**Total: 27 pantallas únicas**

### Conductor: 20 pantallas
1. Splash Screen
2. Login Screen
3. Driver Registration Paso 1
4. Driver Registration Paso 2
5. Driver Registration Paso 3
6. Driver Registration Paso 4
7. Municipality Selection
8. Driver Home
9. Nearby Zones
10. Start Parking Paso 1
11. Start Parking Paso 2
12. Start Parking Paso 3
13. Active Session
14. Payment Flow Paso 1
15. Payment Flow Paso 2
16. Payment Flow Paso 3
17. Parking History
18. Fines Screen - Tab Pending
19. Fines Screen - Tab Paid
20. Driver Profile

### Administrador: 7 pantallas
21. Admin Dashboard
22. Zone Management
23. Zone Editor (new)
24. Zone Editor (edit) - puede ser la misma que #23
25. Rate Management
26. Reports - Tab Revenue
27. Reports - Tab Sessions
28. Reports - Tab Zones

---

## 🔄 Componentes que aparecen en múltiples pantallas

### Bottom Navigation (aparece en 6 pantallas):
- Driver Home
- Nearby Zones
- Active Session
- Parking History
- Fines Screen
- Driver Profile

### App Bar con "Back" (mayoría de pantallas internas)

### Dialogs (overlays):
- GPS Permission (Nearby Zones)
- End Session Confirmation (Active Session)
- Pay Fine Confirmation (Fines Screen)
- Logout Confirmation (Driver Profile)
- Update Rate (Rate Management)

---

*Usa este mapa para conectar las pantallas en Justinmind*
