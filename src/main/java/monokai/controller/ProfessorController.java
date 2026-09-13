package monokai.controller;

import lombok.RequiredArgsConstructor;
import monokai.model.Professor;
import monokai.repository.ProfessorRepository;
import monokai.service.CadastrarUsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/professores")
@RequiredArgsConstructor
public class ProfessorController {

    private final CadastrarUsuarioService cadastrarUsuarioService;
    private final ProfessorRepository professorRepository;

    @PostMapping
    public ResponseEntity<Professor> cadastrar(@RequestBody Professor professor) {
        return ResponseEntity.ok(cadastrarUsuarioService.exec(professor));
    }

    @GetMapping
    public ResponseEntity<List<Professor>> listar() {
        return ResponseEntity.ok(professorRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Professor> buscarPorId(@PathVariable Long id) {
        return professorRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Professor> atualizar(@PathVariable Long id, @RequestBody Professor professor) {
        if (!professorRepository.existsById(id)) return ResponseEntity.notFound().build();
        professor.setId(id);
        return ResponseEntity.ok(professorRepository.save(professor));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        if (!professorRepository.existsById(id)) return ResponseEntity.notFound().build();
        professorRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
