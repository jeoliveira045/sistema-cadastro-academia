package monokai.service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;

import monokai.model.Usuario;
import monokai.repository.RoleRepository;
import monokai.repository.UsuarioRepository;


@Service("listUserDetailService")
class ListUserDetailService implements UserDetailsManager{

    private final PasswordEncoder passwordEncoder;
    private final UsuarioRepository usuarioRepository;
    private final RoleRepository roleRepository;

    public ListUserDetailService(
        UsuarioRepository usuarioRepository, 
        PasswordEncoder passwordEncoder,
        RoleRepository roleRepository){
        this.passwordEncoder = passwordEncoder;
        this.usuarioRepository = usuarioRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDetails usuario = usuarioRepository.buscarPorUsername(username).map(user -> {
            List<GrantedAuthority> roles = user.getRoles().stream().map(role -> {
                return (GrantedAuthority) () -> role.getRole();
            }).toList();
            return new ConcreteUserDetails(user.getUsername(), user.getSenha(), roles);
        }).orElseThrow(() -> new UsernameNotFoundException("usuario não encontrado!"));
        return usuario;
    }

    @Override
    public void createUser(UserDetails user) {
        if(usuarioRepository.existePorUsername(user.getUsername())) throw new RuntimeException("Usuario já existe na base");
        Usuario usuario = new Usuario();
        usuario.setUsername(user.getUsername());
        usuario.setSenha(passwordEncoder.encode(user.getPassword()));
        usuario.setRoles(user.getAuthorities().stream().map(role -> {
            return this.roleRepository.buscarPorRole(role.getAuthority()).orElseThrow();
        }).toList());
        usuarioRepository.salvar(usuario);
    }

    @Override
    public void updateUser(UserDetails user) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void deleteUser(String username) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean userExists(String username) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}


class ConcreteUserDetails implements UserDetails{
    String username;
    String password;
    Collection<? extends GrantedAuthority> roles;

    public ConcreteUserDetails(String username, String password){
        this.username = username;
        this.password = password;
    }

    public ConcreteUserDetails(String username, String password, Collection<? extends GrantedAuthority> roles) {
        this.username = username;
        this.password = password;
        this.roles = roles;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }


}