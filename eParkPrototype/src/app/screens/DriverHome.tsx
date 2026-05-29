import { useNavigate } from "react-router";
import { MobileContainer } from "../components/MobileContainer";
import { BottomNav } from "../components/BottomNav";
import {
  AppBar,
  Toolbar,
  Typography,
  Card,
  CardContent,
  Button,
  Chip,
  IconButton,
  Badge,
} from "@mui/material";
import {
  Notifications,
  PlayArrow,
  Map,
  DirectionsCar,
  AccessTime,
} from "@mui/icons-material";

export function DriverHome() {
  const navigate = useNavigate();

  return (
    <MobileContainer withBottomNav>
      <div className="h-full flex flex-col bg-gray-50">
        <AppBar position="static">
          <Toolbar>
            <Typography variant="h6" sx={{ flexGrow: 1 }}>
              e-park
            </Typography>
            <IconButton color="inherit" onClick={() => alert("Notifications (prototype)")}>
              <Badge badgeContent={3} color="error">
                <Notifications />
              </Badge>
            </IconButton>
          </Toolbar>
        </AppBar>

        <div className="flex-1 overflow-y-auto p-4 pb-20">
          <Card sx={{ mb: 3, bgcolor: "primary.main", color: "white" }}>
            <CardContent>
              <Typography variant="caption" sx={{ opacity: 0.9 }}>
                Welcome back
              </Typography>
              <Typography variant="h5" fontWeight="bold" gutterBottom>
                Juan Pérez
              </Typography>
              <div className="flex items-center gap-2 mt-2">
                <DirectionsCar fontSize="small" />
                <Typography variant="body2">ABC-1234</Typography>
              </div>
            </CardContent>
          </Card>

          <Card sx={{ mb: 3, border: 2, borderColor: "success.main" }}>
            <CardContent>
              <div className="flex items-start justify-between mb-2">
                <div>
                  <Chip
                    label="Active Session"
                    color="success"
                    size="small"
                    sx={{ mb: 1 }}
                  />
                  <Typography variant="h6" fontWeight="bold">
                    Zone A - Spot #142
                  </Typography>
                  <Typography variant="body2" color="text.secondary">
                    Av. Central, San José
                  </Typography>
                </div>
                <div className="text-right">
                  <Typography variant="h5" fontWeight="bold" color="primary">
                    ₡850
                  </Typography>
                  <Typography variant="caption" color="text.secondary">
                    current cost
                  </Typography>
                </div>
              </div>
              <div className="flex items-center gap-1 mb-3">
                <AccessTime fontSize="small" color="action" />
                <Typography variant="body2" color="text.secondary">
                  1h 42min elapsed
                </Typography>
              </div>
              <Button
                fullWidth
                variant="contained"
                onClick={() => navigate("/active-session")}
              >
                View Session
              </Button>
            </CardContent>
          </Card>

          <Typography variant="subtitle1" fontWeight="bold" sx={{ mb: 2 }}>
            Quick Actions
          </Typography>

          <div className="grid grid-cols-2 gap-3 mb-4">
            <Card>
              <CardContent
                className="text-center cursor-pointer"
                onClick={() => navigate("/start-parking")}
              >
                <div className="bg-blue-100 rounded-full p-3 inline-flex mb-2">
                  <PlayArrow sx={{ fontSize: 32, color: "primary.main" }} />
                </div>
                <Typography variant="body2" fontWeight="medium">
                  Start Parking
                </Typography>
              </CardContent>
            </Card>

            <Card>
              <CardContent
                className="text-center cursor-pointer"
                onClick={() => navigate("/nearby-zones")}
              >
                <div className="bg-green-100 rounded-full p-3 inline-flex mb-2">
                  <Map sx={{ fontSize: 32, color: "success.main" }} />
                </div>
                <Typography variant="body2" fontWeight="medium">
                  Nearby Zones
                </Typography>
              </CardContent>
            </Card>
          </div>

          <Card sx={{ bgcolor: "info.50" }}>
            <CardContent>
              <Typography variant="subtitle2" fontWeight="bold" gutterBottom>
                Recent Notifications
              </Typography>
              <div className="space-y-2">
                <div className="flex items-start gap-2">
                  <div className="bg-orange-500 rounded-full w-2 h-2 mt-1.5"></div>
                  <div>
                    <Typography variant="body2">
                      Parking expiring in 15 minutes
                    </Typography>
                    <Typography variant="caption" color="text.secondary">
                      5 minutes ago
                    </Typography>
                  </div>
                </div>
                <div className="flex items-start gap-2">
                  <div className="bg-green-500 rounded-full w-2 h-2 mt-1.5"></div>
                  <div>
                    <Typography variant="body2">
                      Payment successful - ₡1,200
                    </Typography>
                    <Typography variant="caption" color="text.secondary">
                      2 hours ago
                    </Typography>
                  </div>
                </div>
              </div>
            </CardContent>
          </Card>
        </div>

        <BottomNav />
      </div>
    </MobileContainer>
  );
}
