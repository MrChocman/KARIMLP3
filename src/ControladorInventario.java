import java.util.ArrayList;
import java.util.List;

public class ControladorInventario {

    private RepositorioArchivo repositorio;
    private RepositorioBD repositorioBD;
    private List<Inventariobserver> observadores = new ArrayList<>();
    private Reportestrategy estrategiaReporte;

    public ControladorInventario(String archivo) {
        this.repositorio = new RepositorioArchivo(archivo);
        this.repositorioBD = new RepositorioBD();
        this.estrategiaReporte = new detallada(); 
    }

    //OBSERVER
    public void agregarObservador(Inventariobserver obs) {
        observadores.add(obs);
    }

    private void notificar(String evento, String detalle) {
        for (Inventariobserver obs : observadores) {
            obs.cambioinventario(detalle, detalle);
        }
    }

    //STRATEGY
    public void setEstrategiaReporte(Reportestrategy estrategia) {
        this.estrategiaReporte = estrategia;
        notificar("VISUALIZACIÓN", "Vista cambiada a: " + estrategia.getName());
    }

    public String obtenerReporteGenerado() {
        return estrategiaReporte.reporte(repositorio.listar());
    }
    
    public List<ParAsociado<Equipo, Mantenimiento>> listarAsociaciones() {
        return repositorio.listar();
    }

    // NEGOCIO
    public void registrarAsociacion(Equipo e, Mantenimiento m) {
        repositorio.agregar(e, m);
        notificar("NUEVO REGISTRO", "Equipo Registrado: " + e.getNombre());
    }

    public void eliminarEquipoPorId(int id) {
        List<ParAsociado<Equipo, Mantenimiento>> lista = repositorio.listar();
        ParAsociado<Equipo, Mantenimiento> borrar = null;
        for (ParAsociado<Equipo, Mantenimiento> par : lista) {
            if (par.getPrimero().getId() == id) {
                borrar = par;
                break;
            }
        }
        if (borrar != null) {
            repositorio.eliminarObjeto(borrar);
            notificar("ELIMINACIÓN", "ID " + id + " eliminado correctamente.");
        } else {
            notificar("ERROR", "No se encontró el ID " + id);
        }
    }

    
    public void actualizarEquipo(int id, String nuevoNombre, String nuevoTipo, String nuevaDesc, String nuevoCostoStr) {
        List<ParAsociado<Equipo, Mantenimiento>> lista = repositorio.listar();
        ParAsociado<Equipo, Mantenimiento> encontrado = null;
        

        for (ParAsociado<Equipo, Mantenimiento> par : lista) {
            if (par.getPrimero().getId() == id) {
                encontrado = par;
                break;
            }
        }
        
        if (encontrado != null) {
            Equipo viejoE = encontrado.getPrimero();
            Mantenimiento viejoM = encontrado.getSegundo();


            String nombreFinal = (nuevoNombre == null || nuevoNombre.trim().isEmpty()) ? viejoE.getNombre() : nuevoNombre;
            String tipoFinal = (nuevoTipo == null || nuevoTipo.trim().isEmpty()) ? viejoE.getTipo() : nuevoTipo;
            String descFinal = (nuevaDesc == null || nuevaDesc.trim().isEmpty()) ? viejoM.getDescripcion() : nuevaDesc;
            
            double costoFinal = viejoM.getCosto();
            if (nuevoCostoStr != null && !nuevoCostoStr.trim().isEmpty()) {
                try {
                    costoFinal = Double.parseDouble(nuevoCostoStr);
                } catch(NumberFormatException e) {

                }
            }


            repositorio.eliminarObjeto(encontrado);
            

            Equipo equipoNuevo = new Equipo(id, nombreFinal, tipoFinal);
            

            Mantenimiento mantNuevo = new Mantenimiento(
                viejoM.getId(), 
                descFinal, 
                viejoM.getTecnico(), 
                viejoM.getFecha(), 
                costoFinal
            );

            repositorio.agregar(equipoNuevo, mantNuevo);
            
            notificar("ACTUALIZACIÓN", "Registro ID " + id + " actualizado con éxito.");
        } else {
            notificar("ERROR", "No se encontró el equipo con ID " + id);
        }
    }

    public void limpiarInventario() {
        repositorio.limpiar();
        notificar("LIMPIEZA", "Se ha vaciado todo el inventario.");
    }

    public boolean guardarArchivo() {
        try {
            repositorio.guardarEnArchivo();
            notificar("ARCHIVO", "Datos guardados en TXT.");
            return true;
        } catch(Exception e) {
            notificar("ERROR", e.getMessage());
            return false;
        }
    }

    public boolean cargarArchivo() {
        try {
            repositorio.cargarDesdeArchivo();
            notificar("ARCHIVO", "Datos cargados desde TXT.");
            return true;
        } catch(Exception e) {
            notificar("ERROR", e.getMessage());
            return false;
        }
    }

    public boolean guardarEnBD() {
        try {
            boolean ok = repositorioBD.guardarAsociacionesEnBD(repositorio.listar());
            notificar("BASE DATOS", ok ? "Sincronización completada." : "Fallo en sincronización.");
            return ok;
        } catch(Exception e) {
            notificar("ERROR BD", e.getMessage());
            return false;
        }
    }
}