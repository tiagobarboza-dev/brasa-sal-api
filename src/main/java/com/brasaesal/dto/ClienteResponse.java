package com.brasaesal.dto;

import com.brasaesal.model.Cliente;
import com.brasaesal.model.Comanda;

/**
 * Cliente com sua comanda mais recente (ou null se nunca teve uma), como a tela de
 * Clientes espera: { id, nome, mesa, comanda: { id, numero, aberta } | null }.
 * A escolha da comanda mais recente é feita pelo Service (Etapa 8).
 */
public record ClienteResponse(
        Long id,
        String nome,
        int mesa,
        ComandaResumoResponse comanda
) {
    public static ClienteResponse de(Cliente c, Comanda maisRecente) {
        return new ClienteResponse(
                c.getId(),
                c.getNome(),
                c.getMesa(),
                maisRecente == null ? null : ComandaResumoResponse.de(maisRecente));
    }
}
