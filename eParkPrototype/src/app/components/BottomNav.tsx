import { BottomNavigation, BottomNavigationAction, Paper } from "@mui/material";
import { Home, History, Receipt, Person } from "@mui/icons-material";
import { useNavigate, useLocation } from "react-router";

export function BottomNav() {
  const navigate = useNavigate();
  const location = useLocation();

  const getValueFromPath = (path: string) => {
    if (path === "/driver") return 0;
    if (path === "/history") return 1;
    if (path === "/fines") return 2;
    if (path === "/profile") return 3;
    return 0;
  };

  const value = getValueFromPath(location.pathname);

  return (
    <Paper sx={{ position: "fixed", bottom: 0, left: 0, right: 0 }} elevation={3}>
      <BottomNavigation
        value={value}
        onChange={(_, newValue) => {
          const paths = ["/driver", "/history", "/fines", "/profile"];
          navigate(paths[newValue]);
        }}
        showLabels
      >
        <BottomNavigationAction label="Home" icon={<Home />} />
        <BottomNavigationAction label="History" icon={<History />} />
        <BottomNavigationAction label="Fines" icon={<Receipt />} />
        <BottomNavigationAction label="Profile" icon={<Person />} />
      </BottomNavigation>
    </Paper>
  );
}
