import java.util.List;
interface Reportestrategy {
    String getName();
    String reporte(List<ParAsociado<Equipo, Mantenimiento>> dato);
}
