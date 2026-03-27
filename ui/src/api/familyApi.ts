const BASE = "/api";

export type FamilyRole = "DAD" | "MOM" | "CHILD";

export interface FamilyMember {
  id: number;
  name: string;
  role: FamilyRole;
}

export interface Family {
  id: number;
  name: string;
  familyMembers: FamilyMember[];
}

export async function getFamilies(): Promise<Family[]> {
  const res = await fetch(`${BASE}/families`);
  if (!res.ok) throw new Error(`Failed to fetch families: ${res.status}`);
  return res.json();
}

export async function createFamily(name: string): Promise<Family> {
  const res = await fetch(`${BASE}/families`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ name }),
  });
  if (!res.ok) throw new Error(`Failed to create family: ${res.status}`);
  return res.json();
}

export async function addFamilyMember(
  familyId: number,
  name: string,
  role: FamilyRole,
): Promise<Family> {
  const res = await fetch(`${BASE}/families/members`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ familyId, name, role }),
  });
  if (!res.ok) throw new Error(`Failed to add member: ${res.status}`);
  return res.json();
}

/** Map a FamilyRole to a CSS colour token used throughout the UI */
export function roleColor(role: FamilyRole, childIndex = 0): string {
  if (role === "DAD") return "blue";
  if (role === "MOM") return "pink";
  const childColors = ["green", "orange", "accent"];
  return childColors[childIndex % childColors.length];
}

export function roleLabel(role: FamilyRole): string {
  return role.charAt(0) + role.slice(1).toLowerCase();
}
