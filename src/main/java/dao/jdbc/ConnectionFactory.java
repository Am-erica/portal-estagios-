
package dao.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {

    private static final String URL =
            "jdbc:postgresql://aws-0-us-east-1.pooler.supabase.com:5432/postgres";
    private static final String USUARIO = "postgres.dmuqkickdxdnjcpdtxoe";
    private static final String SENHA = "INSERIR-SENHA";

    public static Connection obterConexao() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, SENHA);
    }
}
