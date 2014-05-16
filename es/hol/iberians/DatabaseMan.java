package es.hol.iberians;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseMan {
    public static String host;
    public static int port;
    public static String user;
    public static String pass;
    public static String db;
    public static Connection connect;
    public DatabaseMan(){
        
    }
    public static void connect(){
        try{
            Class.forName("com.mysql.jdbc.Driver");
        } catch(ClassNotFoundException e){
            e.printStackTrace();
        }
        try{
            connect = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + db +"?user=" + user + "&password=" + pass);
            
        } catch(SQLException e){
            e.printStackTrace();
        }
    }
    
    public ResultSet query(String sql) {
        Statement s;
        try {
            s = connect.createStatement();
            ResultSet r = s.executeQuery(sql);
            return r;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

    }
    
    public Integer update(String sql) {
        Statement s;
        try {
            s = connect.createStatement();
            return s.executeUpdate(sql);
        } catch (SQLException e) {
            if(!e.getMessage().contains("Duplicate entry")) {
                e.printStackTrace();
            } else {
                System.out.print("Huehue");
            }
            return null;
        }
}
}
