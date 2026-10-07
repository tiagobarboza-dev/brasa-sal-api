package com.brasaesal.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

/**
 * Item do cardápio. Vem da classe Produto.java original (nome + preço),
 * agora com id, categoria e disponibilidade (usados pelo front-end).
 */
@Entity
@Table(name = "produto")
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Único: no Java original o produto era identificado pelo nome.
    @Column(nullable = false, unique = true, length = 100)
    private String nome;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal preco;

    // Texto livre (ex.: "Bebidas", "Pratos"). Sem enum, por decisão do projeto.
    @Column(length = 50)
    private String categoria;

    @Column(nullable = false)
    private boolean disponivel = true;

    protected Produto() {
        // exigido pelo JPA
    }

    public Produto(String nome, BigDecimal preco, String categoria) {
        this.nome = nome;
        this.preco = preco;
        this.categoria = categoria;
        this.disponivel = true;
    }

    public Long getId() { return id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public BigDecimal getPreco() { return preco; }
    public void setPreco(BigDecimal preco) { this.preco = preco; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public boolean isDisponivel() { return disponivel; }
    public void setDisponivel(boolean disponivel) { this.disponivel = disponivel; }
}
