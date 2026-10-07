package com.brasaesal.dto;

import com.brasaesal.config.RestauranteConstantes;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Entrada de POST /api/clientes e PUT /api/clientes/{id}. */
public record ClienteRequest(

        @NotBlank(message = "O nome do cliente é obrigatório.")
        @Size(max = 100, message = "O nome do cliente deve ter no máximo 100 caracteres.")
        String nome,

        @NotNull(message = "O número da mesa é obrigatório.")
        @Min(value = 1, message = "A mesa deve estar entre 1 e " + RestauranteConstantes.TOTAL_MESAS + ".")
        @Max(value = RestauranteConstantes.TOTAL_MESAS,
             message = "A mesa deve estar entre 1 e " + RestauranteConstantes.TOTAL_MESAS + ".")
        Integer mesa
) {
}
