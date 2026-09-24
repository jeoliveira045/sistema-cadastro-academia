package monokai.service;

import monokai.model.Aluno;
import monokai.model.Professor;
import monokai.model.Situacao;
import monokai.repository.AlunoRepository;
import monokai.repository.ProfessorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CadastrarUsuarioServiceTest {

    @Mock
    private AlunoRepository alunoRepository;

    @Mock
    private ProfessorRepository professorRepository;

    @InjectMocks
    private CadastrarUsuarioService service;

    private Aluno aluno;
    private Professor professor;

    @BeforeEach
    void setUp() {
        aluno = new Aluno();
        aluno.setNome("Carlos");
        aluno.setSobrenome("Oliveira");
        aluno.setCpf("123.456.789-00");
        aluno.setEmail("carlos@email.com");

        professor = new Professor();
        professor.setNome("Ana");
        professor.setSobrenome("Ferreira");
        professor.setCpf("987.654.321-00");
        professor.setEmail("ana@email.com");
        professor.setCref("012345-G/SP");
    }

    @Test
    void deveCadastrarAlunoComSucesso() {
        when(alunoRepository.listarTodos()).thenReturn(List.of());
        when(alunoRepository.salvar(aluno)).thenReturn(aluno);

        Aluno resultado = service.exec(aluno);

        assertThat(resultado.getSituacao()).isEqualTo(Situacao.ATIVO);
        assertThat(resultado.getDataCriacao()).isNotNull();
        verify(alunoRepository).salvar(aluno);
    }

    @Test
    void deveLancarExcecaoQuandoAlunoJaExistePorCpf() {
        Aluno existente = new Aluno();
        existente.setCpf("123.456.789-00");
        existente.setEmail("outro@email.com");
        when(alunoRepository.listarTodos()).thenReturn(List.of(existente));

        assertThatThrownBy(() -> service.exec(aluno))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("CPF ou e-mail");
    }

    @Test
    void deveLancarExcecaoQuandoAlunoJaExistePorEmail() {
        Aluno existente = new Aluno();
        existente.setCpf("000.000.000-00");
        existente.setEmail("carlos@email.com");
        when(alunoRepository.listarTodos()).thenReturn(List.of(existente));

        assertThatThrownBy(() -> service.exec(aluno))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("CPF ou e-mail");
    }

    @Test
    void deveCadastrarProfessorComSucesso() {
        when(professorRepository.salvar(professor)).thenReturn(professor);

        Professor resultado = service.exec(professor);

        assertThat(resultado.getSituacao()).isEqualTo(Situacao.ATIVO);
        assertThat(resultado.getDataCriacao()).isNotNull();
        verify(professorRepository).salvar(professor);
    }
}
