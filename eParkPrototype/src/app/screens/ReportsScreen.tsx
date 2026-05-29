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
  TextField,
  Button,
  Tabs,
  Tab,
  Chip,
} from "@mui/material";
import {
  ArrowBack,
  Download,
  TrendingUp,
  TrendingDown,
  CalendarToday,
} from "@mui/icons-material";
import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer,
  LineChart,
  Line,
  PieChart,
  Pie,
  Cell,
} from "recharts";

const revenueData = [
  { day: "Mon", revenue: 45000 },
  { day: "Tue", revenue: 52000 },
  { day: "Wed", revenue: 48000 },
  { day: "Thu", revenue: 61000 },
  { day: "Fri", revenue: 55000 },
  { day: "Sat", revenue: 38000 },
  { day: "Sun", revenue: 42000 },
];

const sessionsData = [
  { day: "Mon", sessions: 142 },
  { day: "Tue", sessions: 168 },
  { day: "Wed", sessions: 155 },
  { day: "Thu", sessions: 189 },
  { day: "Fri", sessions: 176 },
  { day: "Sat", sessions: 123 },
  { day: "Sun", sessions: 134 },
];

const zonePerformance = [
  { name: "Zone A", value: 35, color: "#1976d2" },
  { name: "Zone B", value: 25, color: "#42a5f5" },
  { name: "Zone C", value: 20, color: "#64b5f6" },
  { name: "Zone D", value: 15, color: "#90caf9" },
  { name: "Others", value: 5, color: "#bbdefb" },
];

export function ReportsScreen() {
  const navigate = useNavigate();
  const [tabValue, setTabValue] = useState(0);
  const [dateRange, setDateRange] = useState("week");

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
            <Typography variant="h6" sx={{ flexGrow: 1 }}>
              Reports
            </Typography>
            <IconButton color="inherit">
              <Download />
            </IconButton>
          </Toolbar>
        </AppBar>

        <div className="p-4">
          <div className="flex gap-2 mb-3">
            <TextField
              select
              size="small"
              value={dateRange}
              onChange={(e) => setDateRange(e.target.value)}
              SelectProps={{ native: true }}
              sx={{ bgcolor: "white" }}
            >
              <option value="today">Today</option>
              <option value="week">This Week</option>
              <option value="month">This Month</option>
              <option value="custom">Custom Range</option>
            </TextField>
            <Button variant="outlined" size="small" startIcon={<CalendarToday />}>
              May 1-7, 2026
            </Button>
          </div>
        </div>

        <Tabs
          value={tabValue}
          onChange={(_, newValue) => setTabValue(newValue)}
          variant="fullWidth"
          sx={{ bgcolor: "white", borderBottom: 1, borderColor: "divider" }}
        >
          <Tab label="Revenue" />
          <Tab label="Sessions" />
          <Tab label="Zones" />
        </Tabs>

        <div className="flex-1 overflow-y-auto p-4">
          {tabValue === 0 && (
            <>
              <div className="grid grid-cols-2 gap-3 mb-4">
                <Card>
                  <CardContent>
                    <Typography variant="caption" color="text.secondary">
                      Total Revenue
                    </Typography>
                    <Typography variant="h6" fontWeight="bold" color="primary">
                      ₡341,000
                    </Typography>
                    <div className="flex items-center gap-1 mt-1">
                      <TrendingUp fontSize="small" color="success" />
                      <Typography variant="caption" color="success.main">
                        +12.5%
                      </Typography>
                    </div>
                  </CardContent>
                </Card>

                <Card>
                  <CardContent>
                    <Typography variant="caption" color="text.secondary">
                      Avg. Daily
                    </Typography>
                    <Typography variant="h6" fontWeight="bold" color="primary">
                      ₡48,714
                    </Typography>
                    <div className="flex items-center gap-1 mt-1">
                      <TrendingUp fontSize="small" color="success" />
                      <Typography variant="caption" color="success.main">
                        +8.2%
                      </Typography>
                    </div>
                  </CardContent>
                </Card>
              </div>

              <Card>
                <CardContent>
                  <Typography variant="subtitle2" fontWeight="bold" gutterBottom>
                    Revenue by Day
                  </Typography>
                  <ResponsiveContainer width="100%" height={250}>
                    <BarChart data={revenueData}>
                      <CartesianGrid strokeDasharray="3 3" />
                      <XAxis dataKey="day" />
                      <YAxis />
                      <Tooltip
                        formatter={(value: any) => `₡${value.toLocaleString()}`}
                      />
                      <Bar dataKey="revenue" fill="#1976d2" />
                    </BarChart>
                  </ResponsiveContainer>
                </CardContent>
              </Card>
            </>
          )}

          {tabValue === 1 && (
            <>
              <div className="grid grid-cols-2 gap-3 mb-4">
                <Card>
                  <CardContent>
                    <Typography variant="caption" color="text.secondary">
                      Total Sessions
                    </Typography>
                    <Typography variant="h6" fontWeight="bold" color="primary">
                      1,087
                    </Typography>
                    <div className="flex items-center gap-1 mt-1">
                      <TrendingUp fontSize="small" color="success" />
                      <Typography variant="caption" color="success.main">
                        +15.3%
                      </Typography>
                    </div>
                  </CardContent>
                </Card>

                <Card>
                  <CardContent>
                    <Typography variant="caption" color="text.secondary">
                      Avg. Duration
                    </Typography>
                    <Typography variant="h6" fontWeight="bold" color="primary">
                      1.8h
                    </Typography>
                    <div className="flex items-center gap-1 mt-1">
                      <TrendingDown fontSize="small" color="error" />
                      <Typography variant="caption" color="error.main">
                        -3.1%
                      </Typography>
                    </div>
                  </CardContent>
                </Card>
              </div>

              <Card>
                <CardContent>
                  <Typography variant="subtitle2" fontWeight="bold" gutterBottom>
                    Sessions by Day
                  </Typography>
                  <ResponsiveContainer width="100%" height={250}>
                    <LineChart data={sessionsData}>
                      <CartesianGrid strokeDasharray="3 3" />
                      <XAxis dataKey="day" />
                      <YAxis />
                      <Tooltip />
                      <Line
                        type="monotone"
                        dataKey="sessions"
                        stroke="#1976d2"
                        strokeWidth={2}
                      />
                    </LineChart>
                  </ResponsiveContainer>
                </CardContent>
              </Card>

              <Card sx={{ mt: 3 }}>
                <CardContent>
                  <Typography variant="subtitle2" fontWeight="bold" gutterBottom>
                    Fine Statistics
                  </Typography>
                  <div className="grid grid-cols-2 gap-3 mt-3">
                    <div>
                      <Typography variant="caption" color="text.secondary">
                        Fines Issued
                      </Typography>
                      <Typography variant="h6" fontWeight="bold">
                        37
                      </Typography>
                    </div>
                    <div>
                      <Typography variant="caption" color="text.secondary">
                        Fine Revenue
                      </Typography>
                      <Typography variant="h6" fontWeight="bold">
                        ₡156,500
                      </Typography>
                    </div>
                  </div>
                </CardContent>
              </Card>
            </>
          )}

          {tabValue === 2 && (
            <>
              <Card sx={{ mb: 3 }}>
                <CardContent>
                  <Typography variant="subtitle2" fontWeight="bold" gutterBottom>
                    Zone Performance Distribution
                  </Typography>
                  <ResponsiveContainer width="100%" height={220}>
                    <PieChart>
                      <Pie
                        data={zonePerformance}
                        cx="50%"
                        cy="50%"
                        labelLine={false}
                        label={({ name, value }) => `${name}: ${value}%`}
                        outerRadius={80}
                        fill="#8884d8"
                        dataKey="value"
                      >
                        {zonePerformance.map((entry, index) => (
                          <Cell key={`cell-${index}`} fill={entry.color} />
                        ))}
                      </Pie>
                      <Tooltip />
                    </PieChart>
                  </ResponsiveContainer>
                </CardContent>
              </Card>

              <Card>
                <CardContent>
                  <Typography variant="subtitle2" fontWeight="bold" gutterBottom>
                    Top Performing Zones
                  </Typography>
                  <div className="space-y-3 mt-3">
                    {[
                      {
                        name: "Zone A - Centro",
                        sessions: 382,
                        revenue: 119500,
                        occupancy: 85,
                      },
                      {
                        name: "Zone B - Plaza",
                        sessions: 271,
                        revenue: 85200,
                        occupancy: 78,
                      },
                      {
                        name: "Zone C - Mercado",
                        sessions: 218,
                        revenue: 68300,
                        occupancy: 92,
                      },
                      {
                        name: "Zone D - Parque",
                        sessions: 163,
                        revenue: 48900,
                        occupancy: 42,
                      },
                    ].map((zone, index) => (
                      <div
                        key={index}
                        className="flex items-center justify-between pb-3 border-b last:border-b-0"
                      >
                        <div>
                          <Typography variant="body2" fontWeight="medium">
                            {zone.name}
                          </Typography>
                          <Typography variant="caption" color="text.secondary">
                            {zone.sessions} sessions • ₡{zone.revenue.toLocaleString()}
                          </Typography>
                        </div>
                        <Chip
                          label={`${zone.occupancy}%`}
                          size="small"
                          color={
                            zone.occupancy > 80
                              ? "error"
                              : zone.occupancy > 60
                              ? "success"
                              : "default"
                          }
                        />
                      </div>
                    ))}
                  </div>
                </CardContent>
              </Card>
            </>
          )}
        </div>

        <div className="p-4 border-t bg-white">
          <Button
            fullWidth
            variant="outlined"
            startIcon={<Download />}
            onClick={() => alert("Report download (prototype)")}
          >
            Export Report as PDF
          </Button>
        </div>
      </div>
    </MobileContainer>
  );
}
