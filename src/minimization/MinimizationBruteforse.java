package minimization;

import generators.Factory;
import helpers.Helpers;
import helpers.Tuple;

import java.sql.*;

/**
 * Created by gogen on 29.04.15.
 */
public class MinimizationBruteforse extends AbstractMinimization {
    private static final String TABLE_NAME = "bruteforse_results";
    private static final String INSERT_SQL = "INSERT INTO " + TABLE_NAME + " VALUES (?, ?, ?, ?)";
    private static final String SELECT_SQL = "SELECT deep, delta FROM " + TABLE_NAME +
            " WHERE deep IN ( SELECT max(deep) FROM " + TABLE_NAME + " WHERE ring = ? )";
    private PreparedStatement insertStmt = null;
    private PreparedStatement selectStmt = null;
    private double[] listOfCos;

    public MinimizationBruteforse(Connection conn, int ring, double delta) throws SQLException {
        super(conn, ring, delta);
        factory = Factory.initBruteforse(ring);
        insertStmt = conn.prepareStatement(INSERT_SQL);
        selectStmt = conn.prepareStatement(SELECT_SQL);
        selectStmt.setInt(1, ring);
        insertStmt.setInt(1, ring);
        listOfCos = Helpers.calcListOfCos(ring);
    }

    public void start(int deepCount) throws SQLException {
        Tuple<Integer, Double> pair = getLastResult();
        factory.changeDeep(pair.first);
        int[] resultSet = null;
        double currDelta = 1.0;
        int hightBound = Helpers.getHightBound(ring, delta);
        double previousDelta = pair.second;
        while (deepCount > 0 && factory.getDeep() <= hightBound) {
            int[] set;
            while ((set = factory.nextSet()) != null && currDelta > delta) {
                currDelta = Helpers.getMaxForAllX(listOfCos, ring, previousDelta, set);
                if (currDelta < previousDelta) {
                    previousDelta = currDelta;
                    resultSet = set.clone();
                }
            }
            System.out.print("Deep: " + factory.getDeep());
            if(resultSet != null && resultSet.length == factory.getDeep() - 1) {
                System.out.println("Set: " + Helpers.showArray(resultSet));
                insertStmt.setInt(2, resultSet.length);
                insertStmt.setString(3, Helpers.showArray(resultSet));
                insertStmt.setDouble(4, previousDelta);
                insertStmt.execute();
            }
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
