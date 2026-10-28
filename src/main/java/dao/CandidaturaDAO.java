
package dao;

import model.Candidatura;
import java.sql.SQLException;
import java.util.List;

public interface CandidaturaDAO {

    Candidatura salvar(Candidatura candidatura) throws SQLException;

    List<Candidatura> listarPorEstudante(Long estudanteId) throws SQLException;

    List<Candidatura> listarPorVaga(Long vagaId) throws SQLException;

    boolean existeCandidatura(Long estudanteId, Long vagaId) throws SQLException;

    long contarTodas() throws SQLException;
}
