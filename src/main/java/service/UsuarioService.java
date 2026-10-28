
package service;

import dao.UsuarioDAO;
import model.Perfil;
import model.Usuario;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class UsuarioService {

    private final UsuarioDAO usuarioDAO;

    public UsuarioService(UsuarioDAO usuarioDAO) {
        this.usuarioDAO = usuarioDAO;
    }

    public Usuario cadastrar(Usuario usuario) throws SQLException {
        if (usuario == null) {
            throw new IllegalArgumentException("Usuário obrigatório.");
        }

        if (usuario.getNome() == null || usuario.getNome().isBlank()) {
            throw new IllegalArgumentException("Informe o nome.");
        }

        if (usuario.getEmail() == null || usuario.getEmail().isBlank()) {
            throw new IllegalArgumentException("Informe o e-mail.");
        }

        if (usuario.getPerfil() == null) {
            throw new IllegalArgumentException("Informe o perfil.");
        }

        if (usuarioDAO.buscarPorEmail(usuario.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Este e-mail já está cadastrado.");
        }

        if (usuario.getPerfil() == Perfil.ESTUDANTE
                && (usuario.getCurso() == null || usuario.getCurso().isBlank())) {
            throw new IllegalArgumentException("Informe o curso do estudante.");
        }

        if (usuario.getPerfil() == Perfil.EMPRESA
                && (usuario.getNomeEmpresa() == null
                || usuario.getNomeEmpresa().isBlank())) {
            throw new IllegalArgumentException("Informe o nome da empresa.");
        }

        return usuarioDAO.salvar(usuario);
    }

    public Optional<Usuario> buscarPorId(Long id) throws SQLException {
        return usuarioDAO.buscarPorId(id);
    }

    public List<Usuario> listarTodos() throws SQLException {
        return usuarioDAO.listarTodos();
    }

    public boolean atualizar(Usuario usuario) throws SQLException {
        if (usuario == null || usuario.getId() == null) {
            throw new IllegalArgumentException("Usuário e ID são obrigatórios.");
        }

        if (usuario.getNome() == null || usuario.getNome().isBlank()
                || usuario.getEmail() == null || usuario.getEmail().isBlank()) {
            throw new IllegalArgumentException("Nome e e-mail são obrigatórios.");
        }

        Optional<Usuario> existente = usuarioDAO.buscarPorEmail(usuario.getEmail());

        if (existente.isPresent()
                && !existente.get().getId().equals(usuario.getId())) {
            throw new IllegalArgumentException("Este e-mail já pertence a outro usuário.");
        }

        return usuarioDAO.atualizar(usuario);
    }

    public boolean excluir(Long id) throws SQLException {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID inválido.");
        }

        return usuarioDAO.excluir(id);
    }
}
