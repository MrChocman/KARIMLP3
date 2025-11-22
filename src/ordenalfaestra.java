import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
public class ordenalfaestra implements Reportestrategy{
    @Override
    public String reporte(List<ParAsociado<Equipo, Mantenimiento>> dato) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== VISTA COMPACTA (A-Z por Equipo) ===\n\n");
        
        List<ParAsociado<Equipo, Mantenimiento>> copia = new ArrayList<>(dato);
        copia.sort(new Comparator<ParAsociado<Equipo, Mantenimiento>>() {
            @Override
            public int compare(ParAsociado<Equipo, Mantenimiento> p1, ParAsociado<Equipo, Mantenimiento> p2) {
                return p1.getPrimero().getNombre().compareTo(p2.getPrimero().getNombre());
            }
        });

        for (ParAsociado<Equipo, Mantenimiento> par : copia) {
            sb.append("• ").append(par.getPrimero().getNombre()).append(" (ID:").append(par.getPrimero().getId()).append(")").append(" | ").append(par.getSegundo().getDescripcion()).append("\n");
        }
        return sb.toString();
    }

}
