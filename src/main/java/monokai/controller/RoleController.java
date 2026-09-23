package monokai.controller;

import lombok.RequiredArgsConstructor;
import monokai.model.Role;
import monokai.repository.RoleRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
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
