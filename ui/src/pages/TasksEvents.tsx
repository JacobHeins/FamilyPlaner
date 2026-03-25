import { useState, useEffect } from "react";
import { Plus, CheckSquare, Zap, Trash2 } from "lucide-react";
import { getFamilies } from "../api/familyApi";
import "./TasksEvents.css";

type Item = {
  id: number;
  title: string;
  assignee: string;
  type: "task" | "event";
  done: boolean;
  day: string;
  time?: string;
};

const MEMBERS = ["Mom", "Dad", "Luca", "Mia", "All"];
const DAYS = [
  "Monday",
  "Tuesday",
  "Wednesday",
  "Thursday",
  "Friday",
  "Saturday",
  "Sunday",
];

const sample: Item[] = [
  {
    id: 1,
    title: "Buy groceries",
    assignee: "Mom",
    type: "task",
    done: false,
    day: "Monday",
  },
  {
    id: 2,
    title: "Pick up dry cleaning",
    assignee: "Dad",
    type: "task",
    done: false,
    day: "Tuesday",
  },
  {
    id: 3,
    title: "Football training",
    assignee: "Luca",
    type: "event",
    done: false,
    day: "Monday",
    time: "16:00",
  },
  {
    id: 4,
    title: "Dentist appointment",
    assignee: "Mia",
    type: "event",
    done: false,
    day: "Tuesday",
    time: "10:30",
  },
  {
    id: 5,
    title: "Book summer holiday",
    assignee: "Dad",
    type: "task",
    done: false,
    day: "Friday",
  },
  {
    id: 6,
    title: "Sign permission slip",
    assignee: "Mom",
    type: "task",
    done: true,
    day: "Monday",
  },
  {
    id: 7,
    title: "Parent-teacher meeting",
    assignee: "All",
    type: "event",
    done: false,
    day: "Wednesday",
    time: "18:00",
  },
];

let nextId = 20;

export default function TasksEvents() {
  const [items, setItems] = useState<Item[]>(sample);
  const [tab, setTab] = useState<"all" | "tasks" | "events">("all");
  const [showForm, setShowForm] = useState(false);
  const [members, setMembers] = useState<string[]>(MEMBERS);
  const [form, setForm] = useState<Omit<Item, "id" | "done">>({
    title: "",
    assignee: MEMBERS[0],
    type: "task",
    day: DAYS[0],
  });

  useEffect(() => {
    getFamilies()
      .then((families) => {
        const names = families.flatMap((f) =>
          f.familyMembers.map((m) => m.name),
        );
        if (names.length > 0) {
          setMembers(["All", ...names]);
          setForm((f) => ({ ...f, assignee: names[0] }));
        }
      })
      .catch(() => {});
  }, []);

  const filtered = items.filter(
    (i) =>
      tab === "all" ||
      (tab === "tasks" ? i.type === "task" : i.type === "event"),
  );

  function toggle(id: number) {
    setItems((prev) =>
      prev.map((i) => (i.id === id ? { ...i, done: !i.done } : i)),
    );
  }

  function remove(id: number) {
    setItems((prev) => prev.filter((i) => i.id !== id));
  }

  function submit() {
    if (!form.title.trim()) return;
    setItems((prev) => [...prev, { id: nextId++, ...form, done: false }]);
    setShowForm(false);
    setForm({ title: "", assignee: MEMBERS[0], type: "task", day: DAYS[0] });
  }

  return (
    <div className="page">
      <header className="page-header">
        <div>
          <h1 className="page-title">Tasks &amp; Events</h1>
          <p className="page-subtitle">
            Keep track of everything your family needs to do.
          </p>
        </div>
        <button className="btn-primary" onClick={() => setShowForm((s) => !s)}>
          <Plus size={16} /> New Item
        </button>
      </header>

      {showForm && (
        <div className="te-form card">
          <div className="te-form-row">
            <input
              className="te-input"
              placeholder="Title…"
              value={form.title}
              onChange={(e) =>
                setForm((f) => ({ ...f, title: e.target.value }))
              }
              onKeyDown={(e) => e.key === "Enter" && submit()}
              autoFocus
            />
          </div>
          <div className="te-form-row">
            <div className="type-toggle">
              <button
                className={`type-btn${form.type === "task" ? " active" : ""}`}
                onClick={() => setForm((f) => ({ ...f, type: "task" }))}
              >
                <CheckSquare size={14} /> Task
              </button>
              <button
                className={`type-btn${form.type === "event" ? " active" : ""}`}
                onClick={() => setForm((f) => ({ ...f, type: "event" }))}
              >
                <Zap size={14} /> Event
              </button>
            </div>
            <select
              className="te-input"
              value={form.assignee}
              onChange={(e) =>
                setForm((f) => ({ ...f, assignee: e.target.value }))
              }
            >
              {members.map((m) => (
                <option key={m}>{m}</option>
              ))}
            </select>
            <select
              className="te-input"
              value={form.day}
              onChange={(e) => setForm((f) => ({ ...f, day: e.target.value }))}
            >
              {DAYS.map((d) => (
                <option key={d}>{d}</option>
              ))}
            </select>
            {form.type === "event" && (
              <input
                className="te-input"
                type="time"
                value={form.time || ""}
                onChange={(e) =>
                  setForm((f) => ({ ...f, time: e.target.value }))
                }
              />
            )}
          </div>
          <div className="add-actions">
            <button className="add-cancel" onClick={() => setShowForm(false)}>
              Cancel
            </button>
            <button className="add-submit" onClick={submit}>
              Add
            </button>
          </div>
        </div>
      )}

      <div className="te-tabs">
        {(["all", "tasks", "events"] as const).map((t) => (
          <button
            key={t}
            className={`te-tab${tab === t ? " active" : ""}`}
            onClick={() => setTab(t)}
          >
            {t.charAt(0).toUpperCase() + t.slice(1)}
          </button>
        ))}
      </div>

      <div className="te-list card">
        {filtered.length === 0 && (
          <p className="te-empty">No items here yet. Add one above!</p>
        )}
        {filtered.map((item) => (
          <div key={item.id} className={`te-item${item.done ? " done" : ""}`}>
            <button
              className={`task-check${item.done ? " checked" : ""}`}
              onClick={() => toggle(item.id)}
            >
              {item.done && (
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
            </button>
            <div className="te-info">
              <span className="te-title">{item.title}</span>
              <span className="te-meta">
                {item.assignee} · {item.day}
                {item.time ? ` · ${item.time}` : ""}
              </span>
            </div>
            <span
              className={`te-badge ${item.type === "task" ? "badge-task" : "badge-event"}`}
            >
              {item.type}
            </span>
            <button className="te-delete" onClick={() => remove(item.id)}>
              <Trash2 size={14} />
            </button>
          </div>
        ))}
      </div>
    </div>
  );
}
