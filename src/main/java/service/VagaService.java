
package service;

import dao.VagaDAO;
import dao.AreaDAO;
import dao.UsuarioDAO;
import model.Area;
import model.Modalidade;
import model.Perfil;
import model.SituacaoVaga;
import model.Usuario;
import model.Vaga;

import java.sql.SQLException;
import java.util.List;

public class VagaService {

    private final VagaDAO vagaDAO;
    private final AreaDAO areaDAO;
    private final UsuarioDAO usuarioDAO;

    public VagaService(VagaDAO vagaDAO, AreaDAO areaDAO, UsuarioDAO usuarioDAO) {
        this.vagaDAO = vagaDAO;
        this.areaDAO = areaDAO;
        this.usuarioDAO = usuarioDAO;
    }

    public Vaga publicar(Vaga vaga) throws SQLException {
        validarDados(vaga);

        Usuario empresa = usuarioDAO.buscarPorId(
                vaga.getEmpresaResponsavel().getId())
                .orElseThrow(() -> new IllegalArgumentException("Empresa não encontrada."));

        if (empresa.getPerfil() != Perfil.EMPRESA) {
            throw new IllegalArgumentException("Somente empresas podem publicar vagas.");
        }

        Area area = areaDAO.buscarPorId(vaga.getArea().getId())
                .orElseThrow(() -> new IllegalArgumentException("Área não encontrada."));

        vaga.setSituacao(SituacaoVaga.ABERTA);

        Vaga vagaValidada = new Vaga(
                null, empresa, vaga.getTitulo().trim(), vaga.getDescricao().trim(),
                area, vaga.getLocalizacao().trim(), vaga.getModalidade(),
                vaga.getCargaHorariaSemanal(), SituacaoVaga.ABERTA);

        return vagaDAO.salvar(vagaValidada);
    }

    public List<Vaga> listarAbertas() throws SQLException {
        return vagaDAO.listarAbertas();
    }

    public List<Vaga> filtrarAbertas(Long areaId, Modalidade modalidade)
            throws SQLException {
        if (areaId != null && areaDAO.buscarPorId(areaId).isEmpty()) {
            throw new IllegalArgumentException("Área não encontrada.");
        }

        return vagaDAO.filtrarAbertas(areaId, modalidade);
    }

    public List<Vaga> listarDaEmpresa(Long empresaId) throws SQLException {
        Usuario empresa = usuarioDAO.buscarPorId(empresaId)
                .orElseThrow(() -> new IllegalArgumentException("Empresa não encontrada."));

        if (empresa.getPerfil() != Perfil.EMPRESA) {
            throw new IllegalArgumentException("O usuário informado não é uma empresa.");
        }

        return vagaDAO.listarPorEmpresa(empresaId);
    }

    public boolean atualizar(Long vagaId, Long empresaId, Vaga dadosNovos)
            throws SQLException {
        if (vagaId == null || empresaId == null || dadosNovos == null) {
            throw new IllegalArgumentException("Dados da atualização são obrigatórios.");
        }

        Vaga existente = vagaDAO.buscarPorId(vagaId)
                .orElseThrow(() -> new IllegalArgumentException("Vaga não encontrada."));

        if (!existente.getEmpresaResponsavel().getId().equals(empresaId)) {
            throw new IllegalArgumentException("Você não pode editar esta vaga.");
        }

        if (existente.getSituacao() != SituacaoVaga.ABERTA) {
            throw new IllegalArgumentException("Não é possível editar uma vaga encerrada.");
        }

        validarDados(dadosNovos);

        Area area = areaDAO.buscarPorId(dadosNovos.getArea().getId())
                .orElseThrow(() -> new IllegalArgumentException("Área não encontrada."));

        Usuario empresa = existente.getEmpresaResponsavel();

        Vaga atualizada = new Vaga(
                vagaId, empresa, dadosNovos.getTitulo().trim(),
                dadosNovos.getDescricao().trim(), area,
                dadosNovos.getLocalizacao().trim(), dadosNovos.getModalidade(),
                dadosNovos.getCargaHorariaSemanal(), existente.getSituacao());

        return vagaDAO.atualizar(atualizada);
    }

    public boolean encerrar(Long vagaId, Long empresaId) throws SQLException {
        if (vagaId == null || empresaId == null) {
            throw new IllegalArgumentException("Informe a vaga e a empresa.");
        }

        Vaga vaga = vagaDAO.buscarPorId(vagaId)
                .orElseThrow(() -> new IllegalArgumentException("Vaga não encontrada."));

        if (!vaga.getEmpresaResponsavel().getId().equals(empresaId)) {
            throw new IllegalArgumentException("Você não pode encerrar esta vaga.");
        }

        if (vaga.getSituacao() == SituacaoVaga.ENCERRADA) {
            throw new IllegalArgumentException("Esta vaga já está encerrada.");
        }

        return vagaDAO.encerrar(vagaId, empresaId);
    }

    private void validarDados(Vaga vaga) {
        if (vaga == null) {
            throw new IllegalArgumentException("Os dados da vaga são obrigatórios.");
        }

        if (vaga.getEmpresaResponsavel() == null
                || vaga.getEmpresaResponsavel().getId() == null) {
            throw new IllegalArgumentException("Informe a empresa responsável.");
        }

        if (vaga.getTitulo() == null || vaga.getTitulo().isBlank()) {
            throw new IllegalArgumentException("Informe o título da vaga.");
        }

        if (vaga.getDescricao() == null || vaga.getDescricao().isBlank()) {
            throw new IllegalArgumentException("Informe a descrição da vaga.");
        }

        if (vaga.getArea() == null || vaga.getArea().getId() == null) {
            throw new IllegalArgumentException("Informe a área da vaga.");
        }

        if (vaga.getLocalizacao() == null || vaga.getLocalizacao().isBlank()) {
            throw new IllegalArgumentException("Informe a localização da vaga.");
        }

        if (vaga.getModalidade() == null) {
            throw new IllegalArgumentException("Informe a modalidade da vaga.");
        }

        if (vaga.getCargaHorariaSemanal() <= 0) {
            throw new IllegalArgumentException("A carga horária deve ser positiva.");
        }
    }
}
