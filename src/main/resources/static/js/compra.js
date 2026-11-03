// Fluxo de compra (Lucas): usado dentro de Detalhes do Jogo e compartilhado com a Biblioteca e o Painel.
// Usa somente os endpoints existentes: GET /compras (compras do usuário do token) e POST /compras { jogoId }.
import { api, json } from './api.js';
import { $, escapeHtml, formatarPreco } from './jogos-ui.js';

// A navbar compartilhada aponta "Minha biblioteca" para /compras, que é o endpoint JSON.
// Corrigimos o link apenas nas telas que usam este módulo, sem editar o navbar.js.
export function ajustarNavbarCompras(perfil) {
  document.querySelectorAll('#navbar a[href="/compras"]').forEach(link => { link.href = '/biblioteca.html'; });
  const navLinks = document.querySelector('#navbar .nav-links');
  if (perfil === 'ADMIN' && navLinks && !navLinks.querySelector('[data-admin-panel]')) {
    const link = document.createElement('a');
    link.href = '/painel-admin.html';
    link.textContent = 'Painel admin';
    link.dataset.adminPanel = 'true';
    navLinks.appendChild(link);
  }
  document.querySelectorAll('#navbar a[href]').forEach(link => {
    const ativo = link.pathname === location.pathname;
    link.classList.toggle('active', ativo);
    if (ativo) link.setAttribute('aria-current', 'page'); else link.removeAttribute('aria-current');
  });
}

export const formatarDataHora = valor => {
  const data = valor ? new Date(valor) : null;
  return data && !Number.isNaN(data.getTime())
    ? data.toLocaleDateString('pt-BR', { day: '2-digit', month: '2-digit', year: 'numeric' })
    : '—';
};

async function minhasCompras() {
  const compras = await api('/compras');
  return Array.isArray(compras) ? compras : [];
}

function garantirEstilo() {
  if (document.querySelector('link[href="/css/compras.css"]')) return;
  const link = document.createElement('link');
  link.rel = 'stylesheet';
  link.href = '/css/compras.css';
  document.head.appendChild(link);
}

function criarModal() {
  const modal = document.createElement('div');
  modal.id = 'buyModal';
  modal.className = 'modal-backdrop hidden';
  modal.setAttribute('role', 'dialog');
  modal.setAttribute('aria-modal', 'true');
  modal.setAttribute('aria-labelledby', 'buyModalTitle');
  modal.innerHTML = `<section class="card modal gh-buy-modal">
    <div class="gh-modal-heading"><div><p class="gh-eyebrow">CHECKOUT / GAMEHUB</p><h2 id="buyModalTitle">CONFIRMAR COMPRA</h2></div><button type="button" class="gh-close" data-buy-close aria-label="Fechar confirmação">×</button></div>
    <div class="gh-buy-summary"><span class="gh-buy-summary__mark" data-buy-mark aria-hidden="true"></span><div><strong data-buy-name></strong><small>Jogo digital · licença vinculada à sua conta</small></div></div>
    <div class="gh-buy-total"><span>TOTAL A PAGAR</span><strong data-buy-price></strong></div>
    <p class="gh-helper-text">O valor cobrado é o preço atual do jogo no momento da confirmação. Após a compra, o jogo aparece em Minha Biblioteca e você já pode avaliá-lo.</p>
    <div class="error hidden" data-buy-error role="alert"></div>
    <div class="modal-actions"><button type="button" class="button" data-buy-close>Cancelar</button><button type="button" class="button primary" data-buy-confirm>Confirmar compra</button></div>
  </section>`;
  document.body.appendChild(modal);
  return modal;
}

function marcarComoAdquirido(botao, nota, compra) {
  botao.disabled = true;
  botao.classList.add('gh-owned');
  botao.innerHTML = 'NA SUA BIBLIOTECA <span aria-hidden="true">✓</span>';
  const data = compra ? ` em ${formatarDataHora(compra.dataCompra)} por ${formatarPreco(compra.valorPago)}` : '';
  nota.classList.remove('gh-purchase-note--notice');
  nota.classList.add('gh-purchase-note--success');
  nota.innerHTML = `Você adquiriu este jogo${escapeHtml(data)}. <a href="/biblioteca.html">Ver minha biblioteca</a>`;
}

// Liga o botão #buyGame de detalhes-jogo.html ao endpoint de compra.
export async function iniciarFluxoCompra(jogo, usuario) {
  garantirEstilo();
  ajustarNavbarCompras(usuario?.perfil);
  const botao = $('#buyGame');
  const nota = $('#purchaseNotice');
  if (!botao || !nota) return;

  let compra = null;
  try {
    compra = (await minhasCompras()).find(c => String(c.jogoId) === String(jogo.id)) ?? null;
  } catch {
    nota.textContent = 'Não foi possível verificar se você já possui este jogo.';
  }
  if (compra) { marcarComoAdquirido(botao, nota, compra); return; }
  if (jogo.ativo === false) {
    nota.textContent = 'Este jogo está fora do catálogo e não pode ser comprado.';
    return;
  }
  nota.textContent = 'Compra única: o jogo fica vinculado à sua conta para sempre.';

  const modal = criarModal();
  const erro = modal.querySelector('[data-buy-error]');
  const confirmar = modal.querySelector('[data-buy-confirm]');
  modal.querySelector('[data-buy-mark]').textContent = String(jogo.nome || 'G').trim().charAt(0).toUpperCase();
  modal.querySelector('[data-buy-name]').textContent = jogo.nome;
  modal.querySelector('[data-buy-price]').textContent = formatarPreco(jogo.preco);

  const abrir = () => {
    erro.classList.add('hidden');
    modal.classList.remove('hidden');
    confirmar.focus();
  };
  const fechar = () => {
    modal.classList.add('hidden');
    botao.focus();
  };

  botao.addEventListener('click', abrir);
  modal.querySelectorAll('[data-buy-close]').forEach(b => b.addEventListener('click', fechar));
  modal.addEventListener('click', e => { if (e.target === modal) fechar(); });
  document.addEventListener('keydown', e => { if (e.key === 'Escape' && !modal.classList.contains('hidden')) fechar(); });

  confirmar.addEventListener('click', async () => {
    confirmar.disabled = true;
    confirmar.textContent = 'Processando...';
    try {
      const nova = await api('/compras', { method: 'POST', body: json({ jogoId: jogo.id }) });
      modal.classList.add('hidden');
      marcarComoAdquirido(botao, nota, nova);
    } catch (falha) {
      // RN05: se a API recusar por compra duplicada, o jogo já está na biblioteca.
      const existente = await minhasCompras().then(l => l.find(c => String(c.jogoId) === String(jogo.id))).catch(() => null);
      if (existente) {
        modal.classList.add('hidden');
        marcarComoAdquirido(botao, nota, existente);
      } else {
        erro.textContent = falha.message;
        erro.classList.remove('hidden');
      }
    } finally {
      confirmar.disabled = false;
      confirmar.textContent = 'Confirmar compra';
    }
  });
}
