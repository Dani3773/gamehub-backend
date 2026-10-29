import { api } from './api.js';

export function filtrarPorCategoria(jogos, categoriaId = '') {
    if (categoriaId === '') return jogos;

    return jogos.filter(jogo =>
        (jogo.categorias ?? []).some(categoria =>
            String(categoria.id) === String(categoriaId)
        )
    );
}

export async function montarFiltroCategorias(
    container,
    aoSelecionar
) {
    if (!container) {
        throw new Error('Container do filtro não encontrado.');
    }

    container.textContent = 'Carregando categorias...';

    let categorias;

    try {
        categorias = await api('/categorias');

        if (!Array.isArray(categorias)) {
            throw new Error('Resposta de categorias inválida.');
        }
    } catch (err) {
        container.textContent =
            `Não foi possível carregar as categorias: ${err.message}`;

        return;
    }

    container.replaceChildren();

    const grupo = document.createElement('div');
    grupo.className = 'chips';
    grupo.setAttribute('role', 'group');
    grupo.setAttribute('aria-label', 'Filtrar por categoria');

    const opcoes = [
        { id: '', nome: 'Todas' },
        ...categorias
    ];

    for (const categoria of opcoes) {
        const botao = document.createElement('button');

        botao.type = 'button';
        botao.className = 'chip';
        botao.textContent = categoria.nome;

        const selecionado = categoria.id === '';

        botao.classList.toggle('active', selecionado);
        botao.setAttribute('aria-pressed', String(selecionado));

        botao.addEventListener('click', () => {
            grupo.querySelectorAll('button').forEach(item => {
                const ativo = item === botao;

                item.classList.toggle('active', ativo);
                item.setAttribute('aria-pressed', String(ativo));
            });

            aoSelecionar(String(categoria.id));
        });

        grupo.appendChild(botao);
    }

    container.appendChild(grupo);
}