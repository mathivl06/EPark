import { useEffect } from "react";
import { useNavigate } from "react-router";
import { MobileContainer } from "../components/MobileContainer";
import { LocalParking } from "@mui/icons-material";
import { CircularProgress } from "@mui/material";

export function SplashScreen() {
  const navigate = useNavigate();

  useEffect(() => {
    const timer = setTimeout(() => {
      navigate("/login");
    }, 2000);
    return () => clearTimeout(timer);
  }, [navigate]);

  return (
    <MobileContainer>
      <div className="h-full flex flex-col items-center justify-center bg-gradient-to-br from-blue-600 to-blue-800">
        <div className="mb-8 bg-white rounded-full p-8 shadow-lg">
          <LocalParking sx={{ fontSize: 80, color: "#1976d2" }} />
        </div>
        <h1 className="text-5xl font-bold text-white mb-2">e-park</h1>
        <p className="text-blue-100 text-lg mb-12">Smart Parking System</p>
        <CircularProgress sx={{ color: "white" }} />
      </div>
    </MobileContainer>
  );
}
