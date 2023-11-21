package org.sqlite;

import java.sql.*;
import java.time.LocalDate;

class OperacionesCRUDPilots2 {

    private static String rutaBaseDatos = "jdbc:postgresql://database-1.ciz5vzqpdhap.us-east-1.rds.amazonaws.com:5432/f12006";
    private static String usuario = "jorgef";
    private static String password = "Pass1234";

    //devuelve el id del piloto creado en la BDD
    public static int CrearPiloto(Piloto piloto) {
        try (Connection conexion = DriverManager.getConnection(rutaBaseDatos, usuario, password)) {
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

    public static int CrearPiloto(Piloto piloto, Connection conexion) throws SQLException {

        String sql = "INSERT INTO drivers VALUES (?,?,?,?,?,?)";
        PreparedStatement consulta = conexion.prepareStatement(sql);
        consulta.setString(1, piloto.getCode());
        consulta.setString(2, piloto.getForename());
        consulta.setString(3, piloto.getSurname());
        consulta.setString(4, piloto.getFechaNacimiento().toString());
        consulta.setString(5, piloto.getNationality());
        consulta.setString(6, piloto.getUrl());

        return consulta.executeQuery().getInt("driverid");

    }

    public static void checkeaFuncionamiento() {
        try (Connection conexion = DriverManager.getConnection(rutaBaseDatos, usuario, password)) {
            String sql = "SELECT * FROM drivers";
            PreparedStatement consulta = conexion.prepareStatement(sql);
            ResultSet resultSet = consulta.executeQuery();
            while (resultSet.next()) {
                System.out.println(resultSet.getString("driverid"));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    public static Piloto LeerPiloto(int id) {
        Piloto piloto;
        try (Connection conexion = DriverManager.getConnection(rutaBaseDatos, usuario, password)) {
            PreparedStatement p = conexion.prepareStatement("SELECT driverid,code,forename,surname,dob,nationality,url FROM drivers WHERE driverid = ?");
            p.setInt(1, id);
            ResultSet resultSet = p.executeQuery();
            if (resultSet.next()) {
                piloto = new Piloto(resultSet.getInt("driverid"), resultSet.getString("code"), resultSet.getString("forename"), resultSet.getString("surname"), LocalDate.parse(resultSet.getString("dob")), resultSet.getString("nationality"), resultSet.getString("url"), new Constructors());
                PreparedStatement p2 = conexion.prepareStatement("SELECT constructorRef,constructorid,name,nationality FROM constructors WHERE constructorid = ?");
                p2.setInt(1, id);
                resultSet = p2.executeQuery();
                if (resultSet.next()) {
                    piloto.getConstructors().setConstructorRef(resultSet.getString("constructorRef"));
                    piloto.getConstructors().setConstructorid(resultSet.getInt("constructorid"));
                    piloto.getConstructors().setName(resultSet.getString("name"));
                    piloto.getConstructors().setNationality(resultSet.getString("nationality"));
                }
                return piloto;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    public boolean ActualizaPiloto(Piloto piloto) {
        try (Connection conexion = DriverManager.getConnection(rutaBaseDatos, usuario, password)) {
            PreparedStatement p = conexion.prepareStatement("UPDATE drivers SET code = ?,forename = ?,surname = ?,dob = ?,nationality = ? WHERE driverid = ?");
            p.setString(1, piloto.getCode());
            p.setString(2, piloto.getForename());
            p.setString(3, piloto.getSurname());
            p.setString(4, piloto.getFechaNacimiento().toString());
            p.setString(5, piloto.getNationality());
            p.setInt(6, piloto.getDriverid());
            return p.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean borraPiloto(Piloto piloto) {
        try (Connection conexion = DriverManager.getConnection(rutaBaseDatos, usuario, password)) {
            PreparedStatement p = conexion.prepareStatement("DELETE FROM drivers WHERE driverid = ?");
            p.setInt(1, piloto.getDriverid());
            return p.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void mostrarClasificacionPilotos() {
        try (Connection connection = DriverManager.getConnection(rutaBaseDatos, usuario, password)) {
            PreparedStatement p = connection.prepareStatement("SELECT D.forename|| ' ' || D.surname AS PILOTO, SUM(R.points) AS PUNTOS FROM drivers D JOIN results R ON D.driverid = R.driverid GROUP BY D.driverid ORDER BY SUM(points) DESC");
            ResultSet r = p.executeQuery();
            while (r.next()) {
                System.out.println(r.getString("PILOTO") + " " + r.getInt("PUNTOS"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void mostrarClasificacionConstructores() {
        try (Connection connection = DriverManager.getConnection(rutaBaseDatos, usuario, password);
             PreparedStatement p = connection.prepareStatement(
                     "SELECT (SELECT C.NAME FROM constructors C WHERE C.constructorid = D.constructorid ) AS EQUIPO, SUM(R.points) AS PUNTOS " +
                             "FROM drivers D JOIN results R ON D.driverid = R.driverid " +
                             "GROUP BY D.constructorid ORDER BY SUM(points) DESC");) {
            ResultSet r = p.executeQuery();
            while (r.next()) {
                System.out.println(r.getString("EQUIPO") + " " + r.getInt("PUNTOS"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void mostrarClasificacionConstructoresPodio() {
        try (Connection connection = DriverManager.getConnection(rutaBaseDatos, usuario, password)) {
            PreparedStatement p = connection.prepareStatement(
                    "SELECT (SELECT C.NAME FROM constructors C WHERE C.constructorid = D.constructorid ) AS EQUIPO, SUM(R.points) AS PUNTOS " +
                            "FROM drivers D JOIN results R ON D.driverid = R.driverid " +
                            "GROUP BY D.constructorid ORDER BY SUM(points) DESC");
            ResultSet r = p.executeQuery();
            int i = 1;
            while (r.next() && i <= 3) {
                System.out.println("Podio nº" + i + " " + r.getString("EQUIPO") + " " + r.getInt("PUNTOS"));
                i++;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    //get_results_by_driver(cod), que recibe un código de piloto y devuelve sus resultados para cada
    //carrera de la temporada.
    //• get_drivers_standings(), que no recibe ningún parámetro y devuelve la clasificación final del
    //mundial.
    public static void get_results_by_driver(int id){
        try (Connection connection = DriverManager.getConnection(rutaBaseDatos, usuario, password)) {
            PreparedStatement p = connection.prepareStatement(
                    "SELECT R.postition, C.name,C.date,C.time" +
                            "FROM  results R JOIN races C ON R.raceid = C.raceid " +
                            "WHERE D.driverid = ?");
            p.setInt(1,id);
            ResultSet r = p.executeQuery();
            while (r.next()) {
                System.out.println(r);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public static void main (String[]args){

        Constructors c1 = new Constructors(1,"seat","seat","italy","url");

        Piloto p1 = new Piloto("JOR","Carlos","Sainz",LocalDate.now(),"","",c1);
        Piloto p2 = new Piloto("PEP","Manuel","Alomá",LocalDate.now(),"","",c1);

        try(Connection connection = DriverManager.getConnection(rutaBaseDatos, usuario, password)){
            connection.setAutoCommit(false);
            try {
                String sql = "INSERT INTO constructors (constructorref,name,nationality,url) VALUES (?,?,?,?) " +
                        "ON CONFLICT (constructorref) DO NOTHING RETURNING constructorref;";
                PreparedStatement p = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                p.setString(1, c1.getConstructorRef());
                p.setString(2, c1.getName());
                p.setString(3, c1.getNationality());
                p.setString(4, c1.getUrl());
                p.executeUpdate();
                ResultSet r;
                if ((r = p.getGeneratedKeys()).next()) {
                    sql = "INSERT INTO drivers(code,forename,surname,dob,nationality,constructorid,url) VALUES (?,?,?,?,?,?,?)";
                    p = connection.prepareStatement(sql);
                    p.setString(1, p1.getCode());
                    p.setString(2, p1.getForename());
                    p.setString(3, p1.getSurname());
                    p.setString(4, p1.getFechaNacimiento().toString());
                    p.setString(5, p1.getNationality());
                    p.setString(6, r.getString(1));
                    p.setString(7, p1.getUrl());
                }
            } catch (SQLException e) {
                e.printStackTrace();
                connection.rollback();
            }
            try {
                String sql = "INSERT INTO constructors (constructorRef,name,nationality,url) VALUES (?,?,?,?) " +
                        "ON CONFLICT (constructorref) DO NOTHING RETURNING constructorref;";
                PreparedStatement p = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                p.setString(1, c1.getConstructorRef());
                p.setString(2, c1.getName());
                p.setString(3, c1.getNationality());
                p.setString(4, c1.getUrl());
                int r = p.executeUpdate();
                if (p.getGeneratedKeys().next()) {
                    sql = "INSERT INTO drivers(code,forename,surname,dob,nationality,constructorid,url) VALUES (?,?,?,?,?,?,?)";
                    p = connection.prepareStatement(sql);
                    p.setString(1, p2.getCode());
                    p.setString(2, p2.getForename());
                    p.setString(3, p2.getSurname());
                    p.setString(4, p2.getFechaNacimiento().toString());
                    p.setString(5, p2.getNationality());
                    p.setString(6, p.getGeneratedKeys().getString(1));
                    p.setString(7, p2.getUrl());
                }
            } catch (SQLException e) {
                e.printStackTrace();
                connection.rollback();
            }
            connection.commit();
        } catch (SQLException e) {
        e.printStackTrace();
        }
        get_results_by_driver(2);
    }
}
