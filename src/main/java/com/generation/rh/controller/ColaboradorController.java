package com.generation.rh.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.generation.rh.model.Colaborador;
import com.generation.rh.records.CalculoSalario;
import com.generation.rh.records.Holerite;
import com.generation.rh.repository.ColaboradorRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/colaboradores")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ColaboradorController {
	
	@Autowired
	private ColaboradorRepository colaboradorRepository;
	
	@Autowired
	private CalcularSalarioService calcularSalarioService;
	
	@GetMapping
	public ResponseEntity<List<Colaborador>> getAll(){
		return ResponseEntity.ok(colaboradorRepository.findAll());
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Colaborador> getById(@PathVariable Long id){
		return colaboradorRepository.findById(id)
				.map(resposta -> ResponseEntity.ok(resposta))
				.orElse(ResponseEntity.notFound().build());
	}
	
	@GetMapping("/nome/{nome}")
	public ResponseEntity<List<Colaborador>> getByNome(@PathVariable String nome){
		return ResponseEntity.ok(colaboradorRepository.findAllByNomeContainingIgnoreCase(nome));
	}
	
	@PostMapping
	public ResponseEntity<Colaborador> post(@Valid @RequestBody Colaborador colaborador){
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(colaboradorRepository.save(colaborador));
	}
	
	@PutMapping
	public ResponseEntity<Colaborador> put(@Valid @RequestBody Colaborador colaborador){
		return colaboradorRepository.findById(colaborador.getId())
				.map(resposta -> ResponseEntity.ok().body(colaboradorRepository.save(colaborador)))
				.orElse(ResponseEntity.notFound().build());
	}
	
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@DeleteMapping("/{id}")
	public void delete(@PathVariable Long id) {
		colaboradorRepository.deleteById(id);
	}
	
	@PostMapping("/calcularsalario/{id}")
	public ResponseEntity<Holerite> calcularSalario(
			@PathVariable Long id,
			@RequestBody CalculoSalario dadosSalario) {

		Holerite holerite = calcularSalarioService.calcularSalario(id, dadosSalario);
		return ResponseEntity.status(HttpStatus.OK).body(holerite);
	}
	
	
	
}

