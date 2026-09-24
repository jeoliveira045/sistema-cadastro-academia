package monokai.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import monokai.model.Aluno;
import monokai.model.Situacao;
import monokai.repository.AlunoRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConfigurarAlunoAtivoInativoService {

    private final AlunoRepository alunoRepository;

    public Aluno exec(Long id) {
        log.info("Buscando aluno na base de dados.");
        Aluno aluno = alunoRepository.buscarPorId(id)
                .orElseThrow(() -> {
                    log.error("Aluno não encontrado.");
                    return new IllegalArgumentException("Aluno não encontrado.");
                });

        var situacao = aluno.getSituacao() == Situacao.ATIVO ? Situacao.INATIVO : Situacao.ATIVO;
        var situacaoString = aluno.getSituacao() == Situacao.ATIVO ? "ATIVO" : "INATIVO";
        log.info("Modificando situação do usuario para " + situacaoString);
        aluno.setSituacao(situacao);
        aluno.setDataAlteracao(LocalDateTime.now());

        return alunoRepository.salvar(aluno);
    }
}
