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
import monokai.model.Role;
import monokai.repository.RoleRepository;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN') or hasRole('PROFESSOR')")

public class RoleController {

    private final RoleRepository roleRepository;

    @PostMapping
    public ResponseEntity<Role> criar(@RequestBody Role role) {
        return ResponseEntity.ok(roleRepository.salvar(role));
    }

    @GetMapping
    public ResponseEntity<List<Role>> listar() {
        return ResponseEntity.ok(roleRepository.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Role> buscarPorId(@PathVariable Long id) {
        return roleRepository.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Role> atualizar(@PathVariable Long id, @RequestBody Role role) {
        if (!roleRepository.existePorId(id)) return ResponseEntity.notFound().build();
        role.setId(id);
        return ResponseEntity.ok(roleRepository.salvar(role));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        if (!roleRepository.existePorId(id)) return ResponseEntity.notFound().build();
        roleRepository.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
