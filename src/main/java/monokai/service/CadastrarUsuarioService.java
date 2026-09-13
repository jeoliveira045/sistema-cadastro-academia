package monokai.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import monokai.model.Aluno;
import monokai.model.Professor;
import monokai.model.Situacao;
import monokai.repository.AlunoRepository;
import monokai.repository.ProfessorRepository;

@Service
@RequiredArgsConstructor
public class CadastrarUsuarioService {

    private final AlunoRepository alunoRepository;
    private final ProfessorRepository professorRepository;

    public Aluno exec(Aluno aluno) {
        if (verificarAlunoExistente(aluno.getCpf(), aluno.getEmail())) {
            throw new IllegalArgumentException("Aluno já cadastrado com o CPF ou e-mail informado.");
        }
        aluno.setDataCriacao(LocalDateTime.now());
        aluno.setSituacao(Situacao.ATIVO);
        return alunoRepository.save(aluno);
    }

    public Professor exec(Professor professor) {
        professor.setDataCriacao(LocalDateTime.now());
        if(professor.getId() != null) professor.setDataAlteracao(LocalDateTime.now());
        professor.setSituacao(Situacao.ATIVO);
        return professorRepository.save(professor);
    }

    private boolean verificarAlunoExistente(String cpf, String email) {
        return alunoRepository.findAll().stream()
                .anyMatch(a -> a.getCpf().equals(cpf) || a.getEmail().equals(email));
    }
}
