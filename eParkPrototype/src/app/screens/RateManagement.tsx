import { useState } from "react";
import { useNavigate } from "react-router";
import { MobileContainer } from "../components/MobileContainer";
import {
  AppBar,
  Toolbar,
  Typography,
  IconButton,
  Card,
  CardContent,
  TextField,
  Button,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  MenuItem,
} from "@mui/material";
import { ArrowBack, Edit, Save } from "@mui/icons-material";

const zones = [
  { id: 1, name: "Zone A - Centro", currentRate: 500 },
  { id: 2, name: "Zone B - Plaza", currentRate: 400 },
  { id: 3, name: "Zone C - Mercado", currentRate: 350 },
  { id: 4, name: "Zone D - Parque", currentRate: 300 },
  { id: 5, name: "Zone E - Hospital", currentRate: 450 },
];

export function RateManagement() {
  const navigate = useNavigate();
  const [selectedZone, setSelectedZone] = useState<number | null>(null);
  const [newRate, setNewRate] = useState("");
  const [showDialog, setShowDialog] = useState(false);

  const handleEditRate = (zone: any) => {
    setSelectedZone(zone.id);
    setNewRate(zone.currentRate.toString());
    setShowDialog(true);
  };

  const handleSaveRate = () => {
    alert("Rate updated (prototype)");
    setShowDialog(false);
    setSelectedZone(null);
    setNewRate("");
  };

  return (
    <MobileContainer>
      <div className="h-full flex flex-col bg-gray-50">
        <AppBar position="static">
          <Toolbar>
            <IconButton
              edge="start"
              color="inherit"
              onClick={() => navigate("/admin")}
            >
              <ArrowBack />
            </IconButton>
            <Typography variant="h6">Rate Management</Typography>
          </Toolbar>
        </AppBar>

        <div className="p-4">
          <Card sx={{ mb: 3, bgcolor: "info.50" }}>
            <CardContent>
              <Typography variant="body2" color="info.dark">
                💡 Rate changes will apply to new parking sessions only. Active sessions maintain their original rate.
              </Typography>
            </CardContent>
          </Card>
        </div>

        <div className="flex-1 overflow-y-auto px-4 pb-4">
          <div className="space-y-3">
            {zones.map((zone) => (
              <Card key={zone.id}>
                <CardContent>
                  <div className="flex items-center justify-between">
                    <div className="flex-1">
                      <Typography variant="subtitle1" fontWeight="bold">
                        {zone.name}
                      </Typography>
                      <div className="flex items-baseline gap-2 mt-1">
                        <Typography variant="h6" color="primary" fontWeight="bold">
                          ₡{zone.currentRate}
                        </Typography>
                        <Typography variant="caption" color="text.secondary">
                          per hour
                        </Typography>
                      </div>
                    </div>
                    <Button
                      variant="outlined"
                      size="small"
                      startIcon={<Edit />}
                      onClick={() => handleEditRate(zone)}
                    >
                      Update
                    </Button>
                  </div>
                </CardContent>
              </Card>
            ))}
          </div>
        </div>

        <Dialog open={showDialog} onClose={() => setShowDialog(false)}>
          <DialogTitle>Update Parking Rate</DialogTitle>
          <DialogContent>
            <TextField
              select
              fullWidth
              label="Select Zone"
              value={selectedZone || ""}
              onChange={(e) => setSelectedZone(Number(e.target.value))}
              sx={{ mt: 2, mb: 3 }}
            >
              {zones.map((zone) => (
                <MenuItem key={zone.id} value={zone.id}>
                  {zone.name}
                </MenuItem>
              ))}
            </TextField>

            <TextField
              fullWidth
              label="New Hourly Rate (₡)"
              type="number"
              value={newRate}
              onChange={(e) => setNewRate(e.target.value)}
              helperText="Enter the new rate per hour in Costa Rican Colones"
            />

            <Typography variant="caption" color="text.secondary" sx={{ mt: 2, display: "block" }}>
              Current rate: ₡
              {zones.find((z) => z.id === selectedZone)?.currentRate}/hour
            </Typography>
          </DialogContent>
          <DialogActions>
            <Button onClick={() => setShowDialog(false)}>Cancel</Button>
            <Button
              variant="contained"
              startIcon={<Save />}
              onClick={handleSaveRate}
              disabled={!newRate || !selectedZone}
            >
              Save Changes
            </Button>
          </DialogActions>
        </Dialog>
      </div>
    </MobileContainer>
  );
}
