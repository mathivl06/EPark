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
  Button,
  TextField,
  MenuItem,
  Stepper,
  Step,
  StepLabel,
  Chip,
} from "@mui/material";
import { ArrowBack, DirectionsCar, CheckCircle } from "@mui/icons-material";

const vehicles = [
  { id: 1, plate: "ABC-1234", model: "Toyota Corolla 2020" },
  { id: 2, plate: "XYZ-5678", model: "Honda Civic 2019" },
];

export function StartParking() {
  const navigate = useNavigate();
  const [activeStep, setActiveStep] = useState(0);
  const [selectedVehicle, setSelectedVehicle] = useState("ABC-1234");
  const [spotCode, setSpotCode] = useState("");
  const [validatedSpot, setValidatedSpot] = useState<any>(null);

  const steps = ["Select Vehicle", "Enter Spot Code", "Confirm"];

  const handleNext = () => {
    if (activeStep === 0) {
      setActiveStep(1);
    } else if (activeStep === 1) {
      setValidatedSpot({
        zone: "Zone A - Centro",
        spot: "142",
        address: "Av. Central, San José",
        rate: "₡500/hour",
      });
      setActiveStep(2);
    } else {
      navigate("/active-session");
    }
  };

  return (
    <MobileContainer>
      <div className="h-full flex flex-col bg-gray-50">
        <AppBar position="static">
          <Toolbar>
            <IconButton
              edge="start"
              color="inherit"
              onClick={() => navigate("/driver")}
            >
              <ArrowBack />
            </IconButton>
            <Typography variant="h6">Start Parking</Typography>
          </Toolbar>
        </AppBar>

        <div className="p-4 flex-1 overflow-y-auto">
          <Stepper activeStep={activeStep} sx={{ mb: 4 }}>
            {steps.map((label) => (
              <Step key={label}>
                <StepLabel>{label}</StepLabel>
              </Step>
            ))}
          </Stepper>

          {activeStep === 0 && (
            <Card>
              <CardContent>
                <Typography variant="subtitle1" fontWeight="bold" gutterBottom>
                  Select Your Vehicle
                </Typography>
                <TextField
                  select
                  fullWidth
                  value={selectedVehicle}
                  onChange={(e) => setSelectedVehicle(e.target.value)}
                  sx={{ mt: 2 }}
                >
                  {vehicles.map((vehicle) => (
                    <MenuItem key={vehicle.id} value={vehicle.plate}>
                      <div className="flex items-center gap-2">
                        <DirectionsCar />
                        <div>
                          <Typography variant="body2" fontWeight="medium">
                            {vehicle.plate}
                          </Typography>
                          <Typography variant="caption" color="text.secondary">
                            {vehicle.model}
                          </Typography>
                        </div>
                      </div>
                    </MenuItem>
                  ))}
                </TextField>
              </CardContent>
            </Card>
          )}

          {activeStep === 1 && (
            <Card>
              <CardContent>
                <Typography variant="subtitle1" fontWeight="bold" gutterBottom>
                  Enter Parking Spot Code
                </Typography>
                <Typography variant="body2" color="text.secondary" sx={{ mb: 3 }}>
                  Find the 4-digit code on the parking spot sign
                </Typography>
                <TextField
                  fullWidth
                  label="Spot Code"
                  placeholder="0142"
                  value={spotCode}
                  onChange={(e) => {
                    const value = e.target.value.replace(/\D/g, "").slice(0, 4);
                    setSpotCode(value);
                  }}
                  inputProps={{
                    maxLength: 4,
                    style: { fontSize: "24px", textAlign: "center", letterSpacing: "8px" },
                  }}
                />
                <Typography variant="caption" color="text.secondary" sx={{ mt: 1, display: "block" }}>
                  Example: 0142, 0256, 1024
                </Typography>
              </CardContent>
            </Card>
          )}

          {activeStep === 2 && validatedSpot && (
            <div className="space-y-3">
              <Card sx={{ bgcolor: "success.50" }}>
                <CardContent className="text-center">
                  <CheckCircle sx={{ fontSize: 60, color: "success.main", mb: 1 }} />
                  <Typography variant="h6" fontWeight="bold">
                    Spot Validated!
                  </Typography>
                </CardContent>
              </Card>

              <Card>
                <CardContent>
                  <Typography variant="subtitle2" color="text.secondary" gutterBottom>
                    Vehicle
                  </Typography>
                  <Typography variant="body1" fontWeight="bold" gutterBottom>
                    {selectedVehicle}
                  </Typography>

                  <Typography variant="subtitle2" color="text.secondary" gutterBottom sx={{ mt: 2 }}>
                    Location
                  </Typography>
                  <Typography variant="body1" fontWeight="bold">
                    {validatedSpot.zone}
                  </Typography>
                  <Typography variant="body2" color="text.secondary">
                    {validatedSpot.address}
                  </Typography>

                  <div className="flex items-center justify-between mt-3 pt-3 border-t">
                    <div>
                      <Typography variant="subtitle2" color="text.secondary">
                        Spot Number
                      </Typography>
                      <Typography variant="h5" fontWeight="bold">
                        #{validatedSpot.spot}
                      </Typography>
                    </div>
                    <div className="text-right">
                      <Typography variant="subtitle2" color="text.secondary">
                        Rate
                      </Typography>
                      <Chip label={validatedSpot.rate} color="primary" />
                    </div>
                  </div>
                </CardContent>
              </Card>
            </div>
          )}
        </div>

        <div className="p-4 border-t bg-white">
          <Button
            fullWidth
            variant="contained"
            size="large"
            onClick={handleNext}
            disabled={activeStep === 1 && spotCode.length !== 4}
            sx={{ py: 1.5 }}
          >
            {activeStep === 2 ? "Start Parking Session" : "Continue"}
          </Button>
        </div>
      </div>
    </MobileContainer>
  );
}
