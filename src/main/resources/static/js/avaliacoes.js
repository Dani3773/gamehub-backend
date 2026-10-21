import { api, json } from './api.js';
import { guard, initials, logout } from './auth.js';
const $ = s => document.querySelector(s), jogoId = new URLSearchParams(location.search).get('jogoId');
$('#logout').onclick=logout;
if(!jogoId){$('#title').textContent='Jogo não informado';throw new Error('jogoId ausente');}
const me=await guard(api); if(!me) throw new Error('Autenticação necessária.');
$('#userChip').innerHTML=`<span class="initials">${initials(me.nome)}</span>${me.nome}`;
let reviews=[], rating=0, editing=null, owned=false;
async function boot(){try{const game=await api(`/jogos/${encodeURIComponent(jogoId)}`);$('#title').textContent=`Avaliações: ${game.nome}`;$('#crumb').textContent=game.nome;}catch{$('#title').textContent=`Avaliações: Jogo #${jogoId}`;$('#crumb').textContent=`Jogo #${jogoId}`;}
  reviews=await api(`/avaliacoes/jogo/${encodeURIComponent(jogoId)}`); render(); panel();
}
function render(){const average=reviews.length?reviews.reduce((n,r)=>n+r.nota,0)/reviews.length:0;$('#summary').textContent=reviews.length?`${average.toFixed(1)} de 5 · ${reviews.length} avaliações`:'Sem avaliações';$('#average').textContent='★'.repeat(Math.round(average))+'☆'.repeat(5-Math.round(average));
  $('#reviews').innerHTML=reviews.map(r=>`<article class="card review ${r.usuarioId===me.id?'mine':''}">${r.usuarioId===me.id?'<span class="badge">SUA AVALIAÇÃO</span>':''}<div class="row"><span class="stars">${'★'.repeat(r.nota)}${'☆'.repeat(5-r.nota)}</span><span class="muted">${r.dataAvaliacao?new Date(r.dataAvaliacao).toLocaleDateString('pt-BR'):''}</span></div><p>${escapeHtml(r.comentario||'')}</p>${r.usuarioId===me.id?`<button class="button edit" data-id="${r.id}">Editar</button> <button class="button danger delete" data-id="${r.id}">Excluir</button>`:''}</article>`).join('')||'<p class="muted">Ainda não há avaliações para este jogo.</p>';
  document.querySelectorAll('.edit').forEach(b=>b.onclick=()=>openEdit(reviews.find(r=>r.id===Number(b.dataset.id))));document.querySelectorAll('.delete').forEach(b=>b.onclick=async()=>{try{await api(`/avaliacoes/${b.dataset.id}`,{method:'DELETE'});await boot();}catch(e){alertBox(e.message);}});
}
function escapeHtml(v){return v.replace(/[&<>"']/g,c=>({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[c]));}
function alertBox(text){const p=$('#panel');p.innerHTML=`<h2 role="alert">${escapeHtml(text)}</h2>`;}
async function panel(){const mine=reviews.find(r=>r.usuarioId===me.id); if(mine){$('#panel').innerHTML='<h2>Você já avaliou este jogo</h2>';return;} try{const purchases=await api('/compras');owned=Array.isArray(purchases)&&purchases.some(c=>c.jogoId===Number(jogoId));}catch{owned=false;}
  if(!owned){$('#panel').innerHTML='<h2>Compre este jogo para avaliar</h2><p class="muted">É necessário ter comprado o jogo para publicar uma avaliação.</p><a class="button primary" href="/jogos">Comprar jogo</a>';return;}
  $('#panel').innerHTML='<h2>Nova avaliação</h2><div id="stars" class="star-picker" role="group" aria-label="Nota de 1 a 5 estrelas"></div><div class="field"><label for="newComment">Comentário</label><textarea id="newComment" maxlength="1000"></textarea></div><div id="newError" class="error hidden" role="alert"></div><button id="publish" class="button primary">Publicar avaliação</button>';
  starButtons($('#stars'),v=>rating=v);$('#publish').onclick=async()=>{try{if(!rating)throw new Error('Selecione uma nota de 1 a 5.');await api('/avaliacoes',{method:'POST',body:json({jogoId:Number(jogoId),nota:rating,comentario:$('#newComment').value})});await boot();}catch(e){if(e.message==='Sem permissão'){$('#panel').innerHTML='<h2>Compre este jogo para avaliar</h2><p>É necessário ter comprado o jogo para publicar uma avaliação.</p><a class="button primary" href="/jogos">Comprar jogo</a>';return;}const err=$('#newError');err.textContent=e.message;err.classList.remove('hidden');}};
}
function starButtons(container,onChange,initial=0){container.innerHTML=Array.from({length:5},(_,i)=>`<button type="button" aria-label="${i+1} estrelas" class="${i<initial?'selected':''}">★</button>`).join('');container.querySelectorAll('button').forEach((b,i)=>b.onclick=()=>{onChange(i+1);container.querySelectorAll('button').forEach((s,n)=>s.classList.toggle('selected',n<=i));});}
function openEdit(r){editing=r;rating=r.nota;$('#comment').value=r.comentario||'';$('#editError').classList.add('hidden');starButtons($('#editStars'),v=>rating=v,rating);$('#modal').classList.remove('hidden');}
$('#cancelEdit').onclick=()=>$('#modal').classList.add('hidden');$('#editForm').onsubmit=async e=>{e.preventDefault();try{await api(`/avaliacoes/${editing.id}`,{method:'PUT',body:json({jogoId:Number(jogoId),nota:rating,comentario:$('#comment').value})});$('#modal').classList.add('hidden');await boot();}catch(err){$('#editError').textContent=err.message;$('#editError').classList.remove('hidden');}};
try{await boot();}catch(e){alertBox(e.message);}
