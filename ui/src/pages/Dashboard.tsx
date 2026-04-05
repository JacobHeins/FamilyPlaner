import { useMemo } from "react";
import {
  Users,
  CalendarDays,
  CheckSquare,
  ArrowRight,
  Plus,
  Loader2,
  AlertCircle,
  Zap,
} from "lucide-react";
import { Link, useNavigate } from "react-router-dom";
import { useGetFamiliesQuery, roleColor, roleLabel } from "../api/familyApi";
import { useGetTodosQuery } from "../api/todoApi";
import { useGetActivitiesQuery } from "../api/activityApi";
import {
  getWeekDates,
  isInCurrentWeek,
  getToday,
  formatTime,
} from "../utils/weekUtils";
import "./Dashboard.css";

export default function Dashboard() {
  const navigate = useNavigate();
  const week = useMemo(() => getWeekDates(), []);
  const today = useMemo(() => getToday(), []);

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

  const { data: activities = [], isLoading: activitiesLoading } =
    useGetActivitiesQuery(
      { familyId: primaryFamily?.id ?? 0 },
      { skip: !primaryFamily },
    );

  const todayActivities = useMemo(
    () =>
      activities
        .filter((a) => a.day === today)
        .sort((a, b) => (a.startTime ?? "").localeCompare(b.startTime ?? "")),
    [activities, today],
  );

  const otherDayActivities = useMemo(() => {
    const otherDays = week.filter((d) => !d.isToday);
    return otherDays
      .map((d) => ({
        ...d,
        items: activities
          .filter((a) => a.day === d.dateStr)
          .sort((a, b) => (a.startTime ?? "").localeCompare(b.startTime ?? "")),
      }))
      .filter((d) => d.items.length > 0);
  }, [activities, week]);

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
  const isActivitiesLoading = familiesLoading || activitiesLoading;
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
        <div className="stat-card stat-pink">
          <div className="stat-icon">
            <Zap size={20} />
          </div>
          <div>
            <div className="stat-value">
              {isActivitiesLoading ? (
                <Loader2 size={20} className="spin" />
              ) : (
                todayActivities.length
              )}
            </div>
            <div className="stat-label">Aktivitäten heute</div>
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
      {/* Heutige Aktivitäten */}
      <section className="section dash-today-section">
        <div className="section-header">
          <h2 className="section-title">
            <Zap size={16} />
            Heutige Aktivitäten
          </h2>
          <Link to="/activities" className="section-link">
            Alle anzeigen <ArrowRight size={14} />
          </Link>
        </div>
        {isActivitiesLoading ? (
          <div className="loading-state">
            <Loader2 size={18} className="spin" /> Aktivitäten werden geladen…
          </div>
        ) : !primaryFamily ? (
          <div className="empty-state">
            Noch keine Familie —{" "}
            <Link to="/members" className="empty-link">
              jetzt erstellen
            </Link>
            .
          </div>
        ) : todayActivities.length === 0 ? (
          <p className="dash-act-empty">Heute keine Aktivitäten geplant.</p>
        ) : (
          <ul className="dash-act-list">
            {todayActivities.map((act) => {
              const start = formatTime(act.startTime);
              const end = formatTime(act.endTime);
              const timeStr =
                start && end ? `${start}–${end}` : start ? `ab ${start}` : null;
              return (
                <li
                  key={act.id}
                  className="dash-act-item"
                  role="button"
                  tabIndex={0}
                  onClick={() =>
                    navigate("/activities", {
                      state: { selectedId: act.id },
                    })
                  }
                  onKeyDown={(e) => {
                    if (e.key === "Enter" || e.key === " ") {
                      navigate("/activities", {
                        state: { selectedId: act.id },
                      });
                    }
                  }}
                >
                  <div className="dash-act-body">
                    <span className="dash-act-name">{act.name}</span>
                    {(timeStr || act.location) && (
                      <span className="dash-act-meta">
                        {timeStr && <span>{timeStr}</span>}
                        {act.location && <span>{act.location}</span>}
                      </span>
                    )}
                  </div>
                  {act.participants.length > 0 && (
                    <div className="dash-act-badges">
                      {act.participants.slice(0, 3).map((p) => (
                        <span key={p.id} className="dash-act-badge">
                          {p.name.charAt(0).toUpperCase()}
                        </span>
                      ))}
                      {act.participants.length > 3 && (
                        <span className="dash-act-badge">
                          +{act.participants.length - 3}
                        </span>
                      )}
                    </div>
                  )}
                  <ArrowRight size={14} className="dash-act-arrow" />
                </li>
              );
            })}
          </ul>
        )}
      </section>
      {/* Weitere Aktivitäten der Woche */}
      {!isActivitiesLoading && otherDayActivities.length > 0 && (
        <section className="section dash-week-preview">
          <div className="section-header">
            <h2 className="section-title">Weitere Aktivitäten der Woche</h2>
            <Link to="/week" className="section-link">
              Alle anzeigen <ArrowRight size={14} />
            </Link>
          </div>
          {otherDayActivities.map((day) => (
            <div key={day.dateStr} className="dash-week-preview-day">
              <div className="dash-week-preview-day-label">
                {day.label} {day.date}.
              </div>
              <ul className="dash-week-preview-list">
                {day.items.map((act) => {
                  const start = formatTime(act.startTime);
                  const end = formatTime(act.endTime);
                  const timeStr =
                    start && end
                      ? `${start}\u2013${end}`
                      : start
                        ? `ab ${start}`
                        : null;
                  return (
                    <li key={act.id} className="dash-week-preview-row">
                      <span className="dash-week-preview-name">{act.name}</span>
                      {timeStr && (
                        <span className="dash-week-preview-time">
                          {timeStr}
                        </span>
                      )}
                    </li>
                  );
                })}
              </ul>
            </div>
          ))}
        </section>
      )}
      {/* Two-column bottom section */}{" "}
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
