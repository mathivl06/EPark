import { useState } from "react";
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
  Chip,
  Button,
  TextField,
  InputAdornment,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
} from "@mui/material";
import {
  ArrowBack,
  MyLocation,
  Search,
  FilterList,
  DirectionsWalk,
  Schedule,
  AttachMoney,
} from "@mui/icons-material";

const zones = [
  {
    id: 1,
    name: "Zone A - Centro",
    address: "Av. Central, San José",
    distance: "120 m",
    available: 8,
    total: 20,
    rate: "₡500/hour",
    hours: "6:00 AM - 10:00 PM",
  },
  {
    id: 2,
    name: "Zone B - Plaza",
    address: "Calle 3, San José",
    distance: "350 m",
    available: 3,
    total: 15,
    rate: "₡400/hour",
    hours: "24 hours",
  },
  {
    id: 3,
    name: "Zone C - Mercado",
    address: "Av. 2, San José",
    distance: "580 m",
    available: 0,
    total: 12,
    rate: "₡350/hour",
    hours: "5:00 AM - 9:00 PM",
  },
  {
    id: 4,
    name: "Zone D - Parque",
    address: "Calle 5, San José",
    distance: "720 m",
    available: 15,
    total: 25,
    rate: "₡300/hour",
    hours: "6:00 AM - 10:00 PM",
  },
];

export function NearbyZones() {
  const navigate = useNavigate();
  const [showGpsDialog, setShowGpsDialog] = useState(false);
  const [search, setSearch] = useState("");

  return (
    <MobileContainer withBottomNav>
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
            <Typography variant="h6" sx={{ flexGrow: 1 }}>
              Nearby Zones
            </Typography>
            <IconButton color="inherit" onClick={() => setShowGpsDialog(true)}>
              <MyLocation />
            </IconButton>
          </Toolbar>
        </AppBar>

        <div className="p-4">
          <div className="flex gap-2 mb-3">
            <TextField
              fullWidth
              size="small"
              placeholder="Search zones..."
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              InputProps={{
                startAdornment: (
                  <InputAdornment position="start">
                    <Search />
                  </InputAdornment>
                ),
              }}
              sx={{ bgcolor: "white" }}
            />
            <Button variant="outlined" startIcon={<FilterList />}>
              Filter
            </Button>
          </div>

          <Card sx={{ mb: 3, bgcolor: "info.50" }}>
            <CardContent className="text-center">
              <Typography variant="body2" color="primary" fontWeight="medium">
                📍 Using current location
              </Typography>
              <Typography variant="caption" color="text.secondary">
                San José, Costa Rica
              </Typography>
            </CardContent>
          </Card>
        </div>

        <div className="flex-1 overflow-y-auto px-4 pb-20">
          <div className="space-y-3">
            {zones.map((zone) => (
              <Card key={zone.id}>
                <CardContent>
                  <div className="flex items-start justify-between mb-2">
                    <div>
                      <Typography variant="subtitle1" fontWeight="bold">
                        {zone.name}
                      </Typography>
                      <Typography variant="body2" color="text.secondary">
                        {zone.address}
                      </Typography>
                    </div>
                    <Chip
                      label={
                        zone.available > 0
                          ? `${zone.available} available`
                          : "Full"
                      }
                      color={zone.available > 0 ? "success" : "error"}
                      size="small"
                    />
                  </div>

                  <div className="grid grid-cols-3 gap-2 mb-3 mt-3">
                    <div className="flex items-center gap-1">
                      <DirectionsWalk fontSize="small" color="action" />
                      <Typography variant="caption">{zone.distance}</Typography>
                    </div>
                    <div className="flex items-center gap-1">
                      <AttachMoney fontSize="small" color="action" />
                      <Typography variant="caption">{zone.rate}</Typography>
                    </div>
                    <div className="flex items-center gap-1">
                      <Schedule fontSize="small" color="action" />
                      <Typography variant="caption" noWrap>
                        {zone.hours}
                      </Typography>
                    </div>
                  </div>

                  <Button
                    fullWidth
                    variant="contained"
                    size="small"
                    onClick={() => navigate("/start-parking")}
                    disabled={zone.available === 0}
                  >
                    {zone.available > 0 ? "Park Here" : "Full"}
                  </Button>
                </CardContent>
              </Card>
            ))}
          </div>
        </div>

        <Dialog open={showGpsDialog} onClose={() => setShowGpsDialog(false)}>
          <DialogTitle>Enable Location Services</DialogTitle>
          <DialogContent>
            <Typography variant="body2">
              e-park would like to access your location to show nearby parking zones.
            </Typography>
          </DialogContent>
          <DialogActions>
            <Button onClick={() => setShowGpsDialog(false)}>Deny</Button>
            <Button
              variant="contained"
              onClick={() => setShowGpsDialog(false)}
            >
              Allow
            </Button>
          </DialogActions>
        </Dialog>

        <BottomNav />
      </div>
    </MobileContainer>
  );
}
