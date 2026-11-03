import { api } from './api.js';
import { guard } from './auth.js';
import { montarNavbar } from './navbar.js';
import { $, escapeHtml, formatarPreco, formatarData, categoriaNomes, arteDoJogo, mostrarMensagem, ajustarNavbarJogos } from './jogos-ui.js';

const id = new URLSearchParams(location.search).get('id');

function mostrarJogo(jogo) {
  const generos = categoriaNomes(jogo);
  $('#breadcrumbGame').textContent = jogo.nome;
  $('#gameTitle').textContent = jogo.nome;
  document.title = `${jogo.nome} | GameHub`;
  $('#gameTags').innerHTML = (generos.length ? generos : ['Jogo digital']).map(nome => `<span class="gh-tag">${escapeHtml(nome)}</span>`).join('');
  $('#detailPoster').innerHTML = arteDoJogo(jogo, 'detail');
  $('#gamePrice').textContent = formatarPreco(jogo.preco);
  $('#gameDate').textContent = formatarData(jogo.dataLancamento);
  $('#gameGenres').textContent = generos.join(', ') || 'Não informadas';
  $('#gameDescription').textContent = jogo.descricao || 'Este jogo ainda não possui uma descrição cadastrada.';
  $('#availability').textContent = jogo.ativo === false ? '● INATIVO' : '● DISPONÍVEL NO CATÁLOGO';
  $('#availability').classList.toggle('gh-unavailable', jogo.ativo === false);
  $('#buyGame').disabled = jogo.ativo === false;
  $('#buyGame').textContent = jogo.ativo === false ? 'JOGO INDISPONÍVEL' : 'COMPRAR JOGO ↗';
  const reviewsUrl = `/avaliacoes.html?jogoId=${encodeURIComponent(jogo.id)}`;
  $('#reviewsLink').href = reviewsUrl;
  $('#reviewMore').href = reviewsUrl;
  $('#detailLoading').classList.add('hidden');
  $('#detailContent').classList.remove('hidden');
}

async function mostrarAvaliacoes(jogoId) {
  const container = $('#reviewHighlights');
  try {
    const avaliacoes = await api(`/avaliacoes/jogo/${encodeURIComponent(jogoId)}`);
    if (!Array.isArray(avaliacoes)) throw new Error('Resposta inválida.');
    const total = avaliacoes.length;
    const media = total ? avaliacoes.reduce((s, a) => s + Number(a.nota), 0) / total : 0;
    const arredondado = Math.max(0, Math.min(5, Math.round(media)));
    $('#ratingStars').textContent = '★'.repeat(arredondado) + '☆'.repeat(5 - arredondado);
    $('#ratingSummary').textContent = total ? `${media.toFixed(1).replace('.', ',')} de 5 · ${total} ${total === 1 ? 'avaliação' : 'avaliações'}` : 'Este jogo ainda não possui avaliações.';
    container.replaceChildren();
    for (const avaliacao of avaliacoes.slice(0, 2)) {
      const item = document.createElement('div');
      item.className = 'gh-mini-review';
      const estrelas = document.createElement('div');
      estrelas.className = 'stars';
      estrelas.textContent = '★'.repeat(Math.max(0, Math.min(5, Number(avaliacao.nota) || 0)));
      const texto = document.createElement('p');
      texto.textContent = avaliacao.comentario || 'Avaliação sem comentário.';
      item.append(estrelas, texto);
      container.appendChild(item);
    }
  } catch {
    $('#ratingSummary').textContent = 'Não foi possível carregar as avaliações neste momento.';
    container.replaceChildren();
  }
}

async function iniciar() {
  const usuario = await guard(api);
  if (!usuario) return;
  montarNavbar(usuario);
  ajustarNavbarJogos(usuario.perfil);
  if (!id || !/^\d+$/.test(id)) {
    $('#detailLoading').classList.add('hidden');
    mostrarMensagem($('#detailError'), 'O identificador do jogo não foi informado. Volte ao catálogo e selecione um jogo.');
    return;
  }
  try {
    const jogo = await api(`/jogos/${encodeURIComponent(id)}`);
    mostrarJogo(jogo);
    $('#buyGame').addEventListener('click', () => {
      $('#purchaseNotice').textContent = 'A integração do botão de compra está com o responsável pelo fluxo de compras. Nenhuma compra foi registrada.';
      $('#purchaseNotice').classList.add('gh-purchase-note--notice');
    });
    await mostrarAvaliacoes(jogo.id);
  } catch (erro) {
    $('#detailLoading').classList.add('hidden');
    mostrarMensagem($('#detailError'), `Não foi possível carregar os detalhes: ${erro.message}`);
  }
}

await iniciar();
