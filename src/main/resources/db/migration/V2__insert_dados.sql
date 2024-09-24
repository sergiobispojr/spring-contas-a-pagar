-- Inserir dados de teste na tabela usuario
INSERT INTO usuario (nome, email, senha, saldo)
VALUES ('Sergio Bispo', 'sergio.bispo@me.com', '$2y$10$nhhwcC/rR8mQwEby41Zxh.1jDqWEhzn0kgO7sKu/0RBQoxiAaBcTS',
        1500.00),
       ('Maria Josefina', 'maria.josefina@example.com', '$2y$10$nhhwcC/rR8mQwEby41Zxh.1jDqWEhzn0kgO7sKu/0RBQoxiAaBcTS',
        1500.00),
       ('José Santos', 'jose.santos@example.com', '$2y$10$nhhwcC/rR8mQwEby41Zxh.1jDqWEhzn0kgO7sKu/0RBQoxiAaBcTS',
        500.00);

-- Inserir dados de teste na tabela conta
INSERT INTO conta (nome, descricao, valor_original, data_vencimento, situacao, data_pagamento, observacao, usuario_id)
VALUES ('Conta de Luz', 'Conta de energia elétrica mensal', 117.75, '2024-09-19', 'PENDENTE', NULL,
        'Pagar até o vencimento', 1),
       ('Conta de Celular', 'Conta de celular', 170.00, '2024-09-20', 'PENDENTE', NULL, 'Verificar consumo', 1),
       ('Internet', 'Conta de internet mensal', 119.00, '2024-09-10', 'PENDENTE', NULL, 'Plano 300MB', 2),
       ('Cartão de Crédito', 'Fatura do cartão de crédito', 587.00, '2024-09-15', 'PENDENTE', NULL,
        'Pagar antes do vencimento', 2),
       ('Condomínio', 'Pagamento do condomínio', 470.00, '2024-09-05', 'PENDENTE', NULL, 'Confirmar valor', 3),
       ('Academia', 'Mensalidade da academia', 120.00, '2024-09-17', 'PENDENTE', NULL, 'Pagar antes do vencimento', 3);
