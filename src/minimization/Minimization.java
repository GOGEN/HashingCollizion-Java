package minimization;

import generators.Factory;
import helpers.Helpers;
import helpers.Tuple;

import java.sql.*;

/**
 * Created by gogen on 29.04.15.
 */
public class Minimization implements IMinimization {
    private static final String TABLE_NAME = "results";

    private static final String INSERT_SQL = "INSERT INTO " + TABLE_NAME + " VALUES (?, ?, ?, ?, ?)";
    private static final String SELECT_SQL = "SELECT deep, delta FROM " + TABLE_NAME +
            " WHERE deep IN ( SELECT max(deep) FROM " + TABLE_NAME + " WHERE ring = ? AND type = ?)";

    int ring;
    double delta;
    Connection conn;
    Factory factory;

    private PreparedStatement insertStmt = null;
    private PreparedStatement selectStmt = null;
    private double[] listOfCos;

    public Minimization(Connection conn, Factory factory, int ring, double delta) throws SQLException {
        this.ring = ring;
        this.delta = delta;
        this.conn = conn;
        this.factory = factory;
        initStmt();
        listOfCos = Helpers.calcListOfCos(ring);
    }

    private void initStmt() throws SQLException {
        insertStmt = conn.prepareStatement(INSERT_SQL);
        selectStmt = conn.prepareStatement(SELECT_SQL);
        selectStmt.setInt(1, ring);
        selectStmt.setString(2, factory.getType());
        insertStmt.setInt(1, ring);
        insertStmt.setString(3, factory.getType());

    }

    public void start(int deepCount, boolean isWrite) throws SQLException {
        Tuple<Integer, Double> pair = getLastResult();
        factory.changeDeep(pair.first);
        int hightBound = Helpers.getHightBound(ring, delta);
        double previousDelta = pair.second;
        while (deepCount > 0 && factory.getDeep() <= hightBound) {
            Tuple<Integer[], Double> tupple = factory.getBestSet(previousDelta);
            if(tupple != null) {
                System.out.println("Deep: " + factory.getDeep() + " Set: " +
                        Helpers.showArray(tupple.first) + " Delta: " + tupple.second);
                if (isWrite) {
                    insertStmt.setInt(2, factory.getDeep());
                    insertStmt.setString(4, Helpers.showArray(tupple.first));
                    insertStmt.setDouble(5, tupple.second);
                    insertStmt.execute();
                }
            }
            factory.nextDeep();
            deepCount --;
        }
    }

    private Tuple<Integer, Double> getLastResult() throws SQLException {
        ResultSet rs = selectStmt.executeQuery();
        Tuple<Integer, Double> pair = new Tuple(Helpers.getLowerBound(ring, delta), 1.0);
        if(rs.next()) {
            pair.first = rs.getInt("deep") + 1;
            pair.second = rs.getDouble("delta");
        }
        rs.close();
        return pair;
    }
}
