package monokai.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Pessoa {

    @Id
    private Long id;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAlteracao;
    private String nome;
    private String sobrenome;
    private LocalDate dtNascimento;
    private String cpf;
    private String endereco;
    private String email;
    private Genero genero;
    private Situacao situacao;

    public String getNomeCompleto() {
        return nome + " " + sobrenome;
    }
}
