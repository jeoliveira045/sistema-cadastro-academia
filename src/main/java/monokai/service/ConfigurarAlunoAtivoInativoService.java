package monokai.service;

import lombok.RequiredArgsConstructor;
import monokai.model.Aluno;
import monokai.model.Situacao;
import monokai.repository.AlunoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ConfigurarAlunoAtivoInativoService {

    private final AlunoRepository alunoRepository;

    public Aluno exec(Long id) {
        Aluno aluno = alunoRepository.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Aluno não encontrado."));

        aluno.setSituacao(aluno.getSituacao() == Situacao.ATIVO ? Situacao.INATIVO : Situacao.ATIVO);
        aluno.setDataAlteracao(LocalDateTime.now());

        return alunoRepository.salvar(aluno);
    }
}
