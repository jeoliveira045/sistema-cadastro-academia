package monokai.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import monokai.model.Aluno;
import monokai.model.Situacao;
import monokai.repository.AlunoRepository;
import monokai.service.CadastrarUsuarioService;
import monokai.service.ConfigurarAlunoAtivoInativoService;
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

@WebMvcTest(AlunoController.class)
class AlunoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // @Autowired
    private ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private CadastrarUsuarioService cadastrarUsuarioService;

    @MockitoBean
    private ConfigurarAlunoAtivoInativoService configurarAlunoAtivoInativoService;

    @MockitoBean
    private AlunoRepository alunoRepository;

    private Aluno aluno;

    @BeforeEach
    void setUp() {
        aluno = new Aluno();
        aluno.setId(1L);
        aluno.setNome("Carlos");
        aluno.setSobrenome("Oliveira");
        aluno.setCpf("123.456.789-00");
        aluno.setEmail("carlos@email.com");
        aluno.setSituacao(Situacao.ATIVO);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveCadastrarAluno() throws Exception {
        when(cadastrarUsuarioService.exec(any(Aluno.class))).thenReturn(aluno);

        mockMvc.perform(post("/alunos")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(aluno)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Carlos"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveListarAlunos() throws Exception {
        when(alunoRepository.listarTodos()).thenReturn(List.of(aluno));

        mockMvc.perform(get("/alunos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("Carlos"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveBuscarAlunoPorId() throws Exception {
        when(alunoRepository.buscarPorId(1L)).thenReturn(Optional.of(aluno));

        mockMvc.perform(get("/alunos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cpf").value("123.456.789-00"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveRetornar404QuandoAlunoNaoEncontrado() throws Exception {
        when(alunoRepository.buscarPorId(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/alunos/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveAlternarSituacaoDoAluno() throws Exception {
        aluno.setSituacao(Situacao.INATIVO);
        when(configurarAlunoAtivoInativoService.exec(1L)).thenReturn(aluno);

        mockMvc.perform(patch("/alunos/1/situacao").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.situacao").value("INATIVO"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveDeletarAluno() throws Exception {
        when(alunoRepository.buscarPorId(1L)).thenReturn(Optional.of(aluno));

        mockMvc.perform(delete("/alunos/1").with(csrf()))
                .andExpect(status().isNoContent());

        verify(alunoRepository).excluir(1L);
    }
}
