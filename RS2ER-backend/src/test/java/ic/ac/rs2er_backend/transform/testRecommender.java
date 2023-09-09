package ic.ac.rs2er_backend.transform;


import ic.ac.rs2er_backend.common.RDBMSType;
import ic.ac.rs2er_backend.common.VisualisationType;
import ic.ac.rs2er_backend.util.DatabaseUtil;
import org.junit.jupiter.api.Test;

import java.util.TreeSet;


public class testRecommender {

    @Test
    public void testRecommend() throws Exception {
        RDBMSType databaseType = RDBMSType.POSTGRESQL;
        String hostname = "localhost";
        String portNum = "5432";
        String databaseName = "mondial_plus";
        String dbUrl = DatabaseUtil.generateDatabaseURL(databaseType, hostname, portNum, databaseName);;
        String userName = "postgres";
        String password = "a123456";

        String driver = DatabaseUtil.recognDriver(databaseType);
        DatabaseUtil.acquireDBConnection(driver, dbUrl, userName, password);
        DotParser.parse();
        DotParser.match();

//        String type = "basic-entity";
//        String relation = "country";
//        String anotherRelation = "country";
//        TreeSet<String> attributes = new TreeSet<>();
//        attributes.add("population");
//        String junctionRelation = "";
        String type = "weak-entity";
        String relation = "country";
        String anotherRelation = "country_development";
        TreeSet<String> attributes = new TreeSet<>();
        attributes.add("gni_per_capita");
        String junctionRelation = "";
        TreeSet<VisualisationType> recommendTypes = Recommender.recommend(type, relation, anotherRelation, attributes, junctionRelation);
        System.out.println(recommendTypes.toString());

        DatabaseUtil.closeDBConnection();
    }
}
