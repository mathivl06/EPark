import { useState } from "react";
import { useNavigate, useParams } from "react-router";
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
  Switch,
  FormControlLabel,
  MenuItem,
} from "@mui/material";
import { ArrowBack, Save } from "@mui/icons-material";

export function ZoneEditor() {
  const navigate = useNavigate();
  const { id } = useParams();
  const isEditing = id !== "new";

  const [formData, setFormData] = useState({
    name: isEditing ? "Zone A - Centro" : "",
    address: isEditing ? "Av. Central, San José" : "",
    spots: isEditing ? "20" : "",
    rate: isEditing ? "500" : "",
    startTime: isEditing ? "06:00" : "",
    endTime: isEditing ? "22:00" : "",
    status: isEditing ? true : false,
    operatingDays: isEditing ? "weekdays" : "",
  });

  const handleSave = () => {
    alert(isEditing ? "Zone updated (prototype)" : "Zone created (prototype)");
    navigate("/admin/zones");
  };

  return (
    <MobileContainer>
      <div className="h-full flex flex-col bg-gray-50">
        <AppBar position="static">
          <Toolbar>
            <IconButton
              edge="start"
              color="inherit"
              onClick={() => navigate("/admin/zones")}
            >
              <ArrowBack />
            </IconButton>
            <Typography variant="h6">
              {isEditing ? "Edit Zone" : "Create New Zone"}
            </Typography>
          </Toolbar>
        </AppBar>

        <div className="flex-1 overflow-y-auto p-4">
          <Card sx={{ mb: 3 }}>
            <CardContent>
              <Typography variant="subtitle1" fontWeight="bold" gutterBottom>
                Basic Information
              </Typography>

              <div className="space-y-3 mt-3">
                <TextField
                  fullWidth
                  label="Zone Name"
                  placeholder="Zone A - Centro"
                  value={formData.name}
                  onChange={(e) =>
                    setFormData({ ...formData, name: e.target.value })
                  }
                />

                <TextField
                  fullWidth
                  label="Address"
                  placeholder="Av. Central, San José"
                  value={formData.address}
                  onChange={(e) =>
                    setFormData({ ...formData, address: e.target.value })
                  }
                />

                <TextField
                  fullWidth
                  label="Available Parking Spots"
                  type="number"
                  value={formData.spots}
                  onChange={(e) =>
                    setFormData({ ...formData, spots: e.target.value })
                  }
                />

                <TextField
                  fullWidth
                  label="Hourly Rate (₡)"
                  type="number"
                  value={formData.rate}
                  onChange={(e) =>
                    setFormData({ ...formData, rate: e.target.value })
                  }
                  helperText="Rate per hour in Costa Rican Colones"
                />
              </div>
            </CardContent>
          </Card>

          <Card sx={{ mb: 3 }}>
            <CardContent>
              <Typography variant="subtitle1" fontWeight="bold" gutterBottom>
                Operating Schedule
              </Typography>

              <div className="space-y-3 mt-3">
                <TextField
                  fullWidth
                  select
                  label="Operating Days"
                  value={formData.operatingDays}
                  onChange={(e) =>
                    setFormData({ ...formData, operatingDays: e.target.value })
                  }
                >
                  <MenuItem value="everyday">Every Day</MenuItem>
                  <MenuItem value="weekdays">Weekdays Only</MenuItem>
                  <MenuItem value="weekends">Weekends Only</MenuItem>
                  <MenuItem value="custom">Custom Schedule</MenuItem>
                </TextField>

                <div className="grid grid-cols-2 gap-3">
                  <TextField
                    label="Start Time"
                    type="time"
                    value={formData.startTime}
                    onChange={(e) =>
                      setFormData({ ...formData, startTime: e.target.value })
                    }
                    InputLabelProps={{ shrink: true }}
                  />
                  <TextField
                    label="End Time"
                    type="time"
                    value={formData.endTime}
                    onChange={(e) =>
                      setFormData({ ...formData, endTime: e.target.value })
                    }
                    InputLabelProps={{ shrink: true }}
                  />
                </div>
              </div>
            </CardContent>
          </Card>

          <Card>
            <CardContent>
              <FormControlLabel
                control={
                  <Switch
                    checked={formData.status}
                    onChange={(e) =>
                      setFormData({ ...formData, status: e.target.checked })
                    }
                  />
                }
                label={
                  <div>
                    <Typography variant="body2" fontWeight="medium">
                      Zone Active
                    </Typography>
                    <Typography variant="caption" color="text.secondary">
                      Drivers can see and use this zone
                    </Typography>
                  </div>
                }
              />
            </CardContent>
          </Card>
        </div>

        <div className="p-4 border-t bg-white">
          <Button
            fullWidth
            variant="contained"
            size="large"
            startIcon={<Save />}
            onClick={handleSave}
            sx={{ py: 1.5 }}
          >
            {isEditing ? "Save Changes" : "Create Zone"}
          </Button>
        </div>
      </div>
    </MobileContainer>
  );
}
