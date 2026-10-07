package com.brasaesal.controller;

import com.brasaesal.dto.DashboardResponse;
import com.brasaesal.service.ComandaService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Resumo do dashboard. Os números são calculados no ComandaService. */
@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final ComandaService comandaService;

    public DashboardController(ComandaService comandaService) {
        this.comandaService = comandaService;
    }

    @GetMapping
    public DashboardResponse obter() {
        return comandaService.obterDashboard();
    }
}
