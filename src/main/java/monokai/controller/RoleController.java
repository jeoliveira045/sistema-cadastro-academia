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
import monokai.model.Role;
import monokai.repository.RoleRepository;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasRole('ADMIN') or hasRole('PROFESSOR')")
public class RoleController {

    private final RoleRepository roleRepository;

    @PostMapping
    public ResponseEntity<Role> criar(@RequestBody Role role) {
        log.info("Criando role: {}", role.getRole());
        return ResponseEntity.ok(roleRepository.salvar(role));
    }

    @GetMapping
    public ResponseEntity<List<Role>> listar() {
        log.info("Listando todas as roles");
        return ResponseEntity.ok(roleRepository.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Role> buscarPorId(@PathVariable Long id) {
        log.info("Buscando role por id: {}", id);
        var resultado = roleRepository.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
        if (resultado.getStatusCode().is4xxClientError()) log.error("Role não encontrada: {}", id);
        return resultado;
    }

    @PutMapping("/{id}")
    public ResponseEntity<Role> atualizar(@PathVariable Long id, @RequestBody Role role) {
        log.info("Atualizando role: {}", id);
        if (!roleRepository.existePorId(id)) {
            log.error("Role não encontrada para atualização: {}", id);
            return ResponseEntity.notFound().build();
        }
        role.setId(id);
        return ResponseEntity.ok(roleRepository.salvar(role));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        log.info("Excluindo role: {}", id);
        if (!roleRepository.existePorId(id)) {
            log.error("Role não encontrada para exclusão: {}", id);
            return ResponseEntity.notFound().build();
        }
        roleRepository.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
