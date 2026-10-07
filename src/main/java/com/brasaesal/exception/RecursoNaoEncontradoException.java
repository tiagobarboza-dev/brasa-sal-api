package com.brasaesal.exception;

/** Recurso inexistente. Será traduzida para HTTP 404 (Etapa 10). */
public class RecursoNaoEncontradoException extends RuntimeException {
    public RecursoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}
