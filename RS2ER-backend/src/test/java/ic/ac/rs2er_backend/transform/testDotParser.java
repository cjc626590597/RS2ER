package ic.ac.rs2er_backend.transform;

import org.junit.jupiter.api.Test;

import java.util.Map;

public class testDotParser {

    @Test
    public void testParseAndMatching(){
        DotParser.parse();
        System.out.println("Relations:---------------------------------------------------------------------------------------------------------------------");
        for (Map.Entry<String, Relation> entry : DotParser.relations.entrySet()) {
            System.out.println(entry.getValue());
        }
        System.out.println("RelationShips:---------------------------------------------------------------------------------------------------------------------");
        for (RelationShip relationShip : DotParser.relationShips) {
            System.out.println(relationShip);
        }
        System.out.println("Pattern Matching:---------------------------------------------------------------------------------------------------------------------");
        DotParser.match();
        DotParser.relations.get("country").printAllRelationships();

        DotParser.relations.get("city").printAllRelationships();
    }
}
