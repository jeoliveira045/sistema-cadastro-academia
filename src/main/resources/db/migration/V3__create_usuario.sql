

CREATE TABLE usuario (
    id       BIGSERIAL    PRIMARY KEY,
    username VARCHAR(50)  NOT NULL UNIQUE,
    senha    VARCHAR(255) NOT NULL
);

INSERT INTO usuario (username, senha) VALUES ('admin', crypt('admin', gen_salt('bf')));

CREATE TABLE roles (
    id        BIGSERIAL    PRIMARY KEY,
    type_role      VARCHAR(50)  NOT NULL UNIQUE,
    desc_role VARCHAR(255)
);

INSERT INTO ROLES(type_role, desc_role) VALUES('ADMIN', 'Administrador do sistema');
INSERT INTO ROLES(type_role, desc_role) VALUES('PROFESSOR', 'Instrutor de alunos');
INSERT INTO ROLES(type_role, desc_role) VALUES('ALUNO', 'aluno');

CREATE TABLE usuario_roles (
    usuario_id BIGINT NOT NULL REFERENCES usuario(id),
    role_id    BIGINT NOT NULL REFERENCES roles(id),
    PRIMARY KEY (usuario_id, role_id)
);


INSERT INTO USUARIO_ROLES(USUARIO_ID, ROLE_ID) 
SELECT U.ID, R.ID FROM USUARIO U INNER JOIN ROLES R ON UPPER(U.USERNAME) = R.TYPE_ROLE;

