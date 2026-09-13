package monokai.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@Table("aluno")
public class Aluno extends Pessoa {
}
