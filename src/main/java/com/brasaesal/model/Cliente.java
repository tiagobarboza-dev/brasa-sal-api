package com.brasaesal.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Pessoa atendida no restaurante. Vem da classe Cliente.java original.
 * No Java o cliente guardava só a comanda ativa; agora guarda todas
 * (relação 1:N), o que preserva o histórico.
 */
@Entity
@Table(name = "cliente")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false)
    private int mesa;

    // Lado inverso da relação (a chave estrangeira fica em comanda.cliente_id).
    // Sem cascade: apagar um cliente NÃO apaga suas comandas (histórico).
    @OneToMany(mappedBy = "cliente")
    private List<Comanda> comandas = new ArrayList<>();

    protected Cliente() {
        // exigido pelo JPA
    }

    public Cliente(String nome, int mesa) {
        this.nome = nome;
        this.mesa = mesa;
    }

    public Long getId() { return id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public int getMesa() { return mesa; }
    public void setMesa(int mesa) { this.mesa = mesa; }

    public List<Comanda> getComandas() { return Collections.unmodifiableList(comandas); }

    /** Mantém os dois lados da relação sincronizados. Chamado pelo construtor de Comanda. */
    void adicionarComanda(Comanda comanda) {
        this.comandas.add(comanda);
    }
}
