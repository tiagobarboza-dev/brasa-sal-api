package com.brasaesal.exception;

/** Dado de entrada inválido (ex.: quantidade menor que 1). Será HTTP 400 (Etapa 10). */
public class RequisicaoInvalidaException extends RuntimeException {
    public RequisicaoInvalidaException(String mensagem) {
        super(mensagem);
    }
}
