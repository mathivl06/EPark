import { useState } from "react";
import { MobileContainer } from "../components/MobileContainer";
import { BottomNav } from "../components/BottomNav";
import {
  AppBar,
  Toolbar,
  Typography,
  Card,
  CardContent,
  Chip,
  Button,
  Tabs,
  Tab,
  Box,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Divider,
} from "@mui/material";
import { Warning, CheckCircle, Receipt } from "@mui/icons-material";

const pendingFines = [
  {
    id: 1,
    type: "Expired Parking",
    zone: "Zone A - Centro",
    date: "May 5, 2026",
    amount: 5000,
    dueDate: "May 20, 2026",
    description: "Parking session exceeded maximum time limit",
  },
  {
    id: 2,
    type: "Invalid Spot",
    zone: "Zone B - Plaza",
    date: "May 1, 2026",
    amount: 3500,
    dueDate: "May 16, 2026",
    description: "Vehicle parked in unauthorized spot",
  },
];

const paidFines = [
  {
    id: 3,
    type: "Expired Parking",
    zone: "Zone C - Mercado",
    date: "April 15, 2026",
    amount: 4500,
    paidDate: "April 18, 2026",
  },
];

export function FinesScreen() {
  const [tabValue, setTabValue] = useState(0);
  const [selectedFine, setSelectedFine] = useState<any>(null);
  const [showPaymentDialog, setShowPaymentDialog] = useState(false);

  const handlePayFine = () => {
    setShowPaymentDialog(false);
    setSelectedFine(null);
    alert("Payment processed (prototype)");
  };

  return (
    <MobileContainer withBottomNav>
      <div className="h-full flex flex-col bg-gray-50">
        <AppBar position="static">
          <Toolbar>
            <Typography variant="h6">Fines</Typography>
          </Toolbar>
        </AppBar>

        {pendingFines.length > 0 && (
          <div className="bg-orange-100 px-4 py-3 border-b border-orange-200">
            <Typography variant="body2" color="warning.dark" fontWeight="medium">
              ⚠️ You have {pendingFines.length} pending fine(s)
            </Typography>
          </div>
        )}

        <Tabs
          value={tabValue}
          onChange={(_, newValue) => setTabValue(newValue)}
          variant="fullWidth"
          sx={{ bgcolor: "white", borderBottom: 1, borderColor: "divider" }}
        >
          <Tab label={`Pending (${pendingFines.length})`} />
          <Tab label={`Paid (${paidFines.length})`} />
        </Tabs>

        <div className="flex-1 overflow-y-auto p-4 pb-20">
          {tabValue === 0 && (
            <>
              {pendingFines.length === 0 ? (
                <Card>
                  <CardContent className="text-center py-8">
                    <CheckCircle sx={{ fontSize: 60, color: "success.main", mb: 2 }} />
                    <Typography variant="h6" fontWeight="bold" gutterBottom>
                      No Pending Fines
                    </Typography>
                    <Typography variant="body2" color="text.secondary">
                      You're all caught up!
                    </Typography>
                  </CardContent>
                </Card>
              ) : (
                <div className="space-y-3">
                  {pendingFines.map((fine) => (
                    <Card key={fine.id} sx={{ border: 2, borderColor: "warning.main" }}>
                      <CardContent>
                        <div className="flex items-start justify-between mb-2">
                          <div className="flex items-start gap-2">
                            <Warning color="warning" />
                            <div>
                              <Typography variant="subtitle1" fontWeight="bold">
                                {fine.type}
                              </Typography>
                              <Typography variant="body2" color="text.secondary">
                                {fine.zone}
                              </Typography>
                            </div>
                          </div>
                          <Chip label="Pending" color="warning" size="small" />
                        </div>

                        <Typography variant="body2" color="text.secondary" sx={{ mb: 2 }}>
                          {fine.description}
                        </Typography>

                        <Divider sx={{ my: 2 }} />

                        <div className="flex justify-between items-center mb-3">
                          <div>
                            <Typography variant="caption" color="text.secondary">
                              Fine Amount
                            </Typography>
                            <Typography variant="h5" fontWeight="bold" color="error">
                              ₡{fine.amount}
                            </Typography>
                          </div>
                          <div className="text-right">
                            <Typography variant="caption" color="text.secondary">
                              Due Date
                            </Typography>
                            <Typography variant="body2" fontWeight="medium">
                              {fine.dueDate}
                            </Typography>
                          </div>
                        </div>

                        <Button
                          fullWidth
                          variant="contained"
                          color="error"
                          onClick={() => {
                            setSelectedFine(fine);
                            setShowPaymentDialog(true);
                          }}
                        >
                          Pay Fine
                        </Button>
                      </CardContent>
                    </Card>
                  ))}
                </div>
              )}
            </>
          )}

          {tabValue === 1 && (
            <div className="space-y-3">
              {paidFines.map((fine) => (
                <Card key={fine.id}>
                  <CardContent>
                    <div className="flex items-start justify-between mb-2">
                      <div className="flex items-start gap-2">
                        <Receipt color="action" />
                        <div>
                          <Typography variant="subtitle1" fontWeight="bold">
                            {fine.type}
                          </Typography>
                          <Typography variant="body2" color="text.secondary">
                            {fine.zone}
                          </Typography>
                        </div>
                      </div>
                      <Chip
                        icon={<CheckCircle />}
                        label="Paid"
                        color="success"
                        size="small"
                      />
                    </div>

                    <Divider sx={{ my: 2 }} />

                    <div className="space-y-1">
                      <div className="flex justify-between">
                        <Typography variant="body2" color="text.secondary">
                          Fine Date
                        </Typography>
                        <Typography variant="body2" fontWeight="medium">
                          {fine.date}
                        </Typography>
                      </div>
                      <div className="flex justify-between">
                        <Typography variant="body2" color="text.secondary">
                          Paid Date
                        </Typography>
                        <Typography variant="body2" fontWeight="medium">
                          {fine.paidDate}
                        </Typography>
                      </div>
                      <div className="flex justify-between">
                        <Typography variant="body2" color="text.secondary">
                          Amount
                        </Typography>
                        <Typography variant="body2" fontWeight="medium">
                          ₡{fine.amount}
                        </Typography>
                      </div>
                    </div>
                  </CardContent>
                </Card>
              ))}
            </div>
          )}
        </div>

        <Dialog open={showPaymentDialog} onClose={() => setShowPaymentDialog(false)}>
          <DialogTitle>Pay Fine</DialogTitle>
          <DialogContent>
            {selectedFine && (
              <>
                <Typography variant="body2" gutterBottom>
                  <strong>Fine Type:</strong> {selectedFine.type}
                </Typography>
                <Typography variant="body2" gutterBottom>
                  <strong>Amount:</strong> ₡{selectedFine.amount}
                </Typography>
                <Typography variant="body2" gutterBottom>
                  <strong>Due Date:</strong> {selectedFine.dueDate}
                </Typography>
                <Typography variant="body2" color="text.secondary" sx={{ mt: 2 }}>
                  Payment will be processed using your default payment method
                </Typography>
              </>
            )}
          </DialogContent>
          <DialogActions>
            <Button onClick={() => setShowPaymentDialog(false)}>Cancel</Button>
            <Button variant="contained" color="error" onClick={handlePayFine}>
              Confirm Payment
            </Button>
          </DialogActions>
        </Dialog>

        <BottomNav />
      </div>
    </MobileContainer>
  );
}
