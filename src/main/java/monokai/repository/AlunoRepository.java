package monokai.repository;

import lombok.RequiredArgsConstructor;
import monokai.model.Aluno;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class AlunoRepository {

    private final PessoaRepository pessoaRepository;
    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Transactional
    public Aluno salvar(Aluno aluno) {
        boolean isNovo = aluno.getId() == null;
        pessoaRepository.salvar(aluno);

        if (isNovo) {
            jdbcTemplate.update(
                "INSERT INTO aluno (id, whatsapp, foto) VALUES (:id, :whatsapp, :foto)",
                params(aluno)
            );
        } else {
            jdbcTemplate.update(
                "UPDATE aluno SET whatsapp = :whatsapp, foto = :foto WHERE id = :id",
                params(aluno)
            );
        }
        return aluno;
    }

    public Optional<Aluno> buscarPorId(Long id) {
        String sql = """
            SELECT p.*, a.whatsapp, a.foto
            FROM pessoa p JOIN aluno a ON a.id = p.id
            WHERE p.id = :id
            """;
        return jdbcTemplate.query(sql, Map.of("id", id), this::mapRow).stream().findFirst();
    }

    public List<Aluno> listarTodos() {
        String sql = """
            SELECT p.*, a.whatsapp, a.foto
            FROM pessoa p JOIN aluno a ON a.id = p.id
            """;
        return jdbcTemplate.query(sql, this::mapRow);
    }

    public boolean existePorId(Long id) {
        String sql = "SELECT COUNT(*) FROM aluno WHERE id = :id";
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Map.of("id", id),
                (rs, rn) -> rs.getInt(1) > 0));
    }

    @Transactional
    public void excluir(Long id) {
        jdbcTemplate.update("DELETE FROM aluno WHERE id = :id", Map.of("id", id));
        pessoaRepository.deletarPorId(id);
    }

    private MapSqlParameterSource params(Aluno aluno) {
        return new MapSqlParameterSource()
                .addValue("id", aluno.getId())
                .addValue("whatsapp", aluno.getWhatsapp())
                .addValue("foto", aluno.getFoto());
    }

    private Aluno mapRow(ResultSet rs, int rowNum) throws SQLException {
        Aluno aluno = new Aluno();
        pessoaRepository.mapPessoaFields(rs, aluno);
        aluno.setWhatsapp(rs.getString("whatsapp"));
        aluno.setFoto(rs.getString("foto"));
        return aluno;
    }
}
