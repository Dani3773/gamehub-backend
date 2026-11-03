// Tela 7 - Painel Administrativo (Lucas). Acesso somente para ADMIN.
// Não existe endpoint de "todas as compras"; o painel consulta GET /compras?usuarioId=ID para cada
// usuário, o que a API permite ao ADMIN (RN08). Nenhum endpoint novo foi criado.
import { api } from './api.js';
import { guard } from './auth.js';
import { montarNavbar } from './navbar.js';
import { $, escapeHtml, formatarPreco, mostrarMensagem, esconderMensagem, ajustarNavbarJogos } from './jogos-ui.js';
import { ajustarNavbarCompras, formatarDataHora } from './compra.js';

const lista = valor => (Array.isArray(valor) ? valor : []);
const plural = (n, um, varios) => `${n} ${n === 1 ? um : varios}`;

function mostrarIndicadores({ usuarios, jogos, compras, categorias }) {
  const ativos = usuarios.filter(u => u.ativo !== false);
  const admins = usuarios.filter(u => u.perfil === 'ADMIN').length;
  const jogosAtivos = jogos.filter(j => j.ativo !== false).length;
  const receita = compras.reduce((s, c) => s + (Number(c.valorPago) || 0), 0);
  const compradores = new Set(compras.map(c => c.usuarioId)).size;

  $('#statUsers').textContent = usuarios.length;
  $('#statUsersInfo').textContent = `${plural(ativos.length, 'ativo', 'ativos')} · ${plural(admins, 'admin', 'admins')}`;
  $('#statGames').textContent = jogosAtivos;
  $('#statGamesInfo').textContent = `${plural(jogos.length, 'cadastrado', 'cadastrados')} · ${plural(categorias.length, 'categoria', 'categorias')}`;
  $('#statSales').textContent = compras.length;
  $('#statSalesInfo').textContent = plural(compradores, 'cliente comprador', 'clientes compradores');
  $('#statRevenue').textContent = formatarPreco(receita);
  $('#statRevenueInfo').textContent = `Ticket médio ${formatarPreco(compras.length ? receita / compras.length : 0)}`;

  $('#shortGames').textContent = `${plural(jogos.length, 'jogo cadastrado', 'jogos cadastrados')}, ${plural(jogos.length - jogosAtivos, 'inativo', 'inativos')}.`;
  $('#shortUsers').textContent = `${plural(usuarios.length - admins, 'cliente', 'clientes')} e ${plural(admins, 'administrador', 'administradores')}.`;
  $('#shortCategoriesText').textContent = `${plural(categorias.length, 'categoria cadastrada', 'categorias cadastradas')}.`;
}

function mostrarUltimasCompras(compras, usuariosPorId, jogosPorId) {
  const recentes = [...compras].sort((a, b) => String(b.dataCompra).localeCompare(String(a.dataCompra))).slice(0, 8);
  $('#salesCount').textContent = compras.length ? `${recentes.length} de ${plural(compras.length, 'compra', 'compras')}` : '';
  if (!recentes.length) {
    $('#salesRows').innerHTML = '<tr><td colspan="4" class="gh-table-empty">Nenhuma compra registrada até o momento.</td></tr>';
    return;
  }
  $('#salesRows').innerHTML = recentes.map(c => {
    const usuario = usuariosPorId.get(String(c.usuarioId));
    const jogo = jogosPorId.get(String(c.jogoId));
    const nomeJogo = jogo?.nome ?? `Jogo #${c.jogoId}`;
    return `<tr>
      <td>${formatarDataHora(c.dataCompra)}</td>
      <td><div class="gh-table-game"><span class="gh-table-avatar">${escapeHtml(String(usuario?.nome || '?').charAt(0).toUpperCase())}</span><div><strong>${escapeHtml(usuario?.nome ?? `Usuário #${c.usuarioId}`)}</strong><small>${escapeHtml(usuario?.email ?? '')}</small></div></div></td>
      <td><a class="gh-text-link" href="/detalhes-jogo.html?id=${encodeURIComponent(c.jogoId)}">${escapeHtml(nomeJogo)}</a></td>
      <td class="gh-table-price">${formatarPreco(c.valorPago)}</td>
    </tr>`;
  }).join('');
}

function mostrarRanking(compras, jogosPorId, avaliacoes) {
  const vendas = new Map();
  for (const c of compras) {
    const atual = vendas.get(String(c.jogoId)) ?? { jogoId: c.jogoId, qtd: 0, total: 0 };
    atual.qtd += 1;
    atual.total += Number(c.valorPago) || 0;
    vendas.set(String(c.jogoId), atual);
  }
  const top = [...vendas.values()].sort((a, b) => b.qtd - a.qtd || b.total - a.total).slice(0, 5);
  const maior = top[0]?.qtd || 1;
  $('#rankList').innerHTML = top.length
    ? top.map(item => `<li><div><strong>${escapeHtml(jogosPorId.get(String(item.jogoId))?.nome ?? `Jogo #${item.jogoId}`)}</strong><small>${formatarPreco(item.total)} em vendas</small><span class="gh-rank-bar" style="width:${Math.round((item.qtd / maior) * 100)}%"></span></div><em>${plural(item.qtd, 'venda', 'vendas')}</em></li>`).join('')
    : '<li class="muted">Ainda não há vendas para ranquear.</li>';

  const media = avaliacoes.length ? avaliacoes.reduce((s, a) => s + Number(a.nota), 0) / avaliacoes.length : 0;
  $('#reviewSummary').textContent = avaliacoes.length
    ? `${plural(avaliacoes.length, 'avaliação publicada', 'avaliações publicadas')} · média ${media.toFixed(1).replace('.', ',')} de 5 ★`
    : 'Nenhuma avaliação publicada ainda.';
}

// A tela de categorias pertence a outro colega; o atalho só vira link quando a página existir.
async function ativarAtalhoCategorias() {
  try {
    const resposta = await fetch('/categorias.html', { method: 'HEAD' });
    if (!resposta.ok) throw new Error();
    const atual = $('#shortCategories');
    const link = document.createElement('a');
    link.id = atual.id;
    link.className = 'card gh-shortcut';
    link.href = '/categorias.html';
    link.innerHTML = '<strong>GERENCIAR CATEGORIAS ↗</strong>';
    link.appendChild(atual.querySelector('p'));
    atual.replaceWith(link);
  } catch {
    const aviso = document.createElement('small');
    aviso.className = 'muted';
    aviso.textContent = 'Tela em desenvolvimento.';
    $('#shortCategories').appendChild(aviso);
  }
}

async function carregar() {
  esconderMensagem($('#panelError'));
  const botao = $('#reload');
  botao.disabled = true;
  try {
    const [usuarios, jogos, categorias, avaliacoes] = await Promise.all([
      api('/api/usuarios'), api('/jogos'), api('/categorias'), api('/avaliacoes').catch(() => [])
    ]).then(r => r.map(lista));
    const compras = (await Promise.all(usuarios.map(u => api(`/compras?usuarioId=${encodeURIComponent(u.id)}`).then(lista)))).flat();
    const usuariosPorId = new Map(usuarios.map(u => [String(u.id), u]));
    const jogosPorId = new Map(jogos.map(j => [String(j.id), j]));
    mostrarIndicadores({ usuarios, jogos, compras, categorias });
    mostrarUltimasCompras(compras, usuariosPorId, jogosPorId);
    mostrarRanking(compras, jogosPorId, avaliacoes);
  } catch (erro) {
    mostrarMensagem($('#panelError'), `Não foi possível carregar o painel: ${erro.message}`);
  } finally {
    botao.disabled = false;
  }
}

async function iniciar() {
  const usuario = await guard(api, true);
  if (!usuario) return;
  montarNavbar(usuario);
  ajustarNavbarJogos(usuario.perfil);
  ajustarNavbarCompras(usuario.perfil);
  $('#reload').addEventListener('click', carregar);
  await Promise.all([carregar(), ativarAtalhoCategorias()]);
}

await iniciar();
