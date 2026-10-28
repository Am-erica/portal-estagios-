
INSERT INTO usuarios (nome, email, perfil, curso, nome_empresa)
VALUES
    ('Ana Silva', 'ana@email.com', 'ESTUDANTE',
     'Análise e Desenvolvimento de Sistemas', NULL),
    ('Tech Solutions', 'contato@techsolutions.com', 'EMPRESA',
     NULL, 'Tech Solutions'),
    ('Administrador', 'admin@portal.com', 'ADMINISTRADOR',
     NULL, NULL)
ON CONFLICT (email) DO NOTHING;

INSERT INTO areas (nome)
VALUES
    ('Tecnologia'),
    ('Administração'),
    ('Marketing')
ON CONFLICT (nome) DO NOTHING;

INSERT INTO vagas (
    empresa_id, titulo, descricao, area_id, localizacao,
    modalidade, carga_horaria_semanal, situacao
)
SELECT
    u.id,
    'Estágio em Desenvolvimento Java',
    'Auxiliar no desenvolvimento e na manutenção de sistemas.',
    a.id,
    'São Paulo',
    'HIBRIDA',
    30,
    'ABERTA'
FROM usuarios u
CROSS JOIN areas a
WHERE u.email = 'contato@techsolutions.com'
  AND a.nome = 'Tecnologia'
  AND NOT EXISTS (
      SELECT 1 FROM vagas v
      WHERE v.titulo = 'Estágio em Desenvolvimento Java'
        AND v.empresa_id = u.id
  );
