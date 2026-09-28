package com.proyecto.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proyecto.model.Autor;
import com.proyecto.repository.AutorRepository;

@Service
public class AutorService {

	@Autowired
	private AutorRepository autorRepository;

	public List<Autor> obtenerTodos() {
		return autorRepository.findAll();
	}

	public Optional<Autor> obtenerPorId(Integer id) {
		return autorRepository.findById(id);
	}

	public Autor guardar(Autor autor) {
		return autorRepository.save(autor);
	}
	
	public List<Autor> buscarPorNombre(String nombre) {
	    return autorRepository.findByNombreContainingIgnoreCase(nombre);
	}

	
	public Autor actualizar(Integer id, Autor autorActualizado) {
		Optional<Autor> opcional = autorRepository.findById(id);
		if (opcional.isPresent()) 
		{
			Autor autor = opcional.get();			
			autor.setApellido(autorActualizado.getApellido());
			autor.setNombre(autorActualizado.getNombre());
			autor.setFechaNacimiento(autorActualizado.getFechaNacimiento());
			autor.setNacionalidad(autorActualizado.getNacionalidad());
			
			return autorRepository.save(autor);
		}
		
		return null;		
	}
	
	public void eliminar(Integer id) {
		autorRepository.deleteById(id);
	}

}
