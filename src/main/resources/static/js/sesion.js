// ==========================================
// 1. ANIMACIÓN DE CARGA PARA FORMULARIOS
// ==========================================
function mostrarCarga() {
    // Detectamos qué botón disparó el envío para personalizar el texto
    const btnReg = document.getElementById('btnRegistrar');
    const btnRec = document.getElementById('btnRecuperar');
    
    if (btnReg) {
        btnReg.style.opacity = '0.7';
        btnReg.style.cursor = 'not-allowed';
        const texto = document.getElementById('btnTexto');
        const icono = document.getElementById('btnIcono');
        if (texto) texto.innerText = 'Enviando código...';
        if (icono) icono.style.display = 'inline-block';
    }

    if (btnRec) {
        btnRec.style.opacity = '0.7';
        btnRec.style.cursor = 'not-allowed';
        const texto = document.getElementById('btnTexto');
        const icono = document.getElementById('btnIcono');
        if (texto) texto.innerText = 'Enviando correo...';
        if (icono) icono.style.display = 'inline-block';
    }
}
// ==========================================
// 2. VALIDACIÓN DE CONTRASEÑA
// REGISTRO Y RESET
// ==========================================

function validarPassword() {
    const inputPass =document.getElementById('inputPassword');

    if (!inputPass) {return;}

    const password =inputPass.value;

    // ==========================================
    // REGLAS DE CONTRASEÑA
    // ==========================================

    const tieneLongitud =password.length >= 8 && new TextEncoder().encode(password).length <= 72;
    const tieneMayus = /[A-Z]/.test(password);
    const tieneMinus =/[a-z]/.test(password);
    const tieneEspecial =/[!@#$%^&*(),.?":{}|<>]/.test(password);

    actualizarRequisito('req-long',tieneLongitud);
    actualizarRequisito('req-mayus',tieneMayus);
    actualizarRequisito('req-minus',tieneMinus);
    actualizarRequisito('req-especial',tieneEspecial);
    const cumpleRequisitos =tieneLongitud &&tieneMayus &&tieneMinus &&tieneEspecial;
    // ==========================================
    // CONFIRMACIÓN DE PASSWORD
    // SOLO SI EXISTE
    // ==========================================
    const inputConfirm =document.getElementById('inputConfirmPassword');
    let coinciden = true;
    if (inputConfirm) {
        const confirmPassword =inputConfirm.value;
        const mensajeCoincidencia =document.getElementById('mensajeCoincidencia');
        coinciden =password === confirmPassword && confirmPassword.length > 0;
        if (confirmPassword.length > 0 && !coinciden) {

            if (mensajeCoincidencia) {
                mensajeCoincidencia.style.display = 'block';
            }
        } else {
            if (mensajeCoincidencia) {
                mensajeCoincidencia.style.display = 'none';
            }
        }
    }

    // ==========================================
    // BOTÓN REGISTRAR
    // ==========================================

    const btnRegistrar =document.getElementById('btnRegistrar');
    if (btnRegistrar) {
        const checkboxLegales =document.getElementById('aceptaLegales');
        const legalesAceptados =checkboxLegales? checkboxLegales.checked: false;
        const registroValido =cumpleRequisitos &&legalesAceptados;
        
		btnRegistrar.disabled =!registroValido;
        if (registroValido) {
            btnRegistrar.classList.remove('btn-deshabilitado');
        } else {
            btnRegistrar.classList.add('btn-deshabilitado');
        }
    }
    // ==========================================
    // BOTÓN RESTABLECER PASSWORD
    // ==========================================
    const btnRestablecer =document.getElementById('btnRestablecer');

    if (btnRestablecer) {
        const resetValido =cumpleRequisitos &&coinciden;
        btnRestablecer.disabled =!resetValido;
        if (resetValido) {
			btnRestablecer.classList.remove('btn-deshabilitado');
        } else {btnRestablecer.classList.add('btn-deshabilitado'
            );
        }
    }
}

function actualizarRequisito(idElemento, cumple) {
    const elemento = document.getElementById(idElemento);
    if (!elemento) return;
    const icono = elemento.querySelector('i');

    if (cumple) {
        elemento.classList.add('cumplido');
        if (icono) icono.className = 'fa-solid fa-circle-check';
    } else {
        elemento.classList.remove('cumplido');
        if (icono) icono.className = 'fa-solid fa-circle-xmark';
    }
}

// ==========================================
// 3. MOSTRAR / OCULTAR CONTRASEÑA (OJITO)
// ==========================================
function toggleVisibilidad(inputId = 'inputPassword', iconId = 'togglePassword') {
    const input = document.getElementById(inputId);
    const icon = document.getElementById(iconId);
    
    if (input && icon) {
        if (input.type === 'password') {
            input.type = 'text';
            icon.className = 'fa-solid fa-eye';
        } else {
            input.type = 'password';
            icon.className = 'fa-solid fa-eye-slash';
        }
    }
}
// Inicialización al cargar la página
document.addEventListener(
    'DOMContentLoaded',
    () => {
        // Estado inicial
        validarPassword();
        // ======================================
        // CHECKBOX LEGALES
        // ======================================
        const checkboxLegales =document.getElementById('aceptaLegales');
        if (checkboxLegales) {
            checkboxLegales.addEventListener('change',validarPassword);
        }
        // ======================================
        // PASSWORD
        // ======================================

        const inputPassword =document.getElementById('inputPassword');
        if (inputPassword) {
            inputPassword.addEventListener('input',validarPassword);
        }
        // ======================================
        // CONFIRMACIÓN PASSWORD
        // RESET
        // ======================================
        const inputConfirm =document.getElementById('inputConfirmPassword');
        if (inputConfirm) {
            inputConfirm.addEventListener('input',validarPassword);
        }
    }
);