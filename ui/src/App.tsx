import { BrowserRouter, Routes, Route } from "react-router-dom";
import Layout from "./components/Layout";
import Dashboard from "./pages/Dashboard";
import WeeklyPlanner from "./pages/WeeklyPlanner";
import FamilyMembers from "./pages/FamilyMembers";
import TasksEvents from "./pages/TasksEvents";
import "./App.css";

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route element={<Layout />}>
          <Route index element={<Dashboard />} />
          <Route path="week" element={<WeeklyPlanner />} />
          <Route path="members" element={<FamilyMembers />} />
          <Route path="tasks" element={<TasksEvents />} />
        </Route>
      </Routes>
    </BrowserRouter>
  );
}

export default App;
