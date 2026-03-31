import { useMemo } from "react";
import {
  Users,
  CalendarDays,
  CheckSquare,
  ArrowRight,
  Plus,
  Loader2,
  AlertCircle,
} from "lucide-react";
import { Link } from "react-router-dom";
import { useGetFamiliesQuery, roleColor, roleLabel } from "../api/familyApi";
import { useGetTodosQuery } from "../api/todoApi";
import "./Dashboard.css";

const DAY_LABELS = ["Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"];

function getWeekDates() {
  const today = new Date();
  const monday = new Date(today);
  monday.setDate(today.getDate() - ((today.getDay() + 6) % 7));
  return DAY_LABELS.map((label, i) => {
    const date = new Date(monday);
    date.setDate(monday.getDate() + i);
    return {
      label,
      date: date.getDate(),
      isToday: date.toDateString() === today.toDateString(),
    };
  });
}

function isInCurrentWeek(dateStr: string | null): boolean {
  if (!dateStr) return false;
  const today = new Date();
  const monday = new Date(today);
  monday.setDate(today.getDate() - ((today.getDay() + 6) % 7));
  monday.setHours(0, 0, 0, 0);
  const sunday = new Date(monday);
  sunday.setDate(monday.getDate() + 6);
  sunday.setHours(23, 59, 59, 999);
  const d = new Date(dateStr);
  return d >= monday && d <= sunday;
}

export default function Dashboard() {
  const week = useMemo(() => getWeekDates(), []);

  const {
    data: families = [],
    isLoading: familiesLoading,
    isError: familiesError,
  } = useGetFamiliesQuery();

  const primaryFamily = families[0] ?? null;

  const {
    data: todos = [],
    isLoading: todosLoading,
    isError: todosError,
  } = useGetTodosQuery(
    { familyId: primaryFamily?.id ?? 0 },
    { skip: !primaryFamily },
  );

  const allMembers = useMemo(
    () =>
      families.flatMap((f) =>
        f.familyMembers.map((m) => ({
          ...m,
          color: roleColor(
            m.role,
            f.familyMembers
              .filter((x) => x.role === "CHILD")
              .findIndex((x) => x.id === m.id),
          ),
        })),
      ),
    [families],
  );

  const weekTodos = useMemo(
    () => todos.filter((t) => isInCurrentWeek(t.dueDate)),
    [todos],
  );

  const openTodosCount = useMemo(
    () => todos.filter((t) => !t.completed).length,
    [todos],
  );

  const openWeekTodos = useMemo(
    () => weekTodos.filter((t) => !t.completed),
    [weekTodos],
  );

  const isLoading = familiesLoading || todosLoading;
  const familyNames = families.map((f) => f.name).join(", ") || "Your family";

  return (
    <div className="page">
      <header className="page-header">
        <div>
          <h1 className="page-title">
            {familiesLoading ? "Loading…" : `Good morning, ${familyNames}! 👋`}
          </h1>
          <p className="page-subtitle">Here's what's happening this week.</p>
        </div>
        <Link to="/tasks" className="btn-primary">
          <Plus size={16} />
          New Task
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
            <div className="stat-value">
              {isLoading ? (
                <Loader2 size={20} className="spin" />
              ) : (
                weekTodos.length
              )}
            </div>
            <div className="stat-label">This Week's Tasks</div>
          </div>
        </div>
        <div className="stat-card stat-orange">
          <div className="stat-icon">
            <CheckSquare size={20} />
          </div>
          <div>
            <div className="stat-value">
              {isLoading ? (
                <Loader2 size={20} className="spin" />
              ) : (
                openTodosCount
              )}
            </div>
            <div className="stat-label">Open Tasks</div>
          </div>
        </div>
        <div className="stat-card stat-pink">
          <div className="stat-icon">
            <CheckSquare size={20} />
          </div>
          <div>
            <div className="stat-value">
              {isLoading ? (
                <Loader2 size={20} className="spin" />
              ) : (
                openWeekTodos.length
              )}
            </div>
            <div className="stat-label">Due This Week</div>
          </div>
        </div>
      </div>

      {/* Week strip */}
      <section className="section">
        <div className="section-header">
          <h2 className="section-title">This Week</h2>
          <Link to="/tasks" className="section-link">
            View all tasks <ArrowRight size={14} />
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

      {/* Error banners */}
      {(familiesError || todosError) && (
        <div className="error-state">
          <AlertCircle size={18} />
          {familiesError
            ? "Could not load family data. Please try again."
            : "Could not load tasks. Please try again."}
        </div>
      )}

      {/* Two-column bottom section */}
      <div className="dashboard-cols">
        {/* Family members */}
        <section className="section card">
          <div className="section-header">
            <h2 className="section-title">Family Members</h2>
            <Link to="/members" className="section-link">
              Manage <ArrowRight size={14} />
            </Link>
          </div>
          {familiesLoading ? (
            <div className="loading-state">
              <Loader2 size={18} className="spin" /> Loading members…
            </div>
          ) : familiesError ? (
            <div className="error-state">
              <AlertCircle size={16} /> Failed to load members.
            </div>
          ) : allMembers.length === 0 ? (
            <div className="empty-state">
              No members yet —{" "}
              <Link to="/members" className="empty-link">
                add them here
              </Link>
              .
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
            </ul>
          )}
        </section>

        {/* Open tasks this week */}
        <section className="section card">
          <div className="section-header">
            <h2 className="section-title">Open Tasks This Week</h2>
            <Link to="/tasks" className="section-link">
              See all <ArrowRight size={14} />
            </Link>
          </div>
          {familiesLoading || todosLoading ? (
            <div className="loading-state">
              <Loader2 size={18} className="spin" /> Loading tasks…
            </div>
          ) : todosError ? (
            <div className="error-state">
              <AlertCircle size={16} /> Failed to load tasks.
            </div>
          ) : !primaryFamily ? (
            <div className="empty-state">
              No family yet —{" "}
              <Link to="/members" className="empty-link">
                create one
              </Link>
              .
            </div>
          ) : openWeekTodos.length === 0 ? (
            <div className="empty-state">
              No open tasks this week —{" "}
              <Link to="/tasks" className="empty-link">
                add one
              </Link>
              !
            </div>
          ) : (
            <ul className="task-list">
              {openWeekTodos.map((t) => (
                <li key={t.id} className="task-item">
                  <div className="task-check" />
                  <div className="task-info">
                    <span className="task-title">{t.name}</span>
                    <span className="task-meta">
                      {t.assignee?.name ?? "Unassigned"}
                      {t.dueDate
                        ? ` · ${new Date(t.dueDate).toLocaleDateString("en-US", { weekday: "short", month: "short", day: "numeric" })}`
                        : ""}
                    </span>
                  </div>
                </li>
              ))}
            </ul>
          )}
        </section>
      </div>
    </div>
  );
}
