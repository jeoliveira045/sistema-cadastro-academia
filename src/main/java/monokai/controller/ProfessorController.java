package monokai.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import monokai.model.Professor;
import monokai.repository.ProfessorRepository;
import monokai.service.CadastrarUsuarioService;

@RestController
@RequestMapping("/professores")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasRole('ADMIN')")
public class ProfessorController {

    private final CadastrarUsuarioService cadastrarUsuarioService;
    private final ProfessorRepository professorRepository;

    @PostMapping
    public ResponseEntity<Professor> cadastrar(@RequestBody Professor professor) {
        log.info("Cadastrando professor: {}", professor.getCpf());
        return ResponseEntity.ok(cadastrarUsuarioService.exec(professor));
    }

    @GetMapping
    public ResponseEntity<List<Professor>> listar() {
        log.info("Listando todos os professores");
        return ResponseEntity.ok(professorRepository.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Professor> buscarPorId(@PathVariable Long id) {
        log.info("Buscando professor por id: {}", id);
        var resultado = professorRepository.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
        if (resultado.getStatusCode().is4xxClientError()) log.error("Professor não encontrado: {}", id);
        return resultado;
    }

    @PutMapping("/{id}")
    public ResponseEntity<Professor> atualizar(@PathVariable Long id, @RequestBody Professor professor) {
        log.info("Atualizando professor: {}", id);
        if (!professorRepository.buscarPorId(id).isPresent()) {
            log.error("Professor não encontrado para atualização: {}", id);
            return ResponseEntity.notFound().build();
        }
        professor.setId(id);
        return ResponseEntity.ok(professorRepository.salvar(professor));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        log.info("Deletando professor: {}", id);
        if (!professorRepository.buscarPorId(id).isPresent()) {
            log.error("Professor não encontrado para exclusão: {}", id);
            return ResponseEntity.notFound().build();
        }
        professorRepository.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
