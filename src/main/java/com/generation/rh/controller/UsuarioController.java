package com.generation.rh.controller;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.generation.rh.model.Usuario;
import com.generation.rh.repository.UsuarioRepository;

@RestController
@RequestMapping("/usuarios")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class UsuarioController {
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@GetMapping
	public ResponseEntity<List<Usuario>> getAll(){
		return ResponseEntity.ok(usuarioRepository.findAll());
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Usuario> getById(@PathVariable Long id){
		return usuarioRepository.findById(id)
				.map(resposta -> ResponseEntity.ok(resposta))
				.orElse(ResponseEntity.notFound().build());
	}
	
	@GetMapping("/nome/{nome}")	
	public ResponseEntity<Object> getByNome(@PathVariable String nome){
		return ResponseEntity.ok(usuarioRepository.findByNomeContainingIgnoreCase(nome));
	}
	
	
	@PostMapping("/cadastrar")
	public ResponseEntity<Usuario> post(@RequestBody Usuario novoUsuario){
		Usuario usuarioCadastrado = usuarioRepository.save(novoUsuario);
		return ResponseEntity.status(201).body(usuarioCadastrado);
	}
	
	@PutMapping("/atualizar")
	public ResponseEntity<Usuario> put(@RequestBody Usuario usuarioAtualizado){
		return usuarioRepository.findById(usuarioAtualizado.getId())
				.map(resposta -> ResponseEntity.ok().body(usuarioRepository.save(usuarioAtualizado)))
				.orElse(ResponseEntity.notFound().build());
	}
	
}
