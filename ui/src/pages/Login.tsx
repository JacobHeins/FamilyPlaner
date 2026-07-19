import { AlertCircle, LogIn } from "lucide-react";
import { FormEvent, useState } from "react";
import { Link, useLocation, useNavigate } from "react-router-dom";
import { useLoginMutation } from "../api/authApi";
import { useAppDispatch } from "../app/store/hooks";
import { tokenStored } from "../app/store/authSlice";
import type { ProblemDetails } from "../app/store/types";
import "./Auth.css";

interface LocationState {
  from?: { pathname?: string };
  sessionExpired?: boolean;
}

function errorMessage(error: unknown): string {
  const detail = (error as { data?: ProblemDetails })?.data?.detail;
  return detail ?? "Die Anmeldung ist fehlgeschlagen. Bitte versuchen Sie es erneut.";
}

export default function Login() {
  const dispatch = useAppDispatch();
  const navigate = useNavigate();
  const location = useLocation();
  const [login, { isLoading }] = useLoginMutation();
  const [familySlug, setFamilySlug] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState<string | null>(null);
  const { from, sessionExpired } = (location.state ?? {}) as LocationState;

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    const slug = familySlug.trim();

    if (!slug || !password) {
      setError("Bitte geben Sie Familienkennung und Passwort ein.");
      return;
    }

    setError(null);
    try {
      const { token } = await login({ familySlug: slug, password }).unwrap();
      dispatch(tokenStored(token));
      navigate(from?.pathname ?? "/", { replace: true });
    } catch (requestError) {
      setError(errorMessage(requestError));
    }
  }

  return (
    <main className="auth-page">
      <section className="auth-panel" aria-labelledby="login-title">
        <div className="auth-brand">FamilyPlanner</div>
        <h1 id="login-title">Willkommen zurück</h1>
        <p className="auth-intro">Melden Sie sich mit Ihrer Familienkennung an.</p>

        {sessionExpired && (
          <div className="error-state" role="alert">
            <AlertCircle size={18} />
            Ihre Sitzung ist abgelaufen. Bitte melden Sie sich erneut an.
          </div>
        )}
        {error && (
          <div className="error-state" role="alert">
            <AlertCircle size={18} />
            {error}
          </div>
        )}

        <form className="auth-form" onSubmit={handleSubmit}>
          <label htmlFor="family-slug">Familienkennung</label>
          <input
            id="family-slug"
            name="familySlug"
            autoComplete="username"
            value={familySlug}
            onChange={(event) => setFamilySlug(event.target.value)}
            placeholder="z. B. familie-muster"
            disabled={isLoading}
          />

          <label htmlFor="password">Passwort</label>
          <input
            id="password"
            name="password"
            type="password"
            autoComplete="current-password"
            value={password}
            onChange={(event) => setPassword(event.target.value)}
            disabled={isLoading}
          />

          <button className="auth-submit" type="submit" disabled={isLoading}>
            <LogIn size={18} />
            {isLoading ? "Anmeldung läuft…" : "Anmelden"}
          </button>
        </form>

        <p className="auth-switch">
          Noch kein Familienkonto? <Link to="/register">Jetzt registrieren</Link>
        </p>
      </section>
    </main>
  );
}