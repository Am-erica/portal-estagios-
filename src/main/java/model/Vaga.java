
package model;

public class Vaga {

    private Long id;
    private Usuario empresaResponsavel;
    private String titulo;
    private String descricao;
    private Area area;
    private String localizacao;
    private Modalidade modalidade;
    private int cargaHorariaSemanal;
    private SituacaoVaga situacao;

    public Vaga(Long id, Usuario empresaResponsavel,
                String titulo, String descricao, Area area,
                String localizacao, Modalidade modalidade,
                int cargaHorariaSemanal, SituacaoVaga situacao) {

        this.id = id;
        this.empresaResponsavel = empresaResponsavel;
        this.titulo = titulo;
        this.descricao = descricao;
        this.area = area;
        this.localizacao = localizacao;
        this.modalidade = modalidade;
        this.cargaHorariaSemanal = cargaHorariaSemanal;
        this.situacao = situacao;
    }

    public Long getId() {
        return id;
    }

    public Usuario getEmpresaResponsavel() {
        return empresaResponsavel;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public Area getArea() {
        return area;
    }

    public String getLocalizacao() {
        return localizacao;
    }

    public Modalidade getModalidade() {
        return modalidade;
    }

    public int getCargaHorariaSemanal() {
        return cargaHorariaSemanal;
    }

    public SituacaoVaga getSituacao() {
        return situacao;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public void setArea(Area area) {
        this.area = area;
    }

    public void setLocalizacao(String localizacao) {
        this.localizacao = localizacao;
    }

    public void setModalidade(Modalidade modalidade) {
        this.modalidade = modalidade;
    }

    public void setCargaHorariaSemanal(int cargaHorariaSemanal) {
        this.cargaHorariaSemanal = cargaHorariaSemanal;
    }

    public void setSituacao(SituacaoVaga situacao) {
        this.situacao = situacao;
    }

    @Override
    public String toString() {
        return "Vaga{" +
                "id=" + id +
                ", titulo='" + titulo + '\'' +
                ", empresa=" + empresaResponsavel.getNomeEmpresa() +
                ", area=" + area.getNome() +
                ", modalidade=" + modalidade +
                ", cargaHorariaSemanal=" + cargaHorariaSemanal +
                ", situacao=" + situacao +
                '}';
    }
}
