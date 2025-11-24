//REALIZADO POR: XIOMARA NUÑEZ CCUPA

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
public class econostrategy implements Reportestrategy{
    private String name = "Reporte Económico (Mayor a menor costo)";

    public String getName() {
        return name;
    }
    
    @Override
    public String reporte(List<ParAsociado<Equipo, Mantenimiento>> dato) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== REPORTE FINANCIERO (Mayores Costos) ===\n\n");

        List<ParAsociado<Equipo, Mantenimiento>> copia = new ArrayList<>(dato);
        copia.sort(new Comparator<ParAsociado<Equipo, Mantenimiento>>() {
            @Override
            public int compare(ParAsociado<Equipo, Mantenimiento> p1, ParAsociado<Equipo, Mantenimiento> p2) {
                return Double.compare(p2.getSegundo().getCosto(), p1.getSegundo().getCosto());
            }
        });

        double total = 0;
        for (ParAsociado<Equipo, Mantenimiento> par : copia) {
            sb.append(String.format("S/ %8.2f  | %s (%s)\n", 
                par.getSegundo().getCosto(), 
                par.getPrimero().getNombre(), 
                par.getSegundo().getDescripcion()));
            total += par.getSegundo().getCosto();
        }
        sb.append("\n==================================\n");
        sb.append("TOTAL GASTADO: S/ ").append(total).append("\n");
        return sb.toString();
    }

    
}
