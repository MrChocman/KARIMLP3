import java.time.LocalDate;

public class Mantenimiento {
    private int id;
    private String descripcion;
    private String tecnico;
    private LocalDate fecha;
    private double costo;

    public Mantenimiento(int id, String descripcion, String tecnico, LocalDate fecha, double costo) {
        this.id = id;
        this.descripcion = descripcion;
        this.tecnico = tecnico;
        this.fecha = fecha;
        this.costo = costo;
    }

    public int getId() { return id; }
    public String getDescripcion() { return descripcion; }
    public String getTecnico() { return tecnico; }
    public LocalDate getFecha() { return fecha; }
    public double getCosto() { return costo; }

    public void setTecnico(String tecnico) {
        this.tecnico = tecnico;
    }

    @Override
    public String toString() {
        
        return String.format("Mantenimiento [%d]: %s, por %s en %s (S/ %.2f)",
                id, descripcion, tecnico, fecha.toString(), costo);
    }
}