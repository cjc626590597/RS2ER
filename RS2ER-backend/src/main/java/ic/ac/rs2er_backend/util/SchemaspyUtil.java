package ic.ac.rs2er_backend.util;

import ic.ac.rs2er_backend.common.RDBMSType;
import ic.ac.rs2er_backend.exception.ParseException;

import java.io.*;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SchemaspyUtil {
    public static void generatePhysicalERD(
            RDBMSType databaseType,
            String hostname,
            String portNum,
            String databaseName,
            String userName,
            String password) {
        String dbUrl = null;
        try {
            dbUrl = DatabaseUtil.generateDatabaseURL(databaseType, hostname, portNum, databaseName);
            String type = recognDatabaseTypeForJar(databaseType);
            String driverPath = DriverManager.getDriver(dbUrl).getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
            if (type.equals("mysql")){
                execute("java -jar ./my_lib/schemaspy-6.2.2.jar -t " + type +
                        " -dp " + driverPath +
                        " -db " + databaseName +
                        " -s " + databaseName +
                        " -host " + hostname +
                        " -port " + portNum +
                        " -u " + userName +
                        " -p " + password +
                        " -o path/to/output" +
                        " -connprops serverTimezone\\=UTC"
                );
            }else if(type.equals("pgsql11")){
                execute("java -jar ./my_lib/schemaspy-6.2.2.jar -t " + type +
                        " -dp " + driverPath +
                        " -db " + databaseName +
                        " -host " + hostname +
                        " -port " + portNum +
                        " -u " + userName +
                        " -p " + password +
                        " -o path/to/output");
            }
        } catch (ParseException | SQLException e) {
            e.printStackTrace();
        }
    }

    public static String recognDatabaseTypeForJar(RDBMSType databaseType) throws ParseException {
        if (databaseType == RDBMSType.MYSQL) {
            return "mysql";
        } else if (databaseType == RDBMSType.POSTGRESQL) {
            return "pgsql11";
        } else if (databaseType == RDBMSType.SQLSERVER) {
            return "mssql";
        }
        throw new ParseException("Cannot recognise current RDBMS type: " + databaseType.toString());
    }

    public static void execute(String cmd) {
        BufferedReader br = null;
        try {
            File file = new File(System.getProperty("user.dir"));
            File tmpFile = new File(System.getProperty("user.dir") + "\\temp.tmp");
            if (!file.exists()) {
                file.mkdirs();
            }
            if (!tmpFile.exists()) {
                tmpFile.createNewFile();
            }

            ProcessBuilder pb = new ProcessBuilder().command("cmd.exe", "/c", cmd).inheritIO();
            pb.redirectErrorStream(true);
            pb.redirectOutput(tmpFile);
            pb.start().waitFor();

            InputStream in = new FileInputStream(tmpFile);
            br = new BufferedReader(new InputStreamReader(in, "GBK"));
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
            br.close();
            br = null;
            tmpFile.delete();
            System.out.println("Executed Successfully");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
