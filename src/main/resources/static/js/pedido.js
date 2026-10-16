// ==========================================
// ABRIR MODAL
// ==========================================

function abrirModalPedido(idNegocio) {
	
	window.negocioPedidoActual =
	        idNegocio;
   


    fetch(
        `/api/pedido/resumen?` +
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
// CONFIRMAR PEDIDO
// ==========================================

function confirmarPedido(boton) {

	    const idNegocio =boton.dataset.negocio;
	    const direccionSeleccionada =document.querySelector('input[name="direccionPedido"]:checked');

	    if (!direccionSeleccionada) {

	        alert("Selecciona una dirección de entrega.");
	        return;
	    }
	    const idDireccion =direccionSeleccionada.value;
	    const metodoPago =document.getElementById("metodoPagoPedido").value;
	    const datos =new URLSearchParams();
		
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
function cancelarPedido(
    idPedido,
    boton
) {

    const confirmar =
        window.confirm(
            "¿Estás seguro de cancelar este pedido?"
        );


    if (!confirmar) {
        return;
    }


    boton.disabled = true;

    boton.innerHTML =
        "Cancelando...";


    fetch(
        `/api/pedido/${idPedido}/cancelar` +

        {
            method: "POST",

            headers: {
                "Accept":
                    "application/json"
            }
        }
    )

    .then(async response => {

        const resultado =
            await response.json();


        if (!response.ok) {

            throw new Error(
                resultado.mensaje ||
                "No se pudo cancelar"
            );
        }


        return resultado;
    })

    .then(resultado => {


        mostrarToast(
            "Pedido cancelado",
            "La orden fue cancelada correctamente."
        );


        // Volvemos a consultar el modal
        // para actualizar el estado visual

        return mostrarPedidoConfirmado(
            idPedido
        );
    })

    .catch(error => {

        console.error(
            "Error cancelando pedido:",
            error
        );


        mostrarToast(
            "No se pudo cancelar",
            error.message
        );


        boton.disabled = false;

        boton.innerHTML =
            `<i class="fa-solid fa-ban"></i>
             Cancelar orden`;
    });
}
function mostrarPedidoConfirmado(
    idPedido
) {

	const contenedor =
	        document.getElementById(
	            "contenedor-modal-pedido"
	        );


	    if (!contenedor) {

	        console.error(
	            "No existe #contenedor-modal-pedido"
	        );

	        return Promise.reject(
	            new Error(
	                "Contenedor modal inexistente"
	            )
	        );
	    }


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
function filtrarPedidos(boton) {

    const filtro =
        boton.dataset.filtro;


    // Cambiar botón activo

    document
        .querySelectorAll(
            ".filtro-pedido"
        )
        .forEach(btn => {

            btn.classList.remove(
                "activo"
            );

        });


    boton.classList.add(
        "activo"
    );


    const tarjetas =
        document.querySelectorAll(
            ".pedido-cliente-card"
        );


    let visibles = 0;


    tarjetas.forEach(tarjeta => {

        const estado =
            tarjeta.dataset.estado;


        let mostrar = false;


        if (filtro === "TODOS") {

            mostrar = true;

        }

        else if (filtro === "CURSO") {

            mostrar =
                estado ===
                    "PENDIENTE_DE_WHATSAPP"

                ||

                estado ===
                    "CONFIRMADO"

                ||

                estado ===
                    "EN_CAMINO";

        }

        else {

            mostrar =
                estado === filtro;

        }


        tarjeta.style.display =
            mostrar
                ? ""
                : "none";


        if (mostrar) {
            visibles++;
        }
    });


    const vacio =
        document.getElementById(
            "sinPedidosFiltro"
        );


    if (vacio) {

        vacio.style.display =
            visibles === 0
                ? "block"
                : "none";
    }
	}
	
function cancelarPedidoDesdeLista(
	    idPedido,
	    boton
	) {


	    if (
	        !window.confirm(
	            "¿Deseas cancelar este pedido?"
	        )
	    ) {

	        return;
	    }


	    boton.disabled = true;

	    boton.textContent =
	        "Cancelando...";


	    fetch(
	        `/api/pedido/${idPedido}/cancelar` +
	        {
	            method: "POST",

	            headers: {
	                "Accept":
	                    "application/json"
	            }
	        }
	    )

	    .then(response => {

	        if (!response.ok) {

	            throw new Error(
	                "No se pudo cancelar el pedido"
	            );
	        }


	        return response.json();
	    })

	    .then(resultado => {


	        mostrarToast(
	            "Pedido cancelado",
	            "La orden fue cancelada correctamente."
	        );


	        setTimeout(() => {

	            window.location.reload();

	        }, 700);

	    })

	    .catch(error => {

	        console.error(
	            error
	        );


	        mostrarToast(
	            "No se pudo cancelar",
	            "Inténtalo nuevamente."
	        );


	        boton.disabled = false;

	        boton.textContent =
	            "Cancelar";
	    });
	}
	
	function cerrarDetallePedido() {

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
	function enviarPedidoWhatsApp() {


	    // ==========================================
	    // MODAL ACTUAL
	    // ==========================================

	    const modal =
	        document.getElementById(
	            "modalPedidoConfirmado"
	        );


	    if (!modal) {

	        console.error(
	            "No existe el modal del pedido"
	        );

	        return;
	    }


	    // ==========================================
	    // DATOS GENERALES
	    // ==========================================

	    const idPedido =
	        modal.dataset.pedido;


	    const negocio =
	        modal.dataset.negocio || "";


	    const direccion =
	        modal.dataset.direccion || "";


	    const referencia =
	        modal.dataset.referencia || "";


	    const metodoPago =
	        modal.dataset.metodoPago || "";


	    const subtotal =
	        Number(
	            modal.dataset.subtotal || 0
	        );


	    const envio =
	        Number(
	            modal.dataset.envio || 0
	        );


	    const total =
	        Number(
	            modal.dataset.total || 0
	        );


	    // ==========================================
	    // TELÉFONO
	    // ==========================================

	    let telefono =
	        modal.dataset.telefono || "";


	    telefono =
	        telefono.replace(
	            /\D/g,
	            ""
	        );


	    if (!telefono) {

	        mostrarToast(
	            "WhatsApp no disponible",
	            "Este negocio no tiene un número de WhatsApp registrado."
	        );

	        return;
	    }


	    // Si en BD guardas:
	    //
	    // 987654321
	    //
	    // agregamos código Perú.

	    if (telefono.length === 9) {

	        telefono =
	            "51" + telefono;
	    }


	    // ==========================================
	    // PRODUCTOS
	    // ==========================================

	    const productos =
	        modal.querySelectorAll(
	            ".producto-pedido-wsp"
	        );


	    let productosTexto = "";


	    productos.forEach(
	        producto => {


	            const nombre =
	                producto.dataset.nombre;


	            const cantidad =
	                Number(
	                    producto.dataset.cantidad
	                );


	            const precio =
	                Number(
	                    producto.dataset.precio
	                );


	            const subtotalProducto =
	                cantidad * precio;


	            productosTexto +=
	                `• ${cantidad}x ${nombre}` +
	                ` - S/ ${subtotalProducto.toFixed(2)}\n`;

	        }
	    );


	    // ==========================================
	    // MENSAJE
	    // ==========================================

	    let mensaje =

	`Hola, acabo de realizar un pedido en Dablyn Delivery.

	🧾 *PEDIDO #${idPedido}*

	🏪 *Negocio:*
	${negocio}

	🍽️ *PRODUCTOS:*
	${productosTexto}
	💰 *RESUMEN:*
	Productos: S/ ${subtotal.toFixed(2)}
	Envío: S/ ${envio.toFixed(2)}
	*Total: S/ ${total.toFixed(2)}*

	📍 *DIRECCIÓN DE ENTREGA:*
	${direccion}`;


	    // Referencia opcional

	    if (referencia) {

	        mensaje +=
	`\n
	Referencia:
	${referencia}`;
	    }


	    mensaje +=
	`\n
	💳 *MÉTODO DE PAGO:*
	${metodoPago}

	Quedo atento a la confirmación del pedido.`;


	    // ==========================================
	    // ABRIR WHATSAPP
	    // ==========================================

	    const urlWhatsApp =
	        `https://wa.me/${telefono}` +
	        `?text=${encodeURIComponent(mensaje)}`;


	    window.open(
	        urlWhatsApp,
	        "_blank",
	        "noopener,noreferrer"
	    );
	}
	function recalcularEnvioPedido(radio) {


	    if (!radio) {
	        return;
	    }


	    

	    const modal =
	        document.getElementById(
	            "modalPedido"
	        );


	    if (!modal) {

	        console.error(
	            "No existe #modalPedido"
	        );

	        return;
	    }


	    const idNegocio =
	        modal.dataset.negocio;


	    const idDireccionUsuario =
	        radio.value;


	    const envioElemento =
	        document.getElementById(
	            "costoEnvioPedido"
	        );


	    const totalElemento =
	        document.getElementById(
	            "totalPedido"
	        );


	    const distanciaElemento =
	        document.getElementById(
	            "distanciaDeliveryPedido"
	        );


	    const botonPedido =
	        document.querySelector(
	            ".btn-hacer-pedido"
	        );


	    // ==========================================
	    // ESTADO CARGANDO
	    // ==========================================

	    if (botonPedido) {

	        botonPedido.disabled = true;
	    }


	    if (envioElemento) {

	        envioElemento.textContent =
	            "...";
	    }


	    // ==========================================
	    // BACKEND
	    // ==========================================

	    const parametros =
	        new URLSearchParams({

	         

	            idNegocio:
	                idNegocio,

	            idDireccionUsuario:
	                idDireccionUsuario
	        });


	    fetch(
	        `/api/pedido/calcular-envio?${parametros}`
	    )

	    .then(async response => {


	        const resultado =
	            await response.json();


	        if (!response.ok) {

	            throw new Error(
	                resultado.mensaje ||
	                "No se pudo calcular el delivery."
	            );
	        }


	        return resultado;
	    })

	    .then(resultado => {


	        // ======================================
	        // COSTO ENVÍO
	        // ======================================

	        if (envioElemento) {

	            envioElemento.textContent =
	                Number(
	                    resultado.costoEnvio
	                ).toFixed(2);
	        }


	        // ======================================
	        // TOTAL
	        // ======================================

	        if (totalElemento) {

	            totalElemento.textContent =
	                Number(
	                    resultado.total
	                ).toFixed(2);
	        }


	        // ======================================
	        // DISTANCIA
	        // ======================================

	        if (distanciaElemento) {

	            distanciaElemento.textContent =
	                `(${Number(
	                    resultado.distanciaKm
	                ).toFixed(2)} km)`;
	        }


	        // ======================================
	        // HABILITAR PEDIDO
	        // ======================================

	        if (botonPedido) {

	            botonPedido.disabled = false;
	        }

	    })

	    .catch(error => {


	        console.error(
	            error
	        );


	        if (envioElemento) {

	            envioElemento.textContent =
	                "--";
	        }


	        if (botonPedido) {

	            botonPedido.disabled = true;
	        }


	        mostrarToast(
	            "No se pudo calcular el envío",
	            error.message
	        );
	    });
	}
	
