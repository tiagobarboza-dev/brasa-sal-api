package com.brasaesal.dto;

import com.brasaesal.model.Comanda;

import java.math.BigDecimal;
import java.util.List;

/**
 * Comanda completa. Segue o formato que o front já lê (numero, cliente, mesa (a da comanda, não a atual do cliente), aberta,
 * abertaEm em milissegundos, itens, total) e acrescenta id, status e fechadaEm.
 * O total é sempre calculado aqui, pelo back-end. Deve ser montada dentro de uma transação
 * (os itens são carregados sob demanda).
 */
public record ComandaResponse(
        Long id,
        Integer numero,
        ClienteResumoResponse cliente,
        int mesa,
        String status,
        boolean aberta,
        long abertaEm,
        Long fechadaEm,
        List<ItemPedidoResponse> itens,
        BigDecimal total
) {
    public static ComandaResponse de(Comanda c) {
        return new ComandaResponse(
                c.getId(),
                c.getNumero(),
                ClienteResumoResponse.de(c.getCliente()),
                c.getMesa(),
                c.getStatus().name(),
                c.isAberta(),
                c.getDataAbertura().toEpochMilli(),
                c.getDataFechamento() == null ? null : c.getDataFechamento().toEpochMilli(),
                c.getItens().stream().map(ItemPedidoResponse::de).toList(),
                c.calcularTotal());
    }
}
