// Desactivar el intento del navegador de mover el scroll por su cuenta
if ('scrollRestoration' in history) {
    history.scrollRestoration = 'manual';
}

// Guardar la posición exacta antes de recargar
window.addEventListener('beforeunload', () => {
    sessionStorage.setItem('posicionScroll', window.scrollY);
});

document.addEventListener('DOMContentLoaded', () => {

    // ==========================================
    // 1. RESTAURAR SCROLL Y MOSTRAR PÁGINA (Sin saltos)
    // ==========================================
    const posicionGuardada = sessionStorage.getItem('posicionScroll');
    
    if (posicionGuardada) {
        window.scrollTo({ top: parseInt(posicionGuardada), left: 0, behavior: 'instant' });
        sessionStorage.removeItem('posicionScroll');
    }
    
    // Un pequeño respiro de renderizado para asegurar que el scroll ya bajó 
    requestAnimationFrame(() => {
        document.body.classList.add('cargado');
    });


    // ==========================================
    // 2. LÓGICA DEL MODAL DE FILTROS (CATEGORÍAS)
    // ==========================================
    const modal = document.getElementById('modalFiltros');
    const btnAbrirCaja = document.getElementById('btnAbrirFiltrosCaja');
    const btnCancelar = document.getElementById('btnCancelarModal');
    const btnAceptar = document.getElementById('btnAceptarModal');
    const btnLimpiar = document.getElementById('btnLimpiarModal');
    const pills = document.querySelectorAll('.filtro-pill');

    const abrirModal = () => { 
        if(modal) modal.style.display = 'flex'; 
    };
    
    if(btnAbrirCaja) btnAbrirCaja.addEventListener('click', abrirModal);
    if(btnCancelar) btnCancelar.addEventListener('click', () => { modal.style.display = 'none'; });

    let categoriaElegida = null;

    pills.forEach(pill => {
        pill.addEventListener('click', () => {
            if (pill.classList.contains('seleccionado')) {
                pill.classList.remove('seleccionado');
                categoriaElegida = null;
            } else {
                pills.forEach(p => p.classList.remove('seleccionado')); 
                pill.classList.add('seleccionado'); 
                categoriaElegida = pill.getAttribute('data-categoria'); 
            }
        });
    });

    if(btnAceptar) {
        btnAceptar.addEventListener('click', () => { 
            const urlParams = new URLSearchParams(window.location.search);
            if (categoriaElegida) {
                urlParams.set('categoria', categoriaElegida);
                window.location.href = window.location.pathname + '?' + urlParams.toString();
            } else {
                if (urlParams.has('categoria')) {
                    urlParams.delete('categoria');
                    window.location.href = window.location.pathname + '?' + urlParams.toString();
                } else {
                    if(modal) modal.style.display = 'none'; 
                }
            }
        });
    }

    if(btnLimpiar) {
        btnLimpiar.addEventListener('click', () => {
            const urlParams = new URLSearchParams(window.location.search);
            if (urlParams.has('categoria')) {
                urlParams.delete('categoria'); 
                window.location.href = window.location.pathname + '?' + urlParams.toString();
            } else {
                pills.forEach(pill => pill.classList.remove('seleccionado'));
                categoriaElegida = null;
            }
        });
    }


    // ==========================================
    // 3. BUSCADOR DINÁMICO CON IMÁGENES
    // ==========================================
    const inputBuscar = document.getElementById('inputBuscar');
    const dropdownResultados = document.getElementById('dropdownResultados');

    if (!inputBuscar || !dropdownResultados) return;

    let timeoutId;

    inputBuscar.addEventListener('input', function(){
        clearTimeout(timeoutId);
        const query = this.value.trim();

        if(query.length < 1){
            dropdownResultados.style.display = "none";
            dropdownResultados.innerHTML = "";
            return;
        }

        timeoutId = setTimeout(() => {
            fetch(`/api/negocios/buscar?q=${encodeURIComponent(query)}`)
            .then(res => res.json())
            .then(data => {
                dropdownResultados.innerHTML = "";

                if(data.length === 0){
                    dropdownResultados.innerHTML = `<div class="search-no-results">No se encontraron resultados</div>`;
                } else {
                    data.forEach(negocio => {
                        const div = document.createElement("div");
                        div.className = "search-item";
                        
						// Ruta corregida usando JavaScript puro en lugar de sintaxis Thymeleaf
						 const imagenHTML = (negocio.imagenLogo && negocio.imagenLogo.trim() !== "") 
						  ? `<img src="/api/imagenes/${negocio.imagenLogo.trim()}" class="search-item-img" alt="Logo">`
						  : `<div class="search-item-placeholder">
						   <i class="fa-solid fa-image"></i>
						    </div>`;
						                      
                        div.innerHTML = `
                            ${imagenHTML}
                            <div class="search-item-info" onclick="abrirRestaurante(${negocio.id})">
                                <span class="search-item-title">${negocio.nombre}</span>
                                <span class="search-item-sub">${negocio.tipo} - ${negocio.direccion}</span>
                            </div>
                        `;
                        
                        div.addEventListener('click', () => {
                            inputBuscar.value = negocio.nombre;
                            dropdownResultados.style.display = 'none';
                        });

                        dropdownResultados.appendChild(div);
                    });
                }
                dropdownResultados.style.display = "block";
            })
            .catch(err => {
                console.error("Error al buscar:", err);
            });
        }, 300);
    });

    // Cerrar el buscador al hacer clic fuera
    document.addEventListener('click', (e) => {
        if (!inputBuscar.contains(e.target) && !dropdownResultados.contains(e.target)) {
            dropdownResultados.style.display = 'none';
        }
    });

	// ==========================================
	    // 5. CARGAR MÁS RESTAURANTES (De 8 en 8)
	    // ==========================================
	    const tarjetas = document.querySelectorAll('.tarjeta-restaurante');
	    const btnVerMas = document.getElementById('btnVerMasRestaurantes');
	    const contenedorVerMas = document.getElementById('contenedorVerMas');
	    const cantidadPorPagina = 8; // Cuántos restaurantes mostrar cada vez

	    // 1. Fase de inicio: Ocultar los que sobran
	    if (tarjetas.length > cantidadPorPagina) {
	        contenedorVerMas.style.display = 'flex'; // Mostrar el botón
	        
	        tarjetas.forEach((tarjeta, index) => {
	            if (index >= cantidadPorPagina) {
	                tarjeta.classList.add('oculto');
	            }
	        });
	    }

	    // 2. Evento del botón: Mostrar los siguientes 8
	    if (btnVerMas) {
	        btnVerMas.addEventListener('click', () => {
	            // Seleccionar solo las tarjetas que están ocultas en este momento
	            const tarjetasOcultas = document.querySelectorAll('.tarjeta-restaurante.oculto');
	            
	            // Quitar la clase 'oculto' a las siguientes 8
	            for (let i = 0; i < cantidadPorPagina && i < tarjetasOcultas.length; i++) {
	                tarjetasOcultas[i].classList.remove('oculto');
	            }

	            // Comprobar si ya no queda ninguna oculta
	            if (document.querySelectorAll('.tarjeta-restaurante.oculto').length === 0) {
	                // Animación suave de desaparición para el botón
	                contenedorVerMas.style.opacity = '0';
	                setTimeout(() => { contenedorVerMas.style.display = 'none'; }, 300);
	            }
	        });
	    }
	
});
function volverInicio(){

    location.reload();

}

// ==========================================
// 4. CARRUSEL DE PROMOCIONES (Auto y Manual)
// ==========================================
let promoIndex = 0;
let intervaloPromo;

const mostrarPromo = (index) => {
    const slides = document.querySelectorAll('.promo-slide');
    const dots = document.querySelectorAll('.promo-dots .dot');

    if (!slides.length) return; // Si no hay carrusel en la página, no hace nada

    // Ocultar todos
    slides.forEach(slide => slide.classList.remove('activo'));
    dots.forEach(dot => dot.classList.remove('activo'));

    // Calcular índice cíclico
    if (index >= slides.length) promoIndex = 0;
    if (index < 0) promoIndex = slides.length - 1;

    // Mostrar el correspondiente
    slides[promoIndex].classList.add('activo');
    dots[promoIndex].classList.add('activo');
};

const avanzarPromo = () => {
    promoIndex++;
    mostrarPromo(promoIndex);
};

// Se vincula globalmente para que el HTML la pueda llamar con onclick=""
window.cambiarSlide = (index) => {
    promoIndex = index;
    mostrarPromo(promoIndex);
    
    // Reiniciar el temporizador automático si el usuario hizo clic manual
    clearInterval(intervaloPromo);
    intervaloPromo = setInterval(avanzarPromo, 5000);
};

// Arrancar el carrusel cuando cargue la página
document.addEventListener('DOMContentLoaded', () => {
    if (document.querySelectorAll('.promo-slide').length > 0) {
        mostrarPromo(promoIndex);
        intervaloPromo = setInterval(avanzarPromo, 5000); // 5000 ms = 5 segundos
    }
});