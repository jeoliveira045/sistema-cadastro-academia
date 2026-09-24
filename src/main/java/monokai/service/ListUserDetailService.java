package monokai.service;

import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import monokai.model.Usuario;
import monokai.repository.RoleRepository;
import monokai.repository.UsuarioRepository;

@Service("listUserDetailService")
@RequiredArgsConstructor
@Slf4j
class ListUserDetailService implements UserDetailsManager {

    private final PasswordEncoder passwordEncoder;
    private final UsuarioRepository usuarioRepository;
    private final RoleRepository roleRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Carregando usuário por username: {}", username);
        return usuarioRepository.buscarPorUsername(username)
                .map(user -> {
                    List<GrantedAuthority> authorities = user.getRoles().stream()
                            .map(role -> (GrantedAuthority) role::getRole)
                            .toList();
                    return new ConcreteUserDetails(user.getUsername(), user.getSenha(), authorities);
                })
                .orElseThrow(() -> {
                    log.error("Usuário não encontrado: {}", username);
                    return new UsernameNotFoundException("Usuário não encontrado: " + username);
                });
    }

    @Override
    public void createUser(UserDetails user) {
        log.info("Criando usuário: {}", user.getUsername());
        if (usuarioRepository.existePorUsername(user.getUsername())) {
            log.error("Usuário já existe na base: {}", user.getUsername());
            throw new IllegalArgumentException("Usuário já existe na base.");
        }
        Usuario usuario = new Usuario();
        usuario.setUsername(user.getUsername());
        usuario.setSenha(passwordEncoder.encode(user.getPassword()));
        usuario.setRoles(user.getAuthorities().stream()
                .map(role -> roleRepository.buscarPorRole(role.getAuthority()).orElseThrow(
                        () -> {
                            log.error("Role não encontrada: {}", role.getAuthority());
                            return new IllegalArgumentException("Role não encontrada: " + role.getAuthority());
                        }))
                .toList());
        usuarioRepository.salvar(usuario);
        log.info("Usuário criado com sucesso: {}", user.getUsername());
    }

    @Override
    public void updateUser(UserDetails user) {
        log.info("Atualizando usuário: {}", user.getUsername());
        Usuario usuario = usuarioRepository.buscarPorUsername(user.getUsername())
                .orElseThrow(() -> {
                    log.error("Usuário não encontrado: {}", user.getUsername());
                    return new UsernameNotFoundException("Usuário não encontrado: " + user.getUsername());
                });
        usuario.setSenha(passwordEncoder.encode(user.getPassword()));
        usuario.setRoles(user.getAuthorities().stream()
                .map(role -> roleRepository.buscarPorRole(role.getAuthority()).orElseThrow(
                        () -> new IllegalArgumentException("Role não encontrada: " + role.getAuthority())))
                .toList());
        usuarioRepository.salvar(usuario);
        log.info("Usuário atualizado com sucesso: {}", user.getUsername());
    }

    @Override
    public void deleteUser(String username) {
        log.info("Deletando usuário: {}", username);
        usuarioRepository.buscarPorUsername(username)
                .ifPresent(user -> {
                    usuarioRepository.excluir(user.getId());
                    log.info("Usuário deletado: {}", username);
                });
    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {
        throw new UnsupportedOperationException("Use updateUser para alterar a senha.");
    }

    @Override
    public boolean userExists(String username) {
        return usuarioRepository.existePorUsername(username);
    }
}
