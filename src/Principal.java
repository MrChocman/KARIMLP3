import javax.swing.SwingUtilities;

public class Principal {

    public static void main(String[] args) {

        String nombreArchivo = "inventario_mantenimiento.txt";
        ControladorInventario controller = new ControladorInventario(nombreArchivo);
        SwingUtilities.invokeLater(() -> {
            VistaInventario vista = new VistaInventario(controller);
            vista.setVisible(true);
        });
    }
}