package monokai.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import monokai.model.Aluno;
import monokai.model.Professor;
import monokai.model.Situacao;
import monokai.repository.AlunoRepository;
import monokai.repository.ProfessorRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class CadastrarUsuarioService {

    private final AlunoRepository alunoRepository;
    private final ProfessorRepository professorRepository;

    public Aluno exec(Aluno aluno) {
        log.info("Verificando a existencia do aluno na base de dados");
        if (verificarAlunoExistente(aluno.getCpf(), aluno.getEmail())) {
            log.error("Aluno já cadastrado no banco de dados");
            throw new IllegalArgumentException("Aluno já cadastrado com o CPF ou e-mail informado.");
        }
        aluno.setDataCriacao(LocalDateTime.now());
        aluno.setSituacao(Situacao.ATIVO);
        log.info("Aluno salvo na base de dados");
        return alunoRepository.salvar(aluno);
    }

    public Professor exec(Professor professor) {
        log.info("Criando professor na base de dados");
        professor.setDataCriacao(LocalDateTime.now());
        if(professor.getId() != null) professor.setDataAlteracao(LocalDateTime.now());
        professor.setSituacao(Situacao.ATIVO);
        log.info("Professor salvo na base de dados");
        return professorRepository.salvar(professor);
    }

    private boolean verificarAlunoExistente(String cpf, String email) {
        return alunoRepository.listarTodos().stream()
                .anyMatch(a -> a.getCpf().equals(cpf) || a.getEmail().equals(email));
    }
}
