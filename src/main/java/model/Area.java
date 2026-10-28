
package model;

public class Area {

    private Long id;
    private String nome;

    public Area(Long id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    @Override
    public String toString() {
        return "Area{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                '}';
    }
}
