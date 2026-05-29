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
  Chip,
  Fab,
  TextField,
  InputAdornment,
  Menu,
  MenuItem,
} from "@mui/material";
import {
  ArrowBack,
  Add,
  Search,
  MoreVert,
  Edit,
  PowerSettingsNew,
  Delete,
} from "@mui/icons-material";

const zones = [
  {
    id: 1,
    name: "Zone A - Centro",
    spots: 20,
    occupied: 12,
    rate: 500,
    status: "active",
    address: "Av. Central",
  },
  {
    id: 2,
    name: "Zone B - Plaza",
    spots: 15,
    occupied: 12,
    rate: 400,
    status: "active",
    address: "Calle 3",
  },
  {
    id: 3,
    name: "Zone C - Mercado",
    spots: 12,
    occupied: 12,
    rate: 350,
    status: "active",
    address: "Av. 2",
  },
  {
    id: 4,
    name: "Zone D - Parque",
    spots: 25,
    occupied: 10,
    rate: 300,
    status: "active",
    address: "Calle 5",
  },
  {
    id: 5,
    name: "Zone E - Hospital",
    spots: 18,
    occupied: 0,
    rate: 450,
    status: "inactive",
    address: "Av. 10",
  },
];

export function ZoneManagement() {
  const navigate = useNavigate();
  const [search, setSearch] = useState("");
  const [anchorEl, setAnchorEl] = useState<null | HTMLElement>(null);
  const [selectedZone, setSelectedZone] = useState<number | null>(null);

  const handleMenuOpen = (event: React.MouseEvent<HTMLElement>, zoneId: number) => {
    setAnchorEl(event.currentTarget);
    setSelectedZone(zoneId);
  };

  const handleMenuClose = () => {
    setAnchorEl(null);
    setSelectedZone(null);
  };

  const filteredZones = zones.filter((zone) =>
    zone.name.toLowerCase().includes(search.toLowerCase())
  );

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
            <Typography variant="h6">Parking Zones</Typography>
          </Toolbar>
        </AppBar>

        <div className="p-4">
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
            sx={{ bgcolor: "white", mb: 3 }}
          />

          <Card sx={{ mb: 3, bgcolor: "info.50" }}>
            <CardContent>
              <div className="flex justify-around text-center">
                <div>
                  <Typography variant="h6" fontWeight="bold" color="primary">
                    24
                  </Typography>
                  <Typography variant="caption" color="text.secondary">
                    Total Zones
                  </Typography>
                </div>
                <div>
                  <Typography variant="h6" fontWeight="bold" color="success.main">
                    22
                  </Typography>
                  <Typography variant="caption" color="text.secondary">
                    Active
                  </Typography>
                </div>
                <div>
                  <Typography variant="h6" fontWeight="bold" color="text.secondary">
                    2
                  </Typography>
                  <Typography variant="caption" color="text.secondary">
                    Inactive
                  </Typography>
                </div>
              </div>
            </CardContent>
          </Card>
        </div>

        <div className="flex-1 overflow-y-auto px-4 pb-4">
          <div className="space-y-3">
            {filteredZones.map((zone) => (
              <Card key={zone.id}>
                <CardContent>
                  <div className="flex items-start justify-between mb-2">
                    <div className="flex-1">
                      <div className="flex items-center gap-2 mb-1">
                        <Typography variant="subtitle1" fontWeight="bold">
                          {zone.name}
                        </Typography>
                        <Chip
                          label={zone.status === "active" ? "Active" : "Inactive"}
                          color={zone.status === "active" ? "success" : "default"}
                          size="small"
                        />
                      </div>
                      <Typography variant="body2" color="text.secondary">
                        {zone.address}
                      </Typography>
                    </div>
                    <IconButton
                      size="small"
                      onClick={(e) => handleMenuOpen(e, zone.id)}
                    >
                      <MoreVert />
                    </IconButton>
                  </div>

                  <div className="grid grid-cols-3 gap-2 mt-3">
                    <div>
                      <Typography variant="caption" color="text.secondary">
                        Spots
                      </Typography>
                      <Typography variant="body2" fontWeight="medium">
                        {zone.spots}
                      </Typography>
                    </div>
                    <div>
                      <Typography variant="caption" color="text.secondary">
                        Occupied
                      </Typography>
                      <Typography variant="body2" fontWeight="medium">
                        {zone.occupied}
                      </Typography>
                    </div>
                    <div>
                      <Typography variant="caption" color="text.secondary">
                        Rate
                      </Typography>
                      <Typography variant="body2" fontWeight="medium">
                        ₡{zone.rate}/hr
                      </Typography>
                    </div>
                  </div>

                  <div className="mt-3 pt-3 border-t">
                    <div className="h-2 bg-gray-200 rounded-full overflow-hidden">
                      <div
                        className="h-full bg-primary-600"
                        style={{
                          width: `${(zone.occupied / zone.spots) * 100}%`,
                          backgroundColor:
                            zone.occupied / zone.spots > 0.8
                              ? "#d32f2f"
                              : "#1976d2",
                        }}
                      ></div>
                    </div>
                    <Typography variant="caption" color="text.secondary" sx={{ mt: 0.5, display: "block" }}>
                      {zone.spots - zone.occupied} spots available
                    </Typography>
                  </div>
                </CardContent>
              </Card>
            ))}
          </div>
        </div>

        <Fab
          color="primary"
          sx={{ position: "fixed", bottom: 24, right: 24 }}
          onClick={() => navigate("/admin/zones/new")}
        >
          <Add />
        </Fab>

        <Menu anchorEl={anchorEl} open={Boolean(anchorEl)} onClose={handleMenuClose}>
          <MenuItem
            onClick={() => {
              navigate(`/admin/zones/${selectedZone}`);
              handleMenuClose();
            }}
          >
            <Edit fontSize="small" sx={{ mr: 1 }} />
            Edit Zone
          </MenuItem>
          <MenuItem onClick={handleMenuClose}>
            <PowerSettingsNew fontSize="small" sx={{ mr: 1 }} />
            Toggle Status
          </MenuItem>
          <MenuItem onClick={handleMenuClose} sx={{ color: "error.main" }}>
            <Delete fontSize="small" sx={{ mr: 1 }} />
            Delete Zone
          </MenuItem>
        </Menu>
      </div>
    </MobileContainer>
  );
}
