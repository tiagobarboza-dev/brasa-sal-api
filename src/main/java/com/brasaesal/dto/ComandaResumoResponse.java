package com.brasaesal.dto;

import com.brasaesal.model.Comanda;

/** Versão curta da comanda, usada dentro do cliente: { id, numero, aberta }. */
public record ComandaResumoResponse(Long id, Integer numero, boolean aberta) {

    public static ComandaResumoResponse de(Comanda c) {
        return new ComandaResumoResponse(c.getId(), c.getNumero(), c.isAberta());
    }
}
