package com.brasaesal.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/** Entrada de POST /api/comandas/{id}/itens (adicionar produto à comanda). */
public record ItemPedidoRequest(

        @NotNull(message = "O produto é obrigatório.")
        Long produtoId,

        @NotNull(message = "A quantidade é obrigatória.")
        @Positive(message = "A quantidade deve ser maior que zero.")
        Integer quantidade
) {
}
