package com.brasaesal.dto;

import com.brasaesal.model.Cliente;

/** Versão curta do cliente, usada dentro da comanda: { id, nome }. */
public record ClienteResumoResponse(Long id, String nome) {

    public static ClienteResumoResponse de(Cliente c) {
        return new ClienteResumoResponse(c.getId(), c.getNome());
    }
}
