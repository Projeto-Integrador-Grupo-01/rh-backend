package com.generation.rh.model;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "tb_departamentos")
public class Departamento {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotBlank(message = "O nome do departamento é obrigatório.")
	private String nome;
	
	@Size(max = 5000, message = "O link do ícone deve ter no máximo 5000 caracteres")
	private String icone;
	
	@OneToMany(mappedBy = "departamento", cascade = CascadeType.REMOVE)
	List<Colaborador> colaborador;
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getNome() {
		return nome;
	}
	
	public void setNome(String nome) {
		this.nome = nome;
	}
	
	public String getIcone() {
		return icone;
	}
	
	public void setIcone(String icone) {
		this.icone = icone;
	}
	
	public List<Colaborador> getColaborador() {
		return colaborador;
	}
	
	public void setColaborador(List<Colaborador> colaborador) {
		this.colaborador = colaborador;
	}

}
