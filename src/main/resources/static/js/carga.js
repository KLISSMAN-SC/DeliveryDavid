// ==========================================
// PANTALLA DE CARGA (TRANSICIÓN ENTRE PÁGINAS)
// ==========================================

// 1. Ocultar el Loader cuando la página termina de cargar completamente
window.addEventListener('load', function() {
    const loader = document.getElementById('pantalla-carga');
    if (loader) {
        loader.classList.add('oculto');
    }
});

// 2. Mostrar el Loader de nuevo cuando el usuario navega a otra página
window.addEventListener('beforeunload', function() {
    const loader = document.getElementById('pantalla-carga');
    if (loader) {
        loader.classList.remove('oculto');
    }
});

// 3. Respaldo de seguridad (Por si el usuario usa el botón "Atrás" del navegador)
window.addEventListener('pageshow', function(event) {
    if (event.persisted) { // Si la página se cargó desde la caché del navegador
        const loader = document.getElementById('pantalla-carga');
        if (loader) {
            loader.classList.add('oculto');
        }
    }
});