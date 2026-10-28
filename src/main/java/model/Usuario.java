
package model;

public class Usuario {

    private Long id;
    private String nome;
    private String email;
    private Perfil perfil;
    private String curso;
    private String nomeEmpresa;

    public Usuario(Long id, String nome, String email, Perfil perfil,
                   String curso, String nomeEmpresa) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.perfil = perfil;
        this.curso = curso;
        this.nomeEmpresa = nomeEmpresa;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public Perfil getPerfil() {
        return perfil;
    }

    public String getCurso() {
        return curso;
    }

    public String getNomeEmpresa() {
        return nomeEmpresa;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setCurso(String curso) {
        this.curso = curso;
    }

    public void setNomeEmpresa(String nomeEmpresa) {
        this.nomeEmpresa = nomeEmpresa;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", email='" + email + '\'' +
                ", perfil=" + perfil +
                '}';
    }
}
