package monokai.repository;

import monokai.model.Aluno;
import org.springframework.data.repository.ListCrudRepository;

public interface AlunoRepository extends ListCrudRepository<Aluno, Long> {
}
