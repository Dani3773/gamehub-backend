import { api, json } from './api.js';
import { guard } from './auth.js';
import { montarNavbar } from './navbar.js';
import { $, escapeHtml, formatarPreco, formatarData, categoriaNomes, mostrarMensagem, esconderMensagem, ajustarNavbarJogos } from './jogos-ui.js';

let jogos = [];
let categorias = [];
let editando = null;
const modal = $('#gameModal');
const form = $('#gameForm');

function linhasDaTabela() {
  const pesquisa = $('#adminSearch').value.trim().toLocaleLowerCase('pt-BR');
  const status = $('#statusFilter').value;
  const filtrados = jogos.filter(jogo => {
    const correspondeNome = String(jogo.nome || '').toLocaleLowerCase('pt-BR').includes(pesquisa);
    const correspondeStatus = status === 'all' || (status === 'active' ? jogo.ativo !== false : jogo.ativo === false);
    return correspondeNome && correspondeStatus;
  });
  $('#adminCount').textContent = `${filtrados.length} ${filtrados.length === 1 ? 'registro' : 'registros'}`;
  if (!filtrados.length) {
    $('#adminRows').innerHTML = '<tr><td colspan="6" class="gh-table-empty">Nenhum jogo encontrado.</td></tr>';
    return;
  }
  $('#adminRows').innerHTML = filtrados.map(jogo => {
    const generos = categoriaNomes(jogo).join(', ') || 'Sem categoria';
    return `<tr>
      <td><div class="gh-table-game"><span class="gh-table-avatar">${escapeHtml(String(jogo.nome || '?').charAt(0).toUpperCase())}</span><div><strong>${escapeHtml(jogo.nome)}</strong><small>ID #${escapeHtml(jogo.id)}</small></div></div></td>
      <td class="gh-table-genres">${escapeHtml(generos)}</td>
      <td class="gh-table-price">${formatarPreco(jogo.preco)}</td>
      <td><span class="gh-state ${jogo.ativo === false ? 'gh-state--inactive' : ''}">${jogo.ativo === false ? 'Inativo' : 'Ativo'}</span></td>
      <td>${formatarData(jogo.dataLancamento)}</td>
      <td><div class="gh-table-actions"><button class="button gh-small-button" type="button" data-edit="${jogo.id}">EDITAR</button>${jogo.ativo === false ? '<span class="muted gh-deactivated-label">Desativado</span>' : `<button class="button danger gh-small-button" type="button" data-deactivate="${jogo.id}">DESATIVAR</button>`}</div></td>
    </tr>`;
  }).join('');
}

function atualizarResumo() {
  const ativos = jogos.filter(j => j.ativo !== false).length;
  $('#statTotal').textContent = jogos.length;
  $('#statActive').textContent = ativos;
  $('#statInactive').textContent = jogos.length - ativos;
}

async function carregarJogos() {
  const dados = await api('/jogos');
  if (!Array.isArray(dados)) throw new Error('A API não retornou uma lista de jogos.');
  jogos = dados;
  atualizarResumo();
  linhasDaTabela();
}

function carregarCategoriasNoFormulario(selecionados = []) {
  const idsSelecionados = new Set(selecionados.map(String));
  const container = $('#categoryCheckboxes');
  container.replaceChildren();
  if (!categorias.length) {
    const aviso = document.createElement('p');
    aviso.className = 'muted';
    aviso.textContent = 'Nenhuma categoria cadastrada. É possível salvar o jogo sem categorias.';
    container.appendChild(aviso);
    return;
  }
  for (const categoria of categorias) {
    const label = document.createElement('label');
    label.className = 'gh-category-option';
    const checkbox = document.createElement('input');
    checkbox.type = 'checkbox';
    checkbox.value = String(categoria.id);
    checkbox.checked = idsSelecionados.has(String(categoria.id));
    const titulo = document.createElement('span');
    titulo.textContent = categoria.nome;
    label.append(checkbox, titulo);
    container.appendChild(label);
  }
}

function abrirModal(jogo = null) {
  editando = jogo;
  form.reset();
  esconderMensagem($('#modalError'));
  $('#modalTitle').textContent = jogo ? 'EDITAR JOGO' : 'NOVO JOGO';
  if (jogo) {
    $('#gameName').value = jogo.nome ?? '';
    $('#gameDescription').value = jogo.descricao ?? '';
    $('#gamePriceInput').value = jogo.preco ?? '';
    $('#gameRelease').value = jogo.dataLancamento ?? '';
  }
  carregarCategoriasNoFormulario(jogo?.categorias?.map(c => c.id) ?? []);
  modal.classList.remove('hidden');
  $('#gameName').focus();
}

function fecharModal() {
  modal.classList.add('hidden');
  editando = null;
  $('#newGame').focus();
}

async function desativarJogo(id) {
  const jogo = jogos.find(j => String(j.id) === String(id));
  if (!jogo || jogo.ativo === false) return;
  if (!window.confirm(`Desativar o jogo "${jogo.nome}"? Ele não aparecerá mais no catálogo.`)) return;
  esconderMensagem($('#adminError'));
  esconderMensagem($('#adminSuccess'));
  try {
    await api(`/jogos/${encodeURIComponent(id)}`, { method: 'DELETE' });
    await carregarJogos();
    mostrarMensagem($('#adminSuccess'), `O jogo "${jogo.nome}" foi desativado.`);
  } catch (erro) {
    mostrarMensagem($('#adminError'), erro.message);
  }
}

async function salvarJogo(evento) {
  evento.preventDefault();
  esconderMensagem($('#modalError'));
  const preco = Number($('#gamePriceInput').value);
  if (!Number.isFinite(preco) || preco <= 0) {
    mostrarMensagem($('#modalError'), 'Informe um preço maior que zero.');
    return;
  }
  const categoriaIds = [...$('#categoryCheckboxes').querySelectorAll('input:checked')].map(input => Number(input.value));
  const payload = {
    nome: $('#gameName').value.trim(),
    descricao: $('#gameDescription').value.trim(),
    preco,
    dataLancamento: $('#gameRelease').value || null,
    categoriaIds
  };
  if (!payload.nome) {
    mostrarMensagem($('#modalError'), 'Informe o nome do jogo.');
    return;
  }
  const botao = $('#saveGame');
  botao.disabled = true;
  try {
    if (editando) {
      await api(`/jogos/${encodeURIComponent(editando.id)}`, { method: 'PUT', body: json(payload) });
    } else {
      await api('/jogos', { method: 'POST', body: json(payload) });
    }
    const mensagem = editando ? 'Jogo atualizado com sucesso.' : 'Novo jogo cadastrado com sucesso.';
    fecharModal();
    await carregarJogos();
    esconderMensagem($('#adminError'));
    mostrarMensagem($('#adminSuccess'), mensagem);
  } catch (erro) {
    mostrarMensagem($('#modalError'), erro.message);
  } finally {
    botao.disabled = false;
  }
}

async function iniciar() {
  const usuario = await guard(api, true);
  if (!usuario) return;
  montarNavbar(usuario);
  ajustarNavbarJogos(usuario.perfil);
  $('#adminSearch').addEventListener('input', linhasDaTabela);
  $('#statusFilter').addEventListener('change', linhasDaTabela);
  $('#newGame').addEventListener('click', () => abrirModal());
  $('#closeModal').addEventListener('click', fecharModal);
  $('#cancelGame').addEventListener('click', fecharModal);
  modal.addEventListener('click', event => { if (event.target === modal) fecharModal(); });
  document.addEventListener('keydown', event => { if (event.key === 'Escape' && !modal.classList.contains('hidden')) fecharModal(); });
  form.addEventListener('submit', salvarJogo);
  $('#adminRows').addEventListener('click', event => {
    const botao = event.target.closest('button');
    if (!botao) return;
    if (botao.dataset.edit) {
      const jogo = jogos.find(j => String(j.id) === botao.dataset.edit);
      if (jogo) abrirModal(jogo);
    }
    if (botao.dataset.deactivate) void desativarJogo(botao.dataset.deactivate);
  });
  try {
    const [listaCategorias] = await Promise.all([api('/categorias'), carregarJogos()]);
    categorias = Array.isArray(listaCategorias) ? listaCategorias : [];
  } catch (erro) {
    mostrarMensagem($('#adminError'), `Erro ao carregar os dados: ${erro.message}`);
    $('#adminRows').innerHTML = '<tr><td colspan="6" class="gh-table-empty">Falha ao carregar o catálogo.</td></tr>';
    $('#newGame').disabled = true;
  }
}

await iniciar();
