import { RouterProvider } from "react-router";
import { router } from "./routes";
import { ThemeProvider, createTheme } from "@mui/material/styles";
import CssBaseline from "@mui/material/CssBaseline";

const theme = createTheme({
  palette: {
    primary: {
      main: "#1976d2",
      light: "#42a5f5",
      dark: "#1565c0",
    },
    secondary: {
      main: "#dc004e",
    },
    background: {
      default: "#f5f5f5",
      paper: "#ffffff",
    },
  },
  typography: {
    fontFamily: '"Roboto", "Helvetica", "Arial", sans-serif',
  },
  shape: {
    borderRadius: 12,
  },
});

function AppContent() {
  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <div className="size-full bg-gray-50">
        <RouterProvider router={router} />
      </div>
    </ThemeProvider>
  );
}

export default function App(props: any) {
  return (
    <div {...props} className="size-full">
      <AppContent />
    </div>
  );
}