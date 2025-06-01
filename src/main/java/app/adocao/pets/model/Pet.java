package app.adocao.pets.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
public class Pet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String nome;

    @ManyToOne
    @JoinColumn(name = "raca_id")
    private Raca raca;

    private int idade;

    @Lob
    private byte[] foto;

    private String descricao;

    private boolean adotado;

    @ManyToOne
    @JoinColumn(name = "adotante_id")
    private Adotante adotante;

    @OneToOne(mappedBy = "pet", cascade = CascadeType.ALL, orphanRemoval = true)
    private Adocao adocao;

    // getters e setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Raca getRaca() {
        return raca;
    }

    public void setRaca(Raca raca) {
        this.raca = raca;
    }

    public int getIdade() {
        return idade;
    }
    public void setIdade(int idade) {
        this.idade = idade;
    }
    public byte[] getFoto() {
        return foto;
    }
    public void setFoto(byte[] foto) {
        this.foto = foto;
    }
    public String getDescricao() {
        return descricao;
    }
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
    public boolean isAdotado() {
        return adotado;
    }
    public void setAdotado(boolean adotado) {
        this.adotado = adotado;
    }
    public Adotante getAdotante() {
        return adotante;
    }
    public void setAdotante(Adotante adotante) {
        this.adotante = adotante;
    }
    public Especie getEspecie() {
        return this.raca != null ? this.raca.getEspecie() : null;
    }
    public void setAdocao(Adocao adocao) {
        this.adocao = adocao;
    }
}