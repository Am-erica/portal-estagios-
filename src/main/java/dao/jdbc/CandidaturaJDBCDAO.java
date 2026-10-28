
package dao.jdbc;

import dao.CandidaturaDAO;
import model.Candidatura;
import model.Usuario;
import model.Vaga;
import model.Area;
import model.Perfil;
import model.Modalidade;
import model.SituacaoVaga;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CandidaturaJDBCDAO implements CandidaturaDAO {

    @Override
    public Candidatura salvar(Candidatura candidatura)
            throws SQLException {

        String sql = """
            INSERT INTO candidaturas
                (estudante_id, vaga_id, data_inscricao)
            VALUES (?, ?, ?)
            RETURNING id, data_inscricao
            """;

        try (Connection conexao = ConnectionFactory.obterConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setLong(1, candidatura.getEstudante().getId());
            stmt.setLong(2, candidatura.getVaga().getId());

            if (candidatura.getDataInscricao() == null) {
                stmt.setTimestamp(3, null);
            } else {
                stmt.setTimestamp(
                    3, Timestamp.valueOf(candidatura.getDataInscricao()));
            }

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    candidatura = new Candidatura(
                        rs.getLong("id"),
                        candidatura.getEstudante(),
                        candidatura.getVaga(),
                        rs.getTimestamp("data_inscricao")
                            .toLocalDateTime()
                    );
                    return candidatura;
                }
            }
        }

        throw new SQLException("Não foi possível salvar a candidatura.");
    }

    @Override
    public List<Candidatura> listarPorEstudante(Long estudanteId)
            throws SQLException {

        String sql = """
            SELECT c.id AS candidatura_id,
                   c.data_inscricao,
                   u.id AS estudante_id,
                   u.nome AS estudante_nome,
                   u.email AS estudante_email,
                   u.perfil AS estudante_perfil,
                   u.curso,
                   u.nome_empresa,
                   v.id AS vaga_id,
                   v.titulo,
                   v.descricao,
                   v.localizacao,
                   v.modalidade,
                   v.carga_horaria_semanal,
                   v.situacao,
                   e.id AS empresa_id,
                   e.nome AS empresa_nome,
                   e.email AS empresa_email,
                   e.perfil AS empresa_perfil,
                   e.curso AS empresa_curso,
                   e.nome_empresa AS empresa_nome_empresa,
                   a.id AS area_id,
                   a.nome AS area_nome
            FROM candidaturas c
            JOIN usuarios u ON u.id = c.estudante_id
            JOIN vagas v ON v.id = c.vaga_id
            JOIN usuarios e ON e.id = v.empresa_id
            JOIN areas a ON a.id = v.area_id
            WHERE c.estudante_id = ?
            ORDER BY c.data_inscricao DESC
            """;

        try (Connection conexao = ConnectionFactory.obterConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setLong(1, estudanteId);

            try (ResultSet rs = stmt.executeQuery()) {
                List<Candidatura> candidaturas = new ArrayList<>();

                while (rs.next()) {
                    candidaturas.add(mapearCandidatura(rs));
                }

                return candidaturas;
            }
        }
    }

    @Override
    public List<Candidatura> listarPorVaga(Long vagaId)
            throws SQLException {

        String sql = """
            SELECT c.id AS candidatura_id,
                   c.data_inscricao,
                   u.id AS estudante_id,
                   u.nome AS estudante_nome,
                   u.email AS estudante_email,
                   u.perfil AS estudante_perfil,
                   u.curso,
                   u.nome_empresa,
                   v.id AS vaga_id,
                   v.titulo,
                   v.descricao,
                   v.localizacao,
                   v.modalidade,
                   v.carga_horaria_semanal,
                   v.situacao,
                   e.id AS empresa_id,
                   e.nome AS empresa_nome,
                   e.email AS empresa_email,
                   e.perfil AS empresa_perfil,
                   e.curso AS empresa_curso,
                   e.nome_empresa AS empresa_nome_empresa,
                   a.id AS area_id,
                   a.nome AS area_nome
            FROM candidaturas c
            JOIN usuarios u ON u.id = c.estudante_id
            JOIN vagas v ON v.id = c.vaga_id
            JOIN usuarios e ON e.id = v.empresa_id
            JOIN areas a ON a.id = v.area_id
            WHERE c.vaga_id = ?
            ORDER BY c.data_inscricao DESC
            """;

        try (Connection conexao = ConnectionFactory.obterConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setLong(1, vagaId);

            try (ResultSet rs = stmt.executeQuery()) {
                List<Candidatura> candidaturas = new ArrayList<>();

                while (rs.next()) {
                    candidaturas.add(mapearCandidatura(rs));
                }

                return candidaturas;
            }
        }
    }

    @Override
    public boolean existeCandidatura(
            Long estudanteId, Long vagaId) throws SQLException {

        String sql = """
            SELECT EXISTS (
                SELECT 1
                FROM candidaturas
                WHERE estudante_id = ? AND vaga_id = ?
            )
            """;

        try (Connection conexao = ConnectionFactory.obterConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setLong(1, estudanteId);
            stmt.setLong(2, vagaId);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getBoolean(1);
            }
        }
    }

    @Override
    public long contarTodas() throws SQLException {

        String sql = "SELECT COUNT(*) FROM candidaturas";

        try (Connection conexao = ConnectionFactory.obterConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            rs.next();
            return rs.getLong(1);
        }
    }

    private Candidatura mapearCandidatura(ResultSet rs)
            throws SQLException {

        Usuario estudante = new Usuario(
            rs.getLong("estudante_id"),
            rs.getString("estudante_nome"),
            rs.getString("estudante_email"),
            Perfil.valueOf(rs.getString("estudante_perfil")),
            rs.getString("curso"),
            rs.getString("nome_empresa")
        );

        Usuario empresa = new Usuario(
            rs.getLong("empresa_id"),
            rs.getString("empresa_nome"),
            rs.getString("empresa_email"),
            Perfil.valueOf(rs.getString("empresa_perfil")),
            rs.getString("empresa_curso"),
            rs.getString("empresa_nome_empresa")
        );

        Area area = new Area(
            rs.getLong("area_id"),
            rs.getString("area_nome")
        );

        Vaga vaga = new Vaga(
            rs.getLong("vaga_id"),
            empresa,
            rs.getString("titulo"),
            rs.getString("descricao"),
            area,
            rs.getString("localizacao"),
            Modalidade.valueOf(rs.getString("modalidade")),
            rs.getInt("carga_horaria_semanal"),
            SituacaoVaga.valueOf(rs.getString("situacao"))
        );

        Timestamp timestamp = rs.getTimestamp("data_inscricao");
        LocalDateTime data = timestamp == null
            ? null
            : timestamp.toLocalDateTime();

        return new Candidatura(
            rs.getLong("candidatura_id"),
            estudante,
            vaga,
            data
        );
    }
}
