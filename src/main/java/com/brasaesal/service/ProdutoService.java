package com.brasaesal.service;

import com.brasaesal.dto.ProdutoRequest;
import com.brasaesal.dto.ProdutoResponse;
import com.brasaesal.exception.RecursoNaoEncontradoException;
import com.brasaesal.exception.RegraNegocioException;
import com.brasaesal.exception.RequisicaoInvalidaException;
import com.brasaesal.model.Produto;
import com.brasaesal.repository.ItemPedidoRepository;
import com.brasaesal.repository.ProdutoRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/** Regras do cardápio. */
@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final ItemPedidoRepository itemPedidoRepository;

    public ProdutoService(ProdutoRepository produtoRepository, ItemPedidoRepository itemPedidoRepository) {
        this.produtoRepository = produtoRepository;
        this.itemPedidoRepository = itemPedidoRepository;
    }

    @Transactional(readOnly = true)
    public List<ProdutoResponse> listar() {
        return produtoRepository.findAll(Sort.by("id")).stream().map(ProdutoResponse::de).toList();
    }

    @Transactional(readOnly = true)
    public ProdutoResponse buscar(Long id) {
        return ProdutoResponse.de(buscarEntidade(id));
    }

    @Transactional
    public ProdutoResponse criar(ProdutoRequest req) {
        String nome = validar(req);
        if (produtoRepository.existsByNomeIgnoreCase(nome)) {
            throw new RegraNegocioException("Já existe um produto com o nome \"" + nome + "\".");
        }
        Produto produto = new Produto(nome, req.preco(), textoOuNulo(req.categoria()));
        if (req.disponivel() != null) {
            produto.setDisponivel(req.disponivel());
        }
        return ProdutoResponse.de(produtoRepository.save(produto));
    }

    @Transactional
    public ProdutoResponse atualizar(Long id, ProdutoRequest req) {
        Produto produto = buscarEntidade(id);
        String nome = validar(req);
        if (produtoRepository.existsByNomeIgnoreCaseAndIdNot(nome, id)) {
            throw new RegraNegocioException("Já existe um produto com o nome \"" + nome + "\".");
        }
        produto.setNome(nome);
        produto.setPreco(req.preco());
        produto.setCategoria(textoOuNulo(req.categoria()));
        if (req.disponivel() != null) {
            produto.setDisponivel(req.disponivel());
        }
        return ProdutoResponse.de(produtoRepository.saveAndFlush(produto));
    }

    @Transactional
    public void excluir(Long id) {
        Produto produto = buscarEntidade(id);
        if (itemPedidoRepository.existsByProdutoId(id)) {
            throw new RegraNegocioException(
                    "Este produto já foi pedido em comandas e não pode ser excluído. Marque-o como indisponível.");
        }
        produtoRepository.delete(produto);
    }

    // ----- auxiliares -----

    private Produto buscarEntidade(Long id) {
        return produtoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto não encontrado."));
    }

    /** Segunda barreira de validação (a primeira é o Bean Validation do DTO). Devolve o nome já sem espaços nas pontas. */
    private String validar(ProdutoRequest req) {
        if (req.nome() == null || req.nome().isBlank()) {
            throw new RequisicaoInvalidaException("O nome do produto é obrigatório.");
        }
        if (req.preco() == null || req.preco().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RequisicaoInvalidaException("O preço deve ser maior que zero.");
        }
        return req.nome().trim();
    }

    private String textoOuNulo(String texto) {
        return texto == null || texto.isBlank() ? null : texto.trim();
    }
}
