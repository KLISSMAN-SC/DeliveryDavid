
function abrirRestaurante(idNegocio){

    fetch("/api/restaurante/" + idNegocio)

    .then(response => response.text())

    .then(html => {

        document.getElementById("contenido-principal")
        .innerHTML = html;

        window.scrollTo(0,0);
		
		cargarCarrito(idNegocio)

    });

}
function agregarAlCarrito(boton) {

    const idProducto =
        boton.dataset.producto;

    const idNegocio =
        boton.dataset.negocio;


    // TEMPORAL
    // Después lo reemplazaremos por el usuario de sesión.
    const idUsuario = 1;


    fetch(
        `/api/carrito/agregar` +
        `?idUsuario=${idUsuario}` +
        `&idNegocio=${idNegocio}` +
        `&idProducto=${idProducto}`,
        {
            method: "POST"
        }
    )

    .then(response => {

        if (!response.ok) {
            throw new Error(
                "No se pudo agregar el producto"
            );
        }

        return response.json();

    })

    .then(carrito => {

        console.log(
            "Producto agregado:",
            carrito
        );

        // Actualizar carrito inmediatamente
        mostrarCarrito(carrito);

    })

    .catch(error => {

        console.error(
            "Error:",
            error
        );

    });
}
function cargarCarrito(idNegocio) {

    const idUsuario = 1; // temporal

    console.log("Consultando carrito del negocio:", idNegocio);

    fetch(
        `/api/carrito/actual` +
        `?idUsuario=${idUsuario}` +
        `&idNegocio=${idNegocio}`
    )

    .then(response => {

        console.log(
            "Respuesta GET carrito:",
            response.status
        );

        if (!response.ok) {
            throw new Error(
                "No se pudo obtener el carrito"
            );
        }

        // Si tu Controller devuelve null,
        // puede venir una respuesta vacía.
        return response.text();
    })

    .then(texto => {

        // No existe carrito todavía
        if (!texto) {

            mostrarCarrito({
                detalles: []
            });

            return;
        }

        const carrito = JSON.parse(texto);

        console.log(
            "Carrito recuperado de BD:",
            carrito
        );

        mostrarCarrito(carrito);
    })

    .catch(error => {

        console.error(
            "Error cargando carrito:",
            error
        );

    });
}

function mostrarCarrito(carrito) {

    const contenedor =
        document.getElementById(
            "contenido-carrito"
        );


    if (!contenedor) {
        return;
    }


    // Carrito sin productos
    if (
        !carrito.detalles ||
        carrito.detalles.length === 0
    ) {

        contenedor.innerHTML = `

            <div class="carrito-vacio">

                <i class="fa-solid fa-bag-shopping"></i>

                <p>
                    Tu pedido está vacío
                </p>

            </div>

        `;

        return;
    }


    let html = "";


    carrito.detalles.forEach(
        detalle => {

            const producto =
                detalle.producto;


            const subtotal =
                Number(
                    detalle.precioUnitario
                ) *
                detalle.cantidad;


            html += `

                <div class="item-carrito">

                    <div class="item-carrito-info">

                        <h4>
                            ${producto.nombre}
                        </h4>

                        <span>
                            S/
                            ${Number(
                                detalle.precioUnitario
                            ).toFixed(2)}
                        </span>

                    </div>


                    <div
                        class="item-carrito-cantidad">

                        <button
                            onclick="disminuirProducto(
                                ${producto.idProducto},
                                ${carrito.negocio.idNegocio}
                            )">

                            −

                        </button>


                        <span>
                            ${detalle.cantidad}
                        </span>


                        <button
                            onclick="aumentarProducto(
                                ${producto.idProducto},
                                ${carrito.negocio.idNegocio}
                            )">

                            +

                        </button>
						
						<button
							class="btn-eliminar-carrito"
							onclick="eliminarProducto(
							${producto.idProducto},
							${carrito.negocio.idNegocio}
							)"
							title="Eliminar producto">
							<i class="fa-solid fa-trash"></i>

						</button>

                    </div>


                    <strong>

                        S/
                        ${subtotal.toFixed(2)}

                    </strong>

                </div>

            `;
        }
    );


    // Calcular subtotal
    let subtotal = 0;


    carrito.detalles.forEach(
        detalle => {

            subtotal +=
                Number(
                    detalle.precioUnitario
                ) *
                detalle.cantidad;

        }
    );


	const envio =
	    carrito.negocio &&
	    carrito.negocio.zona
	        ? Number(
	            carrito.negocio.zona.costoEnvioBase
	          )
	        : 0;


    const total =
        subtotal + envio;


    html += `

        <div class="resumen-carrito">

            <div>

                <span>
                    Subtotal
                </span>

                <strong>
                    S/
                    ${subtotal.toFixed(2)}
                </strong>

            </div>


            <div>

                <span>
                    Envío
                </span>

                <strong>
                    S/
                    ${envio.toFixed(2)}
                </strong>

            </div>


            <hr>


            <div class="total-carrito">

                <span>
                    Total
                </span>

                <strong>
                    S/
                    ${total.toFixed(2)}
                </strong>

            </div>


            <button
                class="btn-confirmar-pedido">

                Continuar pedido

            </button>

        </div>

    `;


    contenedor.innerHTML = html;
}
function aumentarProducto(idProducto, idNegocio) {

    const idUsuario = 1; // temporal

    fetch(
        `/api/carrito/agregar` +
        `?idUsuario=${idUsuario}` +
        `&idNegocio=${idNegocio}` +
        `&idProducto=${idProducto}`,
        {
            method: "POST"
        }
    )
    .then(response => {

        if (!response.ok) {
            throw new Error(
                "No se pudo aumentar la cantidad"
            );
        }

        return response.json();
    })
    .then(carrito => {

        mostrarCarrito(carrito);

    })
    .catch(error => {

        console.error(
            "Error aumentando producto:",
            error
        );

    });
}
function disminuirProducto(
    idProducto,
    idNegocio
) {

    const idUsuario = 1; // temporal

    fetch(
        `/api/carrito/disminuir` +
        `?idUsuario=${idUsuario}` +
        `&idNegocio=${idNegocio}` +
        `&idProducto=${idProducto}`,
        {
            method: "POST"
        }
    )
    .then(response => {

        if (!response.ok) {
            throw new Error(
                "No se pudo disminuir la cantidad"
            );
        }

        return response.json();
    })
    .then(carrito => {

        mostrarCarrito(carrito);

    })
    .catch(error => {

        console.error(
            "Error disminuyendo producto:",
            error
        );

    });
}
function eliminarProducto(idProducto, idNegocio) {

    const idUsuario = 1; // temporal

    fetch(
        `/api/carrito/eliminar` +
        `?idUsuario=${idUsuario}` +
        `&idNegocio=${idNegocio}` +
        `&idProducto=${idProducto}`,
        {
            method: "DELETE"
        }
    )

    .then(response => {

        if (!response.ok) {
            throw new Error(
                "No se pudo eliminar el producto"
            );
        }

        return response.json();
    })

    .then(carrito => {

        console.log(
            "Producto eliminado:",
            carrito
        );

        mostrarCarrito(carrito);
    })

    .catch(error => {

        console.error(
            "Error eliminando producto:",
            error
        );

    });
}