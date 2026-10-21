const TOKEN_KEY = 'gamehub.token';

export const token = () => localStorage.getItem(TOKEN_KEY);
export const saveToken = value => localStorage.setItem(TOKEN_KEY, value);
export function logout() { localStorage.removeItem(TOKEN_KEY); location.href = '/index.html'; }
export function decodeToken() {
  try { return JSON.parse(atob(token().split('.')[1].replace(/-/g, '+').replace(/_/g, '/'))); }
  catch { return {}; }
}
export function initials(name = '') { return name.trim().split(/\s+/).slice(0, 2).map(part => part[0] || '').join('').toUpperCase(); }
export async function currentUser(api) { return api('/api/usuarios/me'); }
export async function guard(api, admin = false) {
  if (!token()) { location.href = '/index.html'; return null; }
  try {
    const user = await currentUser(api);
    if (admin && user.perfil !== 'ADMIN') { location.href = '/perfil.html'; return null; }
    return user;
  } catch { return null; }
}
