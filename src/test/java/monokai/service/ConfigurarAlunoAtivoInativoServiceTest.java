package monokai.service;

import monokai.model.Aluno;
import monokai.model.Situacao;
import monokai.repository.AlunoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConfigurarAlunoAtivoInativoServiceTest {

    @Mock
    private AlunoRepository alunoRepository;

    @InjectMocks
    private ConfigurarAlunoAtivoInativoService service;

    @Test
    void deveAlternarDeAtivoParaInativo() {
        Aluno aluno = new Aluno();
        aluno.setId(1L);
        aluno.setSituacao(Situacao.ATIVO);
        when(alunoRepository.buscarPorId(1L)).thenReturn(Optional.of(aluno));
        when(alunoRepository.salvar(aluno)).thenReturn(aluno);

        Aluno resultado = service.exec(1L);

        assertThat(resultado.getSituacao()).isEqualTo(Situacao.INATIVO);
        assertThat(resultado.getDataAlteracao()).isNotNull();
    }

    @Test
    void deveAlternarDeInativoParaAtivo() {
        Aluno aluno = new Aluno();
        aluno.setId(1L);
        aluno.setSituacao(Situacao.INATIVO);
        when(alunoRepository.buscarPorId(1L)).thenReturn(Optional.of(aluno));
        when(alunoRepository.salvar(aluno)).thenReturn(aluno);

        Aluno resultado = service.exec(1L);

        assertThat(resultado.getSituacao()).isEqualTo(Situacao.ATIVO);
    }

    @Test
    void deveLancarExcecaoQuandoAlunoNaoEncontrado() {
        when(alunoRepository.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.exec(99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("não encontrado");
    }
}
