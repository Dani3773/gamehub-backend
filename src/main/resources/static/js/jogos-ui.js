// Funções visuais compartilhadas somente pelas telas de jogos do Gustavo.
// O layout principal e as cores continuam sendo fornecidos pelo style.css da equipe.

export const $ = selector => document.querySelector(selector);

export function escapeHtml(value) {
  return String(value ?? '').replace(/[&<>"']/g, char => ({
    '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;'
  }[char]));
}

const moeda = new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' });
export const formatarPreco = valor => moeda.format(Number(valor) || 0);

export function formatarData(data) {
  if (!data || !/^\d{4}-\d{2}-\d{2}$/.test(String(data))) return 'Não informada';
  const [ano, mes, dia] = String(data).split('-');
  return `${dia}/${mes}/${ano}`;
}

export function categoriaNomes(jogo) {
  return Array.isArray(jogo.categorias) ? jogo.categorias.map(c => c.nome).filter(Boolean) : [];
}

function corDaArte(jogo) {
  const cores = ['red', 'violet', 'blue', 'orange', 'green'];
  const id = Number(jogo.id);
  const index = Number.isFinite(id) ? Math.abs(Math.floor(id)) % cores.length : 0;
  return cores[index];
}

export function arteDoJogo(jogo, tamanho = '') {
  const titulo = escapeHtml(jogo.nome || 'Jogo sem nome');
  const genero = escapeHtml(categoriaNomes(jogo)[0] || 'Jogo digital');
  const marca = String(jogo.nome || 'G').trim().charAt(0).toUpperCase();
  const classe = tamanho ? ` gh-art--${tamanho}` : '';
  return `<div class="gh-art gh-art--${corDaArte(jogo)}${classe}">
    <span class="gh-art__mesh" aria-hidden="true"></span>
    <span class="gh-art__circle" aria-hidden="true"></span>
    <span class="gh-art__letter" aria-hidden="true">${escapeHtml(marca)}</span>
    <div class="gh-art__details"><small>GAMEHUB / JOGO DIGITAL</small><strong>${titulo}</strong><em>${genero}</em></div>
  </div>`;
}

export function mostrarMensagem(elemento, mensagem) {
  elemento.textContent = mensagem;
  elemento.classList.remove('hidden');
}

export function esconderMensagem(elemento) {
  elemento.textContent = '';
  elemento.classList.add('hidden');
}

// A navbar pertence a outro colega e ainda aponta /jogos (endpoint JSON).
// Corrigimos seus links apenas nestas três telas, sem editar o arquivo compartilhado.
export function ajustarNavbarJogos(perfil) {
  document.querySelectorAll('#navbar a[href="/jogos"]').forEach(link => {
    link.href = '/jogos.html';
  });
  document.querySelectorAll('#navbar a[href="/jogos.html"]').forEach(link => {
    if (location.pathname === '/jogos.html') {
      link.classList.add('active');
      link.setAttribute('aria-current', 'page');
    }
  });
  if (perfil === 'ADMIN') {
    const navLinks = document.querySelector('#navbar .nav-links');
    if (navLinks && !navLinks.querySelector('[data-manage-games]')) {
      const link = document.createElement('a');
      link.href = '/gerenciar-jogos.html';
      link.textContent = 'Gerenciar jogos';
      link.dataset.manageGames = 'true';
      if (location.pathname === '/gerenciar-jogos.html') {
        link.classList.add('active');
        link.setAttribute('aria-current', 'page');
      }
      navLinks.appendChild(link);
    }
  }
}
