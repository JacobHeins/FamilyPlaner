import { AlertCircle, LogOut, Menu, X } from "lucide-react";
import { useState } from "react";
import { Outlet, useNavigate } from "react-router-dom";
import { permissionErrorCleared, tokenCleared } from "../app/store/authSlice";
import { useAppDispatch, useAppSelector } from "../app/store/hooks";
import Sidebar from "./Sidebar";
import "./Layout.css";

export default function Layout() {
  const dispatch = useAppDispatch();
  const navigate = useNavigate();
  const [isSidebarOpen, setIsSidebarOpen] = useState(false);
  const permissionError = useAppSelector((state) => state.auth.permissionError);

  function handleLogout() {
    dispatch(tokenCleared());
    navigate("/login", { replace: true });
  }

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
          <button
            type="button"
            className="logout-button"
            onClick={handleLogout}
            aria-label="Abmelden"
            title="Abmelden"
          >
            <LogOut size={18} />
          </button>
        </div>
        {permissionError && (
          <div className="layout-permission-error error-state" role="alert">
            <AlertCircle size={18} />
            {permissionError}
            <button
              type="button"
              className="error-dismiss"
              onClick={() => dispatch(permissionErrorCleared())}
              aria-label="Meldung schließen"
            >
              <X size={16} />
            </button>
          </div>
        )}
        <Outlet />
      </main>
    </div>
  );
}
