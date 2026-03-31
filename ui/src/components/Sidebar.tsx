import { NavLink } from "react-router-dom";
import { LayoutDashboard, X, Users, CheckSquare, Sparkles } from "lucide-react";
import "./Sidebar.css";

const nav = [
  { to: "/", icon: LayoutDashboard, label: "Dashboard" },
  { to: "/members", icon: Users, label: "Family" },
  { to: "/tasks", icon: CheckSquare, label: "Todos" },
];

interface SidebarProps {
  isOpen: boolean;
  onClose: () => void;
}

export default function Sidebar({ isOpen, onClose }: SidebarProps) {
  return (
    <>
      <button
        type="button"
        aria-label="Close navigation"
        className={`sidebar-backdrop${isOpen ? " visible" : ""}`}
        onClick={onClose}
      />
      <aside className={`sidebar${isOpen ? " open" : ""}`}>
        <div className="sidebar-brand">
          <div className="sidebar-brand-main">
            <div className="sidebar-logo">
              <Sparkles size={20} />
            </div>
            <div>
              <span className="sidebar-brand-name">FamilyPlaner</span>
              <p className="sidebar-brand-tagline">Household command center</p>
            </div>
          </div>
          <button
            type="button"
            className="sidebar-close"
            onClick={onClose}
            aria-label="Close navigation"
          >
            <X size={16} />
          </button>
        </div>

        <nav className="sidebar-nav">
          {nav.map(({ to, icon: Icon, label }) => (
            <NavLink
              key={to}
              to={to}
              end={to === "/"}
              onClick={onClose}
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
          <p className="sidebar-footer-label">Current scope</p>
          <p className="sidebar-footer-text">
            Dashboard, family management, and todo detail views.
          </p>
        </div>
      </aside>
    </>
  );
}
