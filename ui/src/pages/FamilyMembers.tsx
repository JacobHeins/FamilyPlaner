import { useState } from "react";
import {
  Plus,
  User,
  Loader2,
  AlertCircle,
  Pencil,
  Check,
  X,
} from "lucide-react";
import {
  useGetFamiliesQuery,
  useCreateFamilyMutation,
  useUpdateFamilyMutation,
  useAddFamilyMemberMutation,
  roleColor,
  roleLabel,
} from "../api/familyApi";
import type { FamilyRole } from "../api/familyApi";
import "./FamilyMembers.css";

const ROLES: FamilyRole[] = ["DAD", "MOM", "CHILD"];

export default function FamilyMembers() {
  const { data: families = [], isLoading, isError } = useGetFamiliesQuery();

  const [createFamily, { isLoading: creating }] = useCreateFamilyMutation();
  const [updateFamily, { isLoading: renaming }] = useUpdateFamilyMutation();
  const [addMember, { isLoading: addingMember }] = useAddFamilyMemberMutation();

  const [showMemberForm, setShowMemberForm] = useState(false);
  const [showFamilyForm, setShowFamilyForm] = useState(false);
  const [renamingFamilyId, setRenamingFamilyId] = useState<number | null>(null);
  const [renameValue, setRenameValue] = useState("");

  const [memberForm, setMemberForm] = useState<{
    name: string;
    role: FamilyRole;
    familyId: number | "";
  }>({ name: "", role: "CHILD", familyId: "" });
  const [familyName, setFamilyName] = useState("");
  const [formError, setFormError] = useState<string | null>(null);

  const allMembersCount = families.reduce(
    (acc, f) => acc + f.familyMembers.length,
    0,
  );

  function startRename(id: number, currentName: string) {
    setRenamingFamilyId(id);
    setRenameValue(currentName);
  }

  function cancelRename() {
    setRenamingFamilyId(null);
    setRenameValue("");
  }

  async function submitRename(id: number) {
    if (!renameValue.trim()) return;
    setFormError(null);
    try {
      await updateFamily({ id, name: renameValue.trim() }).unwrap();
      setRenamingFamilyId(null);
      setRenameValue("");
    } catch {
      setFormError("Familie konnte nicht umbenannt werden.");
    }
  }

  async function submitFamily() {
    if (!familyName.trim()) return;
    setFormError(null);
    try {
      const created = await createFamily({ name: familyName.trim() }).unwrap();
      setMemberForm((f) => ({ ...f, familyId: created.id }));
      setShowFamilyForm(false);
      setFamilyName("");
    } catch {
      setFormError("Familie konnte nicht erstellt werden.");
    }
  }

  async function submitMember() {
    const familyId =
      memberForm.familyId !== ""
        ? Number(memberForm.familyId)
        : families[0]?.id;
    if (!memberForm.name.trim() || !familyId) return;
    setFormError(null);
    try {
      await addMember({
        familyId,
        name: memberForm.name.trim(),
        role: memberForm.role,
      }).unwrap();
      setShowMemberForm(false);
      setMemberForm((f) => ({ ...f, name: "" }));
    } catch {
      setFormError("Mitglied konnte nicht hinzugefügt werden.");
    }
  }

  function getMemberColor(
    familyId: number,
    memberId: number,
    role: FamilyRole,
  ) {
    const family = families.find((f) => f.id === familyId);
    if (!family) return roleColor(role);
    const children = family.familyMembers.filter((m) => m.role === "CHILD");
    const idx = children.findIndex((m) => m.id === memberId);
    return roleColor(role, idx >= 0 ? idx : 0);
  }

  return (
    <div className="page">
      <header className="page-header">
        <div>
          <h1 className="page-title">Familienmitglieder</h1>
          <p className="page-subtitle">
            {isLoading
              ? "Wird geladen…"
              : `${allMembersCount} Mitglied${allMembersCount !== 1 ? "er" : ""} in ${families.length} Familie${families.length !== 1 ? "n" : ""}`}
          </p>
        </div>
        <div className="fm-header-actions">
          <button
            className="btn-secondary"
            onClick={() => setShowFamilyForm((s) => !s)}
          >
            <Plus size={16} /> Neue Familie
          </button>
          <button
            className="btn-primary"
            onClick={() => {
              setMemberForm((f) => ({
                ...f,
                familyId: families[0]?.id ?? "",
              }));
              setShowMemberForm((s) => !s);
            }}
            disabled={families.length === 0}
          >
            <Plus size={16} /> Mitglied hinzufügen
          </button>
        </div>
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

      {showFamilyForm && (
        <div className="member-form-inline card">
          <input
            className="fm-input"
            placeholder="Familienname…"
            value={familyName}
            onChange={(e) => setFamilyName(e.target.value)}
            onKeyDown={(e) => e.key === "Enter" && submitFamily()}
            autoFocus
          />
          <div className="add-actions">
            <button
              className="add-cancel"
              onClick={() => setShowFamilyForm(false)}
            >
              Abbrechen
            </button>
            <button
              className="add-submit"
              onClick={submitFamily}
              disabled={creating}
            >
              {creating ? "Wird erstellt…" : "Erstellen"}
            </button>
          </div>
        </div>
      )}

      {showMemberForm && (
        <div className="member-form-inline card">
          <input
            className="fm-input"
            placeholder="Mitgliedsname…"
            value={memberForm.name}
            onChange={(e) =>
              setMemberForm((f) => ({ ...f, name: e.target.value }))
            }
            onKeyDown={(e) => e.key === "Enter" && submitMember()}
            autoFocus
          />
          <div className="fm-form-row">
            <select
              className="fm-input"
              value={memberForm.role}
              onChange={(e) =>
                setMemberForm((f) => ({
                  ...f,
                  role: e.target.value as FamilyRole,
                }))
              }
            >
              {ROLES.map((r) => (
                <option key={r} value={r}>
                  {roleLabel(r)}
                </option>
              ))}
            </select>
            {families.length > 1 && (
              <select
                className="fm-input"
                value={memberForm.familyId}
                onChange={(e) =>
                  setMemberForm((f) => ({
                    ...f,
                    familyId: Number(e.target.value),
                  }))
                }
              >
                {families.map((f) => (
                  <option key={f.id} value={f.id}>
                    {f.name}
                  </option>
                ))}
              </select>
            )}
          </div>
          <div className="add-actions">
            <button
              className="add-cancel"
              onClick={() => setShowMemberForm(false)}
            >
              Abbrechen
            </button>
            <button
              className="add-submit"
              onClick={submitMember}
              disabled={addingMember}
            >
              {addingMember ? "Wird hinzugefügt…" : "Hinzufügen"}
            </button>
          </div>
        </div>
      )}

      {isLoading ? (
        <div className="loading-state">
          <Loader2 size={28} className="spin" />
          <span>Mitglieder werden geladen…</span>
        </div>
      ) : isError ? (
        <div className="error-state">
          <AlertCircle size={18} />
          Familiendaten konnten nicht geladen werden. Bitte die Seite neu laden.
        </div>
      ) : families.length === 0 ? (
        <div className="empty-state">
          Noch keine Familien — oben eine erstellen!
        </div>
      ) : (
        families.map((family) => (
          <div key={family.id} className="family-section">
            <div className="family-section-header">
              {renamingFamilyId === family.id ? (
                <div className="family-rename-row">
                  <input
                    className="fm-input family-rename-input"
                    value={renameValue}
                    onChange={(e) => setRenameValue(e.target.value)}
                    onKeyDown={(e) => {
                      if (e.key === "Enter") submitRename(family.id);
                      if (e.key === "Escape") cancelRename();
                    }}
                    autoFocus
                  />
                  <button
                    className="rename-action rename-confirm"
                    onClick={() => submitRename(family.id)}
                    disabled={renaming}
                    title="Bestätigen"
                  >
                    <Check size={14} />
                  </button>
                  <button
                    className="rename-action rename-cancel"
                    onClick={cancelRename}
                    title="Abbrechen"
                  >
                    <X size={14} />
                  </button>
                </div>
              ) : (
                <>
                  <span className="family-section-title">{family.name}</span>
                  <button
                    className="family-rename-btn"
                    onClick={() => startRename(family.id, family.name)}
                    title="Familie umbenennen"
                  >
                    <Pencil size={13} />
                    Umbenennen
                  </button>
                </>
              )}
            </div>
            <div className="members-grid">
              {family.familyMembers.map((m) => {
                const color = getMemberColor(family.id, m.id, m.role);
                return (
                  <div key={m.id} className="member-card">
                    <div className={`member-avatar avatar-${color}`}>
                      {m.name.charAt(0).toUpperCase()}
                    </div>
                    <div className="member-name">{m.name}</div>
                    <div className={`member-role role-${color}`}>
                      {roleLabel(m.role)}
                    </div>
                    <div className="member-stats">
                      <div className="member-stat">
                        <User size={12} />
                        <span>Aktiv</span>
                      </div>
                    </div>
                  </div>
                );
              })}
              {family.familyMembers.length === 0 && (
                <p className="empty-family">
                  Noch keine Mitglieder — oben hinzufügen.
                </p>
              )}
            </div>
          </div>
        ))
      )}
    </div>
  );
}
