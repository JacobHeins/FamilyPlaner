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

const DAY_LABELS = ["Mo", "Di", "Mi", "Do", "Fr", "Sa", "So"];

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
  const familyNames = families.map((f) => f.name).join(", ") || "Ihre Familie";

  return (
    <div className="page">
      <header className="page-header">
        <div>
          <h1 className="page-title">
            {familiesLoading
              ? "Wird geladen…"
              : `Guten Morgen, ${familyNames}! 👋`}
          </h1>
          <p className="page-subtitle">Das steht diese Woche an.</p>
        </div>
        <Link to="/tasks" className="btn-primary">
          <Plus size={16} />
          Neue Aufgabe
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
            <div className="stat-label">Familienmitglieder</div>
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
            <div className="stat-label">Aufgaben diese Woche</div>
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
            <div className="stat-label">Offene Aufgaben</div>
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
            <div className="stat-label">Fällig diese Woche</div>
          </div>
        </div>
      </div>

      {/* Week strip */}
      <section className="section">
        <div className="section-header">
          <h2 className="section-title">Diese Woche</h2>
          <Link to="/tasks" className="section-link">
            Alle Aufgaben <ArrowRight size={14} />
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
            ? "Familiendaten konnten nicht geladen werden. Bitte erneut versuchen."
            : "Aufgaben konnten nicht geladen werden. Bitte erneut versuchen."}
        </div>
      )}

      {/* Two-column bottom section */}
      <div className="dashboard-cols">
        {/* Family members */}
        <section className="section card">
          <div className="section-header">
            <h2 className="section-title">Familienmitglieder</h2>
            <Link to="/members" className="section-link">
              Verwalten <ArrowRight size={14} />
            </Link>
          </div>
          {familiesLoading ? (
            <div className="loading-state">
              <Loader2 size={18} className="spin" /> Mitglieder werden geladen…
            </div>
          ) : familiesError ? (
            <div className="error-state">
              <AlertCircle size={16} /> Mitglieder konnten nicht geladen werden.
            </div>
          ) : allMembers.length === 0 ? (
            <div className="empty-state">
              Noch keine Mitglieder —{" "}
              <Link to="/members" className="empty-link">
                hier hinzufügen
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
            <h2 className="section-title">Offene Aufgaben diese Woche</h2>
            <Link to="/tasks" className="section-link">
              Alle anzeigen <ArrowRight size={14} />
            </Link>
          </div>
          {familiesLoading || todosLoading ? (
            <div className="loading-state">
              <Loader2 size={18} className="spin" /> Aufgaben werden geladen…
            </div>
          ) : todosError ? (
            <div className="error-state">
              <AlertCircle size={16} /> Aufgaben konnten nicht geladen werden.
            </div>
          ) : !primaryFamily ? (
            <div className="empty-state">
              Noch keine Familie —{" "}
              <Link to="/members" className="empty-link">
                jetzt erstellen
              </Link>
              .
            </div>
          ) : openWeekTodos.length === 0 ? (
            <div className="empty-state">
              Diese Woche keine offenen Aufgaben —{" "}
              <Link to="/tasks" className="empty-link">
                neue hinzufügen
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
                      {t.dueDate
                        ? new Date(t.dueDate).toLocaleDateString("de-DE", {
                            weekday: "short",
                            month: "short",
                            day: "numeric",
                          })
                        : ""}
                    </span>
                  </div>
                  <span
                    className={`assignee-badge avatar-${t.assignee ? roleColor(t.assignee.role, 0) : "muted"}`}
                  >
                    {t.assignee?.name ?? "Nicht zugewiesen"}
                  </span>
                </li>
              ))}
            </ul>
          )}
        </section>
      </div>
    </div>
  );
}
