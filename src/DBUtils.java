/* This class will have (static?) methods for editing, deleting and retrieving events from the database.
I don't know if this is the right way to go, but I do think I should somehow have these methods in one location
in order to prevent duplicate code. */

import java.sql.*;

public final class DBUtils {

    static Connection localPostgresConnection = getConnection();

    private DBUtils() {
    }

    protected static Connection getConnection() {
        if(localPostgresConnection != null) {
            return localPostgresConnection;
        }
        return getConnection("jdbc:postgresql://localhost/calendar", "postgres", "postgres");

    }

    protected static Connection getConnection(String url, String user, String password) {
        try {
            localPostgresConnection = DriverManager.getConnection(url, user, password);
        }
        catch(SQLException ex) {
            ex.printStackTrace();
        }
        return localPostgresConnection;
    }

    protected static int deleteEvent(int primaryKey) {
        String sql = "DELETE FROM event WHERE id = ?";
        int affectedRows = 0;
        try {
            PreparedStatement statement = getConnection().prepareStatement(sql);
            statement.setInt(1, primaryKey);
            affectedRows = statement.executeUpdate();
        }
        catch (SQLException sqlExc) {
            sqlExc.printStackTrace();
        }
        return affectedRows;
    }
}

