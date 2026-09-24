package monokai.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import monokai.model.Aluno;
import monokai.model.Pessoa;
import monokai.repository.AlunoRepository;
import monokai.service.CadastrarUsuarioService;
import monokai.service.ConfigurarAlunoAtivoInativoService;

@RestController
@RequestMapping("/alunos")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasRole('ADMIN') or hasRole('PROFESSOR')")
public class AlunoController {

    private final CadastrarUsuarioService cadastrarUsuarioService;
    private final ConfigurarAlunoAtivoInativoService configurarAlunoAtivoInativoService;
    private final AlunoRepository alunoRepository;

    @PostMapping
    public ResponseEntity<Pessoa> cadastrar(@RequestBody Aluno aluno) {
        log.info("Cadastrando aluno: {}", aluno.getCpf());
        return ResponseEntity.ok(cadastrarUsuarioService.exec(aluno));
    }

    @GetMapping
    public ResponseEntity<List<Aluno>> listar() {
        log.info("Listando todos os alunos");
        return ResponseEntity.ok(alunoRepository.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        log.info("Buscando aluno por id: {}", id);
        var aluno = alunoRepository.buscarPorId(id)
                .map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
        if (aluno.getStatusCode().is4xxClientError()) log.error("Aluno não encontrado: {}", id);
        return aluno;
    }

    @PutMapping("/{id}")
    public ResponseEntity<Aluno> atualizar(@PathVariable Long id, @RequestBody Aluno aluno) {
        log.info("Atualizando aluno: {}", id);
        if (!alunoRepository.buscarPorId(id).isPresent()) {
            log.error("Aluno não encontrado para atualização: {}", id);
            return ResponseEntity.notFound().build();
        }
        aluno.setId(id);
        aluno.setDataAlteracao(LocalDateTime.now());
        return ResponseEntity.ok(alunoRepository.salvar(aluno));
    }

    @PatchMapping("/{id}/situacao")
    public ResponseEntity<Aluno> alternarSituacao(@PathVariable Long id) {
        log.info("Alternando situação do aluno: {}", id);
        return ResponseEntity.ok(configurarAlunoAtivoInativoService.exec(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        log.info("Deletando aluno: {}", id);
        if (!alunoRepository.buscarPorId(id).isPresent()) {
            log.error("Aluno não encontrado para exclusão: {}", id);
            return ResponseEntity.notFound().build();
        }
        alunoRepository.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
