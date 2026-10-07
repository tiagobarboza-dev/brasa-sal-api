package com.brasaesal.model;

/**
 * Situação de uma comanda.
 * ABERTA    → cliente consumindo, mesa ocupada.
 * FECHADA   → cliente pagou, mas a mesa ainda não foi limpa/liberada.
 * LIBERADA  → mesa pronta para o próximo cliente.
 */
public enum StatusComanda {
    ABERTA,
    FECHADA,
    LIBERADA
}