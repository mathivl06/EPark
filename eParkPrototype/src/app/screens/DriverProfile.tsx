import { useState } from "react";
import { useNavigate } from "react-router";
import { MobileContainer } from "../components/MobileContainer";
import { BottomNav } from "../components/BottomNav";
import {
  AppBar,
  Toolbar,
  Typography,
  Card,
  CardContent,
  List,
  ListItem,
  ListItemText,
  ListItemIcon,
  Divider,
  Avatar,
  IconButton,
  Chip,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Button,
} from "@mui/material";
import {
  Person,
  DirectionsCar,
  CreditCard,
  Edit,
  ChevronRight,
  Logout,
  Notifications,
  Language,
  Security,
  Help,
} from "@mui/icons-material";

export function DriverProfile() {
  const navigate = useNavigate();
  const [showLogoutDialog, setShowLogoutDialog] = useState(false);

  const handleLogout = () => {
    setShowLogoutDialog(false);
    navigate("/login");
  };

  return (
    <MobileContainer withBottomNav>
      <div className="h-full flex flex-col bg-gray-50">
        <AppBar position="static">
          <Toolbar>
            <Typography variant="h6">Profile</Typography>
          </Toolbar>
        </AppBar>

        <div className="flex-1 overflow-y-auto pb-20">
          <div className="bg-gradient-to-br from-blue-600 to-blue-800 p-6 text-white">
            <div className="flex items-center gap-4">
              <Avatar
                sx={{ width: 80, height: 80, bgcolor: "white", color: "primary.main" }}
              >
                <Person sx={{ fontSize: 40 }} />
              </Avatar>
              <div className="flex-1">
                <Typography variant="h5" fontWeight="bold" gutterBottom>
                  Juan Pérez
                </Typography>
                <Typography variant="body2" sx={{ opacity: 0.9 }}>
                  juan.perez@email.com
                </Typography>
                <Chip
                  label="Verified"
                  size="small"
                  sx={{
                    mt: 1,
                    bgcolor: "rgba(255,255,255,0.2)",
                    color: "white",
                  }}
                />
              </div>
              <IconButton sx={{ color: "white" }}>
                <Edit />
              </IconButton>
            </div>
          </div>

          <div className="p-4 space-y-3">
            <Card>
              <CardContent>
                <div className="flex items-center justify-between mb-2">
                  <Typography variant="subtitle1" fontWeight="bold">
                    Personal Information
                  </Typography>
                  <IconButton size="small">
                    <Edit fontSize="small" />
                  </IconButton>
                </div>
                <Divider sx={{ my: 2 }} />
                <div className="space-y-2">
                  <div>
                    <Typography variant="caption" color="text.secondary">
                      Full Name
                    </Typography>
                    <Typography variant="body2" fontWeight="medium">
                      Juan Carlos Pérez González
                    </Typography>
                  </div>
                  <div>
                    <Typography variant="caption" color="text.secondary">
                      National ID
                    </Typography>
                    <Typography variant="body2" fontWeight="medium">
                      1-2345-6789
                    </Typography>
                  </div>
                  <div>
                    <Typography variant="caption" color="text.secondary">
                      Phone Number
                    </Typography>
                    <Typography variant="body2" fontWeight="medium">
                      +506 8888-9999
                    </Typography>
                  </div>
                </div>
              </CardContent>
            </Card>

            <Card>
              <List>
                <ListItem key="vehicles">
                  <ListItemIcon>
                    <DirectionsCar />
                  </ListItemIcon>
                  <ListItemText
                    primary="My Vehicles"
                    secondary="2 vehicles registered"
                  />
                  <ChevronRight />
                </ListItem>
                <Divider variant="inset" component="li" />
                <ListItem key="payment">
                  <ListItemIcon>
                    <CreditCard />
                  </ListItemIcon>
                  <ListItemText
                    primary="Payment Methods"
                    secondary="2 cards saved"
                  />
                  <ChevronRight />
                </ListItem>
              </List>
            </Card>

            <Typography variant="subtitle2" color="text.secondary" sx={{ px: 1, mt: 3 }}>
              App Settings
            </Typography>

            <Card>
              <List>
                <ListItem key="notifications">
                  <ListItemIcon>
                    <Notifications />
                  </ListItemIcon>
                  <ListItemText
                    primary="Notifications"
                    secondary="Manage notification preferences"
                  />
                  <ChevronRight />
                </ListItem>
                <Divider variant="inset" component="li" />
                <ListItem key="language">
                  <ListItemIcon>
                    <Language />
                  </ListItemIcon>
                  <ListItemText primary="Language" secondary="English" />
                  <ChevronRight />
                </ListItem>
                <Divider variant="inset" component="li" />
                <ListItem key="security">
                  <ListItemIcon>
                    <Security />
                  </ListItemIcon>
                  <ListItemText
                    primary="Security"
                    secondary="Password & privacy settings"
                  />
                  <ChevronRight />
                </ListItem>
              </List>
            </Card>

            <Typography variant="subtitle2" color="text.secondary" sx={{ px: 1, mt: 3 }}>
              Support
            </Typography>

            <Card>
              <List>
                <ListItem key="help">
                  <ListItemIcon>
                    <Help />
                  </ListItemIcon>
                  <ListItemText
                    primary="Help Center"
                    secondary="FAQs and support"
                  />
                  <ChevronRight />
                </ListItem>
              </List>
            </Card>

            <Card sx={{ mt: 3 }}>
              <List>
                <ListItem
                  key="logout"
                  onClick={() => setShowLogoutDialog(true)}
                  sx={{ cursor: "pointer" }}
                >
                  <ListItemIcon>
                    <Logout color="error" />
                  </ListItemIcon>
                  <ListItemText
                    primary="Logout"
                    primaryTypographyProps={{ color: "error" }}
                  />
                </ListItem>
              </List>
            </Card>

            <div className="text-center py-4">
              <Typography variant="caption" color="text.secondary">
                e-park v1.0.0
              </Typography>
            </div>
          </div>
        </div>

        <Dialog open={showLogoutDialog} onClose={() => setShowLogoutDialog(false)}>
          <DialogTitle>Logout</DialogTitle>
          <DialogContent>
            <Typography variant="body2">
              Are you sure you want to logout?
            </Typography>
          </DialogContent>
          <DialogActions>
            <Button onClick={() => setShowLogoutDialog(false)}>Cancel</Button>
            <Button variant="contained" color="error" onClick={handleLogout}>
              Logout
            </Button>
          </DialogActions>
        </Dialog>

        <BottomNav />
      </div>
    </MobileContainer>
  );
}
