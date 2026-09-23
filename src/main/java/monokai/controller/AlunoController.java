package monokai.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
import monokai.model.Aluno;
import monokai.model.Pessoa;
import monokai.repository.AlunoRepository;
import monokai.service.CadastrarUsuarioService;
import monokai.service.ConfigurarAlunoAtivoInativoService;

@RestController
@RequestMapping("/alunos")
@RequiredArgsConstructor
public class AlunoController {

    private final CadastrarUsuarioService cadastrarUsuarioService;
    private final ConfigurarAlunoAtivoInativoService configurarAlunoAtivoInativoService;
    private final AlunoRepository alunoRepository;

    @PostMapping
    public ResponseEntity<Pessoa> cadastrar(@RequestBody Aluno aluno) {
        return ResponseEntity.ok(cadastrarUsuarioService.exec(aluno));
    }

    @GetMapping
    @PreAuthorize("hasRole('PROFESSOR') or hasRole('ADMIN')")
    public ResponseEntity<List<Aluno>> listar() {
        return ResponseEntity.ok(alunoRepository.listarTodos());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PROFESSOR')")
    public ResponseEntity<Aluno> buscarPorId(@PathVariable Long id) {
        var aluno = alunoRepository.buscarPorId(id)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario não encontrado")); 
        return ResponseEntity.ok(aluno);
    }

    @PutMapping("/{id}")
    
    public ResponseEntity<Aluno> atualizar(@PathVariable Long id, @RequestBody Aluno aluno) {
        if (!alunoRepository.buscarPorId(id).isPresent()) return ResponseEntity.notFound().build();
        aluno.setId(id);
        aluno.setDataAlteracao(LocalDateTime.now());
        return ResponseEntity.ok(alunoRepository.salvar(aluno));
    }

    @PatchMapping("/{id}/situacao")
    public ResponseEntity<Aluno> alternarSituacao(@PathVariable Long id) {
        return ResponseEntity.ok(configurarAlunoAtivoInativoService.exec(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        if (!alunoRepository.buscarPorId(id).isPresent()) return ResponseEntity.notFound().build();
        alunoRepository.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
