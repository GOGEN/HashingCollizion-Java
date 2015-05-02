package minimization;

import generators.Factory;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by gogen on 29.04.15.
 */
public interface IMinimization {

    public static IMinimization init(Connection conn, Factory factory, int ring, double delta) throws SQLException {
        return new Minimization(conn, factory, ring, delta);
    }

    public void start(int deepCount, boolean isWrite) throws SQLException;

}
