// Tela 5 - Minha Biblioteca / Compras (Lucas).
// GET /compras retorna só as compras do usuário do token (RN08); GET /jogos completa nome, arte e categorias.
import { api } from './api.js';
import { guard } from './auth.js';
import { montarNavbar } from './navbar.js';
import { $, escapeHtml, formatarPreco, categoriaNomes, arteDoJogo, mostrarMensagem, ajustarNavbarJogos } from './jogos-ui.js';
import { ajustarNavbarCompras, formatarDataHora } from './compra.js';

let itens = [];

function ordenar(lista) {
  const copia = [...lista];
  switch ($('#libSort').value) {
    case 'old': return copia.sort((a, b) => String(a.compra.dataCompra).localeCompare(String(b.compra.dataCompra)));
    case 'name': return copia.sort((a, b) => String(a.jogo.nome).localeCompare(String(b.jogo.nome), 'pt-BR'));
    case 'price': return copia.sort((a, b) => Number(b.compra.valorPago) - Number(a.compra.valorPago));
    default: return copia.sort((a, b) => String(b.compra.dataCompra).localeCompare(String(a.compra.dataCompra)));
  }
}

function montarCard({ compra, jogo }) {
  const generos = categoriaNomes(jogo).slice(0, 2);
  const etiquetas = (generos.length ? generos : ['Digital']).map(nome => `<span class="gh-game-category">${escapeHtml(nome)}</span>`).join('');
  const detalhes = `/detalhes-jogo.html?id=${encodeURIComponent(jogo.id)}`;
  return `<article class="gh-game-card gh-lib-card">
    <span class="gh-owned-badge">✓ ADQUIRIDO</span>
    <a class="gh-game-card__art-link" href="${detalhes}" aria-label="Ver detalhes de ${escapeHtml(jogo.nome)}">${arteDoJogo(jogo, 'card')}</a>
    <div class="gh-game-card__body">
      <div class="gh-game-card__tags">${etiquetas}</div>
      <h3>${escapeHtml(jogo.nome)}</h3>
      <div class="gh-lib-meta">
        <div><span>VALOR PAGO</span><strong>${formatarPreco(compra.valorPago)}</strong></div>
        <div><span>COMPRADO EM</span><strong>${formatarDataHora(compra.dataCompra)}</strong></div>
      </div>
      <div class="gh-game-card__foot">
        <small class="muted">COMPRA #${escapeHtml(compra.id)}</small>
        <div class="gh-lib-actions">
          <a class="gh-card-cta" href="/avaliacoes.html?jogoId=${encodeURIComponent(jogo.id)}">AVALIAR <span aria-hidden="true">★</span></a>
          <a class="gh-card-cta" href="${detalhes}">DETALHES <span aria-hidden="true">↗</span></a>
        </div>
      </div>
      ${jogo.ativo === false ? '<span class="gh-lib-inactive">● FORA DO CATÁLOGO — SUA LICENÇA CONTINUA VÁLIDA</span>' : ''}
    </div>
  </article>`;
}

function atualizar() {
  const texto = $('#libSearch').value.trim().toLocaleLowerCase('pt-BR');
  const lista = ordenar(itens.filter(i => String(i.jogo.nome || '').toLocaleLowerCase('pt-BR').includes(texto)));
  $('#libGrid').innerHTML = lista.map(montarCard).join('');
  $('#libLabel').textContent = `${lista.length} ${lista.length === 1 ? 'jogo na biblioteca' : 'jogos na biblioteca'}`;
  const vazio = lista.length === 0;
  $('#libEmpty').classList.toggle('hidden', !vazio);
  if (vazio && itens.length) {
    $('#libEmptyTitle').textContent = 'NENHUM JOGO ENCONTRADO';
    $('#libEmptyText').textContent = 'Nenhum jogo da sua biblioteca corresponde à pesquisa.';
    $('#libEmptyAction').classList.add('hidden');
  }
}

function atualizarResumo() {
  const total = itens.reduce((soma, i) => soma + (Number(i.compra.valorPago) || 0), 0);
  $('#statGames').textContent = itens.length;
  $('#statSpent').textContent = formatarPreco(total);
  const ultima = [...itens].sort((a, b) => String(b.compra.dataCompra).localeCompare(String(a.compra.dataCompra)))[0];
  if (ultima) {
    $('#statLast').textContent = formatarDataHora(ultima.compra.dataCompra);
    $('#statLastGame').textContent = ultima.jogo.nome;
  }
}

async function iniciar() {
  const usuario = await guard(api);
  if (!usuario) return;
  montarNavbar(usuario);
  ajustarNavbarJogos(usuario.perfil);
  ajustarNavbarCompras(usuario.perfil);
  $('#libSearch').addEventListener('input', atualizar);
  $('#libSort').addEventListener('change', atualizar);
  try {
    const [compras, jogos] = await Promise.all([api('/compras'), api('/jogos')]);
    if (!Array.isArray(compras)) throw new Error('A API não retornou a lista de compras.');
    const porId = new Map((Array.isArray(jogos) ? jogos : []).map(j => [String(j.id), j]));
    itens = compras.map(compra => ({
      compra,
      jogo: porId.get(String(compra.jogoId)) ?? { id: compra.jogoId, nome: `Jogo #${compra.jogoId}`, categorias: [] }
    }));
    atualizarResumo();
    atualizar();
  } catch (erro) {
    $('#libLabel').textContent = 'Não foi possível carregar a biblioteca.';
    mostrarMensagem($('#libError'), erro.message);
  }
}

await iniciar();
