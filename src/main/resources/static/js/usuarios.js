import { api, json } from './api.js';
import { guard, initials, logout } from './auth.js';
const $ = s => document.querySelector(s), error = $('#error'), modal = $('#modal'), form = $('#userForm');
$('#logout').onclick = logout;
let me, users = [], filter = 'all', editing = null;
me = await guard(api, true); if (!me) throw new Error('Acesso restrito.');
$('#userChip').innerHTML = `<span class="initials">${initials(me.nome)}</span>${me.nome}`;
function render() {
  const query = $('#search').value.toLocaleLowerCase('pt-BR');
  const shown = users.filter(u => (filter === 'all' || (filter === 'inactive' ? !u.ativo : u.perfil === filter)) && `${u.nome} ${u.email}`.toLocaleLowerCase('pt-BR').includes(query));
  $('#rows').innerHTML = shown.map(u => `<tr><td>${u.id}</td><td>${escapeHtml(u.nome)}</td><td>${escapeHtml(u.email)}</td><td><span class="badge ${u.perfil === 'ADMIN' ? 'admin' : ''}">${u.perfil}</span></td><td><span class="status ${u.ativo ? '' : 'inactive'}">${u.ativo ? 'Ativo' : 'Inativo'}</span></td><td>${u.dataCadastro ? new Date(u.dataCadastro).toLocaleDateString('pt-BR') : '—'}</td><td class="actions"><button class="button edit" data-id="${u.id}">Editar</button>${u.perfil !== 'ADMIN' && u.ativo ? `<button class="button danger deactivate" data-id="${u.id}">Desativar</button>` : ''}</td></tr>`).join('');
  $('#count').textContent = `${shown.length} usuários cadastrados`;
  document.querySelectorAll('.edit').forEach(b => b.onclick = () => openEdit(users.find(u => u.id === Number(b.dataset.id))));
  document.querySelectorAll('.deactivate').forEach(b => b.onclick = async () => { if (!window.confirm('Desativar este usuário?')) return; try { await api(`/api/usuarios/${b.dataset.id}`, { method: 'DELETE' }); await load(); } catch (e) { showError(error,e.message); } });
}
function escapeHtml(value) { return value.replace(/[&<>"']/g, c => ({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[c])); }
function showError(el, text) { el.textContent = text; el.classList.remove('hidden'); }
async function load() { users = await api('/api/usuarios'); render(); }
function openEdit(u) { editing = u; $('#modalTitle').textContent = 'Editar usuário'; form.nome.value=u.nome; form.email.value=u.email; form.senha.value=''; $('#senha').required=true; $('#senha').placeholder=''; $('#passwordHint').textContent='A API exige uma senha em toda atualização.'; $('#perfil').value=u.perfil; $('#modalError').classList.add('hidden'); modal.classList.remove('hidden'); }
function openNew() { editing=null; form.reset(); $('#modalTitle').textContent='Novo usuário'; $('#senha').required=true; $('#senha').placeholder=''; $('#passwordHint').textContent=''; $('#perfil').value='CLIENTE'; $('#modalError').classList.add('hidden'); modal.classList.remove('hidden'); }
$('#newUser').onclick=openNew; $('#cancel').onclick=()=>modal.classList.add('hidden'); $('#search').oninput=render;
$('#chips').addEventListener('click', e=>{if(!e.target.dataset.filter)return;filter=e.target.dataset.filter;document.querySelectorAll('.chip').forEach(c=>c.classList.toggle('active',c===e.target));render();});
form.addEventListener('submit',async e=>{e.preventDefault(); const payload={nome:form.nome.value.trim(),email:form.email.value.trim(),senha:form.senha.value,perfil:$('#perfil').value};try{if(editing)await api(`/api/usuarios/${editing.id}/admin`,{method:'PUT',body:json(payload)});else await api('/api/usuarios/admin',{method:'POST',body:json(payload)});modal.classList.add('hidden');await load();}catch(err){showError($('#modalError'),err.message);}});
try{await load();}catch(e){showError(error,e.message);}
