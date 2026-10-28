
package service;

import dao.UsuarioDAO;
import model.Usuario;

import java.sql.SQLException;
import java.util.List;

public class SessaoService {

    private final UsuarioDAO usuarioDAO;

    public SessaoService(UsuarioDAO usuarioDAO) {
        this.usuarioDAO = usuarioDAO;
    }

    public List<Usuario> listarUsuariosDisponiveis()
            throws SQLException {
        return usuarioDAO.listarTodos();
    }

    public Usuario selecionarUsuario(Long id)
            throws SQLException {
        return usuarioDAO.buscarPorId(id)
            .orElseThrow(() ->
                new IllegalArgumentException(
                    "Usuário não encontrado. Selecione um ID válido."));
    }
}
