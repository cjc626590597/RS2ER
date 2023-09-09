package ic.ac.rs2er_backend.util;

import ic.ac.rs2er_backend.common.RDBMSType;
import ic.ac.rs2er_backend.exception.DBConnectionException;
import ic.ac.rs2er_backend.exception.ParseException;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

public class DatabaseUtilTest {

    @Test
    public void testGenerateDatabaseURL() throws ParseException {
        RDBMSType databaseType = RDBMSType.POSTGRESQL;
        String hostname = "localhost";
        String portNum = "5432";
        String databaseName = "test";
        String dbUrl = DatabaseUtil.generateDatabaseURL(databaseType, hostname, portNum, databaseName);
        System.out.println(dbUrl);
    }

    @Test
    public void testAcquireDBConnection() throws DBConnectionException, ParseException {

        Connection conn = null;
        String driver = "";

        try {
            RDBMSType databaseType = RDBMSType.POSTGRESQL;
            String hostname = "localhost";
            String portNum = "5432";
            String databaseName = "test";
            String dbUrl = DatabaseUtil.generateDatabaseURL(databaseType, hostname, portNum, databaseName);;
            String userName = "postgres";
            String password = "a123456";

            driver = DatabaseUtil.recognDriver(databaseType);
            conn = DatabaseUtil.acquireDBConnection(driver, dbUrl, userName, password);
            DatabaseUtil.closeDBConnection();

        } catch (ParseException parseException) {
            throw new ParseException(parseException.getMessage());
        } catch (DBConnectionException dbConnectionException) {
            throw new DBConnectionException(dbConnectionException.getMessage());
        }
    }
}
