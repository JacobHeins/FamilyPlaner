import { useState, useEffect } from "react";
import {
  Users,
  CalendarDays,
  CheckSquare,
  Zap,
  ArrowRight,
  Plus,
  Loader2,
} from "lucide-react";
import { Link } from "react-router-dom";
import {
  getFamilies,
  roleColor,
  roleLabel,
  type Family,
} from "../api/familyApi";
import "./Dashboard.css";

const tasks = [
  { title: "Buy groceries", assignee: "Mom", done: false },
  { title: "Pick up dry cleaning", assignee: "Dad", done: false },
  { title: "Book hotel for summer holiday", assignee: "Dad", done: false },
  { title: "Sign school permission slip", assignee: "Mom", done: true },
];

const days = ["Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"];

function useFamilies() {
  const [families, setFamilies] = useState<Family[]>([]);
  const [loading, setLoading] = useState(true);
  useEffect(() => {
    getFamilies()
      .then(setFamilies)
      .catch(() => {})
      .finally(() => setLoading(false));
  }, []);
  return { families, loading };
}

function getWeekDates() {
  const today = new Date();
  const monday = new Date(today);
  monday.setDate(today.getDate() - ((today.getDay() + 6) % 7));
  return days.map((d, i) => {
    const date = new Date(monday);
    date.setDate(monday.getDate() + i);
    return {
      label: d,
      date: date.getDate(),
      isToday: date.toDateString() === today.toDateString(),
    };
  });
}

export default function Dashboard() {
  const week = getWeekDates();
  const { families, loading: familiesLoading } = useFamilies();
  const allMembers = families.flatMap((f) =>
    f.familyMembers.map((m) => ({
      ...m,
      color: roleColor(
        m.role,
        f.familyMembers
          .filter((x) => x.role === "CHILD")
          .findIndex((x) => x.id === m.id),
      ),
    })),
  );
  const familyNames = families.map((f) => f.name).join(", ") || "Your family";

  return (
    <div className="page">
      <header className="page-header">
        <div>
          <h1 className="page-title">Good morning, {familyNames}! 👋</h1>
          <p className="page-subtitle">Here's what's happening this week.</p>
        </div>
        <Link to="/week" className="btn-primary">
          <Plus size={16} />
          New Plan
        </Link>
      </header>

      {/* Stats row */}
      <div className="stats-grid">
        <div className="stat-card stat-accent">
          <div className="stat-icon">
            <Users size={20} />
          </div>
          <div>
            <div className="stat-value">
              {familiesLoading ? (
                <Loader2 size={20} className="spin" />
              ) : (
                allMembers.length
              )}
            </div>
            <div className="stat-label">Family Members</div>
          </div>
        </div>
        <div className="stat-card stat-green">
          <div className="stat-icon">
            <CalendarDays size={20} />
          </div>
          <div>
            <div className="stat-value">3</div>
            <div className="stat-label">This Week's Plans</div>
          </div>
        </div>
        <div className="stat-card stat-orange">
          <div className="stat-icon">
            <CheckSquare size={20} />
          </div>
          <div>
            <div className="stat-value">7</div>
            <div className="stat-label">Open Tasks</div>
          </div>
        </div>
        <div className="stat-card stat-pink">
          <div className="stat-icon">
            <Zap size={20} />
          </div>
          <div>
            <div className="stat-value">2</div>
            <div className="stat-label">Upcoming Events</div>
          </div>
        </div>
      </div>

      {/* Week strip */}
      <section className="section">
        <div className="section-header">
          <h2 className="section-title">This Week</h2>
          <Link to="/week" className="section-link">
            View full planner <ArrowRight size={14} />
          </Link>
        </div>
        <div className="week-strip">
          {week.map(({ label, date, isToday }) => (
            <div key={label} className={`week-day${isToday ? " today" : ""}`}>
              <span className="week-day-label">{label}</span>
              <span className="week-day-date">{date}</span>
              {isToday && <span className="week-day-dot" />}
            </div>
          ))}
        </div>
      </section>

      {/* Two-column bottom section */}
      <div className="dashboard-cols">
        {/* Family members from API */}
        <section className="section card">
          <div className="section-header">
            <h2 className="section-title">Family Members</h2>
            <Link to="/members" className="section-link">
              Manage <ArrowRight size={14} />
            </Link>
          </div>
          {familiesLoading ? (
            <div className="loading-inline">
              <Loader2 size={18} className="spin" /> Loading…
            </div>
          ) : (
            <ul className="member-pill-list">
              {allMembers.map((m) => (
                <li key={m.id} className={`member-pill avatar-${m.color}`}>
                  <span className="pill-avatar">
                    {m.name.charAt(0).toUpperCase()}
                  </span>
                  <div className="pill-info">
                    <span className="pill-name">{m.name}</span>
                    <span className="pill-role">{roleLabel(m.role)}</span>
                  </div>
                </li>
              ))}
              {allMembers.length === 0 && (
                <li style={{ color: "var(--text-muted)", fontSize: "14px" }}>
                  No members yet — add them on the Members page.
                </li>
              )}
            </ul>
          )}
        </section>

        {/* Tasks */}
        <section className="section card">
          <div className="section-header">
            <h2 className="section-title">Open Tasks</h2>
            <Link to="/tasks" className="section-link">
              See all <ArrowRight size={14} />
            </Link>
          </div>
          <ul className="task-list">
            {tasks.map((t) => (
              <li key={t.title} className={`task-item${t.done ? " done" : ""}`}>
                <div className={`task-check${t.done ? " checked" : ""}`}>
                  {t.done && (
                    <svg width="10" height="8" viewBox="0 0 10 8" fill="none">
                      <path
                        d="M1 4l3 3 5-6"
                        stroke="currentColor"
                        strokeWidth="1.5"
                        strokeLinecap="round"
                        strokeLinejoin="round"
                      />
                    </svg>
                  )}
                </div>
                <div className="task-info">
                  <span className="task-title">{t.title}</span>
                  <span className="task-meta">{t.assignee}</span>
                </div>
              </li>
            ))}
          </ul>
        </section>
      </div>
    </div>
  );
}
