package com.brasaesal.repository;

import com.brasaesal.model.ItemPedido;
import com.brasaesal.model.StatusComanda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;

public interface ItemPedidoRepository extends JpaRepository<ItemPedido, Long> {

    // Impede apagar do cardápio um produto que já foi pedido em alguma comanda (histórico).
    boolean existsByProdutoId(Long produtoId);

    // Dashboard: valor somado das comandas com o status informado (ABERTA = "em aberto").
    // Retorna null quando não há itens; o Service trata como zero.
    @Query("select sum(i.precoUnitario * i.quantidade) from ItemPedido i where i.comanda.status = :status")
    BigDecimal somarValorPorStatusComanda(@Param("status") StatusComanda status);
}
