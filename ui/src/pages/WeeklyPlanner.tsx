import { useMemo } from "react";
import { Loader2, AlertCircle, ArrowRight } from "lucide-react";
import { Link } from "react-router-dom";
import { useGetFamiliesQuery, roleColor } from "../api/familyApi";
import { useGetActivitiesQuery } from "../api/activityApi";
import { getWeekDates, isInCurrentWeek, formatTime } from "../utils/weekUtils";
import type { Activity } from "../app/store/types";
import "./WeeklyPlanner.css";

export default function WeeklyPlanner() {
  const weekDays = useMemo(() => getWeekDates(), []);

  const {
    data: families = [],
    isLoading: familiesLoading,
    isError: familiesError,
  } = useGetFamiliesQuery();

  const primaryFamily = families[0] ?? null;

  const {
    data: activities = [],
    isLoading: activitiesLoading,
    isError: activitiesError,
  } = useGetActivitiesQuery(
    { familyId: primaryFamily?.id ?? 0 },
    { skip: !primaryFamily },
  );

  const isLoading = familiesLoading || activitiesLoading;
  const isError = familiesError || activitiesError;

  const weekActivities = useMemo(
    () => activities.filter((a) => isInCurrentWeek(a.day)),
    [activities],
  );

  const activitiesByDay = useMemo(() => {
    const map: Record<string, Activity[]> = {};
    for (const day of weekDays) {
      map[day.dateStr] = weekActivities.filter((a) => a.day === day.dateStr);
    }
    return map;
  }, [weekDays, weekActivities]);

  return (
    <div className="wp-page">
      <header className="wp-header">
        <div>
          <h1 className="page-title">Wochenübersicht</h1>
          <p className="page-subtitle">
            Aktivitäten dieser Woche auf einen Blick.
          </p>
        </div>
        <Link to="/activities" className="wp-cta-link">
          Aktivität planen <ArrowRight size={14} />
        </Link>
      </header>

      {isLoading && (
        <div className="wp-loading">
          <Loader2 size={18} className="spin" />
          Aktivitäten werden geladen…
        </div>
      )}

      {!isLoading && isError && (
        <div className="wp-error">
          <AlertCircle size={16} />
          Aktivitäten konnten nicht geladen werden. Bitte erneut versuchen.
        </div>
      )}

      {!isLoading && !isError && weekActivities.length === 0 && (
        <p className="wp-empty-week-hint">
          Diese Woche noch keine Aktivitäten geplant.{" "}
          <Link to="/activities">Jetzt planen →</Link>
        </p>
      )}

      {!isLoading && !isError && (
        <div className="wp-week-grid">
          {weekDays.map((day) => {
            const dayActivities = activitiesByDay[day.dateStr] ?? [];
            return (
              <div
                key={day.dateStr}
                className={`wp-day-col${day.isToday ? " wp-day-today" : ""}`}
              >
                <div className="wp-day-header">
                  <span className="wp-day-label">{day.label}</span>
                  <span className="wp-day-date">{day.date}</span>
                </div>

                {dayActivities.length === 0 ? (
                  <p className="wp-empty-day">
                    {day.isToday ? "Heute leer" : "–"}
                  </p>
                ) : (
                  dayActivities.map((act) => (
                    <ActivityCard key={act.id} activity={act} />
                  ))
                )}
              </div>
            );
          })}
        </div>
      )}
    </div>
  );
}

function ActivityCard({ activity }: { activity: Activity }) {
  const start = formatTime(activity.startTime);
  const end = formatTime(activity.endTime);
  const timeStr =
    start && end ? `${start}–${end}` : start ? `ab ${start}` : null;

  return (
    <div className="wp-activity-card">
      <span className="wp-activity-name">{activity.name}</span>
      {timeStr && <span className="wp-activity-time">{timeStr}</span>}
      {activity.location && (
        <span className="wp-activity-location">📍 {activity.location}</span>
      )}
      {activity.participants && activity.participants.length > 0 && (
        <div className="wp-participants">
          {activity.participants.map((p) => (
            <span
              key={p.id}
              className={`wp-participant-badge avatar-${roleColor(p.role)}`}
            >
              {p.name}
            </span>
          ))}
        </div>
      )}
    </div>
  );
}
