
package dao;

import model.Area;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface AreaDAO {

    Area salvar(Area area) throws SQLException;

    Optional<Area> buscarPorId(Long id) throws SQLException;

    List<Area> listarTodas() throws SQLException;

    boolean atualizar(Area area) throws SQLException;

    boolean excluir(Long id) throws SQLException;

    boolean possuiVagasVinculadas(Long id) throws SQLException;
}
