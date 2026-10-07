package com.brasaesal.service;

import com.brasaesal.config.RestauranteConstantes;
import com.brasaesal.dto.AlterarQuantidadeRequest;
import com.brasaesal.dto.ComandaResponse;
import com.brasaesal.dto.DashboardResponse;
import com.brasaesal.dto.ItemPedidoRequest;
import com.brasaesal.exception.RecursoNaoEncontradoException;
import com.brasaesal.exception.RegraNegocioException;
import com.brasaesal.exception.RequisicaoInvalidaException;
import com.brasaesal.model.Cliente;
import com.brasaesal.model.Comanda;
import com.brasaesal.model.ItemPedido;
import com.brasaesal.model.Produto;
import com.brasaesal.model.StatusComanda;
import com.brasaesal.repository.ClienteRepository;
import com.brasaesal.repository.ComandaRepository;
import com.brasaesal.repository.ItemPedidoRepository;
import com.brasaesal.repository.ProdutoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

/**
 * Regras de negócio das comandas. É aqui que vive a lógica que era do Comanda.java,
 * do Cliente.java e do Main.java originais: abrir, adicionar (somando se já existe),
 * remover quantidade (apagando o item ao zerar), calcular total e fechar.
 * Todo total é calculado aqui; nada que venha do front é confiado.
 */
@Service
public class ComandaService {

    private final ComandaRepository comandaRepository;
    private final ClienteRepository clienteRepository;
    private final ProdutoRepository produtoRepository;
    private final ItemPedidoRepository itemPedidoRepository;

    public ComandaService(ComandaRepository comandaRepository,
                          ClienteRepository clienteRepository,
                          ProdutoRepository produtoRepository,
                          ItemPedidoRepository itemPedidoRepository) {
        this.comandaRepository = comandaRepository;
        this.clienteRepository = clienteRepository;
        this.produtoRepository = produtoRepository;
        this.itemPedidoRepository = itemPedidoRepository;
    }

    // ---------------------------------------------------------------- consultas

    @Transactional(readOnly = true)
    public List<ComandaResponse> listar() {
        return comandaRepository.buscarTodasDetalhadas().stream().map(ComandaResponse::de).toList();
    }

    @Transactional(readOnly = true)
    public ComandaResponse buscar(Long id) {
        return ComandaResponse.de(buscarEntidade(id));
    }

    // ---------------------------------------------------------------- abrir

    /**
     * Equivale a Cliente.abrirComanda() + a checagem "já possui comanda aberta" do Main.
     * A mesa atual do cliente é copiada para a comanda (new Comanda faz a cópia).
     */
    @Transactional
    public ComandaResponse abrir(Long clienteId) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente não encontrado."));

        if (comandaRepository.existsByClienteIdAndStatus(clienteId, StatusComanda.ABERTA)) {
            throw new RegraNegocioException("Este cliente já possui uma comanda aberta.");
        }

        int numero = comandaRepository.buscarMaiorNumero() + 1;
        Comanda comanda = comandaRepository.save(new Comanda(numero, cliente));
        return ComandaResponse.de(comanda);
    }

    // ---------------------------------------------------------------- itens

    /** Equivale a Comanda.adicionarItem(): se o produto já está na comanda, soma a quantidade. */
    @Transactional
    public ComandaResponse adicionarItem(Long comandaId, ItemPedidoRequest req) {
        Comanda comanda = buscarEntidade(comandaId);
        exigirAberta(comanda, "Não é possível adicionar itens: a comanda está fechada.");

        if (req.produtoId() == null) {
            throw new RequisicaoInvalidaException("O produto é obrigatório.");
        }
        exigirQuantidadePositiva(req.quantidade());

        Produto produto = produtoRepository.findById(req.produtoId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto não encontrado."));
        if (!produto.isDisponivel()) {
            throw new RegraNegocioException("O produto \"" + produto.getNome() + "\" está indisponível.");
        }

        Optional<ItemPedido> existente = localizarItem(comanda, produto.getId());
        if (existente.isPresent()) {
            existente.get().incrementarQuantidade(req.quantidade());
        } else {
            comanda.adicionarItem(new ItemPedido(comanda, produto, req.quantidade()));
        }

        // saveAndFlush grava agora, para o item novo já ter id na resposta.
        return ComandaResponse.de(comandaRepository.saveAndFlush(comanda));
    }

    /**
     * Equivale a Comanda.removerItem(): remove a quantidade pedida; se a quantidade restante
     * for zero (ou menos), o item é removido por inteiro. Sem quantidade, remove o item todo.
     */
    @Transactional
    public ComandaResponse removerItem(Long comandaId, Long produtoId, Integer quantidade) {
        Comanda comanda = buscarEntidade(comandaId);
        exigirAberta(comanda, "Não é possível remover itens: a comanda está fechada.");

        if (quantidade != null) {
            exigirQuantidadePositiva(quantidade);
        }

        ItemPedido item = localizarItem(comanda, produtoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto não encontrado na comanda."));

        if (quantidade == null || item.getQuantidade() <= quantidade) {
            comanda.removerItem(item);          // orphanRemoval apaga a linha
        } else {
            item.decrementarQuantidade(quantidade);
        }

        return ComandaResponse.de(comandaRepository.saveAndFlush(comanda));
    }

    /** Define a quantidade de um item para um valor exato (endpoint extra do plano; o front não usa). */
    @Transactional
    public ComandaResponse alterarQuantidade(Long comandaId, Long produtoId, AlterarQuantidadeRequest req) {
        Comanda comanda = buscarEntidade(comandaId);
        exigirAberta(comanda, "Não é possível alterar itens: a comanda está fechada.");
        exigirQuantidadePositiva(req.quantidade());

        ItemPedido item = localizarItem(comanda, produtoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto não encontrado na comanda."));

        int diferenca = req.quantidade() - item.getQuantidade();
        if (diferenca > 0) {
            item.incrementarQuantidade(diferenca);
        } else if (diferenca < 0) {
            item.decrementarQuantidade(-diferenca);
        }

        return ComandaResponse.de(comandaRepository.saveAndFlush(comanda));
    }

    // ---------------------------------------------------------------- fechar

    /** Equivale a Comanda.fechar(): impede novas alterações. O total é calculado no back-end. */
    @Transactional
    public ComandaResponse fechar(Long id) {
        Comanda comanda = buscarEntidade(id);
        exigirAberta(comanda, "Esta comanda já está fechada.");
        comanda.fechar();
        return ComandaResponse.de(comandaRepository.saveAndFlush(comanda));
    }

        /** Libera a mesa após o fechamento, deixando-a pronta para o próximo cliente. */
    @Transactional
    public void liberarMesa(Long id) {
        Comanda comanda = buscarEntidade(id);
        if (comanda.getStatus() == StatusComanda.LIBERADA) {
            throw new RegraNegocioException("Esta mesa já foi liberada.");
        }
        if (comanda.isAberta()) {
            throw new RegraNegocioException("Não é possível liberar uma comanda aberta.");
        }
        comanda.liberarMesa();
        comandaRepository.saveAndFlush(comanda);
    }

    // ---------------------------------------------------------------- dashboard

    /**
     * Usa comanda.mesa (a mesa em que cada comanda foi aberta), não a mesa atual do cliente.
     * Mesa ocupada = tem comanda aberta. Qualquer outra mesa de 1 a 12 (inclusive depois de
     * fechar a comanda) é livre. Comandas fechadas continuam no histórico.
     */
    @Transactional(readOnly = true)
    public DashboardResponse obterDashboard() {
        List<Integer> ocupadas = comandaRepository.buscarMesasPorStatus(StatusComanda.ABERTA)
                .stream().sorted().toList();
        List<Integer> livres = IntStream.rangeClosed(1, RestauranteConstantes.TOTAL_MESAS)
                .boxed().filter(m -> !ocupadas.contains(m)).toList();

        BigDecimal valorAberto = itemPedidoRepository.somarValorPorStatusComanda(StatusComanda.ABERTA);
        if (valorAberto == null) {
            valorAberto = BigDecimal.ZERO.setScale(2);
        }

        return new DashboardResponse(
                comandaRepository.countByStatus(StatusComanda.ABERTA),
                comandaRepository.countByStatus(StatusComanda.FECHADA),
                comandaRepository.contarClientesAtendidos(),
                valorAberto,
                ocupadas,
                livres);
    }

    // ---------------------------------------------------------------- auxiliares

    private Comanda buscarEntidade(Long id) {
        return comandaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Comanda não encontrada."));
    }

    private void exigirAberta(Comanda comanda, String mensagem) {
        if (!comanda.isAberta()) {
            throw new RegraNegocioException(mensagem);
        }
    }

    private void exigirQuantidadePositiva(Integer quantidade) {
        if (quantidade == null || quantidade <= 0) {
            throw new RequisicaoInvalidaException("Quantidade inválida.");
        }
    }

    private Optional<ItemPedido> localizarItem(Comanda comanda, Long produtoId) {
        return comanda.getItens().stream()
                .filter(i -> i.getProduto().getId().equals(produtoId))
                .findFirst();
    }
}
