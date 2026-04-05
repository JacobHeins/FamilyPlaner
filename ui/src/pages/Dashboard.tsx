import { useMemo, useState } from "react";
import {
  CalendarDays,
  CheckSquare,
  ArrowRight,
  Loader2,
  AlertCircle,
} from "lucide-react";
import { Link, useNavigate } from "react-router-dom";
import { useGetFamiliesQuery, roleColor } from "../api/familyApi";
import { useGetTodosQuery } from "../api/todoApi";
import { useGetActivitiesQuery } from "../api/activityApi";
import { getWeekDates, getToday, formatTime } from "../utils/weekUtils";
import "./Dashboard.css";

export default function Dashboard() {
  const navigate = useNavigate();
  const week = useMemo(() => getWeekDates(), []);
  const today = useMemo(() => getToday(), []);
  const [selectedDay, setSelectedDay] = useState<string>(today);

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

  const {
    data: activities = [],
    isLoading: activitiesLoading,
    isError: activitiesError,
  } = useGetActivitiesQuery(
    { familyId: primaryFamily?.id ?? 0 },
    { skip: !primaryFamily },
  );

  const activitiesByDay = useMemo(() => {
    const map: Record<string, typeof activities> = {};
    for (const day of week) {
      map[day.dateStr] = activities
        .filter((a) => a.day === day.dateStr)
        .sort((a, b) => (a.startTime ?? "").localeCompare(b.startTime ?? ""));
    }
    return map;
  }, [activities, week]);

  const selectedDayActivities = useMemo(
    () => activitiesByDay[selectedDay] ?? [],
    [activitiesByDay, selectedDay],
  );

  const openTodos = useMemo(() => todos.filter((t) => !t.completed), [todos]);

  const isLoading = familiesLoading || todosLoading;
  const isActivitiesLoading = familiesLoading || activitiesLoading;
  const familyNames = families.map((f) => f.name).join(", ") || "Ihre Familie";
  const selectedWeekDay = week.find((d) => d.dateStr === selectedDay);

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
      </header>

      {/* Error banners */}
      {(familiesError || activitiesError) && (
        <div className="error-state">
          <AlertCircle size={18} />
          {familiesError
            ? "Familiendaten konnten nicht geladen werden. Bitte erneut versuchen."
            : "Aktivitäten konnten nicht geladen werden. Bitte erneut versuchen."}
        </div>
      )}

      {/* Selected-day activity detail */}
      <section className="section dash-today-section">
        <div className="section-header">
          <h2 className="section-title">
            <CalendarDays size={16} />
            {selectedWeekDay
              ? `${selectedWeekDay.label}, ${selectedWeekDay.date}.`
              : "Heute"}
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
        ) : selectedDayActivities.length === 0 ? (
          <p className="dash-act-empty">
            Keine Aktivitäten an diesem Tag geplant.
          </p>
        ) : (
          <ul className="dash-act-list">
            {selectedDayActivities.map((act) => {
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
                      {act.participants.map((p) => (
                        <span
                          key={p.id}
                          className={`dash-act-badge avatar-${roleColor(p.role, 0)}`}
                        >
                          {p.name}
                        </span>
                      ))}
                    </div>
                  )}
                  <ArrowRight size={14} className="dash-act-arrow" />
                </li>
              );
            })}
          </ul>
        )}
      </section>

      {/* Embedded weekly overview — selectable day columns */}
      <section className="section">
        <div className="section-header">
          <h2 className="section-title">
            <CalendarDays size={16} />
            Wochenübersicht
          </h2>
          <Link to="/week" className="section-link">
            Vollansicht <ArrowRight size={14} />
          </Link>
        </div>
        {isActivitiesLoading ? (
          <div className="loading-state">
            <Loader2 size={18} className="spin" /> Woche wird geladen…
          </div>
        ) : (
          <div className="dash-week-grid">
            {week.map((day) => {
              const dayActs = activitiesByDay[day.dateStr] ?? [];
              const isSelected = day.dateStr === selectedDay;
              let colClass = "dash-day-col";
              if (day.isToday) colClass += " dash-day-today";
              if (isSelected) colClass += " dash-day-selected";
              return (
                <div
                  key={day.dateStr}
                  className={colClass}
                  role="button"
                  tabIndex={0}
                  aria-pressed={isSelected}
                  onClick={() => setSelectedDay(day.dateStr)}
                  onKeyDown={(e) => {
                    if (e.key === "Enter" || e.key === " ") {
                      setSelectedDay(day.dateStr);
                    }
                  }}
                >
                  <div className="dash-day-header">
                    <span className="dash-day-label">{day.label}</span>
                    <span className="dash-day-date">{day.date}</span>
                  </div>
                  {dayActs.length === 0 ? (
                    <p className="dash-day-empty">–</p>
                  ) : (
                    dayActs.slice(0, 3).map((act) => (
                      <div key={act.id} className="dash-day-activity">
                        <span className="dash-day-act-name">{act.name}</span>
                        {act.startTime && (
                          <span className="dash-day-act-time">
                            {formatTime(act.startTime)}
                          </span>
                        )}
                      </div>
                    ))
                  )}
                  {dayActs.length > 3 && (
                    <p className="dash-day-more">
                      +{dayActs.length - 3} weitere
                    </p>
                  )}
                </div>
              );
            })}
          </div>
        )}
      </section>

      {/* Open todos */}
      <section className="section card">
        <div className="section-header">
          <h2 className="section-title">
            <CheckSquare size={16} />
            Offene Aufgaben
          </h2>
          <Link to="/tasks" className="section-link">
            Alle anzeigen <ArrowRight size={14} />
          </Link>
        </div>
        {isLoading ? (
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
        ) : openTodos.length === 0 ? (
          <div className="empty-state">
            Keine offenen Aufgaben —{" "}
            <Link to="/tasks" className="empty-link">
              neue hinzufügen
            </Link>
            !
          </div>
        ) : (
          <ul className="task-list">
            {openTodos.map((t) => (
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
  );
}
