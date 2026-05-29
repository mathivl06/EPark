import { useState, useEffect } from "react";
import { useNavigate } from "react-router";
import { MobileContainer } from "../components/MobileContainer";
import { BottomNav } from "../components/BottomNav";
import {
  AppBar,
  Toolbar,
  Typography,
  IconButton,
  Card,
  CardContent,
  Button,
  LinearProgress,
  Chip,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
} from "@mui/material";
import {
  ArrowBack,
  AccessTime,
  Place,
  AttachMoney,
  Stop,
  Warning,
} from "@mui/icons-material";

export function ActiveSession() {
  const navigate = useNavigate();
  const [elapsed, setElapsed] = useState(6120);
  const [showEndDialog, setShowEndDialog] = useState(false);
  const [sessionStatus, setSessionStatus] = useState<"active" | "warning" | "expired">("active");

  useEffect(() => {
    const timer = setInterval(() => {
      setElapsed((prev) => {
        const newElapsed = prev + 1;
        if (newElapsed > 10800) setSessionStatus("expired");
        else if (newElapsed > 9900) setSessionStatus("warning");
        return newElapsed;
      });
    }, 1000);
    return () => clearInterval(timer);
  }, []);

  const formatTime = (seconds: number) => {
    const h = Math.floor(seconds / 3600);
    const m = Math.floor((seconds % 3600) / 60);
    const s = seconds % 60;
    return `${h}h ${m}m ${s}s`;
  };

  const currentCost = Math.floor((elapsed / 3600) * 500);

  const handleEndSession = () => {
    setShowEndDialog(false);
    navigate("/payment");
  };

  return (
    <MobileContainer withBottomNav>
      <div className="h-full flex flex-col bg-gray-50">
        <AppBar
          position="static"
          sx={{
            bgcolor:
              sessionStatus === "expired"
                ? "error.main"
                : sessionStatus === "warning"
                ? "warning.main"
                : "primary.main",
          }}
        >
          <Toolbar>
            <IconButton
              edge="start"
              color="inherit"
              onClick={() => navigate("/driver")}
            >
              <ArrowBack />
            </IconButton>
            <Typography variant="h6" sx={{ flexGrow: 1 }}>
              Active Session
            </Typography>
            {sessionStatus !== "active" && <Warning />}
          </Toolbar>
        </AppBar>

        {sessionStatus === "warning" && (
          <div className="bg-orange-100 px-4 py-2 border-b border-orange-200">
            <Typography variant="body2" color="warning.dark" className="text-center">
              ⚠️ Session expiring soon! Extend or end session
            </Typography>
          </div>
        )}

        {sessionStatus === "expired" && (
          <div className="bg-red-100 px-4 py-2 border-b border-red-200">
            <Typography variant="body2" color="error.dark" className="text-center font-bold">
              🚨 Session expired! Additional fees may apply
            </Typography>
          </div>
        )}

        <div className="flex-1 overflow-y-auto p-4 pb-20">
          <Card sx={{ mb: 3, bgcolor: "primary.50" }}>
            <CardContent className="text-center">
              <Typography variant="subtitle2" color="text.secondary" gutterBottom>
                Elapsed Time
              </Typography>
              <Typography variant="h3" fontWeight="bold" color="primary">
                {formatTime(elapsed)}
              </Typography>
              <LinearProgress
                variant="determinate"
                value={Math.min((elapsed / 10800) * 100, 100)}
                sx={{ mt: 2, height: 8, borderRadius: 4 }}
                color={sessionStatus === "expired" ? "error" : sessionStatus === "warning" ? "warning" : "primary"}
              />
              <Typography variant="caption" color="text.secondary" sx={{ mt: 1, display: "block" }}>
                Maximum 3 hours per session
              </Typography>
            </CardContent>
          </Card>

          <Card sx={{ mb: 3 }}>
            <CardContent>
              <div className="flex items-center justify-between mb-4">
                <Typography variant="h4" fontWeight="bold" color="primary">
                  ₡{currentCost}
                </Typography>
                <Chip
                  label={sessionStatus === "expired" ? "Expired" : sessionStatus === "warning" ? "Expiring" : "Active"}
                  color={sessionStatus === "expired" ? "error" : sessionStatus === "warning" ? "warning" : "success"}
                />
              </div>

              <div className="space-y-3">
                <div className="flex items-start gap-2">
                  <Place color="action" />
                  <div>
                    <Typography variant="subtitle2" color="text.secondary">
                      Location
                    </Typography>
                    <Typography variant="body1" fontWeight="medium">
                      Zone A - Spot #142
                    </Typography>
                    <Typography variant="body2" color="text.secondary">
                      Av. Central, San José
                    </Typography>
                  </div>
                </div>

                <div className="flex items-start gap-2">
                  <AttachMoney color="action" />
                  <div>
                    <Typography variant="subtitle2" color="text.secondary">
                      Hourly Rate
                    </Typography>
                    <Typography variant="body1" fontWeight="medium">
                      ₡500/hour
                    </Typography>
                  </div>
                </div>

                <div className="flex items-start gap-2">
                  <AccessTime color="action" />
                  <div>
                    <Typography variant="subtitle2" color="text.secondary">
                      Started At
                    </Typography>
                    <Typography variant="body1" fontWeight="medium">
                      2:18 PM
                    </Typography>
                  </div>
                </div>
              </div>
            </CardContent>
          </Card>

          <Card sx={{ bgcolor: "info.50", mb: 3 }}>
            <CardContent>
              <Typography variant="body2" color="info.dark">
                💡 <strong>Tip:</strong> End your session before leaving to avoid additional charges
              </Typography>
            </CardContent>
          </Card>

          <Button
            fullWidth
            variant="contained"
            color="error"
            size="large"
            startIcon={<Stop />}
            onClick={() => setShowEndDialog(true)}
            sx={{ py: 1.5 }}
          >
            End Session
          </Button>
        </div>

        <Dialog open={showEndDialog} onClose={() => setShowEndDialog(false)}>
          <DialogTitle>End Parking Session?</DialogTitle>
          <DialogContent>
            <Typography variant="body2" gutterBottom>
              <strong>Duration:</strong> {formatTime(elapsed)}
            </Typography>
            <Typography variant="body2" gutterBottom>
              <strong>Amount due:</strong> ₡{currentCost}
            </Typography>
            <Typography variant="body2" color="text.secondary" sx={{ mt: 2 }}>
              You will be redirected to payment
            </Typography>
          </DialogContent>
          <DialogActions>
            <Button onClick={() => setShowEndDialog(false)}>Cancel</Button>
            <Button variant="contained" onClick={handleEndSession}>
              Confirm & Pay
            </Button>
          </DialogActions>
        </Dialog>

        <BottomNav />
      </div>
    </MobileContainer>
  );
}
