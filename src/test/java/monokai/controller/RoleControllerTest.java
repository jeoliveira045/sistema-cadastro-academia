package monokai.controller;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
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
import monokai.repository.RoleRepository;

@WebMvcTest(RoleController.class)
class RoleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // @Autowired
    private ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private RoleRepository roleRepository;

    private Role role;

    @BeforeEach
    void setUp() {
        role = new Role();
        role.setId(1L);
        role.setRole("ADMIN");
        role.setDescRole("Acesso total ao sistema");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveCriarRole() throws Exception {
        when(roleRepository.salvar(any(Role.class))).thenReturn(role);

        mockMvc.perform(post("/roles")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(role)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("ADMIN"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveListarRoles() throws Exception {
        when(roleRepository.listarTodos()).thenReturn(List.of(role));

        mockMvc.perform(get("/roles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].descRole").value("Acesso total ao sistema"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveBuscarRolePorId() throws Exception {
        when(roleRepository.buscarPorId(1L)).thenReturn(Optional.of(role));

        mockMvc.perform(get("/roles/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("ADMIN"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveRetornar404QuandoRoleNaoEncontrada() throws Exception {
        when(roleRepository.buscarPorId(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/roles/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveAtualizarRole() throws Exception {
        when(roleRepository.existePorId(1L)).thenReturn(true);
        when(roleRepository.salvar(any(Role.class))).thenReturn(role);

        mockMvc.perform(put("/roles/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(role)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("ADMIN"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveExcluirRole() throws Exception {
        when(roleRepository.existePorId(1L)).thenReturn(true);

        mockMvc.perform(delete("/roles/1").with(csrf()))
                .andExpect(status().isNoContent());

        verify(roleRepository).excluir(1L);
    }
}
