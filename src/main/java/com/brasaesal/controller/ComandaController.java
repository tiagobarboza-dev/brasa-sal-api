package com.brasaesal.controller;

import com.brasaesal.dto.AlterarQuantidadeRequest;
import com.brasaesal.dto.ComandaResponse;
import com.brasaesal.dto.ItemPedidoRequest;
import com.brasaesal.service.ComandaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Endpoints de comandas e dos itens delas. Só recebe/devolve HTTP; as regras ficam no ComandaService.
 * Adicionar, remover e alterar item devolvem a comanda atualizada, que é o que o front usa para redesenhar a tela.
 */
@RestController
@RequestMapping("/api")
public class ComandaController {

    private final ComandaService comandaService;

    public ComandaController(ComandaService comandaService) {
        this.comandaService = comandaService;
    }

    @GetMapping("/comandas")
    public List<ComandaResponse> listar() {
        return comandaService.listar();
    }

    @GetMapping("/comandas/{id}")
    public ComandaResponse buscar(@PathVariable Long id) {
        return comandaService.buscar(id);
    }

    /** Abre uma comanda para o cliente (sem corpo na requisição). */
    @PostMapping("/clientes/{clienteId}/comandas")
    @ResponseStatus(HttpStatus.CREATED)
    public ComandaResponse abrir(@PathVariable Long clienteId) {
        return comandaService.abrir(clienteId);
    }

    /** 200 (e não 201): se o produto já estava na comanda, apenas a quantidade aumenta. */
    @PostMapping("/comandas/{id}/itens")
    public ComandaResponse adicionarItem(@PathVariable Long id, @Valid @RequestBody ItemPedidoRequest request) {
        return comandaService.adicionarItem(id, request);
    }

    /** Sem ?quantidade= remove o item inteiro; com ela, remove só essa quantidade. */
    @DeleteMapping("/comandas/{id}/itens/{produtoId}")
    public ComandaResponse removerItem(@PathVariable Long id,
                                       @PathVariable Long produtoId,
                                       @RequestParam(required = false) Integer quantidade) {
        return comandaService.removerItem(id, produtoId, quantidade);
    }

    /** Define a quantidade do item para um valor exato (o front atual não usa). */
    @PatchMapping("/comandas/{id}/itens/{produtoId}")
    public ComandaResponse alterarQuantidade(@PathVariable Long id,
                                             @PathVariable Long produtoId,
                                             @Valid @RequestBody AlterarQuantidadeRequest request) {
        return comandaService.alterarQuantidade(id, produtoId, request);
    }

    @PatchMapping("/comandas/{id}/fechar")
    public ComandaResponse fechar(@PathVariable Long id) {
        return comandaService.fechar(id);
    }

    @PatchMapping("/comandas/{id}/liberar-mesa")
    public ResponseEntity<Void> liberarMesa(@PathVariable Long id) {
        comandaService.liberarMesa(id);
        return ResponseEntity.noContent().build();
    }

}
