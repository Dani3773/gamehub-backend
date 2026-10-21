import { api, json } from './api.js';
import { saveToken, currentUser } from './auth.js';
const form = document.querySelector('#form'), error = document.querySelector('#error');
form.addEventListener('submit', async event => {
  event.preventDefault(); error.classList.add('hidden');
  try {
    const result = await api('/api/auth/login', { method: 'POST', body: json({ email: form.email.value, senha: form.senha.value }) });
    saveToken(result.token);
    const user = await currentUser(api);
    location.href = user.perfil === 'ADMIN' ? '/usuarios.html' : '/perfil.html';
  } catch (e) { error.textContent = e.message === 'E-mail ou senha inválidos.' ? e.message : e.message; error.classList.remove('hidden'); }
});
