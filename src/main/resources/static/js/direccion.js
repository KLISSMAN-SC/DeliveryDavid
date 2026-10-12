
// ==========================================
// INICIALIZAR DIRECCIONES
// ==========================================

function inicializarDireccionesPedido() {

    const radios =
        document.querySelectorAll(
            'input[name="direccionPedido"]'
        );


    radios.forEach(radio => {

        radio.addEventListener(
            "change",
            function() {

                actualizarMapaDireccion(
                    this
                );

            }
        );

    });


    const seleccionada =
        document.querySelector(
            'input[name="direccionPedido"]:checked'
        );


    if (seleccionada) {

        actualizarMapaDireccion(
            seleccionada
        );
    }
}



// ==========================================
// CAMBIAR PUNTO DEL MAPA
// ==========================================

function actualizarMapaDireccion(radio) {

    const modal =
        document.getElementById(
            "modalPedido"
        );


    if (!modal) {
        return;
    }


    const googleKey =
        modal.dataset.googleKey;


    const direccion =
        radio.dataset.direccion;


    const referencia =
        radio.dataset.referencia || "";


    const latitud =
        radio.dataset.latitud;


    const longitud =
        radio.dataset.longitud;


    // Actualizar textos

    const direccionMapa =
        document.getElementById(
            "direccionMapa"
        );


    const referenciaMapa =
        document.getElementById(
            "referenciaMapa"
        );


    if (direccionMapa) {

        direccionMapa.textContent =
            direccion;
    }


    if (referenciaMapa) {

        referenciaMapa.textContent =
            referencia;
    }


    // Actualizar mapa

    const mapa =
        document.getElementById(
            "mapaPedido"
        );


    if (
        mapa &&
        googleKey &&
        latitud &&
        longitud
    ) {

        const coordenadas =
            `${latitud},${longitud}`;


        mapa.src =
            `https://www.google.com/maps/embed/v1/place` +
            `?key=${encodeURIComponent(googleKey)}` +
            `&q=${encodeURIComponent(coordenadas)}`;
    }
}

function abrirModalAgregarDireccion(
    origen = "general"
) {

    window.origenModalDireccion =
        origen;


    const modal =
        document.getElementById(
            "modalDireccion"
        );


    if (!modal) {
        return;
    }


    volverElegirDireccion();


    modal.classList.add(
        "activo"
    );


    document.body.style.overflow =
        "hidden";
}


function detectarUbicacionActual() {

	if (!navigator.geolocation) {

	        alert(
	            "Tu navegador no permite obtener tu ubicación."
	        );

	        return;
	    }


	    navigator.geolocation.getCurrentPosition(

	        function(position) {

	            const latitud =
	                position.coords.latitude;

	            const longitud =
	                position.coords.longitude;


	            mostrarUbicacionDetectada(
	                latitud,
	                longitud
	            );
	        },


	        function(error) {

	            console.error(
	                "Error ubicación:",
	                error
	            );


	            if (error.code === 1) {

	                alert(
	                    "No permitiste acceder a tu ubicación."
	                );

	            } else if (error.code === 2) {

	                alert(
	                    "No pudimos determinar tu ubicación."
	                );

	            } else if (error.code === 3) {

	                alert(
	                    "La solicitud de ubicación tardó demasiado."
	                );

	            } else {

	                alert(
	                    "No se pudo obtener tu ubicación."
	                );
	            }
	        },


	        {
	            enableHighAccuracy: true,
	            timeout: 10000,
	            maximumAge: 0
	        }
	    );
}
function mostrarUbicacionDetectada(
    latitud,
    longitud
) {
	const modalDireccion =
	        document.getElementById(
	            "modalDireccion"
	        );


	    if (!modalDireccion) {

	        console.error(
	            "No existe #modalDireccion"
	        );

	        return;
	    }


	    const googleKey =
	        modalDireccion.dataset.googleKey;


	    if (!googleKey) {

	        console.error(
	            "No se encontró la Google Maps API Key"
	        );

	        return;
	    }


	    const coordenadas =
	        `${latitud},${longitud}`;


	    // Guardar coordenadas

	    document.getElementById(
	        "nuevaLatitud"
	    ).value = latitud;


	    document.getElementById(
	        "nuevaLongitud"
	    ).value = longitud;


	    // Mostrar mapa

	    const mapa =
	        document.getElementById(
	            "mapaNuevaDireccion"
	        );


	    mapa.src =
	        `https://www.google.com/maps/embed/v1/place` +
	        `?key=${encodeURIComponent(googleKey)}` +
	        `&q=${encodeURIComponent(coordenadas)}`;


	    // Cambiar al paso de confirmación

	    document.getElementById(
	        "pasoElegirDireccion"
	    ).classList.add(
	        "oculto"
	    );


	    document.getElementById(
	        "pasoDireccionManual"
	    ).classList.add(
	        "oculto"
	    );


	    document.getElementById(
	        "pasoConfirmarDireccion"
	    ).classList.remove(
	        "oculto"
	    );
   
}
function cerrarModalDireccion() {

	const modal =
	        document.getElementById(
	            "modalDireccion"
	        );


	    if (modal) {

	        modal.classList.remove(
	            "activo"
	        );
	    }


	    // OJO:
	    // seguimos teniendo abierto el modal
	    // del pedido, por eso mantenemos
	    // bloqueado el scroll.

	    if (
	        document.getElementById(
	            "modalPedido"
	        )
	    ) {

	        document.body.style.overflow =
	            "hidden";

	    } else {

	        document.body.style.overflow =
	            "";
	    }
}
function guardarNuevaDireccion() {

    const idUsuario =
        obtenerIdUsuario();


    const alias =
        document.getElementById(
            "aliasDireccion"
        ).value.trim();


    const direccion =
        document.getElementById(
            "direccionTexto"
        ).value.trim();


    const referencia =
        document.getElementById(
            "referenciaDireccion"
        ).value.trim();


    const latitud =
        document.getElementById(
            "nuevaLatitud"
        ).value;


    const longitud =
        document.getElementById(
            "nuevaLongitud"
        ).value;


		// ==============================
		    // VALIDACIONES
		    // ==============================

		    if (!alias) {

		        mostrarToast(
		            "Campo obligatorio",
		            "Ingresa un nombre para la dirección."
		        );

		        document.getElementById(
		            "aliasDireccion"
		        ).focus();

		        return;
		    }


		    if (!direccion) {

		        mostrarToast(
		            "Campo obligatorio",
		            "Ingresa la dirección."
		        );

		        document.getElementById(
		            "direccionTexto"
		        ).focus();

		        return;
		    }


    if (!latitud || !longitud) {

		mostrarToast(
		            "Ubicación requerida",
		            "Primero debes detectar tu ubicación."
		        );

        return;
    }
	if (alias.length > 30) {

	        mostrarToast(
	            "Nombre demasiado largo",
	            "El nombre de la dirección no puede superar 30 caracteres."
	        );

	        return;
	    }


	    if (direccion.length > 150) {

	        mostrarToast(
	            "Dirección demasiado larga",
	            "La dirección no puede superar 150 caracteres."
	        );

	        return;
	    }


	    if (referencia.length > 150) {

	        mostrarToast(
	            "Referencia demasiado larga",
	            "La referencia no puede superar 150 caracteres."
	        );

	        return;
	    }


    const datos =
        new URLSearchParams();


    datos.append(
        "idUsuario",
        idUsuario
    );

    datos.append(
        "alias",
        alias
    );

    datos.append(
        "direccion",
        direccion
    );

    datos.append(
        "referencia",
        referencia
    );

    datos.append(
        "latitud",
        latitud
    );

    datos.append(
        "longitud",
        longitud
    );


    fetch(
        "/api/direcciones/agregar",
        {

            method: "POST",

            headers: {

                "Content-Type":
                    "application/x-www-form-urlencoded",

                "Accept":
                    "application/json"
            },

            body:
                datos.toString()
        }
    )

    .then(response => {

        if (!response.ok) {

            throw new Error(
                "No se pudo guardar la dirección"
            );
        }

        return response.json();
    })

	.then(resultado => {

	    cerrarModalDireccion();


	    mostrarToast(
	        "Dirección guardada",
	        "Tu nueva dirección se registró correctamente."
	    );


	    if (
	        window.origenModalDireccion ===
	        "pedido"
	    ) {

	        abrirModalPedido(
	            window.negocioPedidoActual
	        );

	    } else if (
	        window.origenModalDireccion ===
	        "direcciones"
	    ) {

			setTimeout(() => {

			        window.location.reload();

			    }, 700);

	    }


	    window.origenModalDireccion =
	        null;
	})
     

    .catch(error => {

        console.error(
            "Error guardando dirección:",
            error
        );

		mostrarToast(
		            "No se pudo guardar",
		            "Ocurrió un error al registrar la dirección."
		        );
    });
}
function volverElegirDireccion() {

    document.getElementById(
        "pasoElegirDireccion"
    ).classList.remove("oculto");


    document.getElementById(
        "pasoConfirmarDireccion"
    ).classList.add("oculto");


    document.getElementById(
        "pasoDireccionManual"
    ).classList.add("oculto");
}
function mostrarFormularioManual() {

    document.getElementById(
        "pasoElegirDireccion"
    ).classList.add("oculto");


    document.getElementById(
        "pasoConfirmarDireccion"
    ).classList.add("oculto");


    document.getElementById(
        "pasoDireccionManual"
    ).classList.remove("oculto");
}

function eliminarDireccionUsuario(
    boton
) {

    const idDireccionUsuario =
        boton.dataset.direccion;


    const idUsuario =
        obtenerIdUsuario();


    const confirmar =
        window.confirm(
            "¿Deseas eliminar esta dirección?"
        );


    if (!confirmar) {
        return;
    }


    boton.disabled = true;


    fetch(
        `/api/direcciones/${idDireccionUsuario}` +
        `?idUsuario=${idUsuario}`,
        {
            method: "DELETE",

            headers: {
                "Accept":
                    "application/json"
            }
        }
    )

    .then(response => {

        if (!response.ok) {

            throw new Error(
                "No se pudo eliminar la dirección"
            );
        }


        return response.json();
    })

    .then(resultado => {

		mostrarToast(
		        "Dirección eliminada",
		        "La dirección se eliminó correctamente."
		    );


		    const tarjeta =
		        boton.closest(
		            ".direccion-card"
		        );


		    if (tarjeta) {

		        tarjeta.remove();

		    }


		    setTimeout(() => {

		        window.location.reload();

		    }, 700);
        
    })

    .catch(error => {

        console.error(
            "Error eliminando dirección:",
            error
        );


        mostrarToast(
            "No se pudo eliminar",
            "Ocurrió un error al eliminar la dirección."
        );


        boton.disabled = false;
    });
}