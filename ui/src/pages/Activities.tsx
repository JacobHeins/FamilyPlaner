import { useState, useMemo, useEffect, useRef } from "react";
import { Loader2, AlertCircle, Plus, X, Pencil, Trash2 } from "lucide-react";
import { useLocation } from "react-router-dom";
import { useGetFamiliesQuery, roleColor } from "../api/familyApi";
import {
  useGetActivitiesQuery,
  useCreateActivityMutation,
  useUpdateActivityMutation,
  useDeleteActivityMutation,
} from "../api/activityApi";
import { getToday, isInCurrentWeek, formatTime } from "../utils/weekUtils";
import type {
  Activity,
  CreateActivityRequest,
  UpdateActivityRequest,
  FamilyMember,
} from "../app/store/types";
import "./Activities.css";

interface FormState {
  name: string;
  day: string;
  startTime: string;
  endtime: string;
  location: string;
  description: string;
  participants: number[];
}

function emptyForm(): FormState {
  return {
    name: "",
    day: getToday(),
    startTime: "",
    endtime: "",
    location: "",
    description: "",
    participants: [],
  };
}

function activityToForm(act: Activity): FormState {
  return {
    name: act.name,
    day: act.day,
    startTime: act.startTime ? act.startTime.slice(0, 5) : "",
    endtime: act.endTime ? act.endTime.slice(0, 5) : "",
    location: act.location ?? "",
    description: act.description ?? "",
    participants: act.participants.map((p) => p.id),
  };
}

function formatDate(dateStr: string): string {
  const [y, m, d] = dateStr.split("-");
  return `${d}.${m}.${y}`;
}

export default function Activities() {
  const location = useLocation();
  const highlightedId = useRef<number | undefined>(
    (location.state as { selectedId?: number } | null)?.selectedId,
  ).current;

  const [showForm, setShowForm] = useState(false);
  const [form, setForm] = useState<FormState>(emptyForm());
  const [nameError, setNameError] = useState<string | null>(null);
  const [dayError, setDayError] = useState<string | null>(null);
  const [filterMemberId, setFilterMemberId] = useState<number | null>(null);

  const {
    data: families = [],
    isLoading: familiesLoading,
    isError: familiesError,
  } = useGetFamiliesQuery();

  const primaryFamily = families[0] ?? null;
  const allMembers: FamilyMember[] = useMemo(
    () => primaryFamily?.familyMembers ?? [],
    [primaryFamily],
  );

  const {
    data: activities = [],
    isLoading: activitiesLoading,
    isError: activitiesError,
  } = useGetActivitiesQuery(
    { familyId: primaryFamily?.id ?? 0, memberId: filterMemberId ?? undefined },
    { skip: !primaryFamily },
  );

  const [createActivity, { isLoading: creating }] = useCreateActivityMutation();

  const isLoading = familiesLoading || activitiesLoading;
  const isError = familiesError || activitiesError;

  const weekActivities = useMemo(
    () =>
      activities
        .filter((a) => isInCurrentWeek(a.day))
        .sort((a, b) => {
          if (a.day !== b.day) return a.day.localeCompare(b.day);
          return (a.startTime ?? "").localeCompare(b.startTime ?? "");
        }),
    [activities],
  );

  // Scroll to and highlight an activity arriving via dashboard navigation
  useEffect(() => {
    if (highlightedId == null || activitiesLoading) return;
    const el = document.getElementById(`act-item-${highlightedId}`);
    el?.scrollIntoView({ behavior: "smooth", block: "center" });
  }, [highlightedId, activitiesLoading]);

  function openForm() {
    setForm(emptyForm());
    setNameError(null);
    setDayError(null);
    setShowForm(true);
  }

  function closeForm() {
    setShowForm(false);
    setNameError(null);
    setDayError(null);
  }

  function toggleParticipant(id: number) {
    setForm((prev) => ({
      ...prev,
      participants: prev.participants.includes(id)
        ? prev.participants.filter((p) => p !== id)
        : [...prev.participants, id],
    }));
  }

  async function handleSubmit() {
    let valid = true;
    if (!form.name.trim() || form.name.trim().length < 2) {
      setNameError("Name ist erforderlich (2–50 Zeichen)");
      valid = false;
    } else {
      setNameError(null);
    }
    if (!form.day) {
      setDayError("Datum ist erforderlich");
      valid = false;
    } else {
      setDayError(null);
    }
    if (!valid || !primaryFamily) return;

    const body: CreateActivityRequest = {
      name: form.name.trim(),
      familyId: primaryFamily.id,
      day: form.day,
      ...(form.description.trim()
        ? { description: form.description.trim() }
        : {}),
      ...(form.location.trim() ? { location: form.location.trim() } : {}),
      ...(form.startTime ? { startTime: form.startTime } : {}),
      ...(form.endtime ? { endtime: form.endtime } : {}),
      participants: form.participants.length > 0 ? form.participants : null,
    };

    try {
      await createActivity(body).unwrap();
      closeForm();
    } catch {
      // error shown via isError state
    }
  }

  return (
    <div className="act-page">
      <header className="act-header">
        <div>
          <h1 className="page-title">Aktivitäten</h1>
          <p className="page-subtitle">
            Aktivitäten dieser Woche planen und verwalten.
          </p>
        </div>
        {!showForm && (
          <button className="act-create-btn" onClick={openForm}>
            <Plus size={16} />
            Aktivität planen
          </button>
        )}
      </header>

      {/* Create form */}
      {showForm && (
        <div className="act-form">
          <div
            style={{
              display: "flex",
              justifyContent: "space-between",
              alignItems: "center",
            }}
          >
            <p className="act-form-title">Neue Aktivität</p>
            <button
              type="button"
              onClick={closeForm}
              style={{
                background: "none",
                border: "none",
                cursor: "pointer",
                color: "var(--text-secondary)",
              }}
              aria-label="Formular schließen"
            >
              <X size={18} />
            </button>
          </div>

          <div className="act-form-grid">
            <div className="act-form-row" style={{ gridColumn: "1 / -1" }}>
              <label className="act-label" htmlFor="act-name">
                Name *
              </label>
              <input
                id="act-name"
                className="act-input"
                type="text"
                placeholder="z. B. Fußballtraining"
                maxLength={50}
                value={form.name}
                onChange={(e) =>
                  setForm((f) => ({ ...f, name: e.target.value }))
                }
              />
              {nameError && <p className="act-validation-error">{nameError}</p>}
            </div>

            <div className="act-form-row">
              <label className="act-label" htmlFor="act-day">
                Datum *
              </label>
              <input
                id="act-day"
                className="act-input"
                type="date"
                value={form.day}
                onChange={(e) =>
                  setForm((f) => ({ ...f, day: e.target.value }))
                }
              />
              {dayError && <p className="act-validation-error">{dayError}</p>}
            </div>

            <div className="act-form-row">
              <label className="act-label" htmlFor="act-location">
                Ort
              </label>
              <input
                id="act-location"
                className="act-input"
                type="text"
                placeholder="z. B. Stadtpark"
                maxLength={50}
                value={form.location}
                onChange={(e) =>
                  setForm((f) => ({ ...f, location: e.target.value }))
                }
              />
            </div>

            <div className="act-form-row">
              <label className="act-label" htmlFor="act-start">
                Startzeit
              </label>
              <input
                id="act-start"
                className="act-input"
                type="time"
                value={form.startTime}
                onChange={(e) =>
                  setForm((f) => ({ ...f, startTime: e.target.value }))
                }
              />
            </div>

            <div className="act-form-row">
              <label className="act-label" htmlFor="act-end">
                Endzeit
              </label>
              <input
                id="act-end"
                className="act-input"
                type="time"
                value={form.endtime}
                onChange={(e) =>
                  setForm((f) => ({ ...f, endtime: e.target.value }))
                }
              />
            </div>

            <div className="act-form-row" style={{ gridColumn: "1 / -1" }}>
              <label className="act-label" htmlFor="act-desc">
                Beschreibung
              </label>
              <input
                id="act-desc"
                className="act-input"
                type="text"
                placeholder="Optionale Beschreibung"
                maxLength={300}
                value={form.description}
                onChange={(e) =>
                  setForm((f) => ({ ...f, description: e.target.value }))
                }
              />
            </div>
          </div>

          {allMembers.length > 0 && (
            <div className="act-form-row">
              <label className="act-label">Teilnehmer</label>
              <div className="act-participant-selector">
                {allMembers.map((m) => (
                  <button
                    key={m.id}
                    type="button"
                    className={`act-participant-btn${form.participants.includes(m.id) ? " selected" : ""}`}
                    onClick={() => toggleParticipant(m.id)}
                  >
                    {m.name}
                  </button>
                ))}
              </div>
            </div>
          )}

          <div className="act-form-actions">
            <button
              className="act-btn-save"
              onClick={handleSubmit}
              disabled={creating}
            >
              {creating ? "Wird gespeichert…" : "Speichern"}
            </button>
            <button className="act-btn-cancel" onClick={closeForm}>
              Abbrechen
            </button>
          </div>
        </div>
      )}

      {/* Filter row */}
      {!isLoading && !isError && allMembers.length > 0 && (
        <div className="act-filter-row">
          <button
            className={`act-participant-btn${filterMemberId === null ? " selected" : ""}`}
            onClick={() => setFilterMemberId(null)}
          >
            Alle
          </button>
          {allMembers.map((m) => (
            <button
              key={m.id}
              className={`act-participant-btn${filterMemberId === m.id ? " selected" : ""}`}
              onClick={() =>
                setFilterMemberId(m.id === filterMemberId ? null : m.id)
              }
            >
              {m.name}
            </button>
          ))}
        </div>
      )}

      {/* Loading */}
      {isLoading && (
        <div className="act-loading">
          <Loader2 size={18} className="spin" />
          Aktivitäten werden geladen…
        </div>
      )}

      {/* Error */}
      {!isLoading && isError && (
        <div className="act-error">
          <AlertCircle size={16} />
          Aktivitäten konnten nicht geladen werden. Bitte erneut versuchen.
        </div>
      )}

      {/* Empty */}
      {!isLoading && !isError && weekActivities.length === 0 && (
        <div className="act-empty">
          {filterMemberId !== null
            ? "Keine Aktivitäten für dieses Mitglied"
            : "Diese Woche noch keine Aktivitäten geplant."}
        </div>
      )}

      {/* Activity list */}
      {!isLoading && !isError && weekActivities.length > 0 && (
        <div className="act-list">
          {weekActivities.map((act) => (
            <ActivityItem
              key={act.id}
              activity={act}
              allMembers={allMembers}
              highlighted={act.id === highlightedId}
            />
          ))}
        </div>
      )}
    </div>
  );
}

function ActivityItem({
  activity,
  allMembers,
  highlighted = false,
}: {
  activity: Activity;
  allMembers: FamilyMember[];
  highlighted?: boolean;
}) {
  const [editing, setEditing] = useState(false);
  const [form, setForm] = useState<FormState>(() => activityToForm(activity));
  const [nameError, setNameError] = useState<string | null>(null);

  const [updateActivity, { isLoading: updating }] = useUpdateActivityMutation();
  const [deleteActivity, { isLoading: deleting }] = useDeleteActivityMutation();

  const start = formatTime(activity.startTime);
  const end = formatTime(activity.endTime);
  const timeStr =
    start && end ? `${start}–${end}` : start ? `ab ${start}` : null;

  function openEdit() {
    setForm(activityToForm(activity));
    setNameError(null);
    setEditing(true);
  }

  function cancelEdit() {
    setEditing(false);
    setNameError(null);
  }

  function toggleParticipant(id: number) {
    setForm((prev) => ({
      ...prev,
      participants: prev.participants.includes(id)
        ? prev.participants.filter((p) => p !== id)
        : [...prev.participants, id],
    }));
  }

  async function handleSave() {
    if (!form.name.trim() || form.name.trim().length < 2) {
      setNameError("Name ist erforderlich (2–50 Zeichen)");
      return;
    }
    setNameError(null);

    const body: UpdateActivityRequest = {
      name: form.name.trim(),
      day: form.day,
      ...(form.description.trim()
        ? { description: form.description.trim() }
        : {}),
      ...(form.location.trim() ? { location: form.location.trim() } : {}),
      ...(form.startTime ? { startTime: form.startTime } : {}),
      ...(form.endtime ? { endtime: form.endtime } : {}),
      participants: form.participants.length > 0 ? form.participants : null,
    };

    try {
      await updateActivity({ id: activity.id, body }).unwrap();
      setEditing(false);
    } catch {
      // error reflected via cache invalidation
    }
  }

  async function handleDelete() {
    try {
      await deleteActivity(activity.id).unwrap();
    } catch {
      // error reflected via cache invalidation
    }
  }

  if (editing) {
    return (
      <div className="act-item act-item-editing">
        <div className="act-form-grid">
          <div className="act-form-row" style={{ gridColumn: "1 / -1" }}>
            <label className="act-label">Name *</label>
            <input
              className="act-input"
              type="text"
              maxLength={50}
              value={form.name}
              onChange={(e) => setForm((f) => ({ ...f, name: e.target.value }))}
            />
            {nameError && <p className="act-validation-error">{nameError}</p>}
          </div>
          <div className="act-form-row">
            <label className="act-label">Datum *</label>
            <input
              className="act-input"
              type="date"
              value={form.day}
              onChange={(e) => setForm((f) => ({ ...f, day: e.target.value }))}
            />
          </div>
          <div className="act-form-row">
            <label className="act-label">Ort</label>
            <input
              className="act-input"
              type="text"
              maxLength={50}
              value={form.location}
              onChange={(e) =>
                setForm((f) => ({ ...f, location: e.target.value }))
              }
            />
          </div>
          <div className="act-form-row">
            <label className="act-label">Startzeit</label>
            <input
              className="act-input"
              type="time"
              value={form.startTime}
              onChange={(e) =>
                setForm((f) => ({ ...f, startTime: e.target.value }))
              }
            />
          </div>
          <div className="act-form-row">
            <label className="act-label">Endzeit</label>
            <input
              className="act-input"
              type="time"
              value={form.endtime}
              onChange={(e) =>
                setForm((f) => ({ ...f, endtime: e.target.value }))
              }
            />
          </div>
          <div className="act-form-row" style={{ gridColumn: "1 / -1" }}>
            <label className="act-label">Beschreibung</label>
            <input
              className="act-input"
              type="text"
              maxLength={300}
              value={form.description}
              onChange={(e) =>
                setForm((f) => ({ ...f, description: e.target.value }))
              }
            />
          </div>
        </div>

        {allMembers.length > 0 && (
          <div className="act-form-row">
            <label className="act-label">Teilnehmer</label>
            <div className="act-participant-selector">
              {allMembers.map((m) => (
                <button
                  key={m.id}
                  type="button"
                  className={`act-participant-btn${
                    form.participants.includes(m.id) ? " selected" : ""
                  }`}
                  onClick={() => toggleParticipant(m.id)}
                >
                  {m.name}
                </button>
              ))}
            </div>
          </div>
        )}

        <div className="act-form-actions">
          <button
            className="act-btn-save"
            onClick={handleSave}
            disabled={updating}
          >
            {updating ? "Wird gespeichert…" : "Speichern"}
          </button>
          <button className="act-btn-cancel" onClick={cancelEdit}>
            Abbrechen
          </button>
        </div>
      </div>
    );
  }

  return (
    <div
      id={`act-item-${activity.id}`}
      className={`act-item${highlighted ? " act-item-highlighted" : ""}`}
    >
      <div className="act-item-header">
        <span className="act-item-name">{activity.name}</span>
        <div className="act-item-actions">
          <button
            className="act-btn-icon"
            onClick={openEdit}
            aria-label="Bearbeiten"
            title="Bearbeiten"
          >
            <Pencil size={14} />
          </button>
          <button
            className="act-btn-icon act-btn-delete"
            onClick={handleDelete}
            disabled={deleting}
            aria-label="Löschen"
            title="Löschen"
          >
            <Trash2 size={14} />
          </button>
        </div>
      </div>

      <div className="act-item-meta">
        <span className="act-item-date">{formatDate(activity.day)}</span>
        {timeStr && <span className="act-item-time">🕐 {timeStr}</span>}
        {activity.location && (
          <span className="act-item-location">📍 {activity.location}</span>
        )}
      </div>

      {activity.description && (
        <p className="act-item-description">{activity.description}</p>
      )}

      <div className="act-item-participants">
        {activity.participants && activity.participants.length > 0 ? (
          activity.participants.map((p) => (
            <span
              key={p.id}
              className={`act-participant-badge avatar-${roleColor(p.role)}`}
            >
              {p.name}
            </span>
          ))
        ) : (
          <span className="act-no-participants">Keine Teilnehmer</span>
        )}
      </div>
    </div>
  );
}
