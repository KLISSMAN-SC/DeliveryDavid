package com.proyecto.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.proyecto.model.Autor;
import com.proyecto.services.AutorService;

@Controller
@RequestMapping("/web/autores")
public class AutorWebController {

	@Autowired
	private AutorService autorService;

	@GetMapping
	public String listarAutores(Model model) {
		model.addAttribute("autores", autorService.obtenerTodos());
		return "autores/lista";
	}

	@GetMapping("/nuevo")
	public String formularioNuevo(Model model) {
		model.addAttribute("autor", new Autor());
		return "/autores/formulario";
	}

	@GetMapping("/buscar")
	public String buscarPorNombre(@RequestParam("nombre") String nombre, Model model) {

		if (nombre == null || nombre.trim().isEmpty()) {
			model.addAttribute("autores", autorService.obtenerTodos());
		} else {
			model.addAttribute("autores", autorService.buscarPorNombre(nombre));
		}

		return "autores/lista"; // usa el mismo template de la lista
	}

	@GetMapping("/{id}/editar")
	public String formularioEditar(@PathVariable Integer id, Model model) {
		Optional<Autor> autor = autorService.obtenerPorId(id);
		if (autor.isPresent()) {
			model.addAttribute("autor", autor.get());
			return "/autores/formulario";
		}

		return "redirect:/web/autores";
	}

	@PostMapping
	public String guardarAutor(@ModelAttribute Autor autor) {
		autorService.guardar(autor);
		return "redirect:/web/autores";
	}

	@GetMapping("/{id}/eliminar")
	public String eliminarAutor(@PathVariable Integer id) {
		autorService.eliminar(id);
		return "redirect:/web/autores";
	}

}
