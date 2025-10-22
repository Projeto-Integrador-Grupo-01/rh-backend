package com.generation.rh.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.generation.rh.model.Colaborador;

public interface ColaboradorRepository extends JpaRepository<Colaborador, Long> {

	List<Colaborador> findAllByNomeContainingIgnoreCase(@Param("nome")String nome);
}
