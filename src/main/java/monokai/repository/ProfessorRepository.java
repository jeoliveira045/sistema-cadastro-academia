package monokai.repository;

import lombok.RequiredArgsConstructor;
import monokai.model.Professor;
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
public class ProfessorRepository {

    private final PessoaRepository pessoaRepository;
    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Transactional
    public Professor salvar(Professor professor) {
        boolean isNovo = professor.getId() == null;
        pessoaRepository.salvar(professor);

        if (isNovo) {
            jdbcTemplate.update(
                "INSERT INTO professor (id, cref) VALUES (:id, :cref)",
                params(professor)
            );
        } else {
            jdbcTemplate.update(
                "UPDATE professor SET cref = :cref WHERE id = :id",
                params(professor)
            );
        }
        return professor;
    }

    public Optional<Professor> buscarPorId(Long id) {
        String sql = """
            SELECT p.*, pr.cref
            FROM pessoa p JOIN professor pr ON pr.id = p.id
            WHERE p.id = :id
            """;
        return jdbcTemplate.query(sql, Map.of("id", id), this::mapRow).stream().findFirst();
    }

    public List<Professor> listarTodos() {
        String sql = """
            SELECT p.*, pr.cref
            FROM pessoa p JOIN professor pr ON pr.id = p.id
            """;
        return jdbcTemplate.query(sql, this::mapRow);
    }

    public boolean existePorId(Long id) {
        String sql = "SELECT COUNT(*) FROM professor WHERE id = :id";
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Map.of("id", id),
                (rs, rn) -> rs.getInt(1) > 0));
    }

    @Transactional
    public void excluir(Long id) {
        jdbcTemplate.update("DELETE FROM professor WHERE id = :id", Map.of("id", id));
        pessoaRepository.deletarPorId(id);
    }

    private MapSqlParameterSource params(Professor professor) {
        return new MapSqlParameterSource()
                .addValue("id", professor.getId())
                .addValue("cref", professor.getCref());
    }

    private Professor mapRow(ResultSet rs, int rowNum) throws SQLException {
        Professor professor = new Professor();
        pessoaRepository.mapPessoaFields(rs, professor);
        professor.setCref(rs.getString("cref"));
        return professor;
    }
}
