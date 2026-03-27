import { NavLink } from "react-router-dom";
import {
  LayoutDashboard,
  CalendarDays,
  Users,
  CheckSquare,
  Settings,
  Sparkles,
} from "lucide-react";
import "./Sidebar.css";

const nav = [
  { to: "/", icon: LayoutDashboard, label: "Dashboard" },
  { to: "/week", icon: CalendarDays, label: "Weekly Plan" },
  { to: "/members", icon: Users, label: "Family Members" },
  { to: "/tasks", icon: CheckSquare, label: "Tasks & Events" },
];

export default function Sidebar() {
  return (
    <aside className="sidebar">
      <div className="sidebar-brand">
        <div className="sidebar-logo">
          <Sparkles size={20} />
        </div>
        <span className="sidebar-brand-name">FamilyPlaner</span>
      </div>

      <nav className="sidebar-nav">
        {nav.map(({ to, icon: Icon, label }) => (
          <NavLink
            key={to}
            to={to}
            end={to === "/"}
            className={({ isActive }) =>
              "sidebar-link" + (isActive ? " active" : "")
            }
          >
            <Icon size={18} />
            <span>{label}</span>
          </NavLink>
        ))}
      </nav>

      <div className="sidebar-footer">
        <NavLink
          to="/settings"
          className={({ isActive }) =>
            "sidebar-link" + (isActive ? " active" : "")
          }
        >
          <Settings size={18} />
          <span>Settings</span>
        </NavLink>
      </div>
    </aside>
  );
}
