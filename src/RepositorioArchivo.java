import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;


public class RepositorioArchivo extends RepositorioAsociativo<Equipo, Mantenimiento> {

    private final String nombreArchivo;

    public RepositorioArchivo(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
    }

    public void guardarEnArchivo() throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(nombreArchivo))) {
            
            for (ParAsociado<Equipo, Mantenimiento> par : asociaciones) {
                Equipo e = par.getPrimero();
                Mantenimiento m = par.getSegundo();

                String linea = String.join(",",
                        String.valueOf(e.getId()),
                        e.getNombre(),
                        e.getTipo(),
                        String.valueOf(m.getId()),
                        m.getDescripcion(),
                        m.getTecnico(),
                        m.getFecha().toString(),
                        String.valueOf(m.getCosto())
                );
                
                bw.write(linea);
                bw.newLine();
            }
        }
    }

    @SuppressWarnings("unchecked")
    public void cargarDesdeArchivo() throws IOException, ClassNotFoundException {
        asociaciones.clear();

        try (BufferedReader br = new BufferedReader(new FileReader(nombreArchivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] partes = linea.split(",");
                
                if (partes.length < 8) {
                    System.err.println("Línea mal formada, omitiendo: " + linea);
                    continue;
                }
                
                int idEquipo = Integer.parseInt(partes[0]);
                String nombre = partes[1];
                String tipo = partes[2];
                Equipo e = new Equipo(idEquipo, nombre, tipo);

                int idMant = Integer.parseInt(partes[3]);
                String descripcion = partes[4];
                String tecnico = partes[5];
                LocalDate fecha = LocalDate.parse(partes[6]);
                double costo = Double.parseDouble(partes[7]);
                Mantenimiento m = new Mantenimiento(idMant, descripcion, tecnico, fecha, costo);
                
                asociaciones.add(new ParAsociado<>(e, m));
            }
        }
    }
}