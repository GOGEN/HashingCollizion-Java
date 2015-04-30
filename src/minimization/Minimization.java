package minimization;

import helpers.Tuple;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by gogen on 29.04.15.
 */
public interface Minimization {

    public static Minimization initBruteforse(Connection conn, int ring, double delta) throws SQLException {
        return new MinimizationBruteforse(conn, ring, delta);
    }

    public void start(int deepCount) throws SQLException;
}
