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
function manejarMenuUsuario(opcion) {
	if(opcion === 'panel') { 
		window.location.href = '/api/panel'; 
	}
    if (opcion === "perfil") {
        window.location.href = "/api/perfil";
    }
    if (opcion === "logout") {
        document.getElementById("formLogout").submit();
    }
}
// Abrir/Cerrar el menú de usuario
function toggleDropdown() {
    document.querySelector('.usuario-menu-custom').classList.toggle('activo');
}

// Cerrar el menú automáticamente si se hace clic fuera de él
window.onclick = function(event) {
    if (!event.target.closest('.usuario-menu-custom')) {
        let dropdown = document.querySelector('.usuario-menu-custom');
        if (dropdown && dropdown.classList.contains('activo')) {
            dropdown.classList.remove('activo');
        }
    }
}
// ==========================================
// 6. LÓGICA DEL MODAL DE PEDIDO (CHECKOUT)
// ==========================================

// Función para abrir y cerrar el modal
function abrirModalPedido() {
    const modal = document.getElementById('modalPedido');
    if (modal) {
        modal.style.display = 'flex';
        document.body.style.overflow = 'hidden'; // Evita que la página del fondo se desplace
    }
}

function cerrarModalPedido() {
    const modal = document.getElementById('modalPedido');
    if (modal) {
        modal.style.display = 'none';
        document.body.style.overflow = 'auto'; // Restaura el scroll de la página principal
    }
}

function seleccionarPropina(monto, botonClickeado) {
    // 1. Quitar el color naranja a todos los botones
    const botonesPropina = document.querySelectorAll('.opciones-propina button');
    botonesPropina.forEach(btn => btn.classList.remove('activo'));
    
    // 2. Pintar de naranja el botón que se clickeó (Con respaldo de seguridad)
    if (botonClickeado) {
        botonClickeado.classList.add('activo');
    } else if (window.event && window.event.currentTarget) {
        window.event.currentTarget.classList.add('activo'); 
    }
    
    // 3. Actualizar la variable oculta
    const inputPropina = document.getElementById('propinaPedido');
    if(inputPropina) inputPropina.value = monto;
    
    // 4. Actualizar el texto del resumen ("S/ 3.00")
    const textoResumen = document.getElementById('propinaResumen');
    if(textoResumen) textoResumen.innerText = monto.toFixed(2);
    
    // 5. Recalcular el "Total a pagar"
    const textoTotal = document.getElementById('totalPedido');
    if (textoTotal) {
        const baseTotal = parseFloat(textoTotal.getAttribute('data-base')) || 0;
        const nuevoTotal = baseTotal + monto;
        textoTotal.innerText = nuevoTotal.toFixed(2);
    }
}

// Opcional: Cerrar el modal de pedido si se hace clic fuera de la tarjeta blanca
window.addEventListener('click', function(event) {
    const modal = document.getElementById('modalPedido');
    const contenido = document.querySelector('.modal-pedido-contenido');
    
    // Si el clic fue exactamente en el fondo oscuro (overlay) y no dentro de la tarjeta
    if (event.target === modal) {
        cerrarModalPedido();
    }
});
// ==========================================
// LÓGICA DEL MODAL CRUD USUARIOS
// ==========================================
function abrirModalUsuario(id, nombres, apellidos, dni, telefono, correo, idRol) {
    // Si viene con ID, es una edición
    if (id) {
        document.getElementById('tituloModalUsuario').innerText = 'Editar Usuario';
        document.getElementById('inputIdUsuario').value = id;
        document.getElementById('inputNombres').value = nombres || '';
        document.getElementById('inputApellidos').value = apellidos || '';
        document.getElementById('inputDni').value = dni || '';
        document.getElementById('inputTelefono').value = telefono || '';
        document.getElementById('inputCorreo').value = correo || '';
        document.getElementById('inputRol').value = idRol;
        // La contraseña se deja vacía por seguridad
        document.getElementById('inputPassword').value = '';
        document.getElementById('inputPassword').placeholder = 'Nueva contraseña (Opcional)';
        document.getElementById('inputPassword').removeAttribute('required');
    } else {
        // Es un nuevo usuario, vaciamos todo
        document.getElementById('tituloModalUsuario').innerText = 'Nuevo Usuario';
        document.getElementById('inputIdUsuario').value = '';
        document.getElementById('inputNombres').value = '';
        document.getElementById('inputApellidos').value = '';
        document.getElementById('inputDni').value = '';
        document.getElementById('inputTelefono').value = '';
        document.getElementById('inputCorreo').value = '';
        document.getElementById('inputRol').value = '';
        document.getElementById('inputPassword').value = '';
        document.getElementById('inputPassword').placeholder = 'Contraseña';
        document.getElementById('inputPassword').setAttribute('required', 'true');
    }

    // Mostrar el modal
    document.getElementById('modalUsuario').style.display = 'flex';
}
// ==========================================
// BUSCADOR DINÁMICO EN TIEMPO REAL (PANEL)
// ==========================================
document.addEventListener('DOMContentLoaded', () => {
    const buscadorUsuarios = document.getElementById('buscadorUsuarios');
    const tablaUsuariosBody = document.getElementById('tablaUsuariosBody');

    if (buscadorUsuarios && tablaUsuariosBody) {
        buscadorUsuarios.addEventListener('input', function() {
            // Convertimos lo que el usuario escribe a minúsculas
            const filtro = this.value.toLowerCase();
            const filas = tablaUsuariosBody.getElementsByTagName('tr');

            // Recorremos todas las filas de la tabla
            for (let i = 0; i < filas.length; i++) {
                // Obtenemos la columna de Nombres (índice 1) y DNI (índice 2)
                const colNombre = filas[i].getElementsByTagName('td')[1];
                const colDni = filas[i].getElementsByTagName('td')[2];
                
                if (colNombre || colDni) {
                    const textoNombre = colNombre.textContent || colNombre.innerText;
                    const textoDni = colDni.textContent || colDni.innerText;
                    
                    // Verificamos si el texto escrito coincide con el nombre o el DNI
                    if (textoNombre.toLowerCase().indexOf(filtro) > -1 || textoDni.toLowerCase().indexOf(filtro) > -1) {
                        filas[i].style.display = ""; // Muestra la fila
                    } else {
                        filas[i].style.display = "none"; // Oculta la fila
                    }
                }
            }
        });
    }
});
// ==========================================
// OCULTAR ALERTAS AUTOMÁTICAMENTE (PANEL)
// ==========================================
document.addEventListener('DOMContentLoaded', () => {
    const alertas = document.querySelectorAll('.alerta-mensaje');
    
    if (alertas.length > 0) {
        alertas.forEach(alerta => {
            // Esperar 4 segundos antes de iniciar el desvanecimiento
            setTimeout(() => {
                alerta.style.opacity = '0';
                
                // Esperar medio segundo más para que termine la animación CSS y luego quitar el espacio
                setTimeout(() => {
                    alerta.style.display = 'none';
                }, 500);
            }, 4000);
        });
    }
});
// ==========================================
// MODAL CRUD NEGOCIOS
// ==========================================
function abrirModalNegocio(id, nombre, direccion, latitud, longitud, telefono, estado, idTipo, idZona, idCentro) {
    if (id) {
        document.getElementById('tituloModalNegocio').innerText = 'Editar Negocio';
        document.getElementById('inputIdNegocio').value = id;
        document.getElementById('inputNombreNegocio').value = nombre || '';
        document.getElementById('inputDireccionNegocio').value = direccion || '';
        document.getElementById('inputLatitud').value = latitud || '';
        document.getElementById('inputLongitud').value = longitud || '';
        document.getElementById('inputTelefonoNegocio').value = telefono || '';
        document.getElementById('inputEstado').value = estado || 'ABIERTO';
        document.getElementById('inputTipoNegocio').value = idTipo || '';
        document.getElementById('inputZona').value = idZona || '';
        document.getElementById('inputCentro').value = idCentro || '';
    } else {
        document.getElementById('tituloModalNegocio').innerText = 'Nuevo Negocio';
        document.getElementById('inputIdNegocio').value = '';
        document.getElementById('inputNombreNegocio').value = '';
        document.getElementById('inputDireccionNegocio').value = '';
        document.getElementById('inputLatitud').value = '';
        document.getElementById('inputLongitud').value = '';
        document.getElementById('inputTelefonoNegocio').value = '';
        document.getElementById('inputEstado').value = 'ABIERTO';
        document.getElementById('inputTipoNegocio').value = '';
        document.getElementById('inputZona').value = '';
        document.getElementById('inputCentro').value = '';
    }
    document.getElementById('modalNegocio').style.display = 'flex';
}

// ==========================================
// BUSCADOR DINÁMICO EN TIEMPO REAL (NEGOCIOS)
// ==========================================
document.addEventListener('DOMContentLoaded', () => {
    const buscadorNegocios = document.getElementById('buscadorNegocios');
    const tablaNegociosBody = document.getElementById('tablaNegociosBody');

    if (buscadorNegocios && tablaNegociosBody) {
        buscadorNegocios.addEventListener('input', function() {
            const filtro = this.value.toLowerCase();
            const filas = tablaNegociosBody.getElementsByTagName('tr');

            for (let i = 0; i < filas.length; i++) {
                // Buscamos coincidencia en Nombre (índice 1)
                const colNombre = filas[i].getElementsByTagName('td')[1];
                
                if (colNombre) {
                    const textoNombre = colNombre.textContent || colNombre.innerText;
                    if (textoNombre.toLowerCase().indexOf(filtro) > -1) {
                        filas[i].style.display = ""; 
                    } else {
                        filas[i].style.display = "none"; 
                    }
                }
            }
        });
    }
});
// ==========================================
// MODAL CRUD CATEGORÍAS
// ==========================================
function abrirModalCategoria(id, nombre, idNegocio) {
    if (id) {
        document.getElementById('tituloModalCategoria').innerText = 'Editar Categoría';
        document.getElementById('inputIdCategoria').value = id;
        document.getElementById('inputNombreCategoria').value = nombre || '';
        document.getElementById('inputNegocioCategoria').value = idNegocio || '';
    } else {
        document.getElementById('tituloModalCategoria').innerText = 'Nueva Categoría';
        document.getElementById('inputIdCategoria').value = '';
        document.getElementById('inputNombreCategoria').value = '';
        document.getElementById('inputNegocioCategoria').value = '';
    }
    document.getElementById('modalCategoria').style.display = 'flex';
}

// ==========================================
// BUSCADOR DINÁMICO EN TIEMPO REAL (CATEGORÍAS)
// ==========================================
document.addEventListener('DOMContentLoaded', () => {
    const buscadorCategorias = document.getElementById('buscadorCategorias');
    const tablaCategoriasBody = document.getElementById('tablaCategoriasBody');

    if (buscadorCategorias && tablaCategoriasBody) {
        buscadorCategorias.addEventListener('input', function() {
            const filtro = this.value.toLowerCase();
            const filas = tablaCategoriasBody.getElementsByTagName('tr');

            for (let i = 0; i < filas.length; i++) {
                // Buscamos coincidencia en la columna de Categoría (índice 1)
                const colCategoria = filas[i].getElementsByTagName('td')[1];
                
                if (colCategoria) {
                    const textoCategoria = colCategoria.textContent || colCategoria.innerText;
                    if (textoCategoria.toLowerCase().indexOf(filtro) > -1) {
                        filas[i].style.display = ""; 
                    } else {
                        filas[i].style.display = "none"; 
                    }
                }
            }
        });
    }
});
// ==========================================
// MODAL CRUD PRODUCTOS
// ==========================================
function abrirModalProducto(id, nombre, descripcion, precio, disponible, idNegocio, idCategoria) {
    if (id) {
        document.getElementById('tituloModalProducto').innerText = 'Editar Producto';
        document.getElementById('inputIdProducto').value = id;
        document.getElementById('inputNombreProducto').value = nombre || '';
        document.getElementById('inputDescripcionProducto').value = descripcion || '';
        document.getElementById('inputPrecioProducto').value = precio || '';
        // Convertimos el booleano en texto para el selector
        document.getElementById('inputDisponibleProducto').value = (disponible !== false) ? 'true' : 'false';
        document.getElementById('inputNegocioProducto').value = idNegocio || '';
        document.getElementById('inputCategoriaProducto').value = idCategoria || '';
    } else {
        document.getElementById('tituloModalProducto').innerText = 'Nuevo Producto';
        document.getElementById('inputIdProducto').value = '';
        document.getElementById('inputNombreProducto').value = '';
        document.getElementById('inputDescripcionProducto').value = '';
        document.getElementById('inputPrecioProducto').value = '';
        document.getElementById('inputDisponibleProducto').value = 'true';
        document.getElementById('inputNegocioProducto').value = '';
        document.getElementById('inputCategoriaProducto').value = '';
    }
    document.getElementById('modalProducto').style.display = 'flex';
}

// ==========================================
// BUSCADOR DINÁMICO DUAL (PRODUCTOS O NEGOCIO)
// ==========================================
document.addEventListener('DOMContentLoaded', () => {
    const buscadorProductos = document.getElementById('buscadorProductos');
    const tablaProductosBody = document.getElementById('tablaProductosBody');

    if (buscadorProductos && tablaProductosBody) {
        buscadorProductos.addEventListener('input', function() {
            const filtro = this.value.toLowerCase();
            const filas = tablaProductosBody.getElementsByTagName('tr');

            for (let i = 0; i < filas.length; i++) {
                // Obtenemos la columna Nombre (índice 1) y Negocio (índice 3)
                const colNombre = filas[i].getElementsByTagName('td')[1];
                const colNegocio = filas[i].getElementsByTagName('td')[3];
                
                if (colNombre || colNegocio) {
                    const textoNombre = colNombre ? (colNombre.textContent || colNombre.innerText) : "";
                    const textoNegocio = colNegocio ? (colNegocio.textContent || colNegocio.innerText) : "";
                    
                    // Comprobamos si el texto escrito coincide con ALGUNA de las dos columnas
                    if (textoNombre.toLowerCase().indexOf(filtro) > -1 || textoNegocio.toLowerCase().indexOf(filtro) > -1) {
                        filas[i].style.display = ""; 
                    } else {
                        filas[i].style.display = "none"; 
                    }
                }
            }
        });
    }
});
// ==========================================
// MODAL Y BÚSQUEDA DE PROMOCIONES
// ==========================================
function abrirModalPromo(id, titulo, desc, fInicio, fFin, hInicio, hFin, dias, enIndex, estado, idNegocio) {
    // 1. Limpiar checkboxes de días
    document.querySelectorAll('.check-dia').forEach(chk => chk.checked = false);

    if (id) {
        document.getElementById('tituloModalPromo').innerText = 'Editar Promoción';
        document.getElementById('inputIdPromo').value = id;
        document.getElementById('inputTituloPromo').value = titulo || '';
        document.getElementById('inputFechaInicio').value = fInicio ? fInicio.substring(0, 16) : '';
        document.getElementById('inputFechaFin').value = fFin ? fFin.substring(0, 16) : '';
        document.getElementById('inputHoraInicio').value = hInicio || '';
        document.getElementById('inputHoraFin').value = hFin || '';
        document.getElementById('inputIndexPromo').checked = (enIndex === true || enIndex === 'true');
        document.getElementById('inputEstadoPromo').value = (estado !== false) ? 'true' : 'false';
        document.getElementById('inputNegocioPromo').value = idNegocio || '';
        
        // Marcar los checkboxes guardados (ej: "1,3,5")
        if (dias) {
            dias.split(',').forEach(dia => {
                let checkbox = document.querySelector(`.check-dia[value="${dia}"]`);
                if (checkbox) checkbox.checked = true;
            });
        }
    } else {
        document.getElementById('tituloModalPromo').innerText = 'Nueva Promoción';
        document.getElementById('inputIdPromo').value = '';
        document.getElementById('inputTituloPromo').value = '';
        document.getElementById('inputFechaInicio').value = '';
        document.getElementById('inputFechaFin').value = '';
        document.getElementById('inputHoraInicio').value = '';
        document.getElementById('inputHoraFin').value = '';
        document.getElementById('inputIndexPromo').checked = false;
        document.getElementById('inputEstadoPromo').value = 'true';
        document.getElementById('inputNegocioPromo').value = '';
    }
    document.getElementById('modalPromo').style.display = 'flex';
}

// Búsqueda por Negocio (Índice 3 en la tabla)
document.addEventListener('DOMContentLoaded', () => {
    const buscadorPromos = document.getElementById('buscadorPromociones');
    const tablaPromosBody = document.getElementById('tablaPromocionesBody');

    if (buscadorPromos && tablaPromosBody) {
        buscadorPromos.addEventListener('input', function() {
            const filtro = this.value.toLowerCase();
            const filas = tablaPromosBody.getElementsByTagName('tr');
            for (let i = 0; i < filas.length; i++) {
                const colNegocio = filas[i].getElementsByTagName('td')[3];
                if (colNegocio) {
                    const texto = colNegocio.textContent || colNegocio.innerText;
                    filas[i].style.display = texto.toLowerCase().indexOf(filtro) > -1 ? "" : "none";
                }
            }
        });
    }
});
// ==========================================
// CARRUSEL DE PROMOCIONES: SWIPE / ARRASTRAR
// ==========================================
document.addEventListener('DOMContentLoaded', () => {
    const carrusel = document.querySelector('.banner-promociones');
    if (!carrusel) return;

    let posXInicial = 0;
    let isDragging = false;
    let esUnArrastre = false;

    const iniciarArrastre = (e) => {
        isDragging = true;
        esUnArrastre = false;
        posXInicial = e.type.includes('mouse') ? e.pageX : e.touches[0].clientX;
    };

    const moverArrastre = (e) => {
        if (!isDragging) return;
        const posXActual = e.type.includes('mouse') ? e.pageX : e.touches[0].clientX;
        const distancia = Math.abs(posXInicial - posXActual);
        
        if (distancia > 10) esUnArrastre = true;
    };

    const finalizarArrastre = (e) => {
        if (!isDragging) return;
        isDragging = false;

        if (!esUnArrastre) return; 

        // Recuperar posición final exacta (incluso si el mouse sale del cuadro)
        let posXFinal = 0;
        if(e.type === 'mouseleave' || e.type === 'mouseup') {
            posXFinal = e.pageX;
        } else if (e.changedTouches) {
            posXFinal = e.changedTouches[0].clientX;
        }

        const diferencia = posXInicial - posXFinal;
        
        const puntos = Array.from(document.querySelectorAll('.promo-dots .dot'));
        if (puntos.length <= 1) return; 
        
        let indiceActual = puntos.findIndex(punto => punto.classList.contains('activo'));
        if (indiceActual === -1) indiceActual = 0;

        // Sensibilidad ajustada a 40px para que se sienta fluido
        if (diferencia > 40) { 
            cambiarSlide((indiceActual + 1) % puntos.length);
        } else if (diferencia < -40) {
            cambiarSlide((indiceActual - 1 + puntos.length) % puntos.length);
        }
    };

    // Eventos PC
    carrusel.addEventListener('mousedown', iniciarArrastre);
    carrusel.addEventListener('mousemove', moverArrastre);
    carrusel.addEventListener('mouseup', finalizarArrastre);
    carrusel.addEventListener('mouseleave', finalizarArrastre); 

    // Eventos Móvil
    carrusel.addEventListener('touchstart', iniciarArrastre, {passive: true});
    carrusel.addEventListener('touchmove', moverArrastre, {passive: true});
    carrusel.addEventListener('touchend', finalizarArrastre);

    // GESTIÓN INTELIGENTE DEL CLIC
    const slides = document.querySelectorAll('.promo-slide');
    slides.forEach(slide => {
        slide.addEventListener('click', (e) => {
            // Si estaba arrastrando, detenemos todo para que no abra el restaurante
            if (esUnArrastre) {
                e.preventDefault();
                e.stopPropagation();
                return;
            }
            
            // Si fue un clic simple, redirigimos
            const idNegocio = slide.getAttribute('data-negocio');
            if (idNegocio) {
                abrirRestaurante(idNegocio);
            }
        });
    });
});

// ==========================================
// FUNCIÓN CAMBIAR SLIDE (Si no la tienes aún)
// ==========================================
function cambiarSlide(indice) {
    const slides = document.querySelectorAll('.promo-slide');
    const dots = document.querySelectorAll('.promo-dots .dot');
    
    if (slides.length === 0) return;

    // Remover la clase 'activo' de todos
    slides.forEach(s => s.classList.remove('activo'));
    dots.forEach(d => d.classList.remove('activo'));

    // Añadir la clase 'activo' al nuevo índice
    slides[indice].classList.add('activo');
    dots[indice].classList.add('activo');
}
// ==========================================
// MODAL CRUD TIPOS DE NEGOCIO
// ==========================================
function abrirModalTipoNegocio(id, nombre, icono, estado) {
    if (id) {
        document.getElementById('tituloModalTipo').innerText = 'Editar Tipo de Negocio';
        document.getElementById('inputIdTipo').value = id;
        document.getElementById('inputNombreTipo').value = nombre || '';
        //document.getElementById('inputIconoTipo').value = icono || '';
        document.getElementById('inputEstadoTipo').value = (estado !== false) ? 'true' : 'false';
    } else {
        document.getElementById('tituloModalTipo').innerText = 'Nuevo Tipo de Negocio';
        document.getElementById('inputIdTipo').value = '';
        document.getElementById('inputNombreTipo').value = '';
        //document.getElementById('inputIconoTipo').value = '';
        document.getElementById('inputEstadoTipo').value = 'true';
    }
    document.getElementById('modalTipoNegocio').style.display = 'flex';
}

// ==========================================
// BUSCADOR DINÁMICO EN TIEMPO REAL (TIPOS)
// ==========================================
document.addEventListener('DOMContentLoaded', () => {
    const buscadorTipos = document.getElementById('buscadorTipos');
    const tablaTiposBody = document.getElementById('tablaTiposBody');

    if (buscadorTipos && tablaTiposBody) {
        buscadorTipos.addEventListener('input', function() {
            const filtro = this.value.toLowerCase();
            const filas = tablaTiposBody.getElementsByTagName('tr');

            for (let i = 0; i < filas.length; i++) {
                // Buscamos coincidencia en la columna Nombre (índice 1)
                const colNombre = filas[i].getElementsByTagName('td')[1];
                
                if (colNombre) {
                    const textoNombre = colNombre.textContent || colNombre.innerText;
                    if (textoNombre.toLowerCase().indexOf(filtro) > -1) {
                        filas[i].style.display = ""; 
                    } else {
                        filas[i].style.display = "none"; 
                    }
                }
            }
        });
    }
});