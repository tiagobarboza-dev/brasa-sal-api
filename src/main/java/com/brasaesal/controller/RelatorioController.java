package com.brasaesal.controller;

import com.brasaesal.dto.RelatorioResumoResponse;
import com.brasaesal.service.RelatorioService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class RelatorioController {

    private final RelatorioService relatorioService;

    public RelatorioController(RelatorioService relatorioService) {
        this.relatorioService = relatorioService;
    }

    @GetMapping("/relatorios/resumo")
    public RelatorioResumoResponse resumo() {
        return relatorioService.resumo();
    }
}