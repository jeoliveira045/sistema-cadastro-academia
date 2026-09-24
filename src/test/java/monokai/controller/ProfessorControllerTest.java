package monokai.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import monokai.model.Professor;
import monokai.model.Situacao;
import monokai.repository.ProfessorRepository;
import monokai.service.CadastrarUsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProfessorController.class)
class ProfessorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // @Autowired
    private ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private CadastrarUsuarioService cadastrarUsuarioService;

    @MockitoBean
    private ProfessorRepository professorRepository;

    private Professor professor;

    @BeforeEach
    void setUp() {
        professor = new Professor();
        professor.setId(1L);
        professor.setNome("Ana");
        professor.setSobrenome("Ferreira");
        professor.setCpf("987.654.321-00");
        professor.setEmail("ana@email.com");
        professor.setCref("012345-G/SP");
        professor.setSituacao(Situacao.ATIVO);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveCadastrarProfessor() throws Exception {
        when(cadastrarUsuarioService.exec(any(Professor.class))).thenReturn(professor);

        mockMvc.perform(post("/professores")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(professor)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cref").value("012345-G/SP"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveListarProfessores() throws Exception {
        when(professorRepository.listarTodos()).thenReturn(List.of(professor));

        mockMvc.perform(get("/professores"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("Ana"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveBuscarProfessorPorId() throws Exception {
        when(professorRepository.buscarPorId(1L)).thenReturn(Optional.of(professor));

        mockMvc.perform(get("/professores/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("ana@email.com"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveRetornar404QuandoProfessorNaoEncontrado() throws Exception {
        when(professorRepository.buscarPorId(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/professores/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveDeletarProfessor() throws Exception {
        when(professorRepository.buscarPorId(1L)).thenReturn(Optional.of(professor));

        mockMvc.perform(delete("/professores/1").with(csrf()))
                .andExpect(status().isNoContent());

        verify(professorRepository).excluir(1L);
    }
}
