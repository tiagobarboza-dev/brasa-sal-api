package com.brasaesal.service;

import com.brasaesal.dto.RelatorioResumoResponse;
import com.brasaesal.model.Comanda;
import com.brasaesal.repository.ComandaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class RelatorioService {

    private final ComandaRepository comandaRepository;
    private static final ZoneId ZONE = ZoneId.of("America/Sao_Paulo");
    private static final Locale PT_BR = new Locale("pt", "BR");

    public RelatorioService(ComandaRepository comandaRepository) {
        this.comandaRepository = comandaRepository;
    }

    @Transactional(readOnly = true)
    public RelatorioResumoResponse resumo() {
        LocalDate hoje = LocalDate.now(ZONE);
        LocalDate inicio7 = hoje.minusDays(6);

        Instant desde = inicio7.atStartOfDay(ZONE).toInstant();
        Instant inicioHoje = hoje.atStartOfDay(ZONE).toInstant();

        BigDecimal totalSemana = comandaRepository.somarTotalFechadasDesde(desde);
        if (totalSemana == null) totalSemana = BigDecimal.ZERO;
        totalSemana = totalSemana.setScale(2, RoundingMode.HALF_UP);

        BigDecimal totalHoje = comandaRepository.somarTotalFechadasDesde(inicioHoje);
        if (totalHoje == null) totalHoje = BigDecimal.ZERO;
        totalHoje = totalHoje.setScale(2, RoundingMode.HALF_UP);

        long fechadasHoje = comandaRepository.contarFechadasDesde(inicioHoje);

        BigDecimal ticket = fechadasHoje > 0
                ? totalHoje.divide(BigDecimal.valueOf(fechadasHoje), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        List<Comanda> comandas = comandaRepository.buscarFechadasDesde(desde);
        List<RelatorioResumoResponse.DiaResumo> porDia = new ArrayList<>();

        for (int i = 0; i < 7; i++) {
            LocalDate dia = inicio7.plusDays(i);
            BigDecimal soma = comandas.stream()
                    .filter(c -> c.getDataFechamento() != null
                            && LocalDate.ofInstant(c.getDataFechamento(), ZONE).equals(dia))
                    .map(Comanda::calcularTotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .setScale(2, RoundingMode.HALF_UP);

            porDia.add(new RelatorioResumoResponse.DiaResumo(
                    dia.format(DateTimeFormatter.ofPattern("dd/MM")),
                    dia.getDayOfWeek().getDisplayName(TextStyle.SHORT, PT_BR).replace(".", ""),
                    soma
            ));
        }

        return new RelatorioResumoResponse(totalHoje, totalSemana, ticket, fechadasHoje, porDia);
    }
}