"use strict";
(() => {
 const base=(document.querySelector('meta[name="app-context"]')?.content||'/api/').replace(/\/$/,'');
 let csrfLoading;
 const nativeFetch=window.fetch.bind(window);
 async function csrf() {
  const token=document.querySelector('meta[name="_csrf"]')?.content;
  if(token)return {token,headerName:document.querySelector('meta[name="_csrf_header"]')?.content||'X-CSRF-TOKEN'};
  if(!csrfLoading)csrfLoading=nativeFetch(base+'/csrf',{credentials:'same-origin',cache:'no-store'}).then(r=>{if(!r.ok)throw new Error('No se pudo obtener la protección del formulario.');return r.json();}).finally(()=>{csrfLoading=null;});
  return csrfLoading;
 }
 window.secureFetch=async function(input,options={}) {
  const url=new URL(input,location.href);if(url.origin!==location.origin)throw new Error('Destino no permitido.');
  const headers=new Headers(options.headers||{});headers.set('X-Requested-With','XMLHttpRequest');
  const method=(options.method||'GET').toUpperCase();
  if(!['GET','HEAD','OPTIONS'].includes(method)){const c=await csrf();headers.set(c.headerName,c.token);}
  const response=await nativeFetch(url,{...options,method,headers,credentials:'same-origin'});
  if(response.status===401){location.assign(base+'/login?sesionExpirada=true');throw new Error('La sesión ha vencido.');}
  if(!response.ok&&typeof window.mostrarToast==='function'){
   let message=response.status===403?'No tienes permiso o el formulario venció. Recarga la página.':'No se pudo completar la operación.';
   try {const error=await response.clone().json();message=error.mensaje||message;}catch (_){}
   window.mostrarToast('Revisa la operación',message);
  }
  return response;
 };
 const allowed=new Set(['abrirModalUsuario','abrirModalTipoNegocio','abrirModalNegocio','abrirModalCategoria','abrirModalProducto','abrirModalPromo','cambiarSlide','abrirRestaurante','abrirModalAgregarDireccion','toggleDropdown','toggleCargaCarta','validarPassword','toggleVisibilidad','mostrarCarga','filtrarPedidos','mostrarPedidoConfirmado','cancelarPedidoDesdeLista','eliminarDireccionUsuario','cerrarModalDireccion','detectarUbicacionActual','volverElegirDireccion','guardarNuevaDireccion','volverInicio','filtrarSeccionMenu','agregarAlCarrito','cerrarModalPedido','recalcularEnvioPedido','confirmarPedido','cerrarDetallePedido','enviarPedidoWhatsApp']);
 function dispatch(event,type){
  const selector='[data-'+type+']'+(type==='click'?', [data-close-target], [data-clear-target], [data-submit-target]':'')+(type==='submit'?', [data-confirm]':'');
  const el=event.target.closest(selector);if(!el)return;
  if(el.dataset.enabled==='false'||el.disabled)return;
  if(type==='submit'&&el.dataset.confirm&&!window.confirm(el.dataset.confirm)){event.preventDefault();return;}
  if(type==='click'&&el.dataset.closeTarget){event.preventDefault();const target=document.getElementById(el.dataset.closeTarget);if(target)target.style.display='none';return;}
  if(type==='click'&&el.dataset.clearTarget){event.preventDefault();const target=document.getElementById(el.dataset.clearTarget);if(target)target.value='';return;}
  if(type==='click'&&el.dataset.submitTarget){event.preventDefault();document.getElementById(el.dataset.submitTarget)?.requestSubmit();return;}
  const action=el.dataset[type];if(!allowed.has(action)||typeof window[action]!=='function')return;
  if(type==='click'&&el.tagName==='A')event.preventDefault();
  const args=[];
  for(let i=0;i<Math.min(Number(el.dataset.argc||0),20);i++){
   if(el.hasAttribute('data-self'+i)){args.push(el);continue;}
   if(el.hasAttribute('data-from'+i)){args.push(el.dataset[el.dataset['from'+i]]);continue;}
   if(!el.hasAttribute('data-arg'+i)){args.push(null);continue;}
   let value=el.dataset['arg'+i],kind=el.dataset['kind'+i];
   if(kind==='number')value=value===''?null:Number(value);
   else if(kind==='boolean')value=value===''?null:value==='true';
   args.push(value);
  }
  window[action](...args);
 }
 for(const type of ['click','change','input','submit'])document.addEventListener(type,e=>dispatch(e,type));
 document.addEventListener('input',e=>{if(e.target.matches('[data-digits-only]'))e.target.value=e.target.value.replace(/[^0-9]/g,'');});
})();
