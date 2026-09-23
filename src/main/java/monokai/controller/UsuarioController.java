package monokai.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import monokai.model.Usuario;
import monokai.repository.UsuarioRepository;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UserDetailsManager userDetailsManager;
    private final UsuarioRepository usuarioRepository;

    @PostMapping
    public ResponseEntity<Void> criar(@RequestBody Usuario usuario) {
        userDetailsManager.createUser(toUserDetails(usuario));
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<Usuario>> listar() {
        return ResponseEntity.ok(usuarioRepository.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> buscarPorId(@PathVariable Long id) {
        return usuarioRepository.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> atualizar(@PathVariable Long id, @RequestBody Usuario usuario) {
        if (!usuarioRepository.existePorId(id)) return ResponseEntity.notFound().build();
        userDetailsManager.updateUser(toUserDetails(usuario));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<Void> excluir(@PathVariable String username) {
        if (!userDetailsManager.userExists(username)) return ResponseEntity.notFound().build();
        userDetailsManager.deleteUser(username);
        return ResponseEntity.noContent().build();
    }

    private User toUserDetails(Usuario usuario) {
        List<SimpleGrantedAuthority> authorities = usuario.getRoles() == null ? List.of() :
                usuario.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority(role.getRole()))
                        .toList();
        return new User(usuario.getUsername(), usuario.getSenha(), authorities);
    }
}
