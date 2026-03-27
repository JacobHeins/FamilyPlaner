import { useState, useEffect } from "react";
import { Plus, X, ChevronLeft, ChevronRight } from "lucide-react";
import { getFamilies } from "../api/familyApi";
import "./WeeklyPlanner.css";

const DAYS = [
  "Monday",
  "Tuesday",
  "Wednesday",
  "Thursday",
  "Friday",
  "Saturday",
  "Sunday",
];
const MEMBERS = ["Mom", "Dad", "Luca", "Mia"];
const COLORS = ["accent", "green", "orange", "pink", "blue"];

type Entry = { id: number; title: string; member: string; color: string };
type WeekData = Record<string, Entry[]>;

const sample: WeekData = {
  Monday: [
    { id: 1, title: "Football training", member: "Luca", color: "blue" },
  ],
  Tuesday: [{ id: 2, title: "Dentist", member: "Mia", color: "pink" }],
  Wednesday: [
    { id: 3, title: "Parent meeting", member: "Mom", color: "orange" },
  ],
  Thursday: [{ id: 4, title: "Swimming", member: "Luca", color: "green" }],
  Friday: [],
  Saturday: [{ id: 5, title: "Family brunch", member: "All", color: "accent" }],
  Sunday: [],
};

let nextId = 10;

function getWeekLabel(offset: number) {
  const today = new Date();
  const monday = new Date(today);
  monday.setDate(today.getDate() - ((today.getDay() + 6) % 7) + offset * 7);
  const sunday = new Date(monday);
  sunday.setDate(monday.getDate() + 6);
  const fmt = (d: Date) =>
    d.toLocaleDateString("en-GB", { day: "numeric", month: "short" });
  return `${fmt(monday)} – ${fmt(sunday)}`;
}

function getDayDate(dayIndex: number, weekOffset: number) {
  const today = new Date();
  const monday = new Date(today);
  monday.setDate(today.getDate() - ((today.getDay() + 6) % 7) + weekOffset * 7);
  const d = new Date(monday);
  d.setDate(monday.getDate() + dayIndex);
  return d.getDate();
}

export default function WeeklyPlanner() {
  const [weekOffset, setWeekOffset] = useState(0);
  const [data, setData] = useState<WeekData>(sample);
  const [adding, setAdding] = useState<string | null>(null);
  const [members, setMembers] = useState<string[]>(MEMBERS);
  const [form, setForm] = useState({
    title: "",
    member: MEMBERS[0],
    color: COLORS[0],
  });

  useEffect(() => {
    getFamilies()
      .then((families) => {
        const names = families.flatMap((f) =>
          f.familyMembers.map((m) => m.name),
        );
        if (names.length > 0) {
          setMembers(["All", ...names]);
          setForm((f) => ({ ...f, member: names[0] }));
        }
      })
      .catch(() => {}); // keep defaults on error
  }, []);

  function openAdd(day: string) {
    setAdding(day);
    setForm({ title: "", member: MEMBERS[0], color: COLORS[0] });
  }

  function submitAdd() {
    if (!form.title.trim() || !adding) return;
    const entry: Entry = { id: nextId++, ...form };
    setData((prev) => ({
      ...prev,
      [adding]: [...(prev[adding] || []), entry],
    }));
    setAdding(null);
  }

  function removeEntry(day: string, id: number) {
    setData((prev) => ({
      ...prev,
      [day]: prev[day].filter((e) => e.id !== id),
    }));
  }

  return (
    <div className="page planner-page">
      <header className="page-header">
        <div>
          <h1 className="page-title">Weekly Planner</h1>
          <p className="page-subtitle">
            Plan and organise your family's upcoming week.
          </p>
        </div>
        <div className="week-nav">
          <button
            className="week-nav-btn"
            onClick={() => setWeekOffset((o) => o - 1)}
          >
            <ChevronLeft size={16} />
          </button>
          <span className="week-nav-label">{getWeekLabel(weekOffset)}</span>
          <button
            className="week-nav-btn"
            onClick={() => setWeekOffset((o) => o + 1)}
          >
            <ChevronRight size={16} />
          </button>
        </div>
      </header>

      <div className="planner-grid">
        {DAYS.map((day, idx) => {
          const dateNum = getDayDate(idx, weekOffset);
          const isToday =
            weekOffset === 0 &&
            new Date().toLocaleDateString("en-GB", { weekday: "long" }) === day;

          return (
            <div key={day} className={`planner-col${isToday ? " today" : ""}`}>
              <div className="planner-col-header">
                <span className="planner-day-name">{day.slice(0, 3)}</span>
                <span
                  className={`planner-day-num${isToday ? " today-num" : ""}`}
                >
                  {dateNum}
                </span>
              </div>

              <div className="planner-entries">
                {(data[day] || []).map((e) => (
                  <div key={e.id} className={`planner-entry entry-${e.color}`}>
                    <div className="entry-body">
                      <span className="entry-title">{e.title}</span>
                      <span className="entry-member">{e.member}</span>
                    </div>
                    <button
                      className="entry-remove"
                      onClick={() => removeEntry(day, e.id)}
                    >
                      <X size={12} />
                    </button>
                  </div>
                ))}
              </div>

              {adding === day ? (
                <div className="add-form">
                  <input
                    className="add-input"
                    placeholder="Title…"
                    value={form.title}
                    onChange={(e) =>
                      setForm((f) => ({ ...f, title: e.target.value }))
                    }
                    onKeyDown={(e) => e.key === "Enter" && submitAdd()}
                    autoFocus
                  />
                  <select
                    className="add-select"
                    value={form.member}
                    onChange={(e) =>
                      setForm((f) => ({ ...f, member: e.target.value }))
                    }
                  >
                    {members.map((m) => (
                      <option key={m}>{m}</option>
                    ))}
                  </select>
                  <div className="add-color-row">
                    {COLORS.map((c) => (
                      <button
                        key={c}
                        className={`color-swatch swatch-${c}${form.color === c ? " selected" : ""}`}
                        onClick={() => setForm((f) => ({ ...f, color: c }))}
                      />
                    ))}
                  </div>
                  <div className="add-actions">
                    <button
                      className="add-cancel"
                      onClick={() => setAdding(null)}
                    >
                      Cancel
                    </button>
                    <button className="add-submit" onClick={submitAdd}>
                      Add
                    </button>
                  </div>
                </div>
              ) : (
                <button
                  className="planner-add-btn"
                  onClick={() => openAdd(day)}
                >
                  <Plus size={14} /> Add
                </button>
              )}
            </div>
          );
        })}
      </div>
    </div>
  );
}
