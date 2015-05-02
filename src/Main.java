
import generators.Factory;
import minimization.IMinimization;

import java.sql.*;
/**
 * Created by gogen on 29.04.15.
 */
public class Main {
    public static void main(String[] args){

        Connection conn = null;
        int ring = (int) Math.pow(2, 5);
        double delta = 0.04;

        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:hashing.db");
            System.out.println("Opened database successfully");
            IMinimization minmize = IMinimization.init(conn, Factory.initBruteforse(ring, delta), ring, delta);
            minmize.start(14, false);
            conn.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
    }
}
