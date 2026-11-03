import { api, json } from './api.js';
import { guard } from './auth.js';
import { montarNavbar } from './navbar.js';

const $ = seletor => document.querySelector(seletor);

const form = $('#categoria-form');
const nome = $('#nome');
const descricao = $('#descricao');
const cancelar = $('#cancelar');
const erroForm = $('#erro-form');
const erroLista = $('#erro-lista');

let categoriaEmEdicao = null;

function mostrarErro(elemento, mensagem) {
    elemento.textContent = mensagem;
    elemento.classList.remove('hidden');
}

function limparFormulario() {
    categoriaEmEdicao = null;
    form.reset();
    $('#titulo-form').textContent = 'Nova categoria';
    cancelar.classList.add('hidden');
    erroForm.classList.add('hidden');
}

function iniciarEdicao(categoria) {
    categoriaEmEdicao = categoria.id;
    nome.value = categoria.nome;
    descricao.value = categoria.descricao ?? '';
    $('#titulo-form').textContent = 'Editar categoria';
    cancelar.classList.remove('hidden');
    erroForm.classList.add('hidden');
    nome.focus();
}

async function excluirCategoria(categoria, botao) {
    erroLista.classList.add('hidden');
    botao.disabled = true;

    try {
        await api(`/categorias/${categoria.id}`, { method: 'DELETE' });

        if (categoriaEmEdicao === categoria.id) limparFormulario();

        await carregarCategorias();
    } catch (erro) {
        mostrarErro(erroLista, erro.message);
    } finally {
        botao.disabled = false;
    }
}

function criarLinha(categoria) {
    const linha = document.createElement('tr');

    linha.insertCell().textContent = categoria.nome;
    linha.insertCell().textContent = categoria.descricao || '—';

    const acoes = linha.insertCell();
    acoes.className = 'actions';

    const editar = document.createElement('button');
    editar.type = 'button';
    editar.className = 'button';
    editar.textContent = 'Editar';
    editar.onclick = () => iniciarEdicao(categoria);

    const excluir = document.createElement('button');
    excluir.type = 'button';
    excluir.className = 'button danger';
    excluir.textContent = 'Excluir';
    excluir.onclick = () => excluirCategoria(categoria, excluir);

    acoes.append(editar, excluir);
    return linha;
}

async function carregarCategorias() {
    const estado = $('#estado-lista');
    estado.textContent = 'Carregando categorias...';
    erroLista.classList.add('hidden');

    try {
        const categorias = await api('/categorias');
        $('#categorias').replaceChildren(...categorias.map(criarLinha));
        estado.textContent = categorias.length
            ? ''
            : 'Nenhuma categoria cadastrada.';
    } catch (erro) {
        estado.textContent = '';
        mostrarErro(erroLista, `Falha ao carregar categorias. ${erro.message}`);
    }
}

const admin = await guard(api, true);

if (admin) {
    montarNavbar(admin);

    cancelar.onclick = limparFormulario;

    form.addEventListener('submit', async evento => {
        evento.preventDefault();
        erroForm.classList.add('hidden');

        const dados = {
            nome: nome.value.trim(),
            descricao: descricao.value.trim()
        };

        if (!dados.nome) {
            mostrarErro(erroForm, 'Informe o nome da categoria.');
            nome.focus();
            return;
        }

        const editando = categoriaEmEdicao !== null;
        const rota = editando
            ? `/categorias/${categoriaEmEdicao}`
            : '/categorias';

        const salvar = form.querySelector('[type="submit"]');
        salvar.disabled = true;

        try {
            await api(rota, {
                method: editando ? 'PUT' : 'POST',
                body: json(dados)
            });

            limparFormulario();
            await carregarCategorias();
        } catch (erro) {
            mostrarErro(erroForm, erro.message);
        } finally {
            salvar.disabled = false;
        }
    });

    await carregarCategorias();
}