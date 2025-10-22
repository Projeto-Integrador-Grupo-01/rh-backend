package com.generation.rh.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.generation.rh.model.Departamento;

public interface DepartamentoRepository extends JpaRepository<Departamento, Long> {

	List<Departamento> findAllByNomeContainingIgnoreCase(String nome);
	
}
