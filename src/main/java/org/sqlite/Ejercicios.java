package org.sqlite;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.nio.file.Path;
import java.sql.*;
import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Data
class Piloto{
    private int driverid;
    private String code;
    private String forename;
    private String surname;
    private LocalDate fechaNacimiento;
    private String nationality;
    private String url;

    @Override
    public String toString() {
        return "Piloto{" +
                "code='" + code + '\'' +
                ", forename='" + forename + '\'' +
                ", surname='" + surname + '\'' +
                ", dob='" + fechaNacimiento + '\'' +
                ", nationality='" + nationality + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}

class OperacionesCRUDPilots {

    private static Path rutaBaseDatos = Path.of("src", "main", "resources", "db", "f12006sqlite.db");

    //devuelve el id del piloto creado en la BDD
    public static int CrearPiloto(Piloto piloto) {
        try (Connection conexion = DriverManager.getConnection("jdbc:sqlite:" + rutaBaseDatos.toString())) {
            String sql = "INSERT INTO drivers VALUES (?,?,?,?,?,?)";
            PreparedStatement consulta = conexion.prepareStatement(sql);
            consulta.setString(1, piloto.getCode());
            consulta.setString(2, piloto.getForename());
            consulta.setString(3, piloto.getSurname());
            consulta.setString(4, piloto.getFechaNacimiento().toString());
            consulta.setString(5, piloto.getNationality());
            consulta.setString(6, piloto.getUrl());
            return consulta.executeQuery().getInt("driverid");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return -1;
        }
    }
    public static Piloto LeerPiloto(int id){

        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" +rutaBaseDatos.toString())){
            PreparedStatement p =connection.prepareStatement("SELECT * FROM drivers WHERE driverid = ?");
            p.setInt(1,id);
            ResultSet resultSet = p.executeQuery();
            if(resultSet.next()){
                return new Piloto(resultSet.getInt("driverid"),resultSet.getString("code"),resultSet.getString("forename"),resultSet.getString("surname"),LocalDate.parse(resultSet.getString("dob")),resultSet.getString("nationality"),resultSet.getString("url"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    return null;
    }

    public boolean ActualizaPiloto(Piloto piloto){
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" +rutaBaseDatos.toString())){
            PreparedStatement p = connection.prepareStatement("UPDATE drivers SET code = ?,forename = ?,surname = ?,dob = ?,nationality = ? WHERE driverid = ?");
            p.setString(1,piloto.getCode());
            p.setString(2,piloto.getForename());
            p.setString(3,piloto.getSurname());
            p.setString(4,piloto.getFechaNacimiento().toString());
            p.setString(5,piloto.getNationality());
            p.setInt(6,piloto.getDriverid());
            return p.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean borraPiloto(Piloto piloto){
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" +rutaBaseDatos.toString())){
            PreparedStatement p = connection.prepareStatement("DELETE FROM drivers WHERE driverid = ?");
            p.setInt(1,piloto.getDriverid());
            return p.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public static void mostrarClasificacionPilotos(){
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" +rutaBaseDatos.toString())){
            PreparedStatement p = connection.prepareStatement("SELECT D.forename|| ' ' || D.surname AS PILOTO, SUM(R.points) AS PUNTOS FROM drivers D JOIN results R ON D.driverid = R.driverid GROUP BY D.driverid ORDER BY SUM(points) DESC");
            ResultSet r = p.executeQuery();
            while (r.next()){
                System.out.println(r.getString("PILOTO") + " " + r.getInt("PUNTOS"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void mostrarClasificacionConstructores(){
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" +rutaBaseDatos.toString())){
            PreparedStatement p = connection.prepareStatement("SELECT (SELECT C.NAME FROM constructors C WHERE C.constructorid = D.constructorid ) AS EQUIPO, SUM(R.points) AS PUNTOS FROM drivers D JOIN results R ON D.driverid = R.driverid GROUP BY D.constructorid ORDER BY SUM(points) DESC");
            ResultSet r = p.executeQuery();
            while (r.next()){
                System.out.println(r.getString("EQUIPO") + " " + r.getInt("PUNTOS"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        //mostrarClasificacionPilotos();
        mostrarClasificacionConstructores();
    }
}
