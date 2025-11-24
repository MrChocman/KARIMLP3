import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ControladorInventario {

    private RepositorioArchivo repositorio;
    private RepositorioBD repositorioBD;
    
    private List<Inventariobserver> observadores = new ArrayList<>();
    
    // 2. ESTRATEGIA ACTUAL (Cómo generar reportes)
    private Reportestrategy estrategiaReporte;

    public ControladorInventario(String archivo) {
        this.repositorio = new RepositorioArchivo(archivo);
        this.repositorioBD = new RepositorioBD();
        // Estrategia por defecto
        this.estrategiaReporte = new detallada(); 
    }

    // ==========================================
    // LÓGICA OBSERVER (Notificaciones)
    // ==========================================
    public void agregarObservador(Inventariobserver obs) {
        observadores.add(obs);
    }

    // Método privado que avisa a todos los observadores registrados
    private void notificar(String evento, String detalle) {
        for (Inventariobserver obs : observadores) {
            obs.cambioinventario(detalle, detalle);
        }
    }

    // ==========================================
    // LÓGICA STRATEGY (Visualización)
    // ==========================================
    public void setEstrategiaReporte(Reportestrategy estrategia) {
        this.estrategiaReporte = estrategia;
        notificar("CONFIGURACIÓN", "Vista cambiada a: " + estrategia.getName());
    }

    public String obtenerReporteGenerado() {
        return estrategiaReporte.reporte(repositorio.listar());
    }

    // ==========================================
    // MÉTODOS DE NEGOCIO (CON NOTIFICACIONES)
    // ==========================================

    // CASO 1 Y 4: REGISTRO DE EQUIPO Y MANTENIMIENTO
    public void registrarAsociacion(Equipo e, Mantenimiento m) {
        repositorio.agregar(e, m);
        // Notificamos dos cosas como piden los requerimientos
        notificar("NUEVO EQUIPO", "Registrado: " + e.getNombre() + " (" + e.getTipo() + ")");
        notificar("MANTENIMIENTO", "Asignado a ID " + e.getId() + ": " + m.getDescripcion());
    }

    // CASO 2: ELIMINAR EQUIPO (Búsqueda manual con FOR)
    public void eliminarEquipoPorId(int id) {
        List<ParAsociado<Equipo, Mantenimiento>> lista = repositorio.listar();
        ParAsociado<Equipo, Mantenimiento> aBorrar = null;
        
        // Buscamos manualmente sin Predicates
        for (ParAsociado<Equipo, Mantenimiento> par : lista) {
            if (par.getPrimero().getId() == id) {
                aBorrar = par;
                break; // Encontramos el primero y salimos
            }
        }
        
        if (aBorrar != null) {
            repositorio.eliminarObjeto(aBorrar);
            notificar("ELIMINACIÓN", "Equipo ID " + id + " eliminado del inventario.");
        } else {
            notificar("ERROR", "Intento de eliminar ID " + id + " fallido: No encontrado.");
        }
    }

    // CASO 3: ACTUALIZAR DATOS DE EQUIPO (Búsqueda manual con FOR)
    public void actualizarEquipo(int id, String nuevoNombre, String nuevoTipo) {
        List<ParAsociado<Equipo, Mantenimiento>> lista = repositorio.listar();
        ParAsociado<Equipo, Mantenimiento> parEncontrado = null;
        
        // 1. Buscamos el registro
        for (ParAsociado<Equipo, Mantenimiento> par : lista) {
            if (par.getPrimero().getId() == id) {
                parEncontrado = par;
                break;
            }
        }
        
        if (parEncontrado != null) {
            // 2. Como Equipo no tiene setters, creamos uno nuevo con los datos actualizados
            // y conservamos el mantenimiento original
            Equipo equipoViejo = parEncontrado.getPrimero();
            Mantenimiento mant = parEncontrado.getSegundo();
            
            Equipo equipoNuevo = new Equipo(id, nuevoNombre, nuevoTipo);
            
            // 3. Reemplazamos en el repositorio (borrar viejo, poner nuevo)
            repositorio.eliminarObjeto(parEncontrado);
            repositorio.agregar(equipoNuevo, mant);
            
            notificar("ACTUALIZACIÓN", "Equipo ID " + id + " modificado. Nombre anterior: " + equipoViejo.getNombre());
        } else {
            notificar("ERROR", "No se pudo actualizar ID " + id + ": No existe.");
        }
    }

    // CASO 6: LIMPIEZA TOTAL
    public void limpiarInventario() {
        int cantidad = repositorio.listar().size();
        repositorio.limpiar();
        notificar("LIMPIEZA TOTAL", "Se han borrado " + cantidad + " registros de la memoria.");
    }

    // CASO 5: CARGA Y GUARDADO
    public boolean guardarArchivo() {
        try {
            repositorio.guardarEnArchivo();
            notificar("PERSISTENCIA", "Inventario exportado a archivo TXT correctamente.");
            return true;
        } catch (IOException e) {
            notificar("ERROR CRÍTICO", "Fallo al guardar archivo: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean cargarArchivo() {
        try {
            repositorio.cargarDesdeArchivo();
            int total = repositorio.listar().size();
            notificar("CARGA DATOS", "Importación completada. Registros en memoria: " + total);
            return true;
        } catch (IOException | ClassNotFoundException e) {
            notificar("ERROR CRÍTICO", "Fallo al leer archivo: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean guardarEnBD() {
        notificar("BASE DE DATOS", "Iniciando transacción con la BD...");
        try {
            boolean exito = repositorioBD.guardarAsociacionesEnBD(repositorio.listar());
            if (exito) notificar("BASE DE DATOS", "Sincronización exitosa.");
            else notificar("BASE DE DATOS", "Operación cancelada (Rollback).");
            return exito;
        } catch (SQLException e) {
            notificar("ERROR SQL", "Excepción en BD: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public List<ParAsociado<Equipo, Mantenimiento>> listarAsociaciones() {
        return repositorio.listar();
    }
}