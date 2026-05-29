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
  Radio,
  RadioGroup,
  FormControlLabel,
  Divider,
  Stepper,
  Step,
  StepLabel,
} from "@mui/material";
import {
  ArrowBack,
  CreditCard,
  AccountBalance,
  CheckCircle,
  Download,
} from "@mui/icons-material";

const savedCards = [
  { id: 1, last4: "4242", brand: "Visa", expiry: "12/25" },
  { id: 2, last4: "5555", brand: "Mastercard", expiry: "08/26" },
];

export function PaymentFlow() {
  const navigate = useNavigate();
  const [activeStep, setActiveStep] = useState(0);
  const [selectedPayment, setSelectedPayment] = useState("card-1");

  const steps = ["Select Method", "Confirm", "Complete"];

  const handleNext = () => {
    if (activeStep === steps.length - 1) {
      navigate("/driver");
    } else {
      setActiveStep((prev) => prev + 1);
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
              onClick={() => navigate("/active-session")}
            >
              <ArrowBack />
            </IconButton>
            <Typography variant="h6">Payment</Typography>
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
            <>
              <Card sx={{ mb: 3 }}>
                <CardContent>
                  <Typography variant="h5" fontWeight="bold" gutterBottom>
                    ₡850
                  </Typography>
                  <Typography variant="body2" color="text.secondary">
                    Amount due for parking session
                  </Typography>
                </CardContent>
              </Card>

              <Typography variant="subtitle1" fontWeight="bold" sx={{ mb: 2 }}>
                Saved Payment Methods
              </Typography>

              <RadioGroup
                value={selectedPayment}
                onChange={(e) => setSelectedPayment(e.target.value)}
              >
                {savedCards.map((card) => (
                  <Card key={card.id} sx={{ mb: 2 }}>
                    <CardContent>
                      <FormControlLabel
                        value={`card-${card.id}`}
                        control={<Radio />}
                        label={
                          <div className="flex items-center gap-2 ml-2">
                            <CreditCard />
                            <div>
                              <Typography variant="body1" fontWeight="medium">
                                {card.brand} •••• {card.last4}
                              </Typography>
                              <Typography variant="caption" color="text.secondary">
                                Expires {card.expiry}
                              </Typography>
                            </div>
                          </div>
                        }
                      />
                    </CardContent>
                  </Card>
                ))}

                <Card>
                  <CardContent>
                    <FormControlLabel
                      value="bank"
                      control={<Radio />}
                      label={
                        <div className="flex items-center gap-2 ml-2">
                          <AccountBalance />
                          <Typography variant="body1" fontWeight="medium">
                            Bank Transfer
                          </Typography>
                        </div>
                      }
                    />
                  </CardContent>
                </Card>
              </RadioGroup>
            </>
          )}

          {activeStep === 1 && (
            <>
              <Card sx={{ mb: 3 }}>
                <CardContent>
                  <Typography variant="subtitle2" color="text.secondary" gutterBottom>
                    Payment Summary
                  </Typography>
                  <Divider sx={{ my: 2 }} />
                  <div className="space-y-2">
                    <div className="flex justify-between">
                      <Typography variant="body2">Parking duration</Typography>
                      <Typography variant="body2" fontWeight="medium">
                        1h 42min
                      </Typography>
                    </div>
                    <div className="flex justify-between">
                      <Typography variant="body2">Zone A - Spot #142</Typography>
                      <Typography variant="body2" fontWeight="medium">
                        ₡500/hr
                      </Typography>
                    </div>
                    <Divider sx={{ my: 2 }} />
                    <div className="flex justify-between">
                      <Typography variant="h6" fontWeight="bold">
                        Total
                      </Typography>
                      <Typography variant="h6" fontWeight="bold" color="primary">
                        ₡850
                      </Typography>
                    </div>
                  </div>
                </CardContent>
              </Card>

              <Card>
                <CardContent>
                  <Typography variant="subtitle2" color="text.secondary" gutterBottom>
                    Payment Method
                  </Typography>
                  <div className="flex items-center gap-2 mt-2">
                    <CreditCard color="primary" />
                    <Typography variant="body1" fontWeight="medium">
                      Visa •••• 4242
                    </Typography>
                  </div>
                </CardContent>
              </Card>
            </>
          )}

          {activeStep === 2 && (
            <>
              <Card sx={{ bgcolor: "success.50", mb: 3 }}>
                <CardContent className="text-center">
                  <CheckCircle sx={{ fontSize: 80, color: "success.main", mb: 2 }} />
                  <Typography variant="h5" fontWeight="bold" gutterBottom>
                    Payment Successful!
                  </Typography>
                  <Typography variant="body2" color="text.secondary">
                    Your parking session has been completed
                  </Typography>
                </CardContent>
              </Card>

              <Card sx={{ mb: 3 }}>
                <CardContent>
                  <Typography variant="subtitle2" color="text.secondary" gutterBottom>
                    Receipt #PS-2026-0507-142
                  </Typography>
                  <Divider sx={{ my: 2 }} />
                  <div className="space-y-2">
                    <div className="flex justify-between">
                      <Typography variant="body2">Date</Typography>
                      <Typography variant="body2" fontWeight="medium">
                        May 7, 2026
                      </Typography>
                    </div>
                    <div className="flex justify-between">
                      <Typography variant="body2">Location</Typography>
                      <Typography variant="body2" fontWeight="medium">
                        Zone A - #142
                      </Typography>
                    </div>
                    <div className="flex justify-between">
                      <Typography variant="body2">Duration</Typography>
                      <Typography variant="body2" fontWeight="medium">
                        1h 42min
                      </Typography>
                    </div>
                    <div className="flex justify-between">
                      <Typography variant="body2">Vehicle</Typography>
                      <Typography variant="body2" fontWeight="medium">
                        ABC-1234
                      </Typography>
                    </div>
                    <Divider sx={{ my: 2 }} />
                    <div className="flex justify-between">
                      <Typography variant="body1" fontWeight="bold">
                        Amount Paid
                      </Typography>
                      <Typography variant="body1" fontWeight="bold" color="primary">
                        ₡850
                      </Typography>
                    </div>
                  </div>
                </CardContent>
              </Card>

              <Button
                fullWidth
                variant="outlined"
                startIcon={<Download />}
                sx={{ mb: 2 }}
              >
                Download Receipt
              </Button>
            </>
          )}
        </div>

        <div className="p-4 border-t bg-white">
          <Button
            fullWidth
            variant="contained"
            size="large"
            onClick={handleNext}
            sx={{ py: 1.5 }}
          >
            {activeStep === 0
              ? "Continue"
              : activeStep === 1
              ? "Confirm Payment"
              : "Done"}
          </Button>
        </div>
      </div>
    </MobileContainer>
  );
}
