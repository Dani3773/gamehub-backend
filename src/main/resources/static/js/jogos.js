import { api } from './api.js';
import { guard } from './auth.js';
import { montarNavbar } from './navbar.js';
import { montarFiltroCategorias, filtrarPorCategoria } from './filtros-categoria.js';
import { $, escapeHtml, formatarPreco, categoriaNomes, arteDoJogo, mostrarMensagem, esconderMensagem, ajustarNavbarJogos } from './jogos-ui.js';

let jogos = [];
let categoriaSelecionada = '';
const busca = $('#gameSearch');
const ordenacao = $('#sortGames');

function ordenarJogos(lista) {
  const copia = [...lista];
  switch (ordenacao.value) {
    case 'newest': return copia.sort((a,b) => (b.dataLancamento || '').localeCompare(a.dataLancamento || ''));
    case 'low': return copia.sort((a,b) => Number(a.preco) - Number(b.preco));
    case 'high': return copia.sort((a,b) => Number(b.preco) - Number(a.preco));
    default: return copia.sort((a,b) => String(a.nome || '').localeCompare(String(b.nome || ''), 'pt-BR'));
  }
}

function montarCard(jogo) {
  const generos = categoriaNomes(jogo).slice(0, 2);
  const etiquetas = generos.length ? generos.map(nome => `<span class="gh-game-category">${escapeHtml(nome)}</span>`).join('') : '<span class="gh-game-category">Digital</span>';
  return `<article class="gh-game-card">
    <a class="gh-game-card__art-link" href="/detalhes-jogo.html?id=${encodeURIComponent(jogo.id)}" aria-label="Ver detalhes de ${escapeHtml(jogo.nome)}">${arteDoJogo(jogo, 'card')}</a>
    <div class="gh-game-card__body"><div class="gh-game-card__tags">${etiquetas}</div>
    <h3>${escapeHtml(jogo.nome)}</h3><div class="gh-game-card__foot"><strong>${formatarPreco(jogo.preco)}</strong><a class="gh-card-cta" href="/detalhes-jogo.html?id=${encodeURIComponent(jogo.id)}" aria-label="Ver ${escapeHtml(jogo.nome)}">VER DETALHES <span aria-hidden="true">↗</span></a></div></div>
  </article>`;
}

function atualizarCatalogo() {
  const texto = busca.value.trim().toLocaleLowerCase('pt-BR');
  let lista = filtrarPorCategoria(jogos, categoriaSelecionada);
  if (texto) lista = lista.filter(j => String(j.nome || '').toLocaleLowerCase('pt-BR').includes(texto));
  lista = ordenarJogos(lista);
  $('#gamesGrid').innerHTML = lista.map(montarCard).join('');
  $('#resultsLabel').textContent = `${lista.length} ${lista.length === 1 ? 'jogo encontrado' : 'jogos encontrados'}`;
  $('#emptyCatalog').classList.toggle('hidden', lista.length > 0);
}

function montarDestaque() {
  const destaque = jogos[0];
  if (!destaque) return;
  $('#featuredPoster').innerHTML = `<a href="/detalhes-jogo.html?id=${encodeURIComponent(destaque.id)}" aria-label="Conhecer ${escapeHtml(destaque.nome)}">${arteDoJogo(destaque, 'featured')}</a><span class="gh-featured-badge">EM DESTAQUE</span>`;
}

async function iniciar() {
  const usuario = await guard(api);
  if (!usuario) return;
  montarNavbar(usuario);
  ajustarNavbarJogos(usuario.perfil);
  if (usuario.perfil === 'ADMIN') $('#adminGamesLink').classList.remove('hidden');
  busca.addEventListener('input', atualizarCatalogo);
  ordenacao.addEventListener('change', atualizarCatalogo);
  $('#clearFilters').addEventListener('click', () => { busca.value = ''; busca.focus(); atualizarCatalogo(); });
  try {
    const resposta = await api('/jogos');
    if (!Array.isArray(resposta)) throw new Error('A API não retornou uma lista de jogos.');
    jogos = resposta.filter(jogo => jogo.ativo !== false);
    montarDestaque();
    atualizarCatalogo();
    await montarFiltroCategorias($('#categoryFilters'), id => { categoriaSelecionada = id; atualizarCatalogo(); });
  } catch (erro) {
    $('#resultsLabel').textContent = 'Não foi possível carregar o catálogo.';
    mostrarMensagem($('#catalogError'), erro.message);
  }
}

await iniciar();
