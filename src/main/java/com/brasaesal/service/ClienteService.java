package com.brasaesal.service;

import com.brasaesal.config.RestauranteConstantes;
import com.brasaesal.dto.ClienteRequest;
import com.brasaesal.dto.ClienteResponse;
import com.brasaesal.exception.RecursoNaoEncontradoException;
import com.brasaesal.exception.RegraNegocioException;
import com.brasaesal.exception.RequisicaoInvalidaException;
import com.brasaesal.model.Cliente;
import com.brasaesal.model.Comanda;
import com.brasaesal.repository.ClienteRepository;
import com.brasaesal.repository.ComandaRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Regras de clientes (equivale ao cadastrarCliente do Main original, agora validado). */
@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final ComandaRepository comandaRepository;

    public ClienteService(ClienteRepository clienteRepository, ComandaRepository comandaRepository) {
        this.clienteRepository = clienteRepository;
        this.comandaRepository = comandaRepository;
    }

    @Transactional(readOnly = true)
    public List<ClienteResponse> listar() {
        // Uma única consulta traz todas as comandas (da mais nova para a mais antiga);
        // a primeira de cada cliente é a mais recente.
        Map<Long, Comanda> maisRecente = new HashMap<>();
        for (Comanda c : comandaRepository.buscarTodasDetalhadas()) {
            maisRecente.putIfAbsent(c.getCliente().getId(), c);
        }
        return clienteRepository.findAll(Sort.by("id")).stream()
                .map(cl -> ClienteResponse.de(cl, maisRecente.get(cl.getId())))
                .toList();
    }

    @Transactional(readOnly = true)
    public ClienteResponse buscar(Long id) {
        return paraResponse(buscarEntidade(id));
    }

    @Transactional
    public ClienteResponse criar(ClienteRequest req) {
        String nome = validar(req);
        Cliente cliente = clienteRepository.save(new Cliente(nome, req.mesa()));
        return ClienteResponse.de(cliente, null);
    }

    @Transactional
    public ClienteResponse atualizar(Long id, ClienteRequest req) {
        Cliente cliente = buscarEntidade(id);
        String nome = validar(req);
        cliente.setNome(nome);
        cliente.setMesa(req.mesa());
        clienteRepository.saveAndFlush(cliente);
        return paraResponse(cliente);
    }

    @Transactional
    public void excluir(Long id) {
        Cliente cliente = buscarEntidade(id);
        if (comandaRepository.existsByClienteId(id)) {
            throw new RegraNegocioException(
                    "Este cliente possui comandas registradas e não pode ser excluído.");
        }
        clienteRepository.delete(cliente);
    }

    // ----- auxiliares -----

    private Cliente buscarEntidade(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente não encontrado."));
    }

    private ClienteResponse paraResponse(Cliente cliente) {
        Comanda maisRecente = cliente.getComandas().stream()
                .max(Comparator.comparing(Comanda::getNumero))
                .orElse(null);
        return ClienteResponse.de(cliente, maisRecente);
    }

    /** Segunda barreira de validação (a primeira é o Bean Validation do DTO). Devolve o nome já sem espaços nas pontas. */
    private String validar(ClienteRequest req) {
        if (req.nome() == null || req.nome().isBlank()) {
            throw new RequisicaoInvalidaException("O nome do cliente é obrigatório.");
        }
        if (req.mesa() == null || req.mesa() < 1 || req.mesa() > RestauranteConstantes.TOTAL_MESAS) {
            throw new RequisicaoInvalidaException(
                    "A mesa deve estar entre 1 e " + RestauranteConstantes.TOTAL_MESAS + ".");
        }
        return req.nome().trim();
    }
}
