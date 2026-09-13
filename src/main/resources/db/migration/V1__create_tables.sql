CREATE TABLE pessoa (
    id              BIGSERIAL       PRIMARY KEY,
    data_criacao    TIMESTAMP       NOT NULL DEFAULT NOW(),
    data_alteracao  TIMESTAMP,
    nome            VARCHAR(100)    NOT NULL,
    sobrenome       VARCHAR(100)    NOT NULL,
    dt_nascimento   DATE            NOT NULL,
    cpf             VARCHAR(14)     NOT NULL UNIQUE,
    endereco        VARCHAR(255),
    tipo_sanguineo  VARCHAR(20),
    email           VARCHAR(150)    NOT NULL UNIQUE,
    genero          VARCHAR(20),
    situacao        SMALLINT        NOT NULL DEFAULT 1,
    dtype           VARCHAR(31)     NOT NULL
);

CREATE TABLE professor (
    id   BIGINT PRIMARY KEY REFERENCES pessoa(id),
    cref VARCHAR(20) NOT NULL UNIQUE
);

CREATE TABLE aluno (
    id                          BIGINT          PRIMARY KEY REFERENCES pessoa(id),
    whatsapp                    VARCHAR(20),
    foto                        VARCHAR(255),
    peso                        DOUBLE PRECISION,
    altura                      DOUBLE PRECISION,
    percentual_gordura_corporal DOUBLE PRECISION,
    imc                         DOUBLE PRECISION,
    circunferencias             TEXT,
    historico_lesoes            TEXT,
    condicoes_medicas           TEXT,
    uso_medicamentos_continuos  TEXT,
    restricoes_medicas          TEXT,
    par_q                       TEXT,
    atestado_medico             VARCHAR(255)
);