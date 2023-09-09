package ic.ac.rs2er_backend.util;

import ic.ac.rs2er_backend.common.RDBMSType;
import org.junit.jupiter.api.Test;


public class SchemaspyUtilTest {
    @Test
    public void testExecute(){
        SchemaspyUtil.execute("ping www.google.com");
    }

    @Test
    public void testExecuteSchemaspy(){
        String driverPath = "/D:/myrepo1/org/postgresql/postgresql/42.3.8/postgresql-42.3.8.jar";
        SchemaspyUtil.execute("java -jar ./my_lib/schemaspy-6.2.2.jar -t pgsql11 -dp "+driverPath+" -db test -host localhost -port 5432  -u postgres -p a123456 -o path/to/output");
    }

    @Test
    public void testGeneratePhysicalERD() {
        RDBMSType databaseType = RDBMSType.POSTGRESQL;
        String hostname = "localhost";
        String portNum = "5432";
        String databaseName = "test";
        String userName = "postgres";
        String password = "a123456";
        SchemaspyUtil.generatePhysicalERD(databaseType,
                hostname,
                portNum,
                databaseName,
                userName,
                password);
    }
}
