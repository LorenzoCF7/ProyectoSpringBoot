package com.example.ejemploModelo.TaskController;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.ejemploModelo.Models.Suscripcion;
import com.example.ejemploModelo.Models.Usuario;
import com.example.ejemploModelo.Models.Factura;
import com.example.ejemploModelo.Service.AuditoriaService;
import com.example.ejemploModelo.Service.SuscripcionService;
import com.example.ejemploModelo.Service.UsuarioService;
import com.example.ejemploModelo.Service.PlanService;
import com.example.ejemploModelo.Service.FacturaService;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Controller
@RequestMapping("/suscripciones")
public class SuscripcionController {
    private final SuscripcionService suscripcionService;
    private final UsuarioService usuarioService;
    private final PlanService planService;
    private final AuditoriaService auditoriaService;
    private final FacturaService facturaService;

    public SuscripcionController(SuscripcionService suscripcionService, UsuarioService usuarioService,
                                  PlanService planService, AuditoriaService auditoriaService,
                                  FacturaService facturaService) {
        this.suscripcionService = suscripcionService;
        this.usuarioService = usuarioService;
        this.planService = planService;
        this.auditoriaService = auditoriaService;
        this.facturaService = facturaService;
    }

    private Usuario getUsuarioActual(Authentication auth) {
        return usuarioService.obtenerPorEmail(auth.getName()).orElse(null);
    }

    private boolean esAdmin(Authentication auth) {
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    // Mostrar formulario
    @GetMapping("/nuevo")
    public String mostrarFormulario(Model model, Authentication auth) {
        // Si no es admin, verificar si ya tiene una suscripción
        if (!esAdmin(auth)) {
            Usuario usuario = getUsuarioActual(auth);
            var suscripcionExistente = suscripcionService.obtenerPorUsuario(usuario);
            if (suscripcionExistente.isPresent()) {
                // Ya tiene suscripción, redirigir a editar
                return "redirect:/suscripciones/" + suscripcionExistente.get().getId() + "/editar";
            }
        }
        
        model.addAttribute("suscripcion", new Suscripcion());
        if (esAdmin(auth)) {
            model.addAttribute("usuarios", usuarioService.listarUsuarios());
        }
        model.addAttribute("planes", planService.listarPlanes());
        return "Views/Suscripcion/formulario_suscripcion";
    }

    // Guardar suscripcion
    @PostMapping("/guardar")
    public String guardarSuscripcion(@RequestParam(required = false) Long usuario, @RequestParam Long plan, 
                                      @RequestParam(required = false) String estado,
                                      @RequestParam(required = false) String fechaInicio,
                                      @RequestParam(required = false) String fechaFin,
                                      @RequestParam(defaultValue = "false") boolean renovacionAutomatica,
                                      Authentication auth) {
        try {
            Usuario usuarioObj;
            if (esAdmin(auth)) {
                // Admin puede seleccionar cualquier usuario
                if (usuario == null) {
                    return "redirect:/suscripciones/nuevo?error=true";
                }
                usuarioObj = usuarioService.obtenerPorId(usuario)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            } else {
                // Usuario normal usa su propia cuenta
                usuarioObj = getUsuarioActual(auth);
                
                // Verificar que no tenga ya una suscripción
                var suscripcionExistente = suscripcionService.obtenerPorUsuario(usuarioObj);
                if (suscripcionExistente.isPresent()) {
                    // Ya tiene suscripción, redirigir a editar
                    return "redirect:/suscripciones/" + suscripcionExistente.get().getId() + "/editar?error=already_exists";
                }
            }
            
            // Verificar que el usuario no tenga ya otra suscripción (doble validación)
            var suscripcionExistente = suscripcionService.obtenerPorUsuario(usuarioObj);
            if (suscripcionExistente.isPresent() && !esAdmin(auth)) {
                return "redirect:/suscripciones/" + suscripcionExistente.get().getId() + "/editar?error=already_exists";
            }
            
            var planObj = planService.obtenerPorId(plan)
                .orElseThrow(() -> new RuntimeException("Plan no encontrado"));
            
            // Crear nueva suscripción
            Suscripcion suscripcion = new Suscripcion();
            suscripcion.setUsuario(usuarioObj);
            suscripcion.setPlan(planObj);
            suscripcion.setEstado(com.example.ejemploModelo.Models.EstadoSuscripcion.valueOf(estado != null ? estado : "ACTIVA"));
            suscripcion.setFechaInicio(java.time.LocalDate.parse(fechaInicio));
            suscripcion.setFechaFin(java.time.LocalDate.parse(fechaFin));
            suscripcion.setRenovacionAutomatica(renovacionAutomatica);
            
            suscripcionService.guardarSuscripcion(suscripcion);
            auditoriaService.registrar("CREAR", "Suscripcion", suscripcion.getId(),
                "Suscripción creada - Plan: " + planObj.getNombre() + " - Usuario: " + usuarioObj.getEmail());
            return "redirect:/suscripciones/mi-suscripcion";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/suscripciones/nuevo?error=true";
        }
    }

    // Ver mi suscripción (usuario normal)
    @GetMapping("/mi-suscripcion")
    public String verMiSuscripcion(Authentication auth) {
        if (esAdmin(auth)) {
            return "redirect:/suscripciones/listar";
        }
        
        Usuario usuario = getUsuarioActual(auth);
        var suscripcion = suscripcionService.obtenerPorUsuario(usuario);
        
        if (suscripcion.isPresent()) {
            return "redirect:/suscripciones/" + suscripcion.get().getId();
        } else {
            return "redirect:/suscripciones/nuevo";
        }
    }
    
    // Listar suscripciones (solo admin)
    @GetMapping("/listar")
    public String listarSuscripciones(Model model, Authentication auth) {
        if (!esAdmin(auth)) {
            return "redirect:/suscripciones/mi-suscripcion";
        }
        model.addAttribute("suscripciones", suscripcionService.listarSuscripciones());
        return "Views/Suscripcion/lista_suscripciones";
    }

    // Mostrar detalle de la suscripción
    @GetMapping("/{id}")
    public String mostrarDetalle(@PathVariable Long id, Model model, Authentication auth) {
        var suscripcion = suscripcionService.obtenerPorId(id);
        if (suscripcion.isEmpty()) {
            return "redirect:/suscripciones/listar";
        }
        if (!esAdmin(auth)) {
            Usuario usuario = getUsuarioActual(auth);
            if (suscripcion.get().getUsuario() == null || !suscripcion.get().getUsuario().getId().equals(usuario.getId())) {
                return "redirect:/suscripciones/listar";
            }
        }
        model.addAttribute("suscripcion", suscripcion.get());
        return "Views/Suscripcion/detalle_suscripcion";
    }

    // Mostrar formulario de edición
    @GetMapping("/{id}/editar")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model, Authentication auth) {
        var suscripcion = suscripcionService.obtenerPorId(id);
        if (suscripcion.isEmpty()) {
            return "redirect:/suscripciones/listar";
        }
        if (!esAdmin(auth)) {
            Usuario usuario = getUsuarioActual(auth);
            if (suscripcion.get().getUsuario() == null || !suscripcion.get().getUsuario().getId().equals(usuario.getId())) {
                return "redirect:/suscripciones/listar";
            }
        }
        model.addAttribute("suscripcion", suscripcion.get());
        if (esAdmin(auth)) {
            model.addAttribute("usuarios", usuarioService.listarUsuarios());
        }
        model.addAttribute("planes", planService.listarPlanes());
        return "Views/Suscripcion/editar_suscripcion";
    }

    // Guardar cambios de suscripción
    @PostMapping("/{id}/actualizar")
    public String actualizarSuscripcion(@PathVariable Long id, @RequestParam(required = false) Long usuario, 
                                         @RequestParam Long plan,
                                         @RequestParam(required = false) String estado,
                                         @RequestParam(required = false) String fechaInicio,
                                         @RequestParam(required = false) String fechaFin,
                                         @RequestParam(defaultValue = "false") boolean renovacionAutomatica,
                                         Authentication auth) {
        try {
            Usuario usuarioObj;
            if (esAdmin(auth)) {
                // Admin puede cambiar el usuario
                if (usuario == null) {
                    return "redirect:/suscripciones/" + id + "/editar?error=true";
                }
                usuarioObj = usuarioService.obtenerPorId(usuario)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            } else {
                // Usuario normal mantiene su propia cuenta
                usuarioObj = getUsuarioActual(auth);
                // Verificar que está editando su propia suscripción
                var suscripcionExistente = suscripcionService.obtenerPorId(id);
                if (suscripcionExistente.isEmpty() || !suscripcionExistente.get().getUsuario().getId().equals(usuarioObj.getId())) {
                    return "redirect:/suscripciones/listar";
                }
            }
            
            var planObj = planService.obtenerPorId(plan)
                .orElseThrow(() -> new RuntimeException("Plan no encontrado"));
            
            // Actualizar suscripción existente
            Suscripcion suscripcion = suscripcionService.obtenerPorId(id)
                .orElseThrow(() -> new RuntimeException("Suscripción no encontrada"));
            
            // PRORRATEO: Detectar si cambió a un plan más caro
            boolean cambioPlan = suscripcion.getPlan() != null && !suscripcion.getPlan().getId().equals(planObj.getId());
            Double precioAnterior = suscripcion.getPlan() != null ? suscripcion.getPlan().getPrecio() : 0.0;
            Double precioNuevo = planObj.getPrecio();
            
            if (cambioPlan && precioNuevo > precioAnterior) {
                // Calcular prorrateo solo si el nuevo plan es más caro
                LocalDate hoy = LocalDate.now();
                LocalDate fechaFinActual = suscripcion.getFechaFin();
                
                // Calcular días restantes (desde hoy hasta fechaFin)
                long diasRestantes = ChronoUnit.DAYS.between(hoy, fechaFinActual);
                
                if (diasRestantes > 0) {
                    // Calcular días totales del período actual
                    long diasTotales = ChronoUnit.DAYS.between(suscripcion.getFechaInicio(), suscripcion.getFechaFin());
                    
                    if (diasTotales > 0) {
                        // Calcular monto del prorrateo
                        double diferenciaPrecio = precioNuevo - precioAnterior;
                        double montoProrrateo = diferenciaPrecio * ((double) diasRestantes / (double) diasTotales);
                        
                        // Crear factura automática por el prorrateo
                        Factura facturaProrrateo = new Factura();
                        facturaProrrateo.setMonto(montoProrrateo);
                        facturaProrrateo.setDescripcion("Prorrateo por cambio de plan: " + 
                                                        suscripcion.getPlan().getNombre() + " → " + planObj.getNombre() + 
                                                        " (" + diasRestantes + " días restantes)");
                        facturaProrrateo.setFechaEmision(hoy);
                        facturaProrrateo.setPagada(false); // Pendiente de pago
                        facturaProrrateo.setSuscripcion(suscripcion);
                        
                        facturaService.guardarFactura(facturaProrrateo);
                        
                        auditoriaService.registrar("CREAR", "Factura", facturaProrrateo.getId(),
                            "Factura de prorrateo creada automáticamente - Monto: $" + String.format("%.2f", montoProrrateo) + 
                            " - Cambio de plan: " + suscripcion.getPlan().getNombre() + " → " + planObj.getNombre());
                    }
                }
            }
            
            suscripcion.setUsuario(usuarioObj);
            suscripcion.setPlan(planObj);
            suscripcion.setEstado(com.example.ejemploModelo.Models.EstadoSuscripcion.valueOf(estado != null ? estado : "ACTIVA"));
            suscripcion.setFechaInicio(java.time.LocalDate.parse(fechaInicio));
            suscripcion.setFechaFin(java.time.LocalDate.parse(fechaFin));
            suscripcion.setRenovacionAutomatica(renovacionAutomatica);
            
            suscripcionService.guardarSuscripcion(suscripcion);
            auditoriaService.registrar("EDITAR", "Suscripcion", id,
                "Suscripción editada - Plan: " + planObj.getNombre() + " - Usuario: " + usuarioObj.getEmail());
            return "redirect:/suscripciones/" + id;
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/suscripciones/" + id + "/editar?error=true";
        }
    }

    // Cancelar suscripción (usuario puede cancelar su propia suscripción)
    @PostMapping("/{id}/cancelar")
    public String cancelarSuscripcion(@PathVariable Long id, Authentication auth) {
        try {
            var suscripcionOpt = suscripcionService.obtenerPorId(id);
            if (suscripcionOpt.isEmpty()) {
                return "redirect:/suscripciones/mi-suscripcion?error=not_found";
            }
            
            Suscripcion suscripcion = suscripcionOpt.get();
            
            // Verificar permisos
            if (!esAdmin(auth)) {
                Usuario usuario = getUsuarioActual(auth);
                if (suscripcion.getUsuario() == null || !suscripcion.getUsuario().getId().equals(usuario.getId())) {
                    return "redirect:/suscripciones/mi-suscripcion?error=unauthorized";
                }
            }
            
            // Cambiar estado a CANCELADA
            suscripcion.setEstado(com.example.ejemploModelo.Models.EstadoSuscripcion.CANCELADA);
            suscripcion.setRenovacionAutomatica(false);
            
            suscripcionService.guardarSuscripcion(suscripcion);
            auditoriaService.registrar("CANCELAR", "Suscripcion", id, 
                "Suscripción cancelada por " + auth.getName());
            
            return "redirect:/suscripciones/" + id + "?success=cancelled";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/suscripciones/" + id + "?error=true";
        }
    }

    // Reactivar suscripción (usuario puede reactivar su propia suscripción)
    @PostMapping("/{id}/reactivar")
    public String reactivarSuscripcion(@PathVariable Long id, Authentication auth) {
        try {
            var suscripcionOpt = suscripcionService.obtenerPorId(id);
            if (suscripcionOpt.isEmpty()) {
                return "redirect:/suscripciones/mi-suscripcion?error=not_found";
            }
            
            Suscripcion suscripcion = suscripcionOpt.get();
            
            // Verificar permisos
            if (!esAdmin(auth)) {
                Usuario usuario = getUsuarioActual(auth);
                if (suscripcion.getUsuario() == null || !suscripcion.getUsuario().getId().equals(usuario.getId())) {
                    return "redirect:/suscripciones/mi-suscripcion?error=unauthorized";
                }
            }
            
            // Cambiar estado a ACTIVA
            suscripcion.setEstado(com.example.ejemploModelo.Models.EstadoSuscripcion.ACTIVA);
            suscripcion.setRenovacionAutomatica(true);
            
            suscripcionService.guardarSuscripcion(suscripcion);
            auditoriaService.registrar("REACTIVAR", "Suscripcion", id, 
                "Suscripción reactivada por " + auth.getName());
            
            return "redirect:/suscripciones/" + id + "?success=reactivated";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/suscripciones/" + id + "?error=true";
        }
    }

    // Ver historial de una suscripción (usuarios pueden ver su propio historial)
    @GetMapping("/{id}/historial")
    public String verHistorial(@PathVariable Long id, Model model, Authentication auth) {
        var suscripcionOpt = suscripcionService.obtenerPorId(id);
        if (suscripcionOpt.isEmpty()) {
            return "redirect:/suscripciones/mi-suscripcion";
        }
        
        Suscripcion suscripcion = suscripcionOpt.get();
        
        // Verificar permisos (admin o dueño de la suscripción)
        if (!esAdmin(auth)) {
            Usuario usuario = getUsuarioActual(auth);
            if (suscripcion.getUsuario() == null || !suscripcion.getUsuario().getId().equals(usuario.getId())) {
                return "redirect:/suscripciones/mi-suscripcion";
            }
        }
        
        model.addAttribute("logs", auditoriaService.obtenerPorEntidadYId("Suscripcion", id));
        model.addAttribute("entidad", "Suscripcion");
        model.addAttribute("entidadId", id);
        model.addAttribute("suscripcion", suscripcion);
        return "Views/Admin/auditoria";
    }

    // Eliminar suscripción (solo admin)
    @GetMapping("/{id}/eliminar")
    public String eliminarSuscripcion(@PathVariable Long id, Authentication auth) {
        if (!esAdmin(auth)) {
            return "redirect:/suscripciones/mi-suscripcion";
        }
        suscripcionService.eliminarSuscripcion(id);
        auditoriaService.registrar("ELIMINAR", "Suscripcion", id, "Suscripción eliminada");
        return "redirect:/suscripciones/listar";
    }
}
