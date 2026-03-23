import { useState, useEffect } from 'react'
import { Plus, User, Loader2, AlertCircle } from 'lucide-react'
import {
  getFamilies,
  createFamily,
  addFamilyMember,
  roleColor,
  roleLabel,
  type Family,
  type FamilyRole,
} from '../api/familyApi'
import './FamilyMembers.css'

const ROLES: FamilyRole[] = ['DAD', 'MOM', 'CHILD']

export default function FamilyMembers() {
  const [families, setFamilies] = useState<Family[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [showMemberForm, setShowMemberForm] = useState(false)
  const [showFamilyForm, setShowFamilyForm] = useState(false)
  const [memberForm, setMemberForm] = useState<{ name: string; role: FamilyRole; familyId: number | '' }>({
    name: '',
    role: 'CHILD',
    familyId: '',
  })
  const [familyName, setFamilyName] = useState('')
  const [saving, setSaving] = useState(false)

  useEffect(() => {
    load()
  }, [])

  async function load() {
    try {
      setLoading(true)
      setError(null)
      const data = await getFamilies()
      setFamilies(data)
      if (data.length > 0 && memberForm.familyId === '') {
        setMemberForm(f => ({ ...f, familyId: data[0].id }))
      }
    } catch (e) {
      setError(e instanceof Error ? e.message : 'Unknown error')
    } finally {
      setLoading(false)
    }
  }

  async function submitMember() {
    if (!memberForm.name.trim() || !memberForm.familyId) return
    try {
      setSaving(true)
      const updated = await addFamilyMember(Number(memberForm.familyId), memberForm.name.trim(), memberForm.role)
      setFamilies(prev => prev.map(f => (f.id === updated.id ? updated : f)))
      setShowMemberForm(false)
      setMemberForm(f => ({ ...f, name: '' }))
    } catch (e) {
      setError(e instanceof Error ? e.message : 'Failed to add member')
    } finally {
      setSaving(false)
    }
  }

  async function submitFamily() {
    if (!familyName.trim()) return
    try {
      setSaving(true)
      const created = await createFamily(familyName.trim())
      setFamilies(prev => [...prev, created])
      setMemberForm(f => ({ ...f, familyId: created.id }))
      setShowFamilyForm(false)
      setFamilyName('')
    } catch (e) {
      setError(e instanceof Error ? e.message : 'Failed to create family')
    } finally {
      setSaving(false)
    }
  }

  const allMembers = families.flatMap(f =>
    f.familyMembers.map(m => ({ ...m, familyName: f.name }))
  )

  // Assign child-index per family for color cycling
  function getMemberColor(familyId: number, memberId: number, role: FamilyRole) {
    const family = families.find(f => f.id === familyId)
    if (!family) return roleColor(role)
    const children = family.familyMembers.filter(m => m.role === 'CHILD')
    const idx = children.findIndex(m => m.id === memberId)
    return roleColor(role, idx >= 0 ? idx : 0)
  }

  return (
    <div className="page">
      <header className="page-header">
        <div>
          <h1 className="page-title">Family Members</h1>
          <p className="page-subtitle">
            {loading ? 'Loading…' : `${allMembers.length} member${allMembers.length !== 1 ? 's' : ''} across ${families.length} family`}
          </p>
        </div>
        <div style={{ display: 'flex', gap: '10px' }}>
          <button className="btn-secondary" onClick={() => setShowFamilyForm(s => !s)}>
            <Plus size={16} /> New Family
          </button>
          <button
            className="btn-primary"
            onClick={() => setShowMemberForm(s => !s)}
            disabled={families.length === 0}
          >
            <Plus size={16} /> Add Member
          </button>
        </div>
      </header>

      {error && (
        <div className="error-banner">
          <AlertCircle size={16} />
          {error}
          <button className="error-retry" onClick={load}>Retry</button>
        </div>
      )}

      {showFamilyForm && (
        <div className="member-form-inline card">
          <input
            className="fm-input"
            placeholder="Family name…"
            value={familyName}
            onChange={e => setFamilyName(e.target.value)}
            onKeyDown={e => e.key === 'Enter' && submitFamily()}
            autoFocus
          />
          <div className="add-actions">
            <button className="add-cancel" onClick={() => setShowFamilyForm(false)}>Cancel</button>
            <button className="add-submit" onClick={submitFamily} disabled={saving}>
              {saving ? 'Creating…' : 'Create'}
            </button>
          </div>
        </div>
      )}

      {showMemberForm && (
        <div className="member-form-inline card">
          <input
            className="fm-input"
            placeholder="Member name…"
            value={memberForm.name}
            onChange={e => setMemberForm(f => ({ ...f, name: e.target.value }))}
            onKeyDown={e => e.key === 'Enter' && submitMember()}
            autoFocus
          />
          <div className="fm-form-row">
            <select
              className="fm-input"
              value={memberForm.role}
              onChange={e => setMemberForm(f => ({ ...f, role: e.target.value as FamilyRole }))}
            >
              {ROLES.map(r => <option key={r} value={r}>{roleLabel(r)}</option>)}
            </select>
            {families.length > 1 && (
              <select
                className="fm-input"
                value={memberForm.familyId}
                onChange={e => setMemberForm(f => ({ ...f, familyId: Number(e.target.value) }))}
              >
                {families.map(f => <option key={f.id} value={f.id}>{f.name}</option>)}
              </select>
            )}
          </div>
          <div className="add-actions">
            <button className="add-cancel" onClick={() => setShowMemberForm(false)}>Cancel</button>
            <button className="add-submit" onClick={submitMember} disabled={saving}>
              {saving ? 'Adding…' : 'Add'}
            </button>
          </div>
        </div>
      )}

      {loading ? (
        <div className="loading-state">
          <Loader2 size={28} className="spin" />
          <span>Loading members…</span>
        </div>
      ) : (
        families.map(family => (
          <div key={family.id} className="family-section">
            <div className="family-section-title">{family.name}</div>
            <div className="members-grid">
              {family.familyMembers.map(m => {
                const color = getMemberColor(family.id, m.id, m.role)
                return (
                  <div key={m.id} className="member-card">
                    <div className={`member-avatar avatar-${color}`}>
                      {m.name.charAt(0).toUpperCase()}
                    </div>
                    <div className="member-name">{m.name}</div>
                    <div className={`member-role role-${color}`}>{roleLabel(m.role)}</div>
                    <div className="member-stats">
                      <div className="member-stat">
                        <User size={12} />
                        <span>Active</span>
                      </div>
                    </div>
                  </div>
                )
              })}
              {family.familyMembers.length === 0 && (
                <p className="empty-family">No members yet — add one above.</p>
              )}
            </div>
          </div>
        ))
      )}

      {!loading && families.length === 0 && !error && (
        <div className="empty-state">
          <p>No families found. Create your first family above!</p>
        </div>
      )}
    </div>
  )
}
