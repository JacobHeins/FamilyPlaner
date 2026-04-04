import { BrowserRouter, Navigate, Route, Routes } from "react-router-dom";
import Layout from "./components/Layout";
import Dashboard from "./pages/Dashboard";
import FamilyMembers from "./pages/FamilyMembers";
import Tasks from "./pages/Tasks";
import WeeklyPlanner from "./pages/WeeklyPlanner";
import Activities from "./pages/Activities";
import "./App.css";

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route element={<Layout />}>
          <Route index element={<Dashboard />} />
          <Route path="members" element={<FamilyMembers />} />
          <Route path="tasks" element={<Tasks />} />
          <Route path="week" element={<WeeklyPlanner />} />
          <Route path="activities" element={<Activities />} />
          <Route path="*" element={<Navigate to="/" replace />} />
        </Route>
      </Routes>
    </BrowserRouter>
  );
}

export default App;
