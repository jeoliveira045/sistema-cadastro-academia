package monokai.service;

import lombok.RequiredArgsConstructor;
import monokai.model.Usuario;
import monokai.repository.RoleRepository;
import monokai.repository.UsuarioRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("listUserDetailService")
@RequiredArgsConstructor
class ListUserDetailService implements UserDetailsManager {

    private final PasswordEncoder passwordEncoder;
    private final UsuarioRepository usuarioRepository;
    private final RoleRepository roleRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return usuarioRepository.buscarPorUsername(username)
                .map(user -> {
                    List<GrantedAuthority> authorities = user.getRoles().stream()
                            .map(role -> (GrantedAuthority) role::getRole)
                            .toList();
                    return new ConcreteUserDetails(user.getUsername(), user.getSenha(), authorities);
                })
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + username));
    }

    @Override
    public void createUser(UserDetails user) {
        if (usuarioRepository.existePorUsername(user.getUsername()))
            throw new IllegalArgumentException("Usuário já existe na base.");

        Usuario usuario = new Usuario();
        usuario.setUsername(user.getUsername());
        usuario.setSenha(passwordEncoder.encode(user.getPassword()));
        usuario.setRoles(user.getAuthorities().stream()
                .map(role -> roleRepository.buscarPorRole(role.getAuthority()).orElseThrow(
                        () -> new IllegalArgumentException("Role não encontrada: " + role.getAuthority())))
                .toList());
        usuarioRepository.salvar(usuario);
    }

    @Override
    public void updateUser(UserDetails user) {
        Usuario usuario = usuarioRepository.buscarPorUsername(user.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + user.getUsername()));

        usuario.setSenha(passwordEncoder.encode(user.getPassword()));
        usuario.setRoles(user.getAuthorities().stream()
                .map(role -> roleRepository.buscarPorRole(role.getAuthority()).orElseThrow(
                        () -> new IllegalArgumentException("Role não encontrada: " + role.getAuthority())))
                .toList());
        usuarioRepository.salvar(usuario);
    }

    @Override
    public void deleteUser(String username) {
        usuarioRepository.buscarPorUsername(username)
                .ifPresent(user -> usuarioRepository.excluir(user.getId()));
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
