/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package db;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 *
 * @author rapid
 */
public class DBUtil {
    
    private static final String URL  = 
            "jdbc:mysql://localhost:3306/pff";
    private static final String USER = "root";       // change if needed
    private static final String PASS = "p1FF%";   // change if needed

    public static Connection getConnection() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");  
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
