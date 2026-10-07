package com.brasaesal.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/** Entrada de POST /api/produtos e PUT /api/produtos/{id}. */
public record ProdutoRequest(

        @NotBlank(message = "O nome do produto é obrigatório.")
        @Size(max = 100, message = "O nome do produto deve ter no máximo 100 caracteres.")
        String nome,

        @NotNull(message = "O preço é obrigatório.")
        @Positive(message = "O preço deve ser maior que zero.")
        @Digits(integer = 8, fraction = 2, message = "O preço deve ter no máximo 2 casas decimais.")
        BigDecimal preco,

        @Size(max = 50, message = "A categoria deve ter no máximo 50 caracteres.")
        String categoria,

        // Opcional. Se vier nulo ao cadastrar, o produto nasce disponível.
        Boolean disponivel
) {
}
