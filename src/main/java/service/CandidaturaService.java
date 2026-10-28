
package service;

import dao.CandidaturaDAO;
import dao.UsuarioDAO;
import dao.VagaDAO;
import model.Candidatura;
import model.Perfil;
import model.SituacaoVaga;
import model.Usuario;
import model.Vaga;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class CandidaturaService {

    private final CandidaturaDAO candidaturaDAO;
    private final UsuarioDAO usuarioDAO;
    private final VagaDAO vagaDAO;

    public CandidaturaService(
            CandidaturaDAO candidaturaDAO,
            UsuarioDAO usuarioDAO,
            VagaDAO vagaDAO) {
        this.candidaturaDAO = candidaturaDAO;
        this.usuarioDAO = usuarioDAO;
        this.vagaDAO = vagaDAO;
    }

    public Candidatura candidatar(Long estudanteId, Long vagaId)
            throws SQLException {

        Usuario estudante = usuarioDAO.buscarPorId(estudanteId)
            .orElseThrow(() ->
                new IllegalArgumentException("Estudante não encontrado."));

        if (estudante.getPerfil() != Perfil.ESTUDANTE) {
            throw new IllegalArgumentException(
                "Somente estudantes podem se candidatar.");
        }

        Vaga vaga = vagaDAO.buscarPorId(vagaId)
            .orElseThrow(() ->
                new IllegalArgumentException("Vaga não encontrada."));

        if (vaga.getSituacao() != SituacaoVaga.ABERTA) {
            throw new IllegalArgumentException(
                "Não é possível se candidatar a uma vaga encerrada.");
        }

        if (candidaturaDAO.existeCandidatura(estudanteId, vagaId)) {
            throw new IllegalArgumentException(
                "Você já se candidatou a esta vaga.");
        }

        Candidatura candidatura = new Candidatura(
            null, estudante, vaga, LocalDateTime.now());

        return candidaturaDAO.salvar(candidatura);
    }

    public List<Candidatura> listarDoEstudante(Long estudanteId)
            throws SQLException {
        return candidaturaDAO.listarPorEstudante(estudanteId);
    }

    public List<Candidatura> listarCandidatosDaVaga(
            Long vagaId, Long empresaId) throws SQLException {

        Vaga vaga = vagaDAO.buscarPorId(vagaId)
            .orElseThrow(() ->
                new IllegalArgumentException("Vaga não encontrada."));

        if (!vaga.getEmpresaResponsavel().getId().equals(empresaId)) {
            throw new IllegalArgumentException(
                "Você só pode consultar candidatos das suas próprias vagas.");
        }

        return candidaturaDAO.listarPorVaga(vagaId);
    }

    public long contarTodas() throws SQLException {
        return candidaturaDAO.contarTodas();
    }
}
