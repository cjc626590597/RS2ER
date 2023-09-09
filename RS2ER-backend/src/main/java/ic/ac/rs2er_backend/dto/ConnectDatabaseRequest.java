package ic.ac.rs2er_backend.dto;

import ic.ac.rs2er_backend.common.RDBMSType;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ConnectDatabaseRequest {
    RDBMSType databaseType;
    String hostname;
    String portNum;
    String databaseName;
    String userName ;
    String password;
}
