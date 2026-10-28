
package dao;

import model.Vaga;
import model.Modalidade;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface VagaDAO {

    Vaga salvar(Vaga vaga) throws SQLException;

    Optional<Vaga> buscarPorId(Long id) throws SQLException;

    List<Vaga> listarAbertas() throws SQLException;

    List<Vaga> filtrarAbertas(Long areaId, Modalidade modalidade)
            throws SQLException;

    List<Vaga> listarPorEmpresa(Long empresaId) throws SQLException;

    boolean atualizar(Vaga vaga) throws SQLException;

    boolean encerrar(Long vagaId, Long empresaId) throws SQLException;
}
