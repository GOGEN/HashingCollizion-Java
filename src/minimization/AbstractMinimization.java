package minimization;

import generators.Factory;

import java.sql.Connection;

/**
 * Created by gogen on 29.04.15.
 */
public abstract class AbstractMinimization implements Minimization {
    int ring;
    double delta;
    Connection conn;
    Factory factory;

    public AbstractMinimization(Connection conn, int ring, double delta){
        this.ring = ring;
        this.delta = delta;
        this.conn = conn;
    }
}
