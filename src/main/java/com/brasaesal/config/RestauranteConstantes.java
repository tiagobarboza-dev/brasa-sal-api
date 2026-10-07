package com.brasaesal.config;

/**
 * Valores fixos do restaurante. Em um único lugar para a validação do DTO,
 * o Service e o dashboard usarem o mesmo número de mesas (o front tem TOTAL_MESAS = 12).
 */
public final class RestauranteConstantes {

    public static final int TOTAL_MESAS = 12;

    private RestauranteConstantes() {
    }
}
