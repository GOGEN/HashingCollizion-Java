
import generators.Factory;
import minimization.Minimization;

import java.sql.*;
/**
 * Created by gogen on 29.04.15.
 */
public class Main {
    public static void main(String[] args){

        Connection conn = null;
        int ring = (int) Math.pow(2, 12);
        double delta = 0.06196117028594017;

        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:hashing.db");
            System.out.println("Opened database successfully");
            Minimization minmize = new Minimization(conn, Factory.initRandom(ring, delta));
            minmize.findBest(10, true);
            conn.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
    }
}