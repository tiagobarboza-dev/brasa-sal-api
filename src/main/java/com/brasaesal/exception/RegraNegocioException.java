package com.brasaesal.exception;

/** Operação que viola uma regra do sistema (ex.: comanda fechada). Será HTTP 409 (Etapa 10). */
public class RegraNegocioException extends RuntimeException {
    public RegraNegocioException(String mensagem) {
        super(mensagem);
    }
}
