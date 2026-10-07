package com.brasaesal.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Conjunto de pedidos de um cliente. Vem da classe Comanda.java original.
 * As REGRAS (comanda aberta, quantidade válida, somar item repetido, remover ao zerar)
 * ficam no ComandaService (Etapa 8). Aqui há só estrutura e cálculos simples.
 */
@Entity
@Table(name = "comanda")
public class Comanda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Número visual da comanda (antes: contador estático do Java).
    // Será gerado pelo ComandaService; o índice único impede repetição.
    @Column(nullable = false, unique = true)
    private Integer numero;

    // Cliente 1:N Comanda. LAZY: o cliente só é carregado quando necessário.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    // Mesa em que ESTA comanda foi aberta (cópia da mesa do cliente no momento da abertura).
    // Mudar a mesa do cliente depois não altera este valor: é o registro histórico.
    @Column(nullable = false)
    private int mesa;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private StatusComanda status = StatusComanda.ABERTA;

    // Instant = momento absoluto (UTC), evita erro de fuso ao calcular "há X min" no front.
    @Column(nullable = false)
    private Instant dataAbertura;

    private Instant dataFechamento;

    // Comanda 1:N ItemPedido.
    // cascade ALL: salvar a comanda salva os itens.
    // orphanRemoval: tirar um item da lista apaga a linha no banco
    // (é assim que "remover o produto quando a quantidade chega a zero" acontece).
    @OneToMany(mappedBy = "comanda", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id ASC") // itens na ordem em que foram adicionados, como no Java original
    private List<ItemPedido> itens = new ArrayList<>();

    protected Comanda() {
        // exigido pelo JPA
    }

    public Comanda(Integer numero, Cliente cliente) {
        this.numero = numero;
        this.cliente = cliente;
        this.mesa = cliente.getMesa();   // copia a mesa atual do cliente
        this.status = StatusComanda.ABERTA;
        this.dataAbertura = Instant.now();
        cliente.adicionarComanda(this);
    }

    public Long getId() { return id; }

    public Integer getNumero() { return numero; }

    public Cliente getCliente() { return cliente; }

    /** Mesa em que a comanda foi aberta (não muda se o cliente trocar de mesa depois). */
    public int getMesa() { return mesa; }

    public StatusComanda getStatus() { return status; }
    public boolean isAberta() { return status == StatusComanda.ABERTA; }

    public Instant getDataAbertura() { return dataAbertura; }
    public Instant getDataFechamento() { return dataFechamento; }

    public List<ItemPedido> getItens() { return Collections.unmodifiableList(itens); }

    /** Só liga o item à lista. Quem valida (comanda aberta, produto repetido...) é o Service. */
    public void adicionarItem(ItemPedido item) {
        this.itens.add(item);
    }

    /** Só tira o item da lista (o orphanRemoval apaga no banco). Validações ficam no Service. */
    public void removerItem(ItemPedido item) {
        this.itens.remove(item);
    }

    /** Soma o subtotal de cada item (equivale ao Comanda.calcularTotal() original). */
    public BigDecimal calcularTotal() {
        return itens.stream()
                .map(ItemPedido::calcularSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /** Encerra a comanda (equivale ao Comanda.fechar() original). */
    public void fechar() {
        this.status = StatusComanda.FECHADA;
        this.dataFechamento = Instant.now();
    }

        /** Marca a mesa como liberada (limpa), pronta para o próximo cliente. */
    public void liberarMesa() {
        if (this.status != StatusComanda.FECHADA) {
            throw new IllegalStateException("Só é possível liberar comandas fechadas.");
        }
        this.status = StatusComanda.LIBERADA;
    }
    
}
