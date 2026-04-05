const DAY_LABELS = ["Mo", "Di", "Mi", "Do", "Fr", "Sa", "So"];

/** Returns today as a YYYY-MM-DD string based on the device local time. */
export function getToday(): string {
  const today = new Date();
  const year = today.getFullYear();
  const month = String(today.getMonth() + 1).padStart(2, "0");
  const day = String(today.getDate()).padStart(2, "0");
  return `${year}-${month}-${day}`;
}

export interface WeekDay {
  label: string; // "Mo"–"So"
  dateStr: string; // YYYY-MM-DD
  date: number; // day-of-month number
  isToday: boolean;
}

/** Returns an array of 7 WeekDay entries for Mon–Sun of the current ISO week. */
export function getWeekDates(): WeekDay[] {
  const today = new Date();
  const monday = new Date(today);
  monday.setDate(today.getDate() - ((today.getDay() + 6) % 7));
  const todayStr = getToday();

  return DAY_LABELS.map((label, i) => {
    const d = new Date(monday);
    d.setDate(monday.getDate() + i);
    const year = d.getFullYear();
    const month = String(d.getMonth() + 1).padStart(2, "0");
    const day = String(d.getDate()).padStart(2, "0");
    const dateStr = `${year}-${month}-${day}`;
    return { label, dateStr, date: d.getDate(), isToday: dateStr === todayStr };
  });
}

/** Returns true when the given YYYY-MM-DD string falls within the current ISO week. */
export function isInCurrentWeek(dateStr: string | null): boolean {
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

/** Formats a HH:mm:ss time string to HH:mm for display. */
export function formatTime(timeStr: string | null): string | null {
  if (!timeStr) return null;
  return timeStr.slice(0, 5);
}
