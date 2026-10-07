package com.brasaesal.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Entrada do endpoint de alterar a quantidade de um item para um valor exato
 * (o desenho da URL é definido na Etapa 9). O front atual não usa este endpoint.
 */
public record AlterarQuantidadeRequest(

        @NotNull(message = "A quantidade é obrigatória.")
        @Positive(message = "A quantidade deve ser maior que zero.")
        Integer quantidade
) {
}
