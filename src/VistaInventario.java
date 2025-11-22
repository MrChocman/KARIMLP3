import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.util.List; // Import necesario para listar y buscar el ID máximo
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class VistaInventario extends JFrame implements Inventariobserver {

    private ControladorInventario controller;
    
    // Componente central para el reporte
    private JTextArea areaReportePrincipal; 

    public VistaInventario(ControladorInventario controller) {
        this.controller = controller;
        // Suscribirse antes de iniciar
        this.controller.agregarObservador(this);
        
        initComponentes();
        
        // Cargar el reporte inicial
        actualizarPanelReporte();
    }

    private void initComponentes() {
        setTitle("Gestión de Inventario - VISTA DE REPORTES");
        setSize(950, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // --- MENÚ ---
        JMenuBar menuBar = new JMenuBar();

        // Menú 1: Gestión
        JMenu menuAcciones = new JMenu("Gestión");
        menuAcciones.add(crearItem("Registrar Nuevo Equipo", e -> registrarMantenimiento()));
        menuAcciones.add(crearItem("Actualizar Datos", e -> actualizarEquipo()));
        menuAcciones.add(crearItem("Eliminar Equipo", e -> eliminarEquipo()));
        menuAcciones.addSeparator();
        menuAcciones.add(crearItem("Guardar TXT", e -> controller.guardarArchivo()));
        menuAcciones.add(crearItem("Cargar TXT", e -> controller.cargarArchivo()));
        menuAcciones.add(crearItem("Sincronizar BD", e -> controller.guardarEnBD()));
        menuAcciones.addSeparator();
        menuAcciones.add(crearItem("Limpiar Todo", e -> limpiar()));
        menuAcciones.add(crearItem("Salir", e -> System.exit(0)));
        menuBar.add(menuAcciones);

        // Menú 2: Visualización (Strategy)
        JMenu menuVer = new JMenu("Cambiar Estrategia (Strategy)");
        menuVer.add(crearItem("Vista Detallada (Default)", e -> cambiarEstrategia(new detallada())));
        menuVer.add(crearItem("Vista Compacta (A-Z)", e -> cambiarEstrategia(new ordenalfaestra())));
        menuVer.add(crearItem("Cronología (Fecha)", e -> cambiarEstrategia(new cronoestrate())));
        menuVer.add(crearItem("Financiero (Costo)", e -> cambiarEstrategia(new econostrategy())));
        menuBar.add(menuVer);

        setJMenuBar(menuBar);

        // --- PANEL CENTRAL ---
        areaReportePrincipal = new JTextArea();
        areaReportePrincipal.setEditable(false);
        areaReportePrincipal.setFont(new Font("Monospaced", Font.PLAIN, 14));
        areaReportePrincipal.setBackground(new Color(250, 250, 250)); // Fondo blanco humo
        areaReportePrincipal.setForeground(Color.BLACK);
        areaReportePrincipal.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JScrollPane scrollReporte = new JScrollPane(areaReportePrincipal);
        scrollReporte.setBorder(BorderFactory.createTitledBorder("Reporte de Inventario en Tiempo Real"));
        
        add(scrollReporte, BorderLayout.CENTER);
    }

    private JMenuItem crearItem(String texto, java.awt.event.ActionListener accion) {
        JMenuItem item = new JMenuItem(texto);
        item.addActionListener(accion);
        return item;
    }

    // --- MÉTODOS DE ACTUALIZACIÓN VISUAL ---

    private void cambiarEstrategia(Reportestrategy nuevaEstrategia) {
        controller.setEstrategiaReporte(nuevaEstrategia);
    }

    private void actualizarPanelReporte() {
        String reporte = controller.obtenerReporteGenerado();
        areaReportePrincipal.setText(reporte);
    }

    // --- IMPLEMENTACIÓN DEL OBSERVER ---
    @Override
    public void cambioinventario(String evento, String detalle) {
        SwingUtilities.invokeLater(() -> {
            
            // 1. Mostrar el Pop-up (Notificación)
            int tipo = JOptionPane.INFORMATION_MESSAGE;
            if(evento.contains("ERROR")) tipo = JOptionPane.ERROR_MESSAGE;
            else if(evento.contains("LIMPIEZA") || evento.contains("ELIMINACIÓN")) tipo = JOptionPane.WARNING_MESSAGE;
            
            JOptionPane.showMessageDialog(this, detalle, "Notificación: " + evento, tipo);

            // 2. ACTUALIZAR EL TEXTO DEL CENTRO AUTOMÁTICAMENTE
            actualizarPanelReporte();
        });
    }

    // --- ACCIONES DE GESTIÓN ---

    private void limpiar() {
        if (JOptionPane.showConfirmDialog(this, "¿Borrar todo?", "Confirmar", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            controller.limpiarInventario();
        }
    }
    
    private void eliminarEquipo() {
        String id = JOptionPane.showInputDialog("ID a eliminar:");
        if (id != null && !id.trim().isEmpty()) {
            try { 
                controller.eliminarEquipoPorId(Integer.parseInt(id)); 
            } catch (NumberFormatException e) { 
                JOptionPane.showMessageDialog(this, "ID inválido"); 
            }
        }
    }
    
    private void actualizarEquipo() {
        JPanel p = new JPanel(new GridLayout(0, 2));
        JTextField tId = new JTextField();
        JTextField tNom = new JTextField();
        JTextField tTip = new JTextField();
        p.add(new JLabel("ID Buscar:")); p.add(tId);
        p.add(new JLabel("Nuevo Nombre:")); p.add(tNom);
        p.add(new JLabel("Nuevo Tipo:")); p.add(tTip);
        
        if (JOptionPane.showConfirmDialog(this, p, "Actualizar", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            try { 
                controller.actualizarEquipo(Integer.parseInt(tId.getText()), tNom.getText(), tTip.getText()); 
            } catch (Exception e) { 
                JOptionPane.showMessageDialog(this, "Error en datos"); 
            }
        }
    }

    private void registrarMantenimiento() {
        JPanel p = new JPanel(new GridLayout(0,2, 5, 5)); // Grid con espaciado
        
        // Campos de texto
        JTextField tNom = new JTextField();
        JTextField tTipo = new JTextField(); 
        JTextField tDesc = new JTextField();
        JTextField tCos = new JTextField();
        
        p.add(new JLabel("Nombre Equipo:")); p.add(tNom);
        p.add(new JLabel("Tipo Equipo:")); p.add(tTipo); 
        p.add(new JLabel("Desc. Mant:")); p.add(tDesc);
        p.add(new JLabel("Costo:")); p.add(tCos);

        if (JOptionPane.showConfirmDialog(this, p, "Registrar Nuevo Equipo", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            try {
                // --- LÓGICA DE ID AUTO-INCREMENTAL ---
                List<ParAsociado<Equipo, Mantenimiento>> listaActual = controller.listarAsociaciones();
                int maxIdEquipo = 0;
                int maxIdMant = 0;

                // Buscamos los IDs más altos existentes
                for (ParAsociado<Equipo, Mantenimiento> par : listaActual) {
                    if (par.getPrimero().getId() > maxIdEquipo) {
                        maxIdEquipo = par.getPrimero().getId();
                    }
                    if (par.getSegundo().getId() > maxIdMant) {
                        maxIdMant = par.getSegundo().getId();
                    }
                }

                // El nuevo ID es el máximo encontrado + 1
                // Si la lista está vacía, maxIdEquipo es 0, por lo tanto el primero será 1.
                int nuevoIdEquipo = maxIdEquipo + 1;
                int nuevoIdMant = maxIdMant + 1;
                // -------------------------------------

                String nombre = tNom.getText();
                String tipo = tTipo.getText().isEmpty() ? "General" : tTipo.getText();
                String desc = tDesc.getText();
                String costoTxt = tCos.getText().isEmpty() ? "0" : tCos.getText();

                // Crear objetos con los IDs calculados
                Equipo e = new Equipo(nuevoIdEquipo, nombre, tipo);
                Mantenimiento m = new Mantenimiento(nuevoIdMant, desc, "Técnico de Turno", LocalDate.now(), Double.parseDouble(costoTxt));
                
                controller.registrarAsociacion(e, m);
            } catch (Exception ex) { 
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage()); 
            }
        }
    }
}