INSERT INTO roles (type_role, desc_role)
VALUES ('SERVICE', 'Conta de serviço para comunicação entre sistemas');

INSERT INTO usuario (username, senha)
VALUES ('service-account', crypt('service-account', gen_salt('bf')));

INSERT INTO usuario_roles (usuario_id, role_id)
SELECT u.id, r.id
FROM usuario u
INNER JOIN roles r ON r.type_role = 'SERVICE'
WHERE u.username = 'service-account';
