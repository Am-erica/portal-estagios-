
package service;

import dao.AreaDAO;
import model.Area;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class AreaService {

    private final AreaDAO areaDAO;

    public AreaService(AreaDAO areaDAO) {
        this.areaDAO = areaDAO;
    }

    public Area cadastrar(String nome) throws SQLException {
        validarNome(nome);

        boolean duplicada = areaDAO.listarTodas().stream()
                .anyMatch(area -> area.getNome().equalsIgnoreCase(nome.trim()));

        if (duplicada) {
            throw new IllegalArgumentException("Esta área já está cadastrada.");
        }

        return areaDAO.salvar(new Area(null, nome.trim()));
    }

    public List<Area> listarTodas() throws SQLException {
        return areaDAO.listarTodas();
    }

    public Optional<Area> buscarPorId(Long id) throws SQLException {
        return areaDAO.buscarPorId(id);
    }

    public boolean atualizar(Long id, String novoNome) throws SQLException {
        validarNome(novoNome);

        Area area = areaDAO.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Área não encontrada."));

        boolean duplicada = areaDAO.listarTodas().stream()
                .anyMatch(a -> !a.getId().equals(id)
                        && a.getNome().equalsIgnoreCase(novoNome.trim()));

        if (duplicada) {
            throw new IllegalArgumentException("Já existe outra área com esse nome.");
        }

        area.setNome(novoNome.trim());
        return areaDAO.atualizar(area);
    }

    public boolean excluir(Long id) throws SQLException {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID da área inválido.");
        }

        if (areaDAO.possuiVagasVinculadas(id)) {
            throw new IllegalArgumentException(
                    "Não é possível excluir uma área vinculada a vagas.");
        }

        return areaDAO.excluir(id);
    }

    private void validarNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("O nome da área é obrigatório.");
        }
    }
}
