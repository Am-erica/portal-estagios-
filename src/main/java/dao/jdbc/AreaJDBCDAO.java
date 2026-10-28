
package dao.jdbc;

import dao.AreaDAO;
import model.Area;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AreaJDBCDAO implements AreaDAO {

    @Override
    public Area salvar(Area area) throws SQLException {
        String sql = "INSERT INTO areas (nome) VALUES (?) RETURNING id";

        try (Connection conexao = ConnectionFactory.obterConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setString(1, area.getNome());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Area(rs.getLong("id"), area.getNome());
                }
            }
        }

        throw new SQLException("Não foi possível cadastrar a área.");
    }

    @Override
    public Optional<Area> buscarPorId(Long id) throws SQLException {
        String sql = "SELECT id, nome FROM areas WHERE id = ?";

        try (Connection conexao = ConnectionFactory.obterConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Area(
                        rs.getLong("id"),
                        rs.getString("nome")
                    ));
                }
            }
        }

        return Optional.empty();
    }

    @Override
    public List<Area> listarTodas() throws SQLException {
        String sql = "SELECT id, nome FROM areas ORDER BY nome";
        List<Area> areas = new ArrayList<>();

        try (Connection conexao = ConnectionFactory.obterConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                areas.add(new Area(rs.getLong("id"), rs.getString("nome")));
            }
        }

        return areas;
    }

    @Override
    public boolean atualizar(Area area) throws SQLException {
        String sql = "UPDATE areas SET nome = ? WHERE id = ?";

        try (Connection conexao = ConnectionFactory.obterConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setString(1, area.getNome());
            stmt.setLong(2, area.getId());

            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean excluir(Long id) throws SQLException {
        String sql = "DELETE FROM areas WHERE id = ?";

        try (Connection conexao = ConnectionFactory.obterConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setLong(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean possuiVagasVinculadas(Long id) throws SQLException {
        String sql = "SELECT EXISTS (SELECT 1 FROM vagas WHERE area_id = ?)";

        try (Connection conexao = ConnectionFactory.obterConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                return rs.getBoolean(1);
            }
        }
    }
}
