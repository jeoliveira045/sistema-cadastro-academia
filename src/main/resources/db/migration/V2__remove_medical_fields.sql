ALTER TABLE pessoa
    DROP COLUMN IF EXISTS tipo_sanguineo,
    DROP COLUMN IF EXISTS genero;

ALTER TABLE aluno
    DROP COLUMN IF EXISTS peso,
    DROP COLUMN IF EXISTS altura,
    DROP COLUMN IF EXISTS percentual_gordura_corporal,
    DROP COLUMN IF EXISTS imc,
    DROP COLUMN IF EXISTS circunferencias,
    DROP COLUMN IF EXISTS historico_lesoes,
    DROP COLUMN IF EXISTS condicoes_medicas,
    DROP COLUMN IF EXISTS uso_medicamentos_continuos,
    DROP COLUMN IF EXISTS restricoes_medicas,
    DROP COLUMN IF EXISTS par_q,
    DROP COLUMN IF EXISTS atestado_medico;
