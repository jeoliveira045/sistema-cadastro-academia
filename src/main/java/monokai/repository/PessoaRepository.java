package monokai.repository;

import lombok.RequiredArgsConstructor;
import monokai.model.Genero;
import monokai.model.Pessoa;
import monokai.model.Situacao;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PessoaRepository {

	private final NamedParameterJdbcTemplate jdbcTemplate;

	public Pessoa salvar(Pessoa pessoa) {
		if (pessoa.getId() == null) {
			return inserir(pessoa);
		}
		return atualizar(pessoa);
	}

	private Pessoa inserir(Pessoa pessoa) {
		String sql = """
				INSERT INTO pessoa (
				    data_criacao, nome, sobrenome, dt_nascimento,
				    cpf, endereco, email, genero, situacao, dtype
				) VALUES (
				    :dataCriacao, :nome, :sobrenome, :dtNascimento,
				    :cpf, :endereco, :email, :genero, :situacao, :dtype
				)
				""";

		var keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(sql, params(pessoa), keyHolder, new String[] { "id" });
		pessoa.setId(((Number) keyHolder.getKeys().get("id")).longValue());
		return pessoa;
	}

	private Pessoa atualizar(Pessoa pessoa) {
		String sql = """
				UPDATE pessoa SET
				    data_alteracao = :dataAlteracao,
				    nome           = :nome,
				    sobrenome      = :sobrenome,
				    dt_nascimento  = :dtNascimento,
				    cpf            = :cpf,
				    endereco       = :endereco,
				    email          = :email,
				    genero         = :genero,
				    situacao       = :situacao
				WHERE id = :id
				""";
		jdbcTemplate.update(sql, params(pessoa).addValue("id", pessoa.getId()).addValue("dataAlteracao",
				Timestamp.valueOf(LocalDateTime.now())));
		return pessoa;
	}

	public Optional<Pessoa> buscarPorId(Long id) {
		String sql = "SELECT * FROM pessoa WHERE id = :id";
		var resultado = jdbcTemplate.query(sql, Map.of("id", id), this::mapRow);
		return resultado.stream().findFirst();
	}

	public void deletarPorId(Long id) {
		jdbcTemplate.update("DELETE FROM pessoa WHERE id = :id", Map.of("id", id));
	}

	private MapSqlParameterSource params(Pessoa p) {
		return new MapSqlParameterSource()
				.addValue("dataCriacao",
						p.getDataCriacao() != null ? Timestamp.valueOf(p.getDataCriacao())
								: Timestamp.valueOf(LocalDateTime.now()))
				.addValue("nome", p.getNome()).addValue("sobrenome", p.getSobrenome())
				.addValue("dtNascimento", p.getDtNascimento()).addValue("cpf", p.getCpf())
				.addValue("endereco", p.getEndereco()).addValue("email", p.getEmail())
				.addValue("genero", p.getGenero() != null ? p.getGenero().name() : null)
				.addValue("situacao", p.getSituacao() != null ? p.getSituacao().getCodigo() : null)
				.addValue("dtype", p.getClass().getSimpleName());
	}

	Pessoa mapRow(ResultSet rs, int rowNum) throws SQLException {
		Pessoa pessoa = new Pessoa();
		mapPessoaFields(rs, pessoa);
		return pessoa;
	}

	void mapPessoaFields(ResultSet rs, Pessoa pessoa) throws SQLException {
		pessoa.setId(rs.getLong("id"));
		Timestamp dataCriacao = rs.getTimestamp("data_criacao");
		if (dataCriacao != null)
			pessoa.setDataCriacao(dataCriacao.toLocalDateTime());
		Timestamp dataAlteracao = rs.getTimestamp("data_alteracao");
		if (dataAlteracao != null)
			pessoa.setDataAlteracao(dataAlteracao.toLocalDateTime());
		pessoa.setNome(rs.getString("nome"));
		pessoa.setSobrenome(rs.getString("sobrenome"));
		pessoa.setDtNascimento(rs.getObject("dt_nascimento", LocalDate.class));
		pessoa.setCpf(rs.getString("cpf"));
		pessoa.setEndereco(rs.getString("endereco"));
		pessoa.setEmail(rs.getString("email"));
		String genero = rs.getString("genero");
		if (genero != null)
			pessoa.setGenero(Genero.valueOf(genero));
		int situacao = rs.getInt("situacao");
		if (String.valueOf(situacao) != null)
			pessoa.setSituacao(Situacao.fromCodigo(situacao));
	}
}
