import { api, json } from './api.js';
import { guard, initials, logout } from './auth.js';
import { montarNavbar } from './navbar.js';
const $ = s => document.querySelector(s), form = $('#form'), error = $('#error');
let user;
function fill() {
  $('#avatar').textContent = initials(user.nome); $('#profileName').textContent = user.nome; $('#profileEmail').textContent = user.email;
  $('#profileRole').textContent = user.perfil; $('#profileRole').classList.toggle('admin', user.perfil === 'ADMIN');
  $('#memberSince').textContent = user.dataCadastro ? new Date(user.dataCadastro).toLocaleDateString('pt-BR') : '—';
  $('#status').textContent = user.ativo ? 'Ativo' : 'Inativo';
  montarNavbar(user);
  form.nome.value = user.nome; form.email.value = user.email; form.senha.value = '';
}
user = await guard(api); if (user) fill();
form.addEventListener('submit', async e => { e.preventDefault(); error.classList.add('hidden'); try {
  user = await api(`/api/usuarios/${user.id}`, { method: 'PUT', body: json({ nome: form.nome.value.trim(), email: form.email.value.trim(), senha: form.senha.value }) }); fill();
} catch (err) { error.textContent = err.message; error.classList.remove('hidden'); } });
$('#cancel').onclick = fill;
$('#deactivate').onclick = () => $('#confirm').classList.remove('hidden');
$('#dismiss').onclick = () => $('#confirm').classList.add('hidden');
$('#confirmAction').onclick = async () => { try { await api(`/api/usuarios/${user.id}`, { method: 'DELETE' }); logout(); } catch (err) { $('#confirmTitle').textContent = err.message; } };
