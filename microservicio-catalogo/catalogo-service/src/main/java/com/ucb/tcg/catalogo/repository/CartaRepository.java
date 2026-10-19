package com.ucb.tcg.catalogo.repository;
import com.ucb.tcg.catalogo.model.Carta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartaRepository extends JpaRepository<Carta, Long> {
}