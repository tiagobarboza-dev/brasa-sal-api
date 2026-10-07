package com.brasaesal.repository;

import com.brasaesal.model.Comanda;
import com.brasaesal.model.StatusComanda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ComandaRepository extends JpaRepository<Comanda, Long> {

    // Regra do Java: um cliente não pode ter duas comandas abertas ao mesmo tempo.
    boolean existsByClienteIdAndStatus(Long clienteId, StatusComanda status);

    // Usado para impedir apagar um cliente que já tem comandas (histórico).
    boolean existsByClienteId(Long clienteId);

    // Lista de comandas (a mais nova primeiro, como o front espera) já com cliente, itens
    // e produtos carregados numa única consulta, para evitar dezenas de consultas (N+1).
    @Query("""
            select distinct c from Comanda c
            join fetch c.cliente
            left join fetch c.itens i
            left join fetch i.produto
            order by c.numero desc
            """)
    List<Comanda> buscarTodasDetalhadas();

    // Base para gerar o número da próxima comanda (maior número + 1; 0 se não houver nenhuma).
    @Query("select coalesce(max(c.numero), 0) from Comanda c")
    int buscarMaiorNumero();

    // Dashboard: comandas abertas / fechadas.
    long countByStatus(StatusComanda status);

    // Dashboard: clientes distintos que têm ou tiveram comanda ("clientes atendidos").
    @Query("select count(distinct c.cliente.id) from Comanda c")
    long contarClientesAtendidos();

    // Dashboard: números das mesas (gravadas em comanda.mesa) que têm comanda com o status informado.
    // Com ABERTA = mesas ocupadas; as demais (de 1 a 12) são livres.
    @Query("select distinct c.mesa from Comanda c where c.status = :status")
    List<Integer> buscarMesasPorStatus(@Param("status") StatusComanda status);

    // Relatório: total arrecadado em comandas FECHADAS a partir de uma data.
    @Query("""
            select coalesce(sum(i.precoUnitario * i.quantidade), 0)
            from ItemPedido i
            where i.comanda.status = com.brasaesal.model.StatusComanda.FECHADA
              and i.comanda.dataFechamento >= :desde
            """)
    java.math.BigDecimal somarTotalFechadasDesde(@Param("desde") java.time.Instant desde);

    // Relatório: quantidade de comandas FECHADAS a partir de uma data.
    @Query("""
            select count(c) from Comanda c
            where c.status = com.brasaesal.model.StatusComanda.FECHADA
              and c.dataFechamento >= :desde
            """)
    long contarFechadasDesde(@Param("desde") java.time.Instant desde);

    // Relatório: comandas FECHADAS a partir de uma data (pra agrupar por dia no service).
    @Query("""
            select c from Comanda c
            join fetch c.itens i
            join fetch i.produto
            where c.status = com.brasaesal.model.StatusComanda.FECHADA
              and c.dataFechamento >= :desde
            """)
    List<Comanda> buscarFechadasDesde(@Param("desde") java.time.Instant desde);

}
