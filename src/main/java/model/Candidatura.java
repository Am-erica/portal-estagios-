
package model;

import java.time.LocalDateTime;

public class Candidatura {

    private Long id;
    private Usuario estudante;
    private Vaga vaga;
    private LocalDateTime dataInscricao;

    public Candidatura(Long id, Usuario estudante,
                       Vaga vaga, LocalDateTime dataInscricao) {

        this.id = id;
        this.estudante = estudante;
        this.vaga = vaga;
        this.dataInscricao = dataInscricao;
    }

    public Long getId() {
        return id;
    }

    public Usuario getEstudante() {
        return estudante;
    }

    public Vaga getVaga() {
        return vaga;
    }

    public LocalDateTime getDataInscricao() {
        return dataInscricao;
    }

    @Override
    public String toString() {
        return "Candidatura{" +
                "id=" + id +
                ", estudante=" + estudante.getNome() +
                ", vaga=" + vaga.getTitulo() +
                ", dataInscricao=" + dataInscricao +
                '}';
    }
}
