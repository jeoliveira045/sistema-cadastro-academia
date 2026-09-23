package monokai.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import monokai.model.Usuario;

@Repository
@RequiredArgsConstructor
public class UsuarioRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final RoleRepository roleRepository;

    @Transactional
    public Usuario salvar(Usuario usuario) {
        if (usuario.getId() == null) {
            return inserir(usuario);
        }
        return atualizar(usuario);
    }

    private Usuario inserir(Usuario usuario) {
        String sql = "INSERT INTO usuario (username, senha) VALUES (:username, :senha)";
        var keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(sql, params(usuario), keyHolder, new String[]{"id"});
        usuario.setId(((Number) keyHolder.getKeys().get("id")).longValue());
        salvarRoles(usuario);
        return usuario;
    }

    private Usuario atualizar(Usuario usuario) {
        String sql = "UPDATE usuario SET username = :username, senha = :senha WHERE id = :id";
        jdbcTemplate.update(sql, params(usuario).addValue("id", usuario.getId()));
        jdbcTemplate.update("DELETE FROM usuario_roles WHERE usuario_id = :id", Map.of("id", usuario.getId()));
        salvarRoles(usuario);
        return usuario;
    }

    private void salvarRoles(Usuario usuario) {
        if (usuario.getRoles() == null || usuario.getRoles().isEmpty()) return;
        String sql = "INSERT INTO usuario_roles (usuario_id, role_id) VALUES (:usuarioId, :roleId)";
        usuario.getRoles().forEach(role -> jdbcTemplate.update(sql, new MapSqlParameterSource()
                .addValue("usuarioId", usuario.getId())
                .addValue("roleId", role.getId())));
    }

    public Optional<Usuario> buscarPorId(Long id) {
        String sql = """
            SELECT u.*, r.id AS role_id, r.type_role, r.desc_role
            FROM usuario u
            LEFT JOIN usuario_roles ur ON ur.usuario_id = u.id
            LEFT JOIN roles r ON r.id = ur.role_id
            WHERE u.id = :id
            """;
        return jdbcTemplate.query(sql, Map.of("id", id), this::mapRow).stream().findFirst();
    }

    public Optional<Usuario> buscarPorUsername(String username) {
        String sql = """
            SELECT u.*, r.id AS role_id, r.type_role, r.desc_role
            FROM usuario u
            LEFT JOIN usuario_roles ur ON ur.usuario_id = u.id
            LEFT JOIN roles r ON r.id = ur.role_id
            WHERE u.username = :username
            """;
        return jdbcTemplate.query(sql, Map.of("username", username), this::mapRow).stream().findFirst();
    }

    public List<Usuario> listarTodos() {
        String sql = """
            SELECT u.*, r.id AS role_id, r.type_role, r.desc_role
            FROM usuario u
            LEFT JOIN usuario_roles ur ON ur.usuario_id = u.id
            LEFT JOIN roles r ON r.id = ur.role_id
            """;
        return jdbcTemplate.query(sql, this::mapRow);
    }

    public boolean existePorId(Long id) {
        String sql = "SELECT COUNT(*) FROM usuario WHERE id = :id";
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Map.of("id", id),
                (rs, rn) -> rs.getInt(1) > 0));
    }

    public boolean existePorUsername(String username) {
        String sql = "SELECT COUNT(*) FROM USUARIO WHERE USERNAME = :USERNAME";
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Map.of("username", username), 
                (rs, rn) -> rs.getInt(1) > 0));
    }

    @Transactional
    public void excluir(Long id) {
        jdbcTemplate.update("DELETE FROM usuario_roles WHERE usuario_id = :id", Map.of("id", id));
        jdbcTemplate.update("DELETE FROM usuario WHERE id = :id", Map.of("id", id));
    }

    private Usuario mapRow(ResultSet rs, int rowNum) throws SQLException {
        Usuario usuario = new Usuario();
        usuario.setId(rs.getLong("id"));
        usuario.setUsername(rs.getString("username"));
        usuario.setSenha(rs.getString("senha"));

        long roleId = rs.getLong("role_id");
        if (!rs.wasNull()) {
            usuario.setRoles(List.of(roleRepository.mapRow(rs, rowNum)));
        }
        return usuario;
    }

    private MapSqlParameterSource params(Usuario usuario) {
        return new MapSqlParameterSource()
                .addValue("username", usuario.getUsername())
                .addValue("senha", usuario.getSenha());
    }
}
