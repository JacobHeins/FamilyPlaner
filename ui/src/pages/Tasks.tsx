import { useState, useMemo } from "react";
import {
  Plus,
  Trash2,
  Loader2,
  AlertCircle,
  X,
  Pencil,
  Check,
} from "lucide-react";
import { useGetFamiliesQuery, roleColor } from "../api/familyApi";
import {
  useGetTodosQuery,
  useCreateTodoMutation,
  useUpdateTodoMutation,
  useDeleteTodoMutation,
} from "../api/todoApi";
import type { Todo } from "../app/store/types";
import "./Tasks.css";

type StatusFilter = "all" | "open" | "done";

interface CreateForm {
  name: string;
  description: string;
  deuDate: string;
  assigneeId: number | "";
}

const EMPTY_FORM: CreateForm = {
  name: "",
  description: "",
  deuDate: "",
  assigneeId: "",
};

/** Convert a YYYY-MM-DD date string to a UTC ISO instant the backend expects. */
function toInstant(dateStr: string): string {
  return `${dateStr}T23:59:59.999Z`;
}

export default function Tasks() {
  const { data: families = [], isLoading: familiesLoading } =
    useGetFamiliesQuery();
  const primaryFamily = families[0] ?? null;

  const {
    data: todos = [],
    isLoading: todosLoading,
    isError: todosError,
  } = useGetTodosQuery(
    { familyId: primaryFamily?.id ?? 0 },
    { skip: !primaryFamily },
  );

  const [createTodo, { isLoading: creating }] = useCreateTodoMutation();
  const [updateTodo] = useUpdateTodoMutation();
  const [deleteTodo] = useDeleteTodoMutation();

  const [statusFilter, setStatusFilter] = useState<StatusFilter>("all");
  const [assigneeFilter, setAssigneeFilter] = useState<number | null>(null);
  const [showForm, setShowForm] = useState(false);
  const [form, setForm] = useState<CreateForm>(EMPTY_FORM);
  const [formError, setFormError] = useState<string | null>(null);

  const [editingId, setEditingId] = useState<number | null>(null);
  const [editForm, setEditForm] = useState<{
    name: string;
    description: string;
    deuDate: string;
    assigneeId: number | "";
  }>({ name: "", description: "", deuDate: "", assigneeId: "" });
  const [editError, setEditError] = useState<string | null>(null);
  const [saving, setSaving] = useState(false);

  const allMembers = useMemo(
    () => families.flatMap((f) => f.familyMembers),
    [families],
  );

  const filtered = useMemo(() => {
    let list: Todo[] = todos;
    if (statusFilter === "open") list = list.filter((t) => !t.completed);
    if (statusFilter === "done") list = list.filter((t) => t.completed);
    if (assigneeFilter !== null)
      list = list.filter((t) => t.assignee?.id === assigneeFilter);
    return list;
  }, [todos, statusFilter, assigneeFilter]);

  const openCount = useMemo(
    () => todos.filter((t) => !t.completed).length,
    [todos],
  );

  async function submitCreate() {
    if (!form.name.trim() || !primaryFamily) return;
    setFormError(null);
    try {
      await createTodo({
        name: form.name.trim(),
        description: form.description.trim() || undefined,
        deuDate: form.deuDate ? toInstant(form.deuDate) : undefined,
        familyId: primaryFamily.id,
        assigneeId:
          form.assigneeId !== "" ? Number(form.assigneeId) : undefined,
      }).unwrap();
      setShowForm(false);
      setForm(EMPTY_FORM);
    } catch {
      setFormError("Aufgabe konnte nicht erstellt werden.");
    }
  }

  async function toggleComplete(todo: Todo) {
    try {
      await updateTodo({
        id: todo.id,
        name: todo.name,
        description: todo.description ?? undefined,
        deuDate: todo.dueDate ?? undefined,
        completed: !todo.completed,
        assigneeId: todo.assignee?.id,
      }).unwrap();
    } catch {
      // silent — UI is optimistic via RTK cache invalidation
    }
  }

  async function handleDelete(id: number) {
    try {
      await deleteTodo(id).unwrap();
    } catch {
      // silent
    }
  }

  function startEdit(todo: Todo) {
    setEditingId(todo.id);
    setEditError(null);
    setEditForm({
      name: todo.name,
      description: todo.description ?? "",
      deuDate: todo.dueDate ? todo.dueDate.slice(0, 10) : "",
      assigneeId: todo.assignee?.id ?? "",
    });
  }

  function cancelEdit() {
    setEditingId(null);
    setEditError(null);
  }

  async function submitEdit(todo: Todo) {
    if (!editForm.name.trim()) return;
    setEditError(null);
    setSaving(true);
    try {
      await updateTodo({
        id: todo.id,
        name: editForm.name.trim(),
        description: editForm.description.trim() || undefined,
        deuDate: editForm.deuDate ? toInstant(editForm.deuDate) : undefined,
        completed: todo.completed,
        assigneeId:
          editForm.assigneeId !== "" ? Number(editForm.assigneeId) : undefined,
      }).unwrap();
      setEditingId(null);
    } catch {
      setEditError("Änderungen konnten nicht gespeichert werden.");
    } finally {
      setSaving(false);
    }
  }

  async function handleDeleteFromEdit(id: number) {
    setSaving(true);
    try {
      await deleteTodo(id).unwrap();
      setEditingId(null);
    } catch {
      setEditError("Aufgabe konnte nicht gelöscht werden.");
    } finally {
      setSaving(false);
    }
  }

  const isLoading = familiesLoading || todosLoading;

  return (
    <div className="page">
      <header className="page-header">
        <div>
          <h1 className="page-title">Aufgaben</h1>
          <p className="page-subtitle">
            {isLoading
              ? "Wird geladen…"
              : `${openCount} offene Aufgabe${openCount !== 1 ? "n" : ""} für ${primaryFamily?.name ?? "Ihre Familie"}`}
          </p>
        </div>
        <button
          className="btn-primary"
          onClick={() => setShowForm((s) => !s)}
          disabled={!primaryFamily}
        >
          <Plus size={16} /> Neue Aufgabe
        </button>
      </header>

      {formError && (
        <div className="error-state">
          <AlertCircle size={16} />
          {formError}
          <button className="error-dismiss" onClick={() => setFormError(null)}>
            <X size={14} />
          </button>
        </div>
      )}

      {showForm && (
        <div className="te-form card">
          <div className="te-form-row">
            <input
              className="te-input"
              placeholder="Aufgabenname…"
              value={form.name}
              onChange={(e) => setForm((f) => ({ ...f, name: e.target.value }))}
              onKeyDown={(e) => e.key === "Enter" && submitCreate()}
              autoFocus
            />
          </div>
          <div className="te-form-row">
            <input
              className="te-input"
              placeholder="Beschreibung (optional)…"
              value={form.description}
              onChange={(e) =>
                setForm((f) => ({ ...f, description: e.target.value }))
              }
            />
          </div>
          <div className="te-form-row">
            <input
              className="te-input"
              type="date"
              value={form.deuDate}
              onChange={(e) =>
                setForm((f) => ({ ...f, deuDate: e.target.value }))
              }
              title="Fälligkeitsdatum (optional)"
            />
            <select
              className="te-input"
              value={form.assigneeId}
              onChange={(e) =>
                setForm((f) => ({
                  ...f,
                  assigneeId:
                    e.target.value === "" ? "" : Number(e.target.value),
                }))
              }
            >
              <option value="">Nicht zugewiesen</option>
              {allMembers.map((m) => (
                <option key={m.id} value={m.id}>
                  {m.name}
                </option>
              ))}
            </select>
          </div>
          <div className="add-actions">
            <button className="add-cancel" onClick={() => setShowForm(false)}>
              Abbrechen
            </button>
            <button
              className="add-submit"
              onClick={submitCreate}
              disabled={creating}
            >
              {creating ? "Wird hinzugefügt…" : "Aufgabe hinzufügen"}
            </button>
          </div>
        </div>
      )}

      {/* Assignee filter */}
      {allMembers.length > 0 && (
        <div className="te-filter-row">
          <button
            className={`te-filter-btn${assigneeFilter === null ? " active" : ""}`}
            onClick={() => setAssigneeFilter(null)}
          >
            Alle Mitglieder
          </button>
          {allMembers.map((m) => (
            <button
              key={m.id}
              className={`te-filter-btn${assigneeFilter === m.id ? " active" : ""}`}
              onClick={() =>
                setAssigneeFilter(assigneeFilter === m.id ? null : m.id)
              }
            >
              {m.name}
            </button>
          ))}
        </div>
      )}

      {/* Status tabs */}
      <div className="te-tabs">
        {(["all", "open", "done"] as const).map((s) => (
          <button
            key={s}
            className={`te-tab${statusFilter === s ? " active" : ""}`}
            onClick={() => setStatusFilter(s)}
          >
            {{ all: "Alle", open: "Offen", done: "Erledigt" }[s]}
          </button>
        ))}
      </div>

      {isLoading ? (
        <div className="loading-state">
          <Loader2 size={24} className="spin" /> Aufgaben werden geladen…
        </div>
      ) : todosError ? (
        <div className="error-state">
          <AlertCircle size={18} /> Aufgaben konnten nicht geladen werden. Bitte
          erneut versuchen.
        </div>
      ) : !primaryFamily ? (
        <div className="empty-state">
          Noch keine Familie — auf der{" "}
          <a href="/members" className="empty-link">
            Familienseite
          </a>{" "}
          erstellen.
        </div>
      ) : (
        <div className="te-list card">
          {filtered.length === 0 ? (
            <p className="te-empty">
              {statusFilter !== "all" || assigneeFilter !== null
                ? "Keine Aufgaben entsprechen dem aktuellen Filter."
                : "Noch keine Aufgaben — oben hinzufügen!"}
            </p>
          ) : (
            filtered.map((todo) => (
              <div
                key={todo.id}
                className={`te-item${todo.completed ? " done" : ""}${editingId === todo.id ? " editing" : ""}`}
              >
                {editingId === todo.id ? (
                  <div className="te-edit-form">
                    <div className="te-form-row">
                      <input
                        className="te-input"
                        placeholder="Aufgabenname…"
                        value={editForm.name}
                        onChange={(e) =>
                          setEditForm((f) => ({ ...f, name: e.target.value }))
                        }
                        autoFocus
                      />
                    </div>
                    <div className="te-form-row">
                      <input
                        className="te-input"
                        placeholder="Beschreibung (optional)…"
                        value={editForm.description}
                        onChange={(e) =>
                          setEditForm((f) => ({
                            ...f,
                            description: e.target.value,
                          }))
                        }
                      />
                    </div>
                    <div className="te-form-row">
                      <input
                        className="te-input"
                        type="date"
                        value={editForm.deuDate}
                        onChange={(e) =>
                          setEditForm((f) => ({
                            ...f,
                            deuDate: e.target.value,
                          }))
                        }
                        title="Fälligkeitsdatum"
                      />
                      <select
                        className="te-input"
                        value={editForm.assigneeId}
                        onChange={(e) =>
                          setEditForm((f) => ({
                            ...f,
                            assigneeId:
                              e.target.value === ""
                                ? ""
                                : Number(e.target.value),
                          }))
                        }
                      >
                        <option value="">Nicht zugewiesen</option>
                        {allMembers.map((m) => (
                          <option key={m.id} value={m.id}>
                            {m.name}
                          </option>
                        ))}
                      </select>
                    </div>
                    {editError && (
                      <p className="te-edit-error">
                        <AlertCircle size={13} /> {editError}
                      </p>
                    )}
                    <div className="te-edit-actions">
                      <button
                        className="te-edit-delete"
                        onClick={() => handleDeleteFromEdit(todo.id)}
                        disabled={saving}
                        title="Aufgabe löschen"
                      >
                        <Trash2 size={14} /> Löschen
                      </button>
                      <div className="te-edit-confirm-row">
                        <button
                          className="add-cancel"
                          onClick={cancelEdit}
                          disabled={saving}
                        >
                          Abbrechen
                        </button>
                        <button
                          className="add-submit"
                          onClick={() => submitEdit(todo)}
                          disabled={saving}
                        >
                          {saving ? (
                            <Loader2 size={13} className="spin" />
                          ) : (
                            <Check size={13} />
                          )}
                          Speichern
                        </button>
                      </div>
                    </div>
                  </div>
                ) : (
                  <>
                    <button
                      className={`task-check${todo.completed ? " checked" : ""}`}
                      onClick={() => toggleComplete(todo)}
                      title={
                        todo.completed
                          ? "Als offen markieren"
                          : "Als erledigt markieren"
                      }
                    >
                      {todo.completed && (
                        <svg
                          width="10"
                          height="8"
                          viewBox="0 0 10 8"
                          fill="none"
                        >
                          <path
                            d="M1 4l3 3 5-6"
                            stroke="currentColor"
                            strokeWidth="1.5"
                            strokeLinecap="round"
                            strokeLinejoin="round"
                          />
                        </svg>
                      )}
                    </button>
                    <div className="te-info">
                      <span className="te-title">{todo.name}</span>
                      <span
                        className={`assignee-badge avatar-${todo.assignee ? roleColor(todo.assignee.role, 0) : "muted"}`}
                      >
                        {todo.assignee?.name ?? "Nicht zugewiesen"}
                      </span>
                      <span className="te-meta">
                        {todo.dueDate
                          ? new Date(todo.dueDate).toLocaleDateString("de-DE", {
                              month: "short",
                              day: "numeric",
                            })
                          : ""}
                        {todo.description ? ` · ${todo.description}` : ""}
                      </span>
                    </div>
                    <span
                      className={`te-badge ${
                        todo.completed ? "badge-done" : "badge-task"
                      }`}
                    >
                      {todo.completed ? "erledigt" : "offen"}
                    </span>
                    <button
                      className="te-edit-btn"
                      onClick={() => startEdit(todo)}
                      title="Aufgabe bearbeiten"
                    >
                      <Pencil size={14} />
                    </button>
                    <button
                      className="te-delete"
                      onClick={() => handleDelete(todo.id)}
                      title="Aufgabe löschen"
                    >
                      <Trash2 size={14} />
                    </button>
                  </>
                )}
              </div>
            ))
          )}
        </div>
      )}
    </div>
  );
}
