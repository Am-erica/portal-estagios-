
package dao;

import model.Usuario;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface UsuarioDAO {
    Usuario salvar(Usuario usuario) throws SQLException;

    Optional<Usuario> buscarPorId(Long id) throws SQLException;

    Optional<Usuario> buscarPorEmail(String email) throws SQLException;

    List<Usuario> listarTodos() throws SQLException;

    boolean atualizar(Usuario usuario) throws SQLException;

    boolean excluir(Long id) throws SQLException;
}
