package com.brasaesal.dto;

import com.brasaesal.model.Produto;

import java.math.BigDecimal;

/** Produto do cardápio. Mesmo formato que o front já usa: { id, nome, preco, categoria, disponivel }. */
public record ProdutoResponse(
        Long id,
        String nome,
        BigDecimal preco,
        String categoria,
        boolean disponivel
) {
    public static ProdutoResponse de(Produto p) {
        return new ProdutoResponse(p.getId(), p.getNome(), p.getPreco(), p.getCategoria(), p.isDisponivel());
    }
}
