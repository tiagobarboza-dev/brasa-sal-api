package com.brasaesal.repository;

import com.brasaesal.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    // O Java original comparava o nome ignorando maiúsculas/minúsculas.
    // Estes dois métodos permitem manter essa regra ao cadastrar (POST) e editar (PUT).
    boolean existsByNomeIgnoreCase(String nome);

    boolean existsByNomeIgnoreCaseAndIdNot(String nome, Long id);
}
