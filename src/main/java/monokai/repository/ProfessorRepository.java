package monokai.repository;

import monokai.model.Professor;
import org.springframework.data.repository.ListCrudRepository;

public interface ProfessorRepository extends ListCrudRepository<Professor, Long> {
}
