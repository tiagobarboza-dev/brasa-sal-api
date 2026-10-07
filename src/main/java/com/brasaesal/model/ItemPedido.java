package com.brasaesal.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

/**
 * Um produto e a quantidade pedida dele. Vem da classe ItemPedido.java original.
 * É o elo entre Comanda e Produto (N:1 para cada um).
 * O precoUnitario guarda o preço no momento do pedido, para que mudar o preço
 * do cardápio não altere comandas já feitas.
 */
@Entity
@Table(
    name = "item_pedido",
    // Regra do Java original: o mesmo produto não aparece duas vezes na comanda
    // (a quantidade é somada). Aqui o banco também garante isso.
    uniqueConstraints = @UniqueConstraint(
        name = "uk_item_pedido_comanda_produto",
        columnNames = {"comanda_id", "produto_id"})
)
public class ItemPedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "comanda_id", nullable = false)
    private Comanda comanda;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "produto_id", nullable = false)
    private Produto produto;

    @Column(nullable = false)
    private int quantidade;

    @Column(name = "preco_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal precoUnitario;

    protected ItemPedido() {
        // exigido pelo JPA
    }

    /** Copia o preço atual do produto para precoUnitario (o "preço na hora do pedido"). */
    public ItemPedido(Comanda comanda, Produto produto, int quantidade) {
        this.comanda = comanda;
        this.produto = produto;
        this.quantidade = quantidade;
        this.precoUnitario = produto.getPreco();
    }

    public Long getId() { return id; }

    public Comanda getComanda() { return comanda; }

    public Produto getProduto() { return produto; }

    public int getQuantidade() { return quantidade; }

    public BigDecimal getPrecoUnitario() { return precoUnitario; }

    public void incrementarQuantidade(int qtd) { this.quantidade += qtd; }
    public void decrementarQuantidade(int qtd) { this.quantidade -= qtd; }

    /** Preço unitário x quantidade (antes era double, agora BigDecimal). */
    public BigDecimal calcularSubtotal() {
        return precoUnitario.multiply(BigDecimal.valueOf(quantidade));
    }
}
