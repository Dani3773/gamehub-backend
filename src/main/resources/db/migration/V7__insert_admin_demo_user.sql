-- Usuário de desenvolvimento/demonstração para acesso inicial ao GameHub.
INSERT INTO usuario (nome, email, senha_hash, perfil, ativo)
VALUES (
    'Administrador',
    'admin@gamehub.com',
    '$2a$10$k/QeMMnRxINCuWZ4i4d7Su6ebADNKwVSg9EoVqXRE8VbnTV0lLHee',
    'ADMIN',
    TRUE
);
