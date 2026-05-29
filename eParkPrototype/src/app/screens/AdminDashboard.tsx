import { useNavigate } from "react-router";
import { MobileContainer } from "../components/MobileContainer";
import {
  AppBar,
  Toolbar,
  Typography,
  IconButton,
  Card,
  CardContent,
  Grid,
  Button,
  Badge,
} from "@mui/material";
import {
  Menu,
  Notifications,
  TrendingUp,
  AttachMoney,
  LocalParking,
  Receipt,
  Map,
  Settings,
  Assessment,
  Warning,
} from "@mui/icons-material";

const statsCards = [
  {
    title: "Active Sessions",
    value: "142",
    change: "+12%",
    icon: LocalParking,
    color: "#1976d2",
  },
  {
    title: "Today's Revenue",
    value: "₡458,900",
    change: "+8%",
    icon: AttachMoney,
    color: "#2e7d32",
  },
  {
    title: "Parking Zones",
    value: "24",
    change: "2 offline",
    icon: Map,
    color: "#ed6c02",
  },
  {
    title: "Pending Fines",
    value: "37",
    change: "+5 today",
    icon: Receipt,
    color: "#d32f2f",
  },
];

const recentActivity = [
  { type: "session_started", zone: "Zone A", time: "2 min ago" },
  { type: "payment", amount: "₡850", time: "5 min ago" },
  { type: "fine_issued", zone: "Zone C", time: "15 min ago" },
  { type: "session_ended", zone: "Zone B", time: "18 min ago" },
];

export function AdminDashboard() {
  const navigate = useNavigate();

  return (
    <MobileContainer>
      <div className="h-full flex flex-col bg-gray-50">
        <AppBar position="static">
          <Toolbar>
            <IconButton edge="start" color="inherit">
              <Menu />
            </IconButton>
            <Typography variant="h6" sx={{ flexGrow: 1 }}>
              Admin Dashboard
            </Typography>
            <IconButton color="inherit">
              <Badge badgeContent={5} color="error">
                <Notifications />
              </Badge>
            </IconButton>
          </Toolbar>
        </AppBar>

        <div className="flex-1 overflow-y-auto p-4">
          <Card sx={{ mb: 3, bgcolor: "primary.main", color: "white" }}>
            <CardContent>
              <Typography variant="subtitle2" sx={{ opacity: 0.9 }}>
                Municipality
              </Typography>
              <Typography variant="h5" fontWeight="bold">
                San José
              </Typography>
              <Typography variant="caption" sx={{ opacity: 0.8 }}>
                Administrator Dashboard
              </Typography>
            </CardContent>
          </Card>

          <Grid container spacing={2} sx={{ mb: 3 }}>
            {statsCards.map((stat, index) => (
              <Grid item xs={6} key={index}>
                <Card>
                  <CardContent>
                    <div
                      className="rounded-full p-2 inline-flex mb-2"
                      style={{ backgroundColor: `${stat.color}20` }}
                    >
                      <stat.icon sx={{ color: stat.color }} />
                    </div>
                    <Typography variant="caption" color="text.secondary" display="block">
                      {stat.title}
                    </Typography>
                    <Typography variant="h6" fontWeight="bold">
                      {stat.value}
                    </Typography>
                    <Typography
                      variant="caption"
                      sx={{
                        color: stat.change.includes("+") ? "success.main" : "text.secondary",
                      }}
                    >
                      {stat.change}
                    </Typography>
                  </CardContent>
                </Card>
              </Grid>
            ))}
          </Grid>

          <Typography variant="subtitle1" fontWeight="bold" sx={{ mb: 2 }}>
            Quick Actions
          </Typography>

          <div className="grid grid-cols-2 gap-2 mb-4">
            <Button
              variant="outlined"
              startIcon={<Map />}
              onClick={() => navigate("/admin/zones")}
              sx={{ py: 1.5, justifyContent: "flex-start" }}
            >
              Manage Zones
            </Button>
            <Button
              variant="outlined"
              startIcon={<Settings />}
              onClick={() => navigate("/admin/rates")}
              sx={{ py: 1.5, justifyContent: "flex-start" }}
            >
              Rates
            </Button>
            <Button
              variant="outlined"
              startIcon={<Assessment />}
              onClick={() => navigate("/admin/reports")}
              sx={{ py: 1.5, justifyContent: "flex-start", gridColumn: "span 2" }}
            >
              View Reports
            </Button>
          </div>

          <Card>
            <CardContent>
              <Typography variant="subtitle1" fontWeight="bold" gutterBottom>
                Recent Activity
              </Typography>
              <div className="space-y-3 mt-3">
                {recentActivity.map((activity, index) => (
                  <div key={index} className="flex items-start gap-2">
                    <div
                      className={`w-2 h-2 rounded-full mt-1.5 ${
                        activity.type === "fine_issued"
                          ? "bg-red-500"
                          : activity.type === "payment"
                          ? "bg-green-500"
                          : "bg-blue-500"
                      }`}
                    ></div>
                    <div className="flex-1">
                      <Typography variant="body2">
                        {activity.type === "session_started" && `New session in ${activity.zone}`}
                        {activity.type === "session_ended" && `Session ended in ${activity.zone}`}
                        {activity.type === "payment" && `Payment received: ${activity.amount}`}
                        {activity.type === "fine_issued" && `Fine issued in ${activity.zone}`}
                      </Typography>
                      <Typography variant="caption" color="text.secondary">
                        {activity.time}
                      </Typography>
                    </div>
                  </div>
                ))}
              </div>
            </CardContent>
          </Card>

          <Card sx={{ mt: 3, bgcolor: "warning.50", border: 1, borderColor: "warning.main" }}>
            <CardContent>
              <div className="flex items-start gap-2">
                <Warning color="warning" />
                <div>
                  <Typography variant="subtitle2" fontWeight="bold" gutterBottom>
                    2 Zones Offline
                  </Typography>
                  <Typography variant="body2" color="text.secondary">
                    Zone F and Zone M are currently offline for maintenance
                  </Typography>
                </div>
              </div>
            </CardContent>
          </Card>
        </div>
      </div>
    </MobileContainer>
  );
}
