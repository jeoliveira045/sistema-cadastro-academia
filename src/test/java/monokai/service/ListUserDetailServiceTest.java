package monokai.service;

import monokai.model.Role;
import monokai.model.Usuario;
import monokai.repository.RoleRepository;
import monokai.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListUserDetailServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private ListUserDetailService service;

    private Usuario usuario;
    private Role role;

    @BeforeEach
    void setUp() {
        role = new Role();
        role.setId(1L);
        role.setRole("ADMIN");

        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setUsername("joao.silva");
        usuario.setSenha("$2a$10$hashedPassword");
        usuario.setRoles(List.of(role));
    }

    @Test
    void deveCarregarUsuarioPorUsernameComSucesso() {
        when(usuarioRepository.buscarPorUsername("joao.silva")).thenReturn(Optional.of(usuario));

        UserDetails resultado = service.loadUserByUsername("joao.silva");

        assertThat(resultado.getUsername()).isEqualTo("joao.silva");
        assertThat(resultado.getPassword()).isEqualTo("$2a$10$hashedPassword");
        assertThat(resultado.getAuthorities()).extracting("authority").containsExactly("ADMIN");
    }

    @Test
    void deveLancarUsernameNotFoundExceptionQuandoUsuarioNaoExiste() {
        when(usuarioRepository.buscarPorUsername("inexistente")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.loadUserByUsername("inexistente"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("inexistente");
    }

    @Test
    void deveCriarUsuarioComSucesso() {
        UserDetails user = buildUserDetails("novo.usuario", "ADMIN");
        when(usuarioRepository.existePorUsername("novo.usuario")).thenReturn(false);
        when(roleRepository.buscarPorRole("ADMIN")).thenReturn(Optional.of(role));
        when(passwordEncoder.encode("senha123")).thenReturn("$2a$10$encoded");

        service.createUser(user);

        verify(usuarioRepository).salvar(any(Usuario.class));
    }

    @Test
    void deveLancarExcecaoAoCriarUsuarioDuplicado() {
        UserDetails user = buildUserDetails("joao.silva", "ADMIN");
        when(usuarioRepository.existePorUsername("joao.silva")).thenReturn(true);

        assertThatThrownBy(() -> service.createUser(user))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("já existe");
    }

    @Test
    void deveLancarExcecaoQuandoRoleNaoEncontradaAoCriarUsuario() {
        UserDetails user = buildUserDetails("novo.usuario", "ROLE_INEXISTENTE");
        when(usuarioRepository.existePorUsername("novo.usuario")).thenReturn(false);
        when(roleRepository.buscarPorRole("ROLE_INEXISTENTE")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.createUser(user))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Role não encontrada");
    }

    @Test
    void deveAtualizarUsuarioComSucesso() {
        UserDetails user = buildUserDetails("joao.silva", "ADMIN");
        when(usuarioRepository.buscarPorUsername("joao.silva")).thenReturn(Optional.of(usuario));
        when(roleRepository.buscarPorRole("ADMIN")).thenReturn(Optional.of(role));
        when(passwordEncoder.encode("senha123")).thenReturn("$2a$10$newEncoded");

        service.updateUser(user);

        verify(usuarioRepository).salvar(usuario);
        assertThat(usuario.getSenha()).isEqualTo("$2a$10$newEncoded");
    }

    @Test
    void deveLancarExcecaoAoAtualizarUsuarioInexistente() {
        UserDetails user = buildUserDetails("inexistente", "ADMIN");
        when(usuarioRepository.buscarPorUsername("inexistente")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateUser(user))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("inexistente");
    }

    @Test
    void deveDeletarUsuarioComSucesso() {
        when(usuarioRepository.buscarPorUsername("joao.silva")).thenReturn(Optional.of(usuario));

        service.deleteUser("joao.silva");

        verify(usuarioRepository).excluir(1L);
    }

    @Test
    void naoDeveLancarExcecaoAoDeletarUsuarioInexistente() {
        when(usuarioRepository.buscarPorUsername("inexistente")).thenReturn(Optional.empty());

        assertThatNoException().isThrownBy(() -> service.deleteUser("inexistente"));
        verify(usuarioRepository, never()).excluir(any());
    }

    @Test
    void deveRetornarTrueQuandoUsuarioExiste() {
        when(usuarioRepository.existePorUsername("joao.silva")).thenReturn(true);

        assertThat(service.userExists("joao.silva")).isTrue();
    }

    @Test
    void deveRetornarFalseQuandoUsuarioNaoExiste() {
        when(usuarioRepository.existePorUsername("inexistente")).thenReturn(false);

        assertThat(service.userExists("inexistente")).isFalse();
    }

    @Test
    void deveLancarUnsupportedOperationExceptionEmChangePassword() {
        assertThatThrownBy(() -> service.changePassword("old", "new"))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    private UserDetails buildUserDetails(String username, String... roles) {
        List<SimpleGrantedAuthority> authorities = List.of(roles).stream()
                .map(SimpleGrantedAuthority::new)
                .toList();
        return new User(username, "senha123", authorities);
    }
}
