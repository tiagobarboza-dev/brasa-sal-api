package com.brasaesal.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * Resumo para o dashboard. Mesa ocupada = tem comanda aberta; qualquer outra mesa
 * de 1 a 12 (inclusive após fechar a comanda) é livre.
 */
public record DashboardResponse(
        long comandasAbertas,
        long comandasFechadas,
        long clientesAtendidos,
        BigDecimal valorComandasAbertas,
        List<Integer> mesasOcupadas,
        List<Integer> mesasLivres
) {
}
