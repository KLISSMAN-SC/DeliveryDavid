document.addEventListener('DOMContentLoaded', () => {
    // Conexión con los elementos del HTML
    const modal = document.getElementById('modalFiltros');
    const btnAbrirTxt = document.getElementById('btnAbrirFiltros');
    const btnAbrirCaja = document.getElementById('btnAbrirFiltrosCaja');
    const btnCancelar = document.getElementById('btnCancelarModal');
    const btnAceptar = document.getElementById('btnAceptarModal');
    const btnLimpiar = document.getElementById('btnLimpiarModal');
    
    // Obtener todos los botones de filtro dentro del modal
    const pills = document.querySelectorAll('.filtro-pill');

    // Función para mostrar el modal
    const abrirModal = () => { 
        if(modal) modal.style.display = 'flex'; 
    };
    
    // Asignar eventos de apertura
    if(btnAbrirTxt) btnAbrirTxt.addEventListener('click', abrirModal);
    if(btnAbrirCaja) btnAbrirCaja.addEventListener('click', abrirModal);

    // Funciones para cerrar el modal
    if(btnCancelar) {
        btnCancelar.addEventListener('click', () => { modal.style.display = 'none'; });
    }
    
    if(btnAceptar) {
        btnAceptar.addEventListener('click', () => { 
            modal.style.display = 'none'; 
            // Lógica futura para aplicar los filtros
        });
    }

    // Lógica de selección múltiple al hacer clic en los botones (Pills)
    pills.forEach(pill => {
        pill.addEventListener('click', () => {
            pill.classList.toggle('seleccionado');
        });
    });

    // Lógica para el botón "Limpiar" (quita la selección de todos)
    if(btnLimpiar) {
        btnLimpiar.addEventListener('click', () => {
            pills.forEach(pill => pill.classList.remove('seleccionado'));
        });
    }
	
	
	
});

// --- BUSCADOR DINÁMICO (AUTOCOMPLETE) ---

document.addEventListener('DOMContentLoaded', () => {

    const inputBuscar = document.getElementById('inputBuscar');
    const dropdownResultados = document.getElementById('dropdownResultados');

    console.log("Buscador:", inputBuscar);
    console.log("Dropdown:", dropdownResultados);

    if (!inputBuscar || !dropdownResultados) {
        console.log("No se encontró el buscador");
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


        timeoutId = setTimeout(()=>{


            fetch(`/api/negocios/buscar?q=${encodeURIComponent(query)}`)

            .then(res => res.json())

            .then(data => {


                dropdownResultados.innerHTML="";


                data.forEach(negocio=>{


                    const div=document.createElement("div");

                    div.className="search-item";


                    div.innerHTML=`

                        <span class="search-item-title">
                            ${negocio.nombre}
                        </span>

                        <span class="search-item-sub">
                            ${negocio.tipo} - ${negocio.direccion}
                        </span>

                    `;


                    dropdownResultados.appendChild(div);


                });


                dropdownResultados.style.display="block";


            })

            .catch(err=>{
                console.error(err);
            });


        },300);


    });


});
function controlarCategorias(tipo){

    const seccion =
    document.getElementById("seccionCategorias");


    if(tipo === "RESTAURANTES"){

        seccion.style.display="block";

    }else{

        seccion.style.display="none";

    }

}
