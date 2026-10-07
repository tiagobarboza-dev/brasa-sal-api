package com.brasaesal.dto;

import java.math.BigDecimal;
import java.util.List;

public record RelatorioResumoResponse(
    BigDecimal totalHoje,
    BigDecimal totalSemana,
    BigDecimal ticketMedio,
    long comandasFechadasHoje,
    List<DiaResumo> porDia
) {
    public record DiaResumo(String data, String diaSemana, BigDecimal total) {}
}