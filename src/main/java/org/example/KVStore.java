package org.example;


import org.example.database.*;

import java.sql.*;

/*
    Table:
        create table kvstore(
            key text PRIMARY KEY,
            value text,
            expiry TIMESTAMP);

    indexes: key, expiry (for cleanup job)
 */
public class KVStore {
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/test";
    private static final String DB_USER = "postgres";
    private static final String DB_PASS = "postgres";
    private static int POOL_SIZE = 20;
    private DBConnectionPool dbConnectionPool;

    public KVStore() {
        try {
            dbConnectionPool = new DBConnectionPool(DB_URL, DB_USER, DB_PASS, POOL_SIZE);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void put(String key, String value, long ttlSec) {
        Connection connection = dbConnectionPool.getConnection();

        try {
            String query = "INSERT INTO kvstore (key, value, expiry) VALUES (?, ?, ?) " +
                    " ON CONFLICT(key) DO UPDATE "
                    + " SET value = ?, expiry = ?";

            long ttlMS = System.currentTimeMillis() + ttlSec * 1000;
            Timestamp expiryTime = new Timestamp(ttlMS);

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, key);
            preparedStatement.setString(2, value);
            preparedStatement.setTimestamp(3, expiryTime);
            preparedStatement.setString(4, value);
            preparedStatement.setTimestamp(5, expiryTime);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            dbConnectionPool.returnConnection(connection);
        }
    }

    public String get(String key) {
        Connection connection = dbConnectionPool.getConnection();

        try {
            String query = "SELECT value FROM kvstore " +
                    " WHERE key=? and expiry > now()";

            Timestamp exp = new Timestamp(System.currentTimeMillis());

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, key);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next())
                return resultSet.getString("value");

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            dbConnectionPool.returnConnection(connection);
        }
        return null;
    }

    public void del(String key) {
        Connection connection = dbConnectionPool.getConnection();

        try {
            String query = "UPDATE kvstore SET expiry = null" +
                    " WHERE key=" + key + " and expiry > now()";

            Statement statement = connection.createStatement();
            statement.executeUpdate(query);
        }
        catch(SQLException e) {
            e.printStackTrace();
        }
        finally {
            dbConnectionPool.returnConnection(connection);
        }
    }


}
