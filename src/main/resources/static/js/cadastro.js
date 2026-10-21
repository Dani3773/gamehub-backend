import { api, json } from './api.js';
const form = document.querySelector('#form'), error = document.querySelector('#error');
form.addEventListener('submit', async event => {
  event.preventDefault(); error.classList.add('hidden');
  const email = form.email.value.trim(), senha = form.senha.value;
  if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) { error.textContent = 'E-mail em formato inválido'; error.classList.remove('hidden'); return; }
  if (senha.length < 6 || senha.length > 100) { error.textContent = 'A senha deve ter entre 6 e 100 caracteres'; error.classList.remove('hidden'); return; }
  try { await api('/api/usuarios', { method: 'POST', body: json({ nome: form.nome.value.trim(), email, senha }) }); location.href = '/index.html'; }
  catch (e) { error.textContent = e.message; error.classList.remove('hidden'); }
});
