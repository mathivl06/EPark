import { useState } from "react";
import { useNavigate } from "react-router";
import { MobileContainer } from "../components/MobileContainer";
import {
  AppBar,
  Toolbar,
  Typography,
  TextField,
  Card,
  CardContent,
  CardActionArea,
  InputAdornment,
} from "@mui/material";
import { Search, LocationCity, ChevronRight } from "@mui/icons-material";

const municipalities = [
  { id: 1, name: "San José", zones: 24, active: true },
  { id: 2, name: "Alajuela", zones: 18, active: true },
  { id: 3, name: "Cartago", zones: 15, active: true },
  { id: 4, name: "Heredia", zones: 12, active: true },
  { id: 5, name: "Puntarenas", zones: 8, active: true },
  { id: 6, name: "Limón", zones: 6, active: false },
];

export function MunicipalitySelection() {
  const navigate = useNavigate();
  const [search, setSearch] = useState("");

  const filteredMunicipalities = municipalities.filter((m) =>
    m.name.toLowerCase().includes(search.toLowerCase())
  );

  return (
    <MobileContainer>
      <div className="h-full flex flex-col bg-gray-50">
        <AppBar position="static">
          <Toolbar>
            <Typography variant="h6">Select Municipality</Typography>
          </Toolbar>
        </AppBar>

        <div className="p-4">
          <TextField
            fullWidth
            placeholder="Search municipality..."
            variant="outlined"
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

          <div className="space-y-3">
            {filteredMunicipalities.map((municipality) => (
              <Card key={municipality.id}>
                <CardActionArea
                  onClick={() => navigate("/driver")}
                  disabled={!municipality.active}
                >
                  <CardContent className="flex items-center justify-between">
                    <div className="flex items-center gap-3">
                      <div className="bg-blue-100 rounded-full p-2">
                        <LocationCity sx={{ color: "primary.main" }} />
                      </div>
                      <div>
                        <Typography variant="subtitle1" fontWeight="medium">
                          {municipality.name}
                        </Typography>
                        <Typography variant="caption" color="text.secondary">
                          {municipality.zones} parking zones
                        </Typography>
                      </div>
                    </div>
                    {municipality.active && <ChevronRight color="action" />}
                    {!municipality.active && (
                      <Typography variant="caption" color="error">
                        Coming soon
                      </Typography>
                    )}
                  </CardContent>
                </CardActionArea>
              </Card>
            ))}
          </div>
        </div>
      </div>
    </MobileContainer>
  );
}
