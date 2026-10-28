
package dao.jdbc;

import dao.VagaDAO;
import model.Area;
import model.Modalidade;
import model.SituacaoVaga;
import model.Usuario;
import model.Vaga;
import model.Perfil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class VagaJDBCDAO implements VagaDAO {

    @Override
    public Vaga salvar(Vaga vaga) throws SQLException {
        String sql = """
            INSERT INTO vagas
            (empresa_id, titulo, descricao, area_id, localizacao,
             modalidade, carga_horaria_semanal, situacao)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            RETURNING id
            """;

        try (Connection conexao = ConnectionFactory.obterConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setLong(1, vaga.getEmpresaResponsavel().getId());
            stmt.setString(2, vaga.getTitulo());
            stmt.setString(3, vaga.getDescricao());
            stmt.setLong(4, vaga.getArea().getId());
            stmt.setString(5, vaga.getLocalizacao());
            stmt.setString(6, vaga.getModalidade().name());
            stmt.setInt(7, vaga.getCargaHorariaSemanal());
            stmt.setString(8, vaga.getSituacao().name());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return buscarPorId(rs.getLong("id"))
                            .orElseThrow(() -> new SQLException("Vaga não encontrada após salvar."));
                }
            }
        }

        throw new SQLException("Não foi possível cadastrar a vaga.");
    }

    @Override
    public Optional<Vaga> buscarPorId(Long id) throws SQLException {
        String sql = """
            SELECT v.*, a.nome AS area_nome,
                   u.nome AS usuario_nome, u.email AS usuario_email,
                   u.perfil AS usuario_perfil, u.curso AS usuario_curso,
                   u.nome_empresa
            FROM vagas v
            JOIN areas a ON a.id = v.area_id
            JOIN usuarios u ON u.id = v.empresa_id
            WHERE v.id = ?
            """;

        try (Connection conexao = ConnectionFactory.obterConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearVaga(rs));
                }
            }
        }

        return Optional.empty();
    }

    @Override
    public List<Vaga> listarAbertas() throws SQLException {
        String sql = """
            SELECT v.*, a.nome AS area_nome,
                   u.nome AS usuario_nome, u.email AS usuario_email,
                   u.perfil AS usuario_perfil, u.curso AS usuario_curso,
                   u.nome_empresa
            FROM vagas v
            JOIN areas a ON a.id = v.area_id
            JOIN usuarios u ON u.id = v.empresa_id
            WHERE v.situacao = 'ABERTA'
            ORDER BY v.id
            """;

        return consultarLista(sql, null, null);
    }

    @Override
    public List<Vaga> filtrarAbertas(Long areaId, Modalidade modalidade)
            throws SQLException {
        String sql = """
            SELECT v.*, a.nome AS area_nome,
                   u.nome AS usuario_nome, u.email AS usuario_email,
                   u.perfil AS usuario_perfil, u.curso AS usuario_curso,
                   u.nome_empresa
            FROM vagas v
            JOIN areas a ON a.id = v.area_id
            JOIN usuarios u ON u.id = v.empresa_id
            WHERE v.situacao = 'ABERTA'
              AND (? IS NULL OR v.area_id = ?)
              AND (? IS NULL OR v.modalidade = ?)
            ORDER BY v.id
            """;

        try (Connection conexao = ConnectionFactory.obterConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            if (areaId == null) {
                stmt.setNull(1, Types.BIGINT);
                stmt.setNull(2, Types.BIGINT);
            } else {
                stmt.setLong(1, areaId);
                stmt.setLong(2, areaId);
            }

            if (modalidade == null) {
                stmt.setNull(3, Types.VARCHAR);
                stmt.setNull(4, Types.VARCHAR);
            } else {
                stmt.setString(3, modalidade.name());
                stmt.setString(4, modalidade.name());
            }

            try (ResultSet rs = stmt.executeQuery()) {
                List<Vaga> vagas = new ArrayList<>();
                while (rs.next()) {
                    vagas.add(mapearVaga(rs));
                }
                return vagas;
            }
        }
    }

    @Override
    public List<Vaga> listarPorEmpresa(Long empresaId) throws SQLException {
        String sql = """
            SELECT v.*, a.nome AS area_nome,
                   u.nome AS usuario_nome, u.email AS usuario_email,
                   u.perfil AS usuario_perfil, u.curso AS usuario_curso,
                   u.nome_empresa
            FROM vagas v
            JOIN areas a ON a.id = v.area_id
            JOIN usuarios u ON u.id = v.empresa_id
            WHERE v.empresa_id = ?
            ORDER BY v.id
            """;

        return consultarLista(sql, empresaId, null);
    }

    @Override
    public boolean atualizar(Vaga vaga) throws SQLException {
        String sql = """
            UPDATE vagas
            SET titulo = ?, descricao = ?, area_id = ?, localizacao = ?,
                modalidade = ?, carga_horaria_semanal = ?
            WHERE id = ? AND empresa_id = ? AND situacao = 'ABERTA'
            """;

        try (Connection conexao = ConnectionFactory.obterConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setString(1, vaga.getTitulo());
            stmt.setString(2, vaga.getDescricao());
            stmt.setLong(3, vaga.getArea().getId());
            stmt.setString(4, vaga.getLocalizacao());
            stmt.setString(5, vaga.getModalidade().name());
            stmt.setInt(6, vaga.getCargaHorariaSemanal());
            stmt.setLong(7, vaga.getId());
            stmt.setLong(8, vaga.getEmpresaResponsavel().getId());

            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean encerrar(Long vagaId, Long empresaId) throws SQLException {
        String sql = """
            UPDATE vagas
            SET situacao = 'ENCERRADA'
            WHERE id = ? AND empresa_id = ? AND situacao = 'ABERTA'
            """;

        try (Connection conexao = ConnectionFactory.obterConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setLong(1, vagaId);
            stmt.setLong(2, empresaId);

            return stmt.executeUpdate() > 0;
        }
    }

    private List<Vaga> consultarLista(String sql, Long empresaId, Long id) throws SQLException {
        List<Vaga> vagas = new ArrayList<>();

        try (Connection conexao = ConnectionFactory.obterConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            if (empresaId != null) {
                stmt.setLong(1, empresaId);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    vagas.add(mapearVaga(rs));
                }
            }
        }

        return vagas;
    }

    private Vaga mapearVaga(ResultSet rs) throws SQLException {
        Usuario empresa = new Usuario(
            rs.getLong("empresa_id"),
            rs.getString("usuario_nome"),
            rs.getString("usuario_email"),
            Perfil.valueOf(rs.getString("usuario_perfil")),
            rs.getString("usuario_curso"),
            rs.getString("nome_empresa")
        );

        Area area = new Area(
            rs.getLong("area_id"),
            rs.getString("area_nome")
        );

        return new Vaga(
            rs.getLong("id"),
            empresa,
            rs.getString("titulo"),
            rs.getString("descricao"),
            area,
            rs.getString("localizacao"),
            Modalidade.valueOf(rs.getString("modalidade")),
            rs.getInt("carga_horaria_semanal"),
            SituacaoVaga.valueOf(rs.getString("situacao"))
        );
    }
}
