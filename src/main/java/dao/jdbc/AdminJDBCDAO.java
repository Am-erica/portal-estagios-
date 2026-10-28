
package dao.jdbc;

import dao.AdminDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

public class AdminJDBCDAO implements AdminDAO {

    @Override
    public Map<String, Long> obterContagens() throws SQLException {
        Map<String, Long> contagens = new LinkedHashMap<>();

        String sql = """
            SELECT 'Estudantes' AS categoria, COUNT(*) AS total
            FROM usuarios WHERE perfil = 'ESTUDANTE'
            UNION ALL
            SELECT 'Empresas', COUNT(*)
            FROM usuarios WHERE perfil = 'EMPRESA'
            UNION ALL
            SELECT 'Vagas abertas', COUNT(*)
            FROM vagas WHERE situacao = 'ABERTA'
            UNION ALL
            SELECT 'Vagas encerradas', COUNT(*)
            FROM vagas WHERE situacao = 'ENCERRADA'
            UNION ALL
            SELECT 'Candidaturas', COUNT(*)
            FROM candidaturas
            """;

        try (Connection conexao = ConnectionFactory.obterConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                contagens.put(
                    rs.getString("categoria"),
                    rs.getLong("total")
                );
            }
        }

        return contagens;
    }
}
