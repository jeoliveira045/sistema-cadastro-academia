package monokai.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public abstract class Pessoa {

    @Id
    private Long id;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAlteracao;
    private String nome;
    private String sobrenome;
    private LocalDate dtNascimento;
    private String cpf;
    private String endereco;
    private TipoSanguineo tipoSanguineo;
    private String email;
    private Genero genero;
    private Situacao situacao;

    public String getNomeCompleto() {
        return nome + " " + sobrenome;
    }
}
