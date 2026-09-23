package monokai.repository;

import lombok.RequiredArgsConstructor;
import monokai.model.Role;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RoleRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public Role salvar(Role role) {
        if (role.getId() == null) {
            return inserir(role);
        }
        return atualizar(role);
    }

    private Role inserir(Role role) {
        String sql = "INSERT INTO roles (type_role, desc_role) VALUES (:typeRole, :descRole)";
        var keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(sql, params(role), keyHolder, new String[]{"id"});
        role.setId(((Number) keyHolder.getKeys().get("id")).longValue());
        return role;
    }

    private Role atualizar(Role role) {
        String sql = "UPDATE roles SET type_role = :typeRole, desc_role = :descRole WHERE id = :id";
        jdbcTemplate.update(sql, params(role).addValue("id", role.getId()));
        return role;
    }

    public Optional<Role> buscarPorId(Long id) {
        String sql = "SELECT * FROM roles WHERE id = :id";
        return jdbcTemplate.query(sql, Map.of("id", id), this::mapRow).stream().findFirst();
    }

    public Optional<Role> buscarPorRole(String role) {
        String sql = "SELECT * FROM roles WHERE role_type = :role_type";
        return jdbcTemplate.query(sql, Map.of("role_type", role), this::mapRow).stream().findFirst();
    }

    public List<Role> listarTodos() {
        return jdbcTemplate.query("SELECT * FROM roles", this::mapRow);
    }

    public boolean existePorId(Long id) {
        String sql = "SELECT COUNT(*) FROM roles WHERE id = :id";
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Map.of("id", id),
                (rs, rn) -> rs.getInt(1) > 0));
    }

    public void excluir(Long id) {
        jdbcTemplate.update("DELETE FROM roles WHERE id = :id", Map.of("id", id));
    }

    Role mapRow(ResultSet rs, int rowNum) throws SQLException {
        Role role = new Role();
        role.setId(rs.getLong("id"));
        role.setRole(rs.getString("type_role"));
        role.setDescRole(rs.getString("desc_role"));
        return role;
    }

    private MapSqlParameterSource params(Role role) {
        return new MapSqlParameterSource()
                .addValue("typeRole", role.getRole())
                .addValue("descRole", role.getDescRole());
    }
}
