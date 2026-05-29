import { useState } from "react";
import { useNavigate } from "react-router";
import { MobileContainer } from "../components/MobileContainer";
import { Button, TextField, IconButton, InputAdornment, Link } from "@mui/material";
import { Visibility, VisibilityOff, LocalParking } from "@mui/icons-material";

export function LoginScreen() {
  const navigate = useNavigate();
  const [showPassword, setShowPassword] = useState(false);
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");

  const handleLogin = () => {
    if (email.includes("admin")) {
      navigate("/admin");
    } else {
      navigate("/municipality-select");
    }
  };

  return (
    <MobileContainer>
      <div className="h-full flex flex-col p-6 bg-white">
        <div className="flex-1 flex flex-col justify-center">
          <div className="flex justify-center mb-8">
            <div className="bg-blue-100 rounded-full p-6">
              <LocalParking sx={{ fontSize: 60, color: "#1976d2" }} />
            </div>
          </div>

          <h1 className="text-3xl font-bold text-gray-900 text-center mb-2">
            Welcome to e-park
          </h1>
          <p className="text-gray-600 text-center mb-8">
            Sign in to manage your parking
          </p>

          <div className="space-y-4">
            <TextField
              fullWidth
              label="Email"
              type="email"
              variant="outlined"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              autoComplete="email"
            />

            <TextField
              fullWidth
              label="Password"
              type={showPassword ? "text" : "password"}
              variant="outlined"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              autoComplete="current-password"
              InputProps={{
                endAdornment: (
                  <InputAdornment position="end">
                    <IconButton
                      onClick={() => setShowPassword(!showPassword)}
                      edge="end"
                    >
                      {showPassword ? <VisibilityOff /> : <Visibility />}
                    </IconButton>
                  </InputAdornment>
                ),
              }}
            />

            <p className="text-xs text-gray-500">
              Password must be at least 8 characters
            </p>

            <Button
              fullWidth
              variant="contained"
              size="large"
              onClick={handleLogin}
              sx={{ mt: 2, py: 1.5 }}
            >
              Login
            </Button>

            <Button
              fullWidth
              variant="outlined"
              size="large"
              onClick={() => navigate("/register")}
              sx={{ py: 1.5 }}
            >
              Register
            </Button>

            <div className="text-center mt-4">
              <Link
                component="button"
                variant="body2"
                onClick={() => alert("Password reset flow (prototype)")}
                sx={{ cursor: "pointer" }}
              >
                Forgot password?
              </Link>
            </div>
          </div>
        </div>
      </div>
    </MobileContainer>
  );
}
