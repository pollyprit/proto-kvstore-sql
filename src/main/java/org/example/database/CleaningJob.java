package org.example.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;


public class CleaningJob extends Thread {
    private long runIntervalSec;
    private Connection connection;
    private boolean stop = false;

    public CleaningJob(String url, String user, String password, long runIntervalSec) throws ClassNotFoundException, SQLException {
        this.runIntervalSec = runIntervalSec;
        Class.forName("org.postgresql.Driver");
        connection = DBConnectionFactory.createConnection(url, user, password);

        System.out.println("cleanup job started. run internal(sec) " + runIntervalSec);
    }

    @Override
    public void run() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
        String query = "DELETE FROM kvstore " +
                "WHERE key IN ( " +
                "    SELECT key " +
                "    FROM kvstore " +
                "    WHERE expiry is null or expiry <= now() " +
                "    limit 100 ) ";

        try {
            Thread.sleep(runIntervalSec * 1000);
        } catch (InterruptedException e) {
            // ignore
        }

        try {
            Statement statement = connection.createStatement();
            int rowsDel = statement.executeUpdate(query);

            System.out.println("[" + dateFormat.format(new Date()) +
                    "] cleanup job: rows deleted " + rowsDel);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void stopCleaningJob() {
        this.stop = true;
    }
}
