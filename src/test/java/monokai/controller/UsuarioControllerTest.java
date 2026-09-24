package monokai.controller;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.test.context.support.WithMockUser;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;

import monokai.model.Role;
import monokai.model.Usuario;
import monokai.repository.UsuarioRepository;

@WebMvcTest(UsuarioController.class)
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private UserDetailsManager userDetailsManager;

    @MockitoBean
    private UsuarioRepository usuarioRepository;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        Role role = new Role();
        role.setId(1L);
        role.setRole("ADMIN");

        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setUsername("joao.silva");
        usuario.setSenha("senha123");
        usuario.setRoles(List.of(role));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveCriarUsuario() throws Exception {
        doNothing().when(userDetailsManager).createUser(any());

        mockMvc.perform(post("/usuarios")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuario)))
                .andExpect(status().isOk());

        verify(userDetailsManager).createUser(any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveListarUsuarios() throws Exception {
        when(usuarioRepository.listarTodos()).thenReturn(List.of(usuario));

        mockMvc.perform(get("/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("joao.silva"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveBuscarUsuarioPorId() throws Exception {
        when(usuarioRepository.buscarPorId(1L)).thenReturn(Optional.of(usuario));

        mockMvc.perform(get("/usuarios/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("joao.silva"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveRetornar404QuandoUsuarioNaoEncontrado() throws Exception {
        when(usuarioRepository.buscarPorId(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/usuarios/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveAtualizarUsuario() throws Exception {
        when(usuarioRepository.existePorId(1L)).thenReturn(true);
        doNothing().when(userDetailsManager).updateUser(any());

        mockMvc.perform(put("/usuarios/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuario)))
                .andExpect(status().isOk());

        verify(userDetailsManager).updateUser(any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveExcluirUsuario() throws Exception {
        when(userDetailsManager.userExists("joao.silva")).thenReturn(true);
        doNothing().when(userDetailsManager).deleteUser("joao.silva");

        mockMvc.perform(delete("/usuarios/joao.silva").with(csrf()))
                .andExpect(status().isNoContent());

        verify(userDetailsManager).deleteUser("joao.silva");
    }
}
