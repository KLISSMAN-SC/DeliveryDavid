// ==========================================
// ABRIR MODAL
// ==========================================

function abrirModalPedido(idNegocio) {

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

    const idUsuario = obtenerIdUsuario();


    const idNegocio =
        boton.dataset.negocio;


    // DIRECCIÓN SELECCIONADA

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
        "Procesando...";


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

        console.log(
            "Pedido creado:",
            resultado
        );


        alert(
            "Pedido #" +
            resultado.idPedido +
            " registrado correctamente."
        );


        cerrarModalPedido();


        location.reload();

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