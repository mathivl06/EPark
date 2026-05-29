import { createBrowserRouter } from "react-router";
import { SplashScreen } from "./screens/SplashScreen";
import { LoginScreen } from "./screens/LoginScreen";
import { DriverRegistration } from "./screens/DriverRegistration";
import { DriverHome } from "./screens/DriverHome";
import { MunicipalitySelection } from "./screens/MunicipalitySelection";
import { NearbyZones } from "./screens/NearbyZones";
import { StartParking } from "./screens/StartParking";
import { ActiveSession } from "./screens/ActiveSession";
import { PaymentFlow } from "./screens/PaymentFlow";
import { ParkingHistory } from "./screens/ParkingHistory";
import { FinesScreen } from "./screens/FinesScreen";
import { DriverProfile } from "./screens/DriverProfile";
import { AdminDashboard } from "./screens/AdminDashboard";
import { ZoneManagement } from "./screens/ZoneManagement";
import { ZoneEditor } from "./screens/ZoneEditor";
import { RateManagement } from "./screens/RateManagement";
import { ReportsScreen } from "./screens/ReportsScreen";

export const router = createBrowserRouter([
  {
    path: "/",
    Component: SplashScreen,
  },
  {
    path: "/login",
    Component: LoginScreen,
  },
  {
    path: "/register",
    Component: DriverRegistration,
  },
  {
    path: "/driver",
    Component: DriverHome,
  },
  {
    path: "/municipality-select",
    Component: MunicipalitySelection,
  },
  {
    path: "/nearby-zones",
    Component: NearbyZones,
  },
  {
    path: "/start-parking",
    Component: StartParking,
  },
  {
    path: "/active-session",
    Component: ActiveSession,
  },
  {
    path: "/payment",
    Component: PaymentFlow,
  },
  {
    path: "/history",
    Component: ParkingHistory,
  },
  {
    path: "/fines",
    Component: FinesScreen,
  },
  {
    path: "/profile",
    Component: DriverProfile,
  },
  {
    path: "/admin",
    Component: AdminDashboard,
  },
  {
    path: "/admin/zones",
    Component: ZoneManagement,
  },
  {
    path: "/admin/zones/new",
    Component: ZoneEditor,
  },
  {
    path: "/admin/zones/:id",
    Component: ZoneEditor,
  },
  {
    path: "/admin/rates",
    Component: RateManagement,
  },
  {
    path: "/admin/reports",
    Component: ReportsScreen,
  },
]);
