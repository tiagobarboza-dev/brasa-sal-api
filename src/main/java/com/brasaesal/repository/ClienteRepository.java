package com.brasaesal.repository;

import com.brasaesal.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Os métodos herdados (findAll, findById, save, deleteById) cobrem todo o CRUD de clientes.
 * A consulta "cliente + comanda mais recente" da tela de Clientes é montada no Service
 * a partir de ComandaRepository.buscarTodasDetalhadas().
 */
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
}
