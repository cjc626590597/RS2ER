package ic.ac.rs2er_backend.controller;

import ic.ac.rs2er_backend.common.RDBMSType;
import ic.ac.rs2er_backend.dto.ConnectDatabaseRequest;
import ic.ac.rs2er_backend.exception.DBConnectionException;
import ic.ac.rs2er_backend.exception.ParseException;
import ic.ac.rs2er_backend.util.DatabaseUtil;
import ic.ac.rs2er_backend.util.SchemaspyUtil;
import org.springframework.web.bind.annotation.*;

import java.sql.Connection;

@RestController
@RequestMapping("/database")
public class DatabaseHandler {

    @GetMapping("test")
    public String test(){
        try {
            Thread.sleep(2000);
            return "test";
        } catch (InterruptedException e) {
            return e.toString();
        }
    }

    @PostMapping("/connect")
    public String connect(@RequestBody ConnectDatabaseRequest request){
        RDBMSType databaseType = request.getDatabaseType();
        String hostname = request.getHostname();
        String portNum = request.getPortNum();
        String databaseName = request.getDatabaseName();
        String dbUrl = null;
        String userName = request.getUserName();
        String password = request.getPassword();

        Connection conn = null;
        String driver = "";

        try {
            dbUrl = DatabaseUtil.generateDatabaseURL(databaseType, hostname, portNum, databaseName);

            driver = DatabaseUtil.recognDriver(databaseType);
            conn = DatabaseUtil.acquireDBConnection(driver, dbUrl, userName, password);
            long startTime = System.currentTimeMillis();
//            SchemaspyUtil.generatePhysicalERD(databaseType, hostname, portNum, databaseName, userName, password);
            long endTime = System.currentTimeMillis();
            System.out.println("Schemaspy Time：" + (endTime - startTime) + "ms");

            return "success";
        } catch (ParseException | DBConnectionException e) {
            return e.toString();
        }
    }
}
