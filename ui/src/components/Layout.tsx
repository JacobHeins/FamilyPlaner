import { Menu } from "lucide-react";
import { useState } from "react";
import { Outlet } from "react-router-dom";
import Sidebar from "./Sidebar";
import "./Layout.css";

export default function Layout() {
  const [isSidebarOpen, setIsSidebarOpen] = useState(false);

  return (
    <div className="app-shell">
      <Sidebar isOpen={isSidebarOpen} onClose={() => setIsSidebarOpen(false)} />
      <main className="main-content">
        <div className="app-topbar">
          <button
            type="button"
            className="sidebar-toggle"
            onClick={() => setIsSidebarOpen(true)}
            aria-label="Navigation öffnen"
          >
            <Menu size={18} />
          </button>
          <div className="app-topbar-copy">
            <span className="app-topbar-title">FamilyPlanner</span>
            <span className="app-topbar-subtitle">
              Übersicht, Wochenplan und Aufgaben
            </span>
          </div>
        </div>
        <Outlet />
      </main>
    </div>
  );
}
