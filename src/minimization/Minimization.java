package minimization;

import generators.Factory;
import helpers.Helpers;
import helpers.Tuple;

import java.sql.*;
import java.util.Arrays;

/**
 * Created by gogen on 29.04.15.
 */
public class Minimization {
    private static final String TABLE_NAME = "results";

    private static final String INSERT_SQL = "INSERT INTO " + TABLE_NAME + " VALUES (?, ?, ?, ?, ?)";
    private static final String SELECT_LAST_RESULT_SQL = "SELECT deep, delta FROM " + TABLE_NAME +
            " WHERE deep IN ( SELECT max(deep) FROM " + TABLE_NAME + " WHERE ring = ? AND type = ?)";
    private static final String SELECT_FOR_DEEP_SQL = "SELECT deep, delta FROM " + TABLE_NAME +
            " WHERE ring = ? AND deep = ? AND type = ?";
    private static final String UPDATE_SQL = "UPDATE " + TABLE_NAME + " SET example = ?, delta = ? " +
            "WHERE ring = ? AND deep = ? AND type = ?";

    Connection conn;
    Factory factory;

    private PreparedStatement insertStmt = null;
    private PreparedStatement selectLastResultStmt = null;
    private PreparedStatement selectForDeepStmt = null;
    private PreparedStatement updateStmt = null;

    public Minimization(Connection conn, Factory factory) throws SQLException {
        this.conn = conn;
        this.factory = factory;
        initStmt();
    }

    private void initStmt() throws SQLException {
        selectLastResultStmt = conn.prepareStatement(SELECT_LAST_RESULT_SQL);
        selectLastResultStmt.setInt(1, factory.getRing());
        selectLastResultStmt.setString(2, factory.getType());
        insertStmt = conn.prepareStatement(INSERT_SQL);
        insertStmt.setInt(1, factory.getRing());
        insertStmt.setString(3, factory.getType());
        selectForDeepStmt = conn.prepareStatement(SELECT_FOR_DEEP_SQL);
        selectForDeepStmt.setInt(1, factory.getRing());
        selectForDeepStmt.setString(3, factory.getType());
        updateStmt = conn.prepareStatement(UPDATE_SQL);
        updateStmt.setInt(3, factory.getRing());
        updateStmt.setString(5, factory.getType());
    }

    public void findBest(int deepCount, boolean hasDB) throws SQLException {
        Tuple<Integer, Double> pair = getLastResult();
        factory.changeDeep(pair.first);
        int hightBound = Helpers.getHightBound(factory.getRing());
        double previousDelta = pair.second;
        while (deepCount > 0 && factory.getDeep() <= hightBound) {
            Tuple<Integer[], Double> tupple = factory.getBestSet(previousDelta);
            if(tupple != null) {
                System.out.println("Deep: " + factory.getDeep() + " Set: " +
                        Arrays.toString(tupple.first) + " Delta: " + tupple.second);
                if (hasDB) {
                    insertStmt.setInt(2, factory.getDeep());
                    insertStmt.setString(4, Arrays.toString(tupple.first));
                    insertStmt.setDouble(5, tupple.second);
                    insertStmt.execute();
                }
            }else
                System.out.println("Deep: " + factory.getDeep() + " Set not found");
            factory.nextDeep();
            deepCount --;
        }
    }

    public void calcForDeep(int deep, boolean hasDB) throws SQLException {
        Tuple<Integer, Double> pair = getResultForDeep(deep);
        if(pair != null && hasDB && pair.second < factory.getStoppingCriteria()){
            System.out.println("Deep: " + pair.first + " Delta: " + pair.second);
            return;
        }
        if(Helpers.getHightBound(factory.getRing()) < deep)
            deep = Helpers.getHightBound(factory.getRing());
        factory.changeDeep(deep);
        Tuple<Integer[], Double> tuple = factory.getSetForDelta();
        if(tuple == null) {
            System.out.println("Set not found");
            return;
        }

        System.out.println("Deep: " + factory.getDeep() + " Set: " +
                Arrays.toString(tuple.first) + " Delta: " + tuple.second);
        if(hasDB) {
            if (pair == null) {
                insertStmt.setInt(2, factory.getDeep());
                insertStmt.setString(4, Arrays.toString(tuple.first));
                insertStmt.setDouble(5, tuple.second);
                insertStmt.execute();
            } else if (tuple.second < pair.second) {
                updateStmt.setInt(4, factory.getDeep());
                updateStmt.setString(1, Arrays.toString(tuple.first));
                updateStmt.setDouble(2, tuple.second);
                updateStmt.execute();
            }
        }
    }

    private Tuple<Integer, Double> getLastResult() throws SQLException {
        ResultSet rs = selectLastResultStmt.executeQuery();
        Tuple<Integer, Double> pair = new Tuple(Helpers.getLowerBound(), 1.0);
        if(rs.next()) {
            pair.first = rs.getInt("deep") * 2;
            pair.second = rs.getDouble("delta");
        }
        rs.close();
        return pair;
    }

    private Tuple<Integer, Double> getResultForDeep(int deep) throws SQLException {
        selectForDeepStmt.setInt(2, deep);
        ResultSet rs = selectForDeepStmt.executeQuery();
        if(!rs.next())
            return null;
        Tuple<Integer, Double> pair = new Tuple( rs.getInt("deep"), rs.getDouble("delta"));
        rs.close();
        return pair;
    }
}
