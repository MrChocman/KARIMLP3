import java.util.List;

public class detallada implements Reportestrategy {
    private String name = "Reporte Detallado";

    public String getName() {
        return name;
    }
    
    @Override
    public String reporte(List<ParAsociado<Equipo, Mantenimiento>> dato) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== REPORTE DETALLADO (Orden de Registro) ===\n\n");
        if (dato.isEmpty()) sb.append("(Inventario vacío)\n");

        for (ParAsociado<Equipo, Mantenimiento> par : dato) {
            sb.append("EQUIPO: ").append(par.getPrimero().toString()).append("\n");
            sb.append("   -> MANTENIMIENTO: ").append(par.getSegundo().toString()).append("\n");
            sb.append("--------------------------------------------------\n");
        }
        return sb.toString(); 
    }
}

