Design a complete high-fidelity Android mobile application prototype for a smart municipal parking system called “e-park”.

IMPORTANT:
This must ONLY be a visual and navigable prototype, NOT a functional application.
Do not generate backend logic, real database functionality, executable code, APIs, payment integrations, or production-ready implementation.
The prototype should simulate realistic navigation and user flows between screens using clickable interactions only.

The design must follow:

* Native Android app design principles
* Material Design 3 guidelines
* Modern clean UI
* Minimalist and professional visual style
* Consistent spacing, typography, colors, and components
* Responsive Android phone layouts
* Realistic mobile UX patterns

The application manages municipal parking systems in Costa Rica and includes two different user roles:

1. Driver (Conductor)
2. Municipal Administrator

The prototype must contain complete navigation flows, realistic mobile interactions, and all major screens required for both user types.

---

## GENERAL APP REQUIREMENTS

* Android native mobile design
* Bottom navigation for driver module
* Top app bars where appropriate
* Cards, chips, dialogs, snackbars, and floating action buttons when useful
* Use realistic mock data
* Include icons and mobile UI patterns
* Include loading states, confirmation dialogs, and empty states
* Simulate push notifications visually
* Simulate GPS/location functionality visually
* Simulate offline mode visually
* Include transitions and clickable navigation between screens

DO NOT:

* Create real functionality
* Generate real payment systems
* Generate real authentication
* Generate backend architecture
* Generate code components
* Generate developer implementation

This is ONLY a UX/UI prototype.

---

## ROLE 1: DRIVER USER FLOW

Create the following complete screens and navigation:

1. Splash Screen

* e-park logo
* Simple loading animation
* Modern branding

2. Login Screen

* Email field
* Password field
* “Login” button
* “Register” button
* “Forgot password”
* Password requirements helper text
* Clean Android login layout

3. Driver Registration Flow
   Multiple connected screens:

* Personal information
* Email verification confirmation
* Vehicle registration
* Success confirmation

Required fields:

* Full name
* National ID
* Email
* Password
* Vehicle plate number

4. Driver Home Screen
   Main dashboard with:

* Active parking session card
* Nearby parking zones
* Quick actions
* Notifications preview

Bottom navigation tabs:

* Home
* History
* Fines
* Profile

5. Municipality Selection Screen

* Search municipality
* Select municipality
* Clean card-based layout

6. Nearby Parking Zones Screen
   Use:

* Interactive-style map mockup
* Nearby zones list
* Distance indicators
* Parking availability
* Hourly rates
* Operating hours

Include:

* GPS permission dialog mockup
* Filter and search UI

7. Start Parking Session Flow
   Flow:

* Select vehicle
* Enter 4-digit parking spot code
* Validate parking spot visually
* Confirm parking session

8. Active Parking Session Screen
   Display:

* Elapsed time
* Current cost
* Parking spot number
* Zone information
* Remaining time indicator
* “Finish session” button

Visual states:

* Active
* Near expiration
* Expired warning

9. Payment Flow
   Simulated payment screens only:

* Saved cards list
* Select payment method
* Confirm payment
* Payment success
* Digital receipt

10. Parking History Screen

* Session cards
* Dates
* Amounts
* Status
* Expandable details

11. Fines Screen
    Display:

* Pending fines
* Paid fines
* Fine details
* Pay fine flow

12. Driver Profile Screen

* Personal data
* Vehicles management
* Saved payment methods
* Edit profile options
* Logout option

13. Offline Mode Simulation
    Create visual mockups showing:

* Offline history access
* Sync pending indicator
* Connectivity restored notification

14. Push Notifications
    Create realistic Android notification mockups:

* Parking expiration warning
* Payment confirmation
* Fine notification

---

## ROLE 2: MUNICIPAL ADMINISTRATOR

Create a separate administrator module with independent navigation.

1. Administrator Dashboard
   Cards showing:

* Total sessions
* Revenue
* Active zones
* Registered fines

2. Parking Zone Management

* Zones list
* Create zone
* Edit zone
* Disable zone
* Status indicators

3. Zone Creation / Edit Screen
   Fields:

* Zone name
* Hourly rate
* Available spaces
* Operating schedule

4. Rate Management Screen

* Select zone
* Update pricing
* Confirmation dialog

5. Reports Screen
   Include:

* Date range selector
* Revenue charts
* Sessions summary
* Fines statistics
* Zone performance cards

6. Administrative Notifications
   Mockup notifications for:

* Expired parking sessions
* Reported incidents
* Parking violations

---

## DESIGN STYLE

The interface should feel like:

* Modern civic technology
* Smart city platform
* Professional but user-friendly
* Minimal clutter
* Easy accessibility
* High readability

Use:

* Rounded cards
* Clean typography
* Modern Android spacing
* Consistent iconography
* Soft shadows
* Professional color palette

Avoid:

* Overly futuristic UI
* Complex animations
* Excessive colors
* Unrealistic dashboards
* Desktop-style layouts

---

## FINAL REQUIREMENTS

Generate:

* Fully connected prototype navigation
* Complete Android app screen system
* Clickable flows between screens
* Realistic user journeys
* Consistent design system
* Reusable components
* Mobile-first layouts

Again:
This must remain ONLY a visual interactive prototype for UX/UI demonstration purposes.
Do NOT generate functional app implementation.
