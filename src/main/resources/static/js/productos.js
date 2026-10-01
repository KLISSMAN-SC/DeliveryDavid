let timeoutBusquedaProductos;


document.addEventListener("input", function(event) {

    // Verificar si lo que escribió fue el buscador de productos
    if (event.target.id !== "buscarProductos") {
        return;
    }


    clearTimeout(timeoutBusquedaProductos);


    const input = event.target;

    const texto =
        input.value.trim();

    const idNegocio =
        input.dataset.negocio;


    // Esperar un poco antes de consultar
    timeoutBusquedaProductos = setTimeout(() => {

        buscarProductos(
            idNegocio,
            texto
        );

    }, 300);

});
function buscarProductos(
    idNegocio,
    texto
) {

    console.log(
        "Buscando:",
        texto,
        "Negocio:",
        idNegocio
    );


    fetch(
        `/api/productos/buscar` +
        `?idNegocio=${idNegocio}` +
        `&q=${encodeURIComponent(texto)}`
    )

    .then(response => {

        if (!response.ok) {

            throw new Error(
                "Error buscando productos: " +
                response.status
            );
        }

        return response.text();
    })

    .then(html => {

        const lista =
            document.getElementById(
                "listaProductos"
            );


        if (!lista) {
            return;
        }


        // Reemplazar solamente las tarjetas
        lista.outerHTML = html;

    })

    .catch(error => {

        console.error(
            "Error en buscador:",
            error
        );

    });
}