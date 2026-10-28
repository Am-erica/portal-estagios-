
package dao.jdbc;

import dao.UsuarioDAO;
import model.Perfil;
import model.Usuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UsuarioJDBCDAO implements UsuarioDAO {

    @Override
    public Usuario salvar(Usuario usuario) throws SQLException {
        String sql = """
            INSERT INTO usuarios (nome, email, perfil, curso, nome_empresa)
            VALUES (?, ?, ?, ?, ?)
            RETURNING id
            """;

        try (Connection conexao = ConnectionFactory.obterConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getEmail());
            stmt.setString(3, usuario.getPerfil().name());
            stmt.setString(4, usuario.getCurso());
            stmt.setString(5, usuario.getNomeEmpresa());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Usuario(
                        rs.getLong("id"),
                        usuario.getNome(),
                        usuario.getEmail(),
                        usuario.getPerfil(),
                        usuario.getCurso(),
                        usuario.getNomeEmpresa()
                    );
                }
            }
        }

        throw new SQLException("Não foi possível cadastrar o usuário.");
    }

    @Override
    public Optional<Usuario> buscarPorId(Long id) throws SQLException {
        String sql = "SELECT * FROM usuarios WHERE id = ?";

        try (Connection conexao = ConnectionFactory.obterConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearUsuario(rs));
                }
            }
        }

        return Optional.empty();
    }

    @Override
    public Optional<Usuario> buscarPorEmail(String email) throws SQLException {
        String sql = "SELECT * FROM usuarios WHERE email = ?";

        try (Connection conexao = ConnectionFactory.obterConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, email);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearUsuario(rs));
                }
            }
        }

        return Optional.empty();
    }

    @Override
    public List<Usuario> listarTodos() throws SQLException {
        String sql = "SELECT * FROM usuarios ORDER BY id";
        List<Usuario> usuarios = new ArrayList<>();

        try (Connection conexao = ConnectionFactory.obterConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                usuarios.add(mapearUsuario(rs));
            }
        }

        return usuarios;
    }

    @Override
    public boolean atualizar(Usuario usuario) throws SQLException {
        String sql = """
            UPDATE usuarios
            SET nome = ?, email = ?, curso = ?, nome_empresa = ?
            WHERE id = ?
            """;

        try (Connection conexao = ConnectionFactory.obterConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getEmail());
            stmt.setString(3, usuario.getCurso());
            stmt.setString(4, usuario.getNomeEmpresa());
            stmt.setLong(5, usuario.getId());

            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean excluir(Long id) throws SQLException {
        String sql = "DELETE FROM usuarios WHERE id = ?";

        try (Connection conexao = ConnectionFactory.obterConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setLong(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    private Usuario mapearUsuario(ResultSet rs) throws SQLException {
        return new Usuario(
            rs.getLong("id"),
            rs.getString("nome"),
            rs.getString("email"),
            Perfil.valueOf(rs.getString("perfil")),
            rs.getString("curso"),
            rs.getString("nome_empresa")
        );
    }
}
