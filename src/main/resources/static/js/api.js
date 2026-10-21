import { token, logout } from './auth.js';

export async function api(path, options = {}) {
  const headers = new Headers(options.headers || {});
  if (token()) headers.set('Authorization', `Bearer ${token()}`);
  if (options.body && !(options.body instanceof FormData)) headers.set('Content-Type', 'application/json');
  const response = await fetch(path, { ...options, headers });
  if (response.status === 401) {
    if (token()) { logout(); throw new Error('Sessão expirada.'); }
    throw new Error('E-mail ou senha inválidos.');
  }
  if (response.status === 403) throw new Error('Sem permissão');
  if (!response.ok) {
    const body = await response.text();
    let message = body;
    try { const parsed = JSON.parse(body); message = parsed.message || parsed.detail || body; } catch {}
    throw new Error(message || `Erro ${response.status}`);
  }
  if (response.status === 204) return null;
  const text = await response.text();
  if (!text) return null;
  try { return JSON.parse(text); } catch { return text; }
}
export const json = data => JSON.stringify(data);
