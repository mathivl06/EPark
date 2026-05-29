import { useState } from "react";
import { useNavigate } from "react-router";
import { MobileContainer } from "../components/MobileContainer";
import {
  Button,
  TextField,
  Stepper,
  Step,
  StepLabel,
  AppBar,
  Toolbar,
  IconButton,
  Typography,
  Paper,
} from "@mui/material";
import { ArrowBack, CheckCircle } from "@mui/icons-material";

export function DriverRegistration() {
  const navigate = useNavigate();
  const [activeStep, setActiveStep] = useState(0);
  const [formData, setFormData] = useState({
    fullName: "",
    nationalId: "",
    email: "",
    password: "",
    confirmPassword: "",
    vehiclePlate: "",
  });

  const steps = ["Personal Info", "Email Verification", "Vehicle", "Complete"];

  const handleNext = () => {
    if (activeStep === steps.length - 1) {
      navigate("/driver");
    } else {
      setActiveStep((prev) => prev + 1);
    }
  };

  const handleBack = () => {
    if (activeStep === 0) {
      navigate("/login");
    } else {
      setActiveStep((prev) => prev - 1);
    }
  };

  const renderStepContent = (step: number) => {
    switch (step) {
      case 0:
        return (
          <div className="space-y-4">
            <TextField
              fullWidth
              label="Full Name"
              variant="outlined"
              value={formData.fullName}
              onChange={(e) =>
                setFormData({ ...formData, fullName: e.target.value })
              }
            />
            <TextField
              fullWidth
              label="National ID"
              variant="outlined"
              value={formData.nationalId}
              onChange={(e) =>
                setFormData({ ...formData, nationalId: e.target.value })
              }
            />
            <TextField
              fullWidth
              label="Email"
              type="email"
              variant="outlined"
              value={formData.email}
              onChange={(e) =>
                setFormData({ ...formData, email: e.target.value })
              }
            />
            <TextField
              fullWidth
              label="Password"
              type="password"
              variant="outlined"
              value={formData.password}
              onChange={(e) =>
                setFormData({ ...formData, password: e.target.value })
              }
            />
            <TextField
              fullWidth
              label="Confirm Password"
              type="password"
              variant="outlined"
              value={formData.confirmPassword}
              onChange={(e) =>
                setFormData({ ...formData, confirmPassword: e.target.value })
              }
            />
          </div>
        );
      case 1:
        return (
          <Paper className="p-6 text-center" elevation={0} sx={{ bgcolor: "blue.50" }}>
            <CheckCircle sx={{ fontSize: 60, color: "success.main", mb: 2 }} />
            <Typography variant="h6" gutterBottom>
              Verification Email Sent
            </Typography>
            <Typography variant="body2" color="text.secondary">
              We've sent a verification link to {formData.email}. Please check your inbox.
            </Typography>
            <Typography variant="caption" color="text.secondary" sx={{ mt: 2, display: "block" }}>
              (Simulated for prototype)
            </Typography>
          </Paper>
        );
      case 2:
        return (
          <div className="space-y-4">
            <TextField
              fullWidth
              label="Vehicle Plate Number"
              variant="outlined"
              placeholder="ABC-1234"
              value={formData.vehiclePlate}
              onChange={(e) =>
                setFormData({ ...formData, vehiclePlate: e.target.value })
              }
            />
            <Typography variant="caption" color="text.secondary">
              You can add more vehicles later from your profile
            </Typography>
          </div>
        );
      case 3:
        return (
          <Paper className="p-6 text-center" elevation={0} sx={{ bgcolor: "success.50" }}>
            <CheckCircle sx={{ fontSize: 80, color: "success.main", mb: 2 }} />
            <Typography variant="h5" gutterBottom fontWeight="bold">
              Registration Complete!
            </Typography>
            <Typography variant="body1" color="text.secondary" sx={{ mb: 3 }}>
              Your account has been created successfully
            </Typography>
            <Typography variant="body2" color="text.secondary">
              Welcome, {formData.fullName}
            </Typography>
          </Paper>
        );
      default:
        return null;
    }
  };

  return (
    <MobileContainer>
      <div className="h-full flex flex-col bg-white">
        <AppBar position="static" elevation={0}>
          <Toolbar>
            <IconButton
              edge="start"
              color="inherit"
              onClick={handleBack}
              sx={{ mr: 2 }}
            >
              <ArrowBack />
            </IconButton>
            <Typography variant="h6">Registration</Typography>
          </Toolbar>
        </AppBar>

        <div className="p-6 flex-1 overflow-y-auto">
          <Stepper activeStep={activeStep} sx={{ mb: 4 }}>
            {steps.map((label) => (
              <Step key={label}>
                <StepLabel>{label}</StepLabel>
              </Step>
            ))}
          </Stepper>

          {renderStepContent(activeStep)}
        </div>

        <div className="p-6 border-t">
          <Button
            fullWidth
            variant="contained"
            size="large"
            onClick={handleNext}
            sx={{ py: 1.5 }}
          >
            {activeStep === steps.length - 1 ? "Get Started" : "Continue"}
          </Button>
        </div>
      </div>
    </MobileContainer>
  );
}
