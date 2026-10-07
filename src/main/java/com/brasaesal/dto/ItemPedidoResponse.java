package com.brasaesal.dto;

import com.brasaesal.model.ItemPedido;

import java.math.BigDecimal;

/**
 * Item da comanda. "produto" mostra o produto como está hoje no cardápio;
 * "precoUnitario" é o preço cobrado no momento do pedido; "subtotal" é calculado no back-end.
 */
public record ItemPedidoResponse(
        Long id,
        ProdutoResponse produto,
        int quantidade,
        BigDecimal precoUnitario,
        BigDecimal subtotal
) {
    public static ItemPedidoResponse de(ItemPedido i) {
        return new ItemPedidoResponse(
                i.getId(),
                ProdutoResponse.de(i.getProduto()),
                i.getQuantidade(),
                i.getPrecoUnitario(),
                i.calcularSubtotal());
    }
}
