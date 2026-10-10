// ==========================================
// ABRIR MODAL
// ==========================================

function abrirModalPedido(idNegocio) {
	
	window.negocioPedidoActual =
	        idNegocio;
    const idUsuario = obtenerIdUsuario(); // TEMPORAL


    fetch(
        `/api/pedido/resumen` +
        `?idUsuario=${idUsuario}` +
        `&idNegocio=${idNegocio}`
    )

    .then(response => {

        if (!response.ok) {

            throw new Error(
                "No se pudo cargar el pedido"
            );
        }

        return response.text();
    })

    .then(html => {

        const contenedor =
            document.getElementById(
                "contenedor-modal-pedido"
            );


        contenedor.innerHTML = html;


        document.body.style.overflow =
            "hidden";


        inicializarDireccionesPedido();

    })

    .catch(error => {

        console.error(
            "Error cargando checkout:",
            error
        );

    });
}



// ==========================================
// CERRAR
// ==========================================

function cerrarModalPedido() {

    const contenedor =
        document.getElementById(
            "contenedor-modal-pedido"
        );


    if (contenedor) {
        contenedor.innerHTML = "";
    }


    document.body.style.overflow = "";
}




// ==========================================
// PROPINA
// ==========================================

function seleccionarPropina(valor) {

    const inputPropina =
        document.getElementById(
            "propinaPedido"
        );


    const propinaResumen =
        document.getElementById(
            "propinaResumen"
        );


    const totalElemento =
        document.getElementById(
            "totalPedido"
        );


    inputPropina.value = valor;


    propinaResumen.textContent =
        Number(valor).toFixed(2);


    const totalBase =
        Number(
            totalElemento.dataset.base
        );


    totalElemento.textContent =
        (
            totalBase +
            Number(valor)
        ).toFixed(2);
}



// ==========================================
// CONFIRMAR PEDIDO
// ==========================================

function confirmarPedido(boton) {

	const idUsuario =
	        obtenerIdUsuario();


	    const idNegocio =
	        boton.dataset.negocio;


	    const direccionSeleccionada =
	        document.querySelector(
	            'input[name="direccionPedido"]:checked'
	        );


	    if (!direccionSeleccionada) {

	        alert(
	            "Selecciona una dirección de entrega."
	        );

	        return;
	    }


	    const idDireccion =
	        direccionSeleccionada.value;


	    const metodoPago =
	        document.getElementById(
	            "metodoPagoPedido"
	        ).value;


	    const propina =
	        document.getElementById(
	            "propinaPedido"
	        ).value;


	    const datos =
	        new URLSearchParams();


	    datos.append(
	        "idUsuario",
	        idUsuario
	    );

	    datos.append(
	        "idNegocio",
	        idNegocio
	    );

	    datos.append(
	        "idDireccion",
	        idDireccion
	    );

	    datos.append(
	        "metodoPago",
	        metodoPago
	    );

	    datos.append(
	        "propina",
	        propina
	    );


	    boton.disabled = true;

	    boton.textContent =
	        "Procesando pedido...";


	    // ====================================
	    // CREAR PEDIDO
	    // ====================================

	    fetch(
	        "/api/pedido/confirmar",
	        {

	            method: "POST",

	            headers: {

	                "Content-Type":
	                    "application/x-www-form-urlencoded"
	            },

	            body:
	                datos.toString()
	        }
	    )


	    .then(response => {

	        if (!response.ok) {

	            throw new Error(
	                "No se pudo registrar el pedido"
	            );
	        }


	        return response.json();
	    })


	    .then(resultado => {


	        if (!resultado.ok) {

	            throw new Error(
	                resultado.mensaje ||
	                "Error creando pedido"
	            );
	        }


	        console.log(
	            "Pedido creado:",
	            resultado.idPedido
	        );


	        // Mostrar segundo modal

	        return mostrarPedidoConfirmado(
	            resultado.idPedido
	        );
	    })


	    .catch(error => {

	        console.error(
	            "Error confirmando pedido:",
	            error
	        );


	        boton.disabled = false;

	        boton.textContent =
	            "Hacer pedido";
	    });
}
function mostrarPedidoConfirmado(
    idPedido
) {

    return fetch(
        `/api/pedido/confirmacion/${idPedido}`
    )

    .then(response => {

        if (!response.ok) {

            throw new Error(
                "No se pudo cargar la confirmación"
            );
        }


        return response.text();
    })


    .then(html => {


        const contenedor =
            document.getElementById(
                "contenedor-modal-pedido"
            );


        if (!contenedor) {

            throw new Error(
                "No existe el contenedor del modal"
            );
        }


        // REEMPLAZAMOS EL CHECKOUT
        // POR EL SEGUIMIENTO

        contenedor.innerHTML =
            html;


        document.body.style.overflow =
            "hidden";
    });
}
function finalizarVistaPedido() {

    const contenedor =
        document.getElementById(
            "contenedor-modal-pedido"
        );


    if (contenedor) {

        contenedor.innerHTML =
            "";
    }


    document.body.style.overflow =
        "";


    // Como el carrito anterior quedó FINALIZADO,
    // recargamos para empezar limpio.

    location.reload();
}
let temporizadorToast;


function mostrarToast(
    titulo,
    mensaje
) {

    const toast =
        document.getElementById(
            "toastNotificacion"
        );


    const toastTitulo =
        document.getElementById(
            "toastTitulo"
        );


    const toastMensaje =
        document.getElementById(
            "toastMensaje"
        );


    if (
        !toast ||
        !toastTitulo ||
        !toastMensaje
    ) {

        return;
    }


    // Texto

    toastTitulo.textContent =
        titulo;


    toastMensaje.textContent =
        mensaje;


    // Si había otro temporizador,
    // lo reiniciamos

    clearTimeout(
        temporizadorToast
    );


    // Mostrar

    toast.classList.add(
        "mostrar"
    );


    // Ocultar después de 3 segundos

    temporizadorToast =
        setTimeout(() => {

            toast.classList.remove(
                "mostrar"
            );

        }, 3000);
}