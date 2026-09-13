package monokai.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@Table("professor")
public class Professor extends Pessoa {

    private String cref;
}
