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
  IconButton,
  Collapse,
  Divider,
} from "@mui/material";
import {
  ExpandMore,
  ExpandLess,
  CheckCircle,
  Place,
  AccessTime,
  DirectionsCar,
} from "@mui/icons-material";

const sessions = [
  {
    id: 1,
    date: "May 7, 2026",
    zone: "Zone A - Centro",
    spot: "142",
    address: "Av. Central, San José",
    vehicle: "ABC-1234",
    duration: "1h 42min",
    amount: 850,
    status: "completed",
  },
  {
    id: 2,
    date: "May 6, 2026",
    zone: "Zone B - Plaza",
    spot: "087",
    address: "Calle 3, San José",
    vehicle: "ABC-1234",
    duration: "2h 15min",
    amount: 900,
    status: "completed",
  },
  {
    id: 3,
    date: "May 5, 2026",
    zone: "Zone A - Centro",
    spot: "156",
    address: "Av. Central, San José",
    vehicle: "XYZ-5678",
    duration: "0h 45min",
    amount: 375,
    status: "completed",
  },
  {
    id: 4,
    date: "May 4, 2026",
    zone: "Zone D - Parque",
    spot: "023",
    address: "Calle 5, San José",
    vehicle: "ABC-1234",
    duration: "3h 00min",
    amount: 900,
    status: "completed",
  },
];

export function ParkingHistory() {
  const [expanded, setExpanded] = useState<number | null>(null);

  const toggleExpand = (id: number) => {
    setExpanded(expanded === id ? null : id);
  };

  return (
    <MobileContainer withBottomNav>
      <div className="h-full flex flex-col bg-gray-50">
        <AppBar position="static">
          <Toolbar>
            <Typography variant="h6">Parking History</Typography>
          </Toolbar>
        </AppBar>

        <div className="flex-1 overflow-y-auto p-4 pb-20">
          <Card sx={{ mb: 3, bgcolor: "primary.50" }}>
            <CardContent>
              <Typography variant="subtitle2" color="text.secondary" gutterBottom>
                Total Sessions This Month
              </Typography>
              <Typography variant="h4" fontWeight="bold" color="primary">
                12
              </Typography>
              <Typography variant="body2" color="text.secondary" sx={{ mt: 1 }}>
                Total spent: ₡8,450
              </Typography>
            </CardContent>
          </Card>

          <div className="space-y-3">
            {sessions.map((session) => (
              <Card key={session.id}>
                <CardContent className="pb-2">
                  <div
                    className="flex items-start justify-between cursor-pointer"
                    onClick={() => toggleExpand(session.id)}
                  >
                    <div className="flex-1">
                      <div className="flex items-center gap-2 mb-1">
                        <Typography variant="subtitle1" fontWeight="bold">
                          {session.zone}
                        </Typography>
                        <Chip
                          icon={<CheckCircle />}
                          label="Paid"
                          color="success"
                          size="small"
                        />
                      </div>
                      <Typography variant="body2" color="text.secondary">
                        {session.date} • Spot #{session.spot}
                      </Typography>
                    </div>
                    <div className="text-right">
                      <Typography variant="h6" fontWeight="bold" color="primary">
                        ₡{session.amount}
                      </Typography>
                      <IconButton size="small">
                        {expanded === session.id ? <ExpandLess /> : <ExpandMore />}
                      </IconButton>
                    </div>
                  </div>

                  <Collapse in={expanded === session.id}>
                    <Divider sx={{ my: 2 }} />
                    <div className="space-y-2">
                      <div className="flex items-start gap-2">
                        <Place fontSize="small" color="action" />
                        <div>
                          <Typography variant="caption" color="text.secondary">
                            Location
                          </Typography>
                          <Typography variant="body2" fontWeight="medium">
                            {session.address}
                          </Typography>
                        </div>
                      </div>
                      <div className="flex items-start gap-2">
                        <AccessTime fontSize="small" color="action" />
                        <div>
                          <Typography variant="caption" color="text.secondary">
                            Duration
                          </Typography>
                          <Typography variant="body2" fontWeight="medium">
                            {session.duration}
                          </Typography>
                        </div>
                      </div>
                      <div className="flex items-start gap-2">
                        <DirectionsCar fontSize="small" color="action" />
                        <div>
                          <Typography variant="caption" color="text.secondary">
                            Vehicle
                          </Typography>
                          <Typography variant="body2" fontWeight="medium">
                            {session.vehicle}
                          </Typography>
                        </div>
                      </div>
                    </div>
                  </Collapse>
                </CardContent>
              </Card>
            ))}
          </div>
        </div>

        <BottomNav />
      </div>
    </MobileContainer>
  );
}
