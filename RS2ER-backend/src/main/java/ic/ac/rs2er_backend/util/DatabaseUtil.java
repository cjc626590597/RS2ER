package ic.ac.rs2er_backend.util;

import ic.ac.rs2er_backend.common.RDBMSType;
import ic.ac.rs2er_backend.exception.DBConnectionException;
import ic.ac.rs2er_backend.exception.ParseException;

import java.sql.*;

public class DatabaseUtil {
    public static Connection conn = null;

    public static void closeDBConnection() throws DBConnectionException {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException throwables) {
                throw new DBConnectionException("Fail to close the database connection");
            }
        }
    }


    public static Connection acquireDBConnection(String driver, String dbUrl, String userName, String password) throws DBConnectionException {
        try {
            Class.forName(driver);
            conn = DriverManager.getConnection(dbUrl, userName, password);
            System.out.println(DriverManager.getDriver(dbUrl).getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
        } catch (ClassNotFoundException e) {
            throw new DBConnectionException("Driver not found: " + driver);
        } catch (SQLException e) {
            throw new DBConnectionException("Fail to connect the database. Database Url: " + dbUrl + " User name"
                    + userName + " Password: " + password);
        }
        return conn;
    }


    public static String generateDatabaseURL(RDBMSType databaseType, String hostname, String portNum, String databaseName) throws ParseException {
        if (databaseType == RDBMSType.MYSQL) {
            return "jdbc:mysql://" + hostname + ":" + portNum + "/" + databaseName;
        } else if (databaseType == RDBMSType.POSTGRESQL) {
            return "jdbc:postgresql://" + hostname + ":" + portNum + "/" + databaseName;
        } else if (databaseType == RDBMSType.SQLSERVER) {
            return "jdbc:sqlserver://" + hostname + ":" + portNum + ";DatabaseName=" + databaseName;
        }
        throw new ParseException("Cannot recognise current RDBMS type: " + databaseType.toString());
    }

    public static String recognDriver(RDBMSType databaseType) throws ParseException {
        if (databaseType == RDBMSType.MYSQL) {
//            return "com.mysql.jdbc.Driver";
            return "com.mysql.cj.jdbc.Driver";
        } else if (databaseType == RDBMSType.POSTGRESQL) {
            return "org.postgresql.Driver";
        } else if (databaseType == RDBMSType.SQLSERVER) {
            return "com.microsoft.sqlserver.jdbc.SQLServerDriver";
        }
        throw new ParseException("Cannot recognise current RDBMS type: " + databaseType.toString());
    }
}
