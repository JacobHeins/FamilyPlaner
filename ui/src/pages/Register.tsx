import { AlertCircle, UserPlus } from "lucide-react";
import { FormEvent, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useRegisterMutation } from "../api/authApi";
import { tokenStored } from "../app/store/authSlice";
import { useAppDispatch } from "../app/store/hooks";
import type { ProblemDetails } from "../app/store/types";
import "./Auth.css";

function errorMessage(error: unknown): string {
  const detail = (error as { data?: ProblemDetails })?.data?.detail;
  return detail ?? "Die Registrierung ist fehlgeschlagen. Bitte versuchen Sie es erneut.";
}

export default function Register() {
  const dispatch = useAppDispatch();
  const navigate = useNavigate();
  const [register, { isLoading }] = useRegisterMutation();
  const [familyName, setFamilyName] = useState("");
  const [familySlug, setFamilySlug] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState<string | null>(null);

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    const name = familyName.trim();
    const slug = familySlug.trim();

    if (!name || !slug || !password) {
      setError("Bitte füllen Sie alle Felder aus.");
      return;
    }
    if (password.length < 8) {
      setError("Das Passwort muss mindestens 8 Zeichen lang sein.");
      return;
    }

    setError(null);
    try {
      const { token } = await register({
        familyName: name,
        familySlug: slug,
        password,
      }).unwrap();
      dispatch(tokenStored(token));
      navigate("/", { replace: true });
    } catch (requestError) {
      setError(errorMessage(requestError));
    }
  }

  return (
    <main className="auth-page">
      <section className="auth-panel" aria-labelledby="register-title">
        <div className="auth-brand">FamilyPlanner</div>
        <h1 id="register-title">Familienkonto erstellen</h1>
        <p className="auth-intro">Starten Sie gemeinsam mit Ihrer Familie.</p>

        {error && (
          <div className="error-state" role="alert">
            <AlertCircle size={18} />
            {error}
          </div>
        )}

        <form className="auth-form" onSubmit={handleSubmit}>
          <label htmlFor="family-name">Familienname</label>
          <input
            id="family-name"
            name="familyName"
            autoComplete="organization"
            value={familyName}
            onChange={(event) => setFamilyName(event.target.value)}
            placeholder="z. B. Familie Muster"
            disabled={isLoading}
          />

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
            autoComplete="new-password"
            value={password}
            onChange={(event) => setPassword(event.target.value)}
            disabled={isLoading}
          />

          <button className="auth-submit" type="submit" disabled={isLoading}>
            <UserPlus size={18} />
            {isLoading ? "Konto wird erstellt…" : "Konto erstellen"}
          </button>
        </form>

        <p className="auth-switch">
          Bereits registriert? <Link to="/login">Jetzt anmelden</Link>
        </p>
      </section>
    </main>
  );
}