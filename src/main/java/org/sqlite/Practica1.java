package org.sqlite;

import java.nio.file.Path;
import java.sql.*;
import java.time.LocalDate;





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
        Piloto piloto = null;
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" +rutaBaseDatos.toString())){
            PreparedStatement p =connection.prepareStatement("SELECT driverid,code,forename,surname,dob,nationality, FROM drivers WHERE driverid = ?");
            p.setInt(1,id);
            ResultSet resultSet = p.executeQuery();
            if(resultSet.next()){
                piloto = new Piloto(resultSet.getInt("driverid"),resultSet.getString("code"),resultSet.getString("forename"),resultSet.getString("surname"),LocalDate.parse(resultSet.getString("dob")),resultSet.getString("nationality"),resultSet.getString("url"),new Constructors());

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
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" +rutaBaseDatos.toString());
             PreparedStatement p = connection.prepareStatement(
                     "SELECT (SELECT C.NAME FROM constructors C WHERE C.constructorid = D.constructorid ) AS EQUIPO, SUM(R.points) AS PUNTOS " +
                             "FROM drivers D JOIN results R ON D.driverid = R.driverid " +
                             "GROUP BY D.constructorid ORDER BY SUM(points) DESC");){
            ResultSet r = p.executeQuery();
            while (r.next()){
                System.out.println(r.getString("EQUIPO") + " " + r.getInt("PUNTOS"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void mostrarClasificacionConstructoresPodio(){
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" +rutaBaseDatos.toString());
             PreparedStatement p = connection.prepareStatement(
                     "SELECT (SELECT C.NAME FROM constructors C WHERE C.constructorid = D.constructorid ) AS EQUIPO, SUM(R.points) AS PUNTOS " +
                             "FROM drivers D JOIN results R ON D.driverid = R.driverid " +
                             "GROUP BY D.constructorid ORDER BY SUM(points) DESC")){
            ResultSet r = p.executeQuery();
            int i = 1;
            while (r.next() && i <= 3){
                System.out.println("Podio nº"+i+" "+r.getString("EQUIPO") + " " + r.getInt("PUNTOS"));
                i++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void existeEquipo(String constructorid,String nationality){

    }

    public static void main(String[] args) {
        //mostrarClasificacionPilotos();
        //mostrarClasificacionConstructores();
        mostrarClasificacionConstructoresPodio();
    }
}
