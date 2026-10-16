
function abrirRestaurante(idNegocio) {

    secureFetch("/api/restaurante/" + idNegocio)
        .then(response => {
            // Spring Security redirigió la petición al login
            if (response.redirected && response.url.includes("/login")) {
                window.location.href = response.url;
                return null;
            }
            // Si ocurrió otro error
            if (!response.ok) {
                throw new Error("No se pudo abrir el restaurante");
            }
            return response.text();
        })
        .then(html => {

            // Si fuimos enviados al login, no continuar
            if (html === null) {
                return;
            }

            document.getElementById("contenido-principal").innerHTML = html;
            window.scrollTo(0, 0);
            cargarCarrito(idNegocio);
        })
        .catch(error => {
            console.error("Error abriendo restaurante:", error);
        });
}
function agregarAlCarrito(boton) {
    const idProducto = boton.dataset.producto;
    const idNegocio = boton.dataset.negocio;
    
    secureFetch(`/api/carrito/agregar?idNegocio=${idNegocio}&idProducto=${idProducto}`,{ method: "POST" })
    .then(response => {
        if (!response.ok) {
            throw new Error("No se pudo agregar el producto");
        }
        return response.json();
    })
    .then(carrito => {
        // SOLUCIÓN AL DOBLE CLIC:
        // Si el backend devuelve el carrito "vacío" en el primer intento (problema de BD),
        // forzamos a recargar los datos reales.
        if (!carrito.detalles || carrito.detalles.length === 0) {
            cargarCarrito(idNegocio);
        } else {
            mostrarCarrito(carrito);
        }
    })
    .catch(error => {console.error("Error:", error);});
}
function cargarCarrito(idNegocio) {

    console.log("Consultando carrito del negocio:", idNegocio);

    secureFetch(`/api/carrito/actual?` +`&idNegocio=${idNegocio}`)
    .then(response => {
        console.log("Respuesta GET carrito:",response.status);
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
            mostrarCarrito({detalles: []});
            return;
        }
        const carrito = JSON.parse(texto);
        console.log("Carrito recuperado de BD:",carrito);
        mostrarCarrito(carrito);
    })
    .catch(error => {
        console.error("Error cargando carrito:",error);
    });
}

function mostrarCarrito(carrito) {
 const container=document.getElementById('contenido-carrito');if(!container)return;
 container.replaceChildren();
 const node=(tag,css,text)=>{const e=document.createElement(tag);e.className=css;if(text!==undefined)e.textContent=String(text);return e;};
 if(!carrito.detalles?.length){const empty=node('div','carrito-vacio');empty.append(node('i','fa-solid fa-bag-shopping'),node('p','','Tu pedido está vacío'));container.append(empty);return;}
 const business=Number(carrito.negocio.idNegocio);if(!Number.isSafeInteger(business)||business<1)throw new Error('Carrito no válido.');
 const list=node('div','carrito-items-lista');let total=0;
 for(const d of carrito.detalles){
  const product=Number(d.producto.idProducto),quantity=Number(d.cantidad),price=Number(d.precioUnitario);
  if(!Number.isSafeInteger(product)||!Number.isSafeInteger(quantity)||quantity<1||!Number.isFinite(price))throw new Error('Producto no válido.');
  const item=node('div','carrito-item');item.append(node('h4','carrito-item-titulo',d.producto.nombre));
  const body=node('div','carrito-item-body'),controls=node('div','carrito-controles');
  const button=(css,icon,action)=>{const b=node('button',css);b.type='button';b.append(node('i',icon));b.addEventListener('click',action);return b;};
  controls.append(button('btn-cantidad','fa-solid fa-minus',()=>disminuirProducto(product,business)),node('span','cantidad-numero',quantity),button('btn-cantidad','fa-solid fa-plus',()=>aumentarProducto(product,business)));
  const costs=node('div','carrito-item-precio');costs.append(node('span','precio-subtotal','S/ '+(price*quantity).toFixed(2)),button('btn-eliminar','fa-regular fa-trash-can',()=>eliminarProducto(product,business)));
  body.append(controls,costs);item.append(body);list.append(item);total+=price*quantity;
 }
 const summary=node('div','carrito-resumen');
 for(const [label,css] of [['Subtotal productos','resumen-fila'],['Total (Sin envío)','resumen-fila total']]){const row=node('div',css);row.append(node('span','',label),node('span','','S/ '+total.toFixed(2)));summary.append(row);}
 const next=node('button','btn-continuar-carrito','Continuar pedido');next.type='button';next.addEventListener('click',()=>abrirModalPedido(business));summary.append(next);container.append(list,summary);
}
function aumentarProducto(idProducto, idNegocio) {

    

    secureFetch(`/api/carrito/agregar?` +`&idNegocio=${idNegocio}` +`&idProducto=${idProducto}`,
        {method: "POST"}
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

        console.error("Error aumentando producto:",error);
    });
}
function disminuirProducto(
    idProducto,
    idNegocio
) {

    secureFetch(`/api/carrito/disminuir?` +`&idNegocio=${idNegocio}` +`&idProducto=${idProducto}`,
        {method: "POST"}
    )
    .then(response => {
        if (!response.ok) {
            throw new Error(
                "No se pudo disminuir la cantidad"
            );
        }

        return response.json();
    })
    .then(carrito => {mostrarCarrito(carrito);})
    .catch(error => {console.error("Error disminuyendo producto:",error);});
}
function eliminarProducto(idProducto, idNegocio) {

    secureFetch(`/api/carrito/eliminar?` +`&idNegocio=${idNegocio}` +`&idProducto=${idProducto}`,
        {method: "DELETE"}
    )
    .then(response => {

        if (!response.ok) {
            throw new Error(
                "No se pudo eliminar el producto"
            );
        }

        return response.json();
    })

    .then(carrito => {console.log("Producto eliminado:",carrito);
        mostrarCarrito(carrito);})
    .catch(error => {console.error("Error eliminando producto:",error);});
}