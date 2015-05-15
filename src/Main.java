
import generators.Factory;
import minimization.Minimization;

import java.sql.*;
/**
 * Created by gogen on 29.04.15.
 */
public class Main {
    public static void main(String[] args){

        Connection conn = null;
        int ring = (int) Math.pow(2, 8);
        double delta = 0.066;

        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:hashing.db");
            System.out.println("Opened database successfully");
            Minimization minmize = new Minimization(conn, Factory.initGenetic(ring, delta));
            minmize.calcForDeep(128, true);
            conn.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
    }
}