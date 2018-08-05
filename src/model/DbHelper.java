package model;

import java.sql.*;

public class DbHelper {
    private static Connection connection;
    private static DbHelper instance;
    private static String databaseUrl = "jdbc:sqlite:financialManager.db";

    private DbHelper() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(databaseUrl);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }

        if (!databaseExists()) {
            try {
                Statement statement = connection.createStatement();
                String createSql = readResource(DbHelper.class, "sql/create.sql");
                statement.executeUpdate(createSql);
                statement.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private boolean databaseExists() {
        boolean result = true;
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT count(*) FROM sqlite_master WHERE type='table' AND name='USERS';");
            int count = rs.getInt(1);
            if (count == 0) {
                result = false;
            }
            rs.close();
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private String readResource(Class cpHolder, String path) throws Exception {
        java.net.URL url = cpHolder.getResource(path);
        java.nio.file.Path resPath = java.nio.file.Paths.get(url.toURI());
        return new String(java.nio.file.Files.readAllBytes(resPath), "UTF8");
    }

    public static DbHelper getInstance() {
        if (instance == null) {
            instance = new DbHelper();
        }
        return instance;
    }


    public Connection getConnection() {
        if (connection == null) {
            try {
                connection = DriverManager.getConnection(databaseUrl);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return connection;
    }
}
