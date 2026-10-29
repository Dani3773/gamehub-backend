import { initials, logout } from './auth.js';

export function montarNavbar(user) {
    const container = document.querySelector('#navbar');

    if (!container) return;

    container.innerHTML = `
        <nav class="navbar" aria-label="Navegação principal">
            <a class="brand" href="/jogos">GAMEHUB</a>

            <div class="nav-links">
                <a href="/jogos">Catálogo</a>
                <a href="/compras">Minha biblioteca</a>
            </div>

            <a class="user-chip" href="/perfil.html">
                <span class="initials" data-nav-initials></span>
                <span data-nav-name></span>
            </a>

            <button type="button" class="button" data-nav-logout>
                Sair
            </button>
        </nav>
    `;

    if (user.perfil === 'ADMIN') {
        const linkUsuarios = document.createElement('a');

        linkUsuarios.href = '/usuarios.html';
        linkUsuarios.textContent = 'Usuários';

        container.querySelector('.nav-links').appendChild(linkUsuarios);
    }

    // textContent evita interpretar o nome como HTML.
    container.querySelector('[data-nav-initials]').textContent =
        initials(user.nome);

    container.querySelector('[data-nav-name]').textContent =
        user.nome;

    container.querySelector('[data-nav-logout]')
        .addEventListener('click', logout);

    container.querySelectorAll('a[href]').forEach(link => {
        if (link.pathname === window.location.pathname) {
            link.classList.add('active');
            link.setAttribute('aria-current', 'page');
        }
    });
}