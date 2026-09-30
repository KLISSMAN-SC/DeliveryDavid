document.addEventListener('DOMContentLoaded', () => {

    // ==========================================
    // 1. LÓGICA DEL MODAL DE FILTROS (CATEGORÍAS)
    // ==========================================
    const modal = document.getElementById('modalFiltros');
    const btnAbrirTxt = document.getElementById('btnAbrirFiltros');
    const btnAbrirCaja = document.getElementById('btnAbrirFiltrosCaja');
    const btnCancelar = document.getElementById('btnCancelarModal');
    const btnAceptar = document.getElementById('btnAceptarModal');
    const btnLimpiar = document.getElementById('btnLimpiarModal');
    const pills = document.querySelectorAll('.filtro-pill');

    // Función para mostrar el modal
    const abrirModal = () => { 
        if(modal) modal.style.display = 'flex'; 
    };
    
    // Asignar eventos de apertura y cierre (Cancelar)
    if(btnAbrirTxt) btnAbrirTxt.addEventListener('click', abrirModal);
    if(btnAbrirCaja) btnAbrirCaja.addEventListener('click', abrirModal);
    if(btnCancelar) btnCancelar.addEventListener('click', () => { modal.style.display = 'none'; });

    // Variable para saber qué botón tocó el usuario
    let categoriaElegida = null;

	// Al hacer clic en un filtro (Pills)
	    pills.forEach(pill => {
	        pill.addEventListener('click', () => {
	            // Si hace clic en el que ya está marcado, lo deselecciona
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

	    // Al presionar el botón verde "Aceptar"
	    if(btnAceptar) {
	        btnAceptar.addEventListener('click', () => { 
	            const urlParams = new URLSearchParams(window.location.search);
	            
	            if (categoriaElegida) {
	                // Agregar categoría a la URL
	                urlParams.set('categoria', categoriaElegida);
	                window.location.href = window.location.pathname + '?' + urlParams.toString();
	            } else {
	                // Si presionó aceptar habiendo quitado la categoría, la borramos
	                if (urlParams.has('categoria')) {
	                    urlParams.delete('categoria');
	                    window.location.href = window.location.pathname + '?' + urlParams.toString();
	                } else {
	                    if(modal) modal.style.display = 'none'; 
	                }
	            }
	        });
	    }

    // Al presionar el botón "Limpiar"
    if(btnLimpiar) {
        btnLimpiar.addEventListener('click', () => {
            const urlParams = new URLSearchParams(window.location.search);
            
            // Si la URL tiene un filtro de categoría, lo borra y recarga la página
            if (urlParams.has('categoria')) {
                urlParams.delete('categoria'); 
                window.location.href = window.location.pathname + '?' + urlParams.toString();
            } else {
                // Si no hay filtro en URL, solo despinta los botones del modal
                pills.forEach(pill => pill.classList.remove('seleccionado'));
                categoriaElegida = null;
            }
        });
    }


    // ==========================================
    // 2. BUSCADOR DINÁMICO (AUTOCOMPLETE)
    // ==========================================
    const inputBuscar = document.getElementById('inputBuscar');
    const dropdownResultados = document.getElementById('dropdownResultados');

    if (!inputBuscar || !dropdownResultados) {
        console.log("No se encontró el buscador o el contenedor de resultados.");
        return;
    }

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
                    dropdownResultados.innerHTML = `<div class="search-no-results" style="padding:15px; text-align:center; color:gray;">No se encontraron resultados</div>`;
                } else {
                    data.forEach(negocio => {
                        const div = document.createElement("div");
                        div.className = "search-item";
                        div.innerHTML = `
                            <span class="search-item-title">${negocio.nombre}</span>
                            <span class="search-item-sub">${negocio.tipo} - ${negocio.direccion}</span>
                        `;
                        
                        // Acción opcional: qué hacer si el usuario hace clic en el resultado
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

    // Cerrar el buscador al hacer clic en cualquier otra parte de la pantalla
    document.addEventListener('click', (e) => {
        if (!inputBuscar.contains(e.target) && !dropdownResultados.contains(e.target)) {
            dropdownResultados.style.display = 'none';
        }
    });

});


// ==========================================
// 3. FUNCIONES EXTERNAS
// ==========================================
// Se mantiene tu función original por si la utilizas en otra parte del proyecto
function controlarCategorias(tipo){
    const seccion = document.getElementById("seccionCategorias");
    if(seccion){
        if(tipo === "RESTAURANTES"){
            seccion.style.display="block";
        }else{
            seccion.style.display="none";
        }
    }
}
// ==========================================
// 4. MANTENER LA POSICIÓN DEL SCROLL SIN SALTOS
// ==========================================

// Desactivar el intento del navegador de mover el scroll por su cuenta
if ('scrollRestoration' in history) {
    history.scrollRestoration = 'manual';
}

// Guardar la posición exacta antes de recargar
window.addEventListener('beforeunload', () => {
    sessionStorage.setItem('posicionScroll', window.scrollY);
});

// Restaurar posición instantáneamente y luego mostrar la página
document.addEventListener('DOMContentLoaded', () => {
    const posicionGuardada = sessionStorage.getItem('posicionScroll');
    
    if (posicionGuardada) {
        // Mueve el scroll de forma inmediata (sin animación)
        window.scrollTo({ top: parseInt(posicionGuardada), left: 0, behavior: 'instant' });
        sessionStorage.removeItem('posicionScroll');
    }
    
    // Un pequeño respiro de renderizado para asegurar que el scroll ya bajó 
    // antes de volver la página visible con el efecto fade-in.
    requestAnimationFrame(() => {
        document.body.classList.add('cargado');
    });
});