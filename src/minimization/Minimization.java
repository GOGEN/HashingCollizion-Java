package minimization;

import generators.Factory;
import helpers.Helpers;
import helpers.Tuple;

import java.sql.*;

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

    int ring;
    double delta;
    Connection conn;
    Factory factory;

    private PreparedStatement insertStmt = null;
    private PreparedStatement selectLastResultStmt = null;
    private PreparedStatement selectForDeepStmt = null;
    private PreparedStatement updateStmt = null;

    public Minimization(Connection conn, Factory factory, int ring, double delta) throws SQLException {
        this.ring = ring;
        this.delta = delta;
        this.conn = conn;
        this.factory = factory;
        initStmt();
    }

    private void initStmt() throws SQLException {
        selectLastResultStmt = conn.prepareStatement(SELECT_LAST_RESULT_SQL);
        selectLastResultStmt.setInt(1, ring);
        selectLastResultStmt.setString(2, factory.getType());
        insertStmt = conn.prepareStatement(INSERT_SQL);
        insertStmt.setInt(1, ring);
        insertStmt.setString(3, factory.getType());
        selectForDeepStmt = conn.prepareStatement(SELECT_FOR_DEEP_SQL);
        selectForDeepStmt.setInt(1, ring);
        selectForDeepStmt.setString(3, factory.getType());
        updateStmt = conn.prepareStatement(UPDATE_SQL);
        updateStmt.setInt(3, ring);
        updateStmt.setString(5, factory.getType());
    }

    public void start(int deepCount, boolean isWrite) throws SQLException {
        Tuple<Integer, Double> pair = getLastResult();
        factory.changeDeep(pair.first);
        int hightBound = Helpers.getHightBound(ring, delta);
        double previousDelta = pair.second;
        while (deepCount > 0 && factory.getDeep() <= hightBound) {
            Tuple<Integer[], Double> tupple = factory.getBetterSet(previousDelta);
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

    public void calcForDeep(int deep, boolean isUpdate) throws SQLException {
        Tuple<Integer, Double> pair = getResultForDeep(deep);
        if(pair != null && !isUpdate){
            System.out.println("Deep: " + pair.first + " Delta: " + pair.second);
            return;
        }
        int theoryBound = Helpers.getHightBound(ring, delta);
        System.out.println("Theory bound: " + theoryBound);
        if(theoryBound < deep){
            deep = theoryBound;
        }
        factory.changeDeep(deep);
        Tuple<Integer[], Double> tupple = factory.getSetForDelta();
        if(tupple == null) {
            System.out.println("Set not found");
            return;
        }

        System.out.println("Deep: " + factory.getDeep() + " Set: " +
                Helpers.showArray(tupple.first) + " Delta: " + tupple.second);
        if (pair == null) {
            insertStmt.setInt(2, factory.getDeep());
            insertStmt.setString(4, Helpers.showArray(tupple.first));
            insertStmt.setDouble(5, tupple.second);
            insertStmt.execute();
        }else if(tupple.second < pair.second){
            updateStmt.setInt(4, factory.getDeep());
            updateStmt.setString(1, Helpers.showArray(tupple.first));
            updateStmt.setDouble(2, tupple.second);
            updateStmt.execute();
        }
    }

    private Tuple<Integer, Double> getLastResult() throws SQLException {
        ResultSet rs = selectLastResultStmt.executeQuery();
        Tuple<Integer, Double> pair = new Tuple(Helpers.getLowerBound(ring, delta), 1.0);
        if(rs.next()) {
            pair.first = rs.getInt("deep") + 1;
            pair.second = rs.getDouble("delta");
        }
        rs.close();
        return pair;
    }

    private Tuple<Integer, Double> getResultForDeep(int deep) throws SQLException {
        selectForDeepStmt.setInt(2, deep);
        ResultSet rs = selectForDeepStmt.executeQuery();
        if(!rs.next()){
            return null;
        }
        Tuple<Integer, Double> pair = new Tuple( rs.getInt("deep"), rs.getDouble("delta"));
        rs.close();
        return pair;
    }
}
