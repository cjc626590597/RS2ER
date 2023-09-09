package ic.ac.rs2er_backend.transform;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DotParser {
    public static TreeMap<String, Relation> relations;
    public static ArrayList<RelationShip> relationShips;

    //Parse the dot file to get the relations and relationships
    public static void parse() {
        //File path
        String path = "./path/to/output/diagrams/summary/relationships.real.large.dot";

        relations = new TreeMap<String, Relation>();
        relationShips = new ArrayList<>();
        try {
            String encoding = "UTF-8";
            File file = new File(path);
            if (file.isFile() && file.exists()) {
                InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding);
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt = null;
                //Read one line
                while ((lineTxt = bufferedReader.readLine()) != null) {
                    if (lineTxt.contains("[table]") || lineTxt.contains("[view]")) {
                        Relation relation = null;
                        //Regex expression
                        String relationPattern = "<B>(.*?)</B></TD><TD ALIGN=\"RIGHT\">\\[(.*?)\\]</TD>";
                        Pattern p = Pattern.compile(relationPattern);
                        Matcher m = p.matcher(lineTxt);
                        if (m.find()) {
                            String relationName = m.group(1);
                            String relationType = m.group(2);
                            relation = new Relation(relationName, relationType);
                            while ((lineTxt = bufferedReader.readLine()) != null) {
                                if (lineTxt.contains("PORT")) {
                                    String attributesPattern = "<.+?>(\\w+)</TD>";
                                    p = Pattern.compile(attributesPattern);
                                    m = p.matcher(lineTxt);
                                    if (m.find()) {
                                        String attribute = m.group(1);
                                        if (lineTxt.contains("primaryKeys")) {
                                            relation.addPrimaryKey(attribute);
//                                        } else if (lineTxt.contains("foreignKeys")) {
//                                            relation.addForeignKeys(attribute);
                                        } else {
                                            relation.addNonKey(attribute);
                                        }
                                    }
                                } else if (lineTxt.contains("rows")) {
                                    String rowsPattern = ".*>(.*?)rows</TD>";
                                    p = Pattern.compile(rowsPattern);
                                    m = p.matcher(lineTxt);
                                    if (m.find()) {
                                        String rows = m.group(1);
                                        relation.setRows(Integer.parseInt(rows.replaceAll(" ", "").replaceAll(",", "")));
                                    }
                                } else {
                                    relations.put(relation.getName(), relation);
                                    break;
                                }
                            }
                        }
                    } else {
                        String pattern = "\"(.+?)\":\"(.+?)\":.*?\"(.+?)\":\"(.+?)\".*?arrowtail=(\\w+).*]";
                        Pattern p = Pattern.compile(pattern);
                        Matcher m = p.matcher(lineTxt);
                        if (m.find()) {
                            RelationShip relationShip = null;
                            if (m.group(5).equals("teeodot")) {
                                relationShip = new RelationShip(m.group(1), m.group(2), m.group(3), m.group(4), "one-to-one");
                            } else if (m.group(5).equals("crowodot")) {
                                relationShip = new RelationShip(m.group(1), m.group(2), m.group(3), m.group(4), "one-to-many");
                            }
                            //Add foreign key
//                            relations.get(m.group(1)).addForeignKeys(m.group(2));
                            relations.get(m.group(1)).addForeignKeyWithLink(m.group(2), relationShip);
                            relations.get(m.group(1)).addCompoundForeignKeys(m.group(3), relationShip);
                            //Set the primary key is used
//                            this.relations.get(m.group(3)).getPrimaryKeys().put(m.group(4), true);
                            relationShips.add(relationShip);
                        }
                    }
                }
                read.close();
            } else {
                System.out.println("Couldn't find the file!");
            }
        } catch (Exception e) {
            System.out.println("Read the file unsuccessfully!");
            e.printStackTrace();
        }
    }

    //Schema pattern matching
    public static void match() {
        for (RelationShip relationShip : relationShips) {
            //If the relationship is one-to-many type:
            if (relationShip.getType().equals("one-to-many")) {
                Relation manyRelation = relations.get(relationShip.getRelation1());
                String manyAttribute = relationShip.getAttribute1();
                Relation oneRelation = relations.get(relationShip.getRelation2());
                String oneAttribute = relationShip.getAttribute2();
                //If the attribute on the on the "many" side is also a PK (Primary Key) of that table:
                if (manyRelation.getPrimaryKeys().contains(manyAttribute)) {
                    //If the "many" side has another PK which is also a FK (Foreign Key) in a one-to-many relationship with another entity
                    boolean isManyMany = false;
                    boolean isWeakEntity = false;
                    if (manyRelation.getPrimaryForeignKeys().size() >= 2) {
                        //Many-to-many relationship
                        for (Map.Entry<String, RelationShip> entry : manyRelation.getPrimaryForeignKeys().entrySet()) {
                            if (!entry.getKey().equals(manyAttribute)) {
                                RelationShip anotherRelationship = entry.getValue();
                                if (anotherRelationship != null && anotherRelationship.getType().equals("one-to-many")) {
                                    // Remove composite key relationship
                                    if (oneAttribute.equals(anotherRelationship.getAttribute2()) || !oneRelation.getName().equals(anotherRelationship.getRelation2())) {
                                        isManyMany = true;
                                        manyRelation.setJunctionRelation(true);
                                        RelationShip manyToManyRelationship = new RelationShip(anotherRelationship.getRelation2(), anotherRelationship.getAttribute2(),
                                                relationShip.getRelation2(), relationShip.getAttribute2(), "many-to-many");
                                        manyToManyRelationship.setJunctionRelation(manyRelation.getName());
                                        oneRelation.addManyManyRelationShip(manyToManyRelationship);
                                        if (anotherRelationship.getRelation2().equals(relationShip.getRelation2())) {
                                            oneRelation.addReflexiveRelationShip(manyToManyRelationship);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (!isManyMany) {
                        //else, if the "many" side has only a single FK (this relation’s PK) and has more than one PK:
                        if (manyRelation.getPrimaryKeys().size() > manyRelation.getForeignKeyWithLinks().size()) {
                            for (String entry : manyRelation.getPrimaryKeys()) {
                                if (!entry.equals(manyAttribute) && !manyRelation.getForeignKeyWithLinks().containsKey(entry)) {
                                    //Weak-entity relationship
                                    isWeakEntity = true;
                                    oneRelation.addWeakEntityRelationShips(relationShip);
                                    break;
                                }
                            }
                        }
                    }
                    if (!isManyMany && !isWeakEntity) {
                        //else
                        //One-to-many relationship
                        oneRelation.addOneManyRelationShip(relationShip);
                    }
                }
                //else, if the "many" side is just a FK of that relation:
                else if (manyRelation.getForeignKeyWithLinks().containsKey(manyAttribute)) {
                    //if the "one" side attribute on the relation is a PK:
                    if (oneRelation.getPrimaryKeys().contains(oneAttribute)) {
                        boolean isReflexive = false;
                        boolean isManyMany = false;
                        //if the relation name of the "one" and "many" is the same:
                        if (relationShip.getRelation1().equals(relationShip.getRelation2())) {
                            //Reflexive/recursive relationship
                            isReflexive = true;
                            oneRelation.addReflexiveRelationShip(relationShip);
                        }
                        //else, if the "many" side has another FK in a one-to-many relationship with another entity
                        else if (manyRelation.getForeignKeyWithLinks().size() >= 2) {
                            //Many-to-many relationship
                            for (Map.Entry<String, RelationShip> entry : manyRelation.getForeignKeyWithLinks().entrySet()) {
                                if (!entry.getKey().equals(manyAttribute)) {
                                    RelationShip anotherRelationship = entry.getValue();
                                    if (anotherRelationship != null && anotherRelationship.getType().equals("one-to-many")) {
                                        // Remove composite key relationship
                                        if (!oneRelation.getName().equals(anotherRelationship.getRelation2())) {
                                            isManyMany = true;
                                            oneRelation.addOneManyRelationShip(relationShip);
                                            RelationShip manyToManyRelationship = new RelationShip(anotherRelationship.getRelation2(), anotherRelationship.getAttribute2(),
                                                    relationShip.getRelation2(), relationShip.getAttribute2(), "many-to-many");
                                            manyToManyRelationship.setJunctionRelation(manyRelation.getName());
                                            oneRelation.addManyManyRelationShip(manyToManyRelationship);
                                        }
                                    }
                                }
                            }
                        }
                        if (!isManyMany && !isReflexive) {
                            //else, if the "many" side has a single FK and has one PK:
                            if (manyRelation.getPrimaryKeys().size() > 0) {
                                //One-many relationship
                                oneRelation.addOneManyRelationShip(relationShip);
                            }
                        }
                    }
                    //else:  Not Achieved !!!
                    else {
                        //Chained Weak-entity relationship
                        oneRelation.addWeakEntityRelationShips(relationShip);
                    }
                }
            }
            //If the relationship is Basic Entity type:
            else {
                relations.get(relationShip.getRelation2()).addBasicEntityRelationship(relationShip);
            }
        }
    }
}
