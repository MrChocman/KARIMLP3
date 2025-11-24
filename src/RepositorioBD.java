//REALIZADO POR: XIOMARA NUÑEZ CCUPA

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List; 

public class RepositorioBD {

    public boolean guardarAsociacionesEnBD(List<ParAsociado<Equipo, Mantenimiento>> lista) throws SQLException {
        
        System.out.println("Intentando guardar " + lista.size() + " registros en la BD");

        String sqlEquipo = "INSERT INTO equipo (id, nombre, tipo) VALUES (?, ?, ?) " +
            "ON DUPLICATE KEY UPDATE nombre = VALUES(nombre), tipo = VALUES(tipo)";
        
        String sqlMaint = "INSERT INTO mantenimiento (id, descripcion, tecnico, fecha, costo, equipo_id) VALUES (?, ?, ?, ?, ?, ?) " +
            "ON DUPLICATE KEY UPDATE descripcion = VALUES(descripcion), tecnico = VALUES(tecnico), " +
            "fecha = VALUES(fecha), costo = VALUES(costo), equipo_id = VALUES(equipo_id)";

        try (Connection con = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/klp3_db", "root", "cultural123")) {
            
            try (PreparedStatement psEquipo = con.prepareStatement(sqlEquipo);
                PreparedStatement psMaint = con.prepareStatement(sqlMaint)) {
                
                con.setAutoCommit(false);

                for (ParAsociado<Equipo, Mantenimiento> par : lista) {
                    Equipo e = par.getPrimero();
                    Mantenimiento m = par.getSegundo();

                    psEquipo.setInt(1, e.getId());
                    psEquipo.setString(2, e.getNombre());
                    psEquipo.setString(3, e.getTipo());
                    psEquipo.executeUpdate(); 

                    psMaint.setInt(1, m.getId());
                    psMaint.setString(2, m.getDescripcion());
                    psMaint.setString(3, m.getTecnico());
                    psMaint.setDate(4, Date.valueOf(m.getFecha()));
                    psMaint.setDouble(5, m.getCosto());
                    psMaint.setInt(6, e.getId()); 
                    psMaint.executeUpdate(); 
                }
                
                con.commit(); 
                System.out.println("Guardado en BD exitoso. " + lista.size() + " registros procesados.");
                return true;

            } catch (SQLException e) {
                System.err.println("Error durante la transacción. Revirtiendo cambios...");
                con.rollback(); 
                e.printStackTrace();
                return false;
            }

        } catch (SQLException e) {

            System.err.println("Error de conexión a la BD o durante el commit/rollback.");
            e.printStackTrace();
            throw e; 
        }
    }
}