package ic.ac.rs2er_backend.transform;

import ic.ac.rs2er_backend.common.DataType;
import ic.ac.rs2er_backend.common.VisualisationType;
import ic.ac.rs2er_backend.exception.DBConnectionException;
import ic.ac.rs2er_backend.exception.ParseException;
import ic.ac.rs2er_backend.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

public class Recommender {
    public static TreeSet<VisualisationType> recommend(String type, String relation, String anotherRelation, TreeSet<String> attributes, String junctionRelation) throws DBConnectionException, SQLException, ParseException {
        TreeSet<VisualisationType> recommendTypes = new TreeSet<>();
        Connection conn = DatabaseUtil.conn;
        if (conn == null) {
            throw new DBConnectionException("The database is not yet connected!");
        }
        Statement stmt = null;
        stmt = conn.createStatement();
        StringBuilder attrs = new StringBuilder();
        switch (type) {
            case "basic-entity": {
                TreeSet<String> primaryKeys = DotParser.relations.get(anotherRelation).getPrimaryKeys();
                for (String primaryKey : primaryKeys) {
                    attrs.append(primaryKey);
                    attrs.append(", ");
                }
                for (String attribute : attributes) {
                    attrs.append(attribute);
                    attrs.append(", ");
                }
                attrs.delete(attrs.length() - 2, attrs.length());
                String sql = "select " + attrs + " from " + anotherRelation + " limit 1;";
                ResultSet rs = stmt.executeQuery(sql);
//            while(rs.next()){
//                System.out.println(rs.getInt("population"));
//            }
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();

                TreeMap<String, DataType> primaryKeysTypes = new TreeMap<>();
                TreeMap<String, DataType> attributesTypes = new TreeMap<>();
                for (int i = 1; i <= primaryKeys.size(); i++) {
                    String columnName = metaData.getColumnName(i);
                    DataType columnType = recognizeColumnType(metaData.getColumnType(i));
                    primaryKeysTypes.put(columnName, columnType);
                }
                for (int i = 1 + primaryKeys.size(); i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i);
                    DataType columnType = recognizeColumnType(metaData.getColumnType(i));
                    attributesTypes.put(columnName, columnType);
                }
                if (attributesTypes.size() == 1) {
                    for (Map.Entry<String, DataType> entry : attributesTypes.entrySet()) {
                        if (entry.getValue().equals(DataType.Numeric)) {
                            recommendTypes.add(VisualisationType.BarChart);
                            recommendTypes.add(VisualisationType.DonutChart);
                            if (primaryKeysTypes.get(primaryKeys.iterator().next()).equals(DataType.Lexical)) {
                                recommendTypes.add(VisualisationType.WordCloud);
                            }
                            if (relation.equals("country")) {
                                recommendTypes.add(VisualisationType.ChoroplethMap);
                            }
                        }
                        if (entry.getValue().equals(DataType.Temporal)) {
                            recommendTypes.add(VisualisationType.Calendar);
                        }
                    }
                } else if (attributesTypes.size() == 2) {
                    Boolean isAllScalar = true;
                    for (Map.Entry<String, DataType> entry : attributesTypes.entrySet()) {
                        if (!entry.getValue().equals(DataType.Numeric)) {
                            isAllScalar = false;
                        }
                    }
                    if (isAllScalar) {
                        recommendTypes.add(VisualisationType.ScatterDiagram);
                        recommendTypes.add(VisualisationType.NestedDonutChart);
                    }
                } else if (attributesTypes.size() == 3) {
                    Boolean isAllScalar = true;
                    for (Map.Entry<String, DataType> entry : attributesTypes.entrySet()) {
                        if (!entry.getValue().equals(DataType.Numeric)) {
                            isAllScalar = false;
                        }
                    }
                    if (isAllScalar) {
                        recommendTypes.add(VisualisationType.BubbleChart);
                    }
                }
                break;
            }
            case "weak-entity": {
                ArrayList<RelationShip> relationShips = DotParser.relations.get(anotherRelation).getCompoundForeignKeys().get(relation);
                TreeSet<String> parentPKs = new TreeSet<>();
                for (RelationShip relationShip : relationShips) {
                    parentPKs.add(relationShip.getAttribute1());
                    attrs.append(relationShip.getAttribute1());
                    attrs.append(", ");
                }
                TreeSet<String> childPKs = new TreeSet<>();
                TreeSet<String> primaryKeys = DotParser.relations.get(anotherRelation).getPrimaryKeys();
                for (String primaryKey : primaryKeys) {
                    if (!parentPKs.contains(primaryKey)) {
                        childPKs.add(primaryKey);
                        attrs.append(primaryKey);
                        attrs.append(", ");
                    }
                }
                for (String attribute : attributes) {
                    attrs.append(attribute);
                    attrs.append(", ");
                }
                attrs.delete(attrs.length() - 2, attrs.length());
                String sql = "select " + attrs + " from " + anotherRelation + " limit 1;";
                ResultSet rs = stmt.executeQuery(sql);
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();
                TreeMap<String, DataType> parentPKsTypes = new TreeMap<>();
                TreeMap<String, DataType> childPKsTypes = new TreeMap<>();
                TreeMap<String, DataType> attributesTypes = new TreeMap<>();
                for (int i = 1; i <= parentPKs.size(); i++) {
                    String columnName = metaData.getColumnName(i);
                    DataType columnType = recognizeColumnType(metaData.getColumnType(i));
                    parentPKsTypes.put(columnName, columnType);
                }
                for (int i = 1 + parentPKs.size(); i <= parentPKs.size() + childPKs.size(); i++) {
                    String columnName = metaData.getColumnName(i);
                    DataType columnType = recognizeColumnType(metaData.getColumnType(i));
                    childPKsTypes.put(columnName, columnType);
                }
                for (int i = 1 + primaryKeys.size(); i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i);
                    DataType columnType = recognizeColumnType(metaData.getColumnType(i));
                    attributesTypes.put(columnName, columnType);
                }
                if (attributesTypes.size() == 1) {
                    for (Map.Entry<String, DataType> entry : attributesTypes.entrySet()) {
                        if (entry.getValue().equals(DataType.Numeric)) {
                            recommendTypes.add(VisualisationType.StackedBarChart);
                            recommendTypes.add(VisualisationType.GroupedBarChart);
                            recommendTypes.add(VisualisationType.SpiderChart);
                            if (childPKs.size() == 1 && childPKsTypes.get(childPKs.iterator().next()).equals(DataType.Numeric)) {
                                recommendTypes.add(VisualisationType.LineChart);
                            }
                        }
                    }
                }
                break;
            }
            case "one-many": {
                ArrayList<RelationShip> relationShips = DotParser.relations.get(anotherRelation).getCompoundForeignKeys().get(relation);
                TreeSet<String> parentPKs = new TreeSet<>();
                for (RelationShip relationShip : relationShips) {
                    parentPKs.add(relationShip.getAttribute1());
                    attrs.append(relationShip.getAttribute1());
                    attrs.append(", ");
                }
                TreeSet<String> childPKs = new TreeSet<>();
                TreeSet<String> primaryKeys = DotParser.relations.get(anotherRelation).getPrimaryKeys();
                for (String primaryKey : primaryKeys) {
                    childPKs.add(primaryKey);
                    attrs.append(primaryKey);
                    attrs.append(", ");
                }
                for (String attribute : attributes) {
                    attrs.append(attribute);
                    attrs.append(", ");
                }
                attrs.delete(attrs.length() - 2, attrs.length());
                String sql = "select " + attrs + " from " + anotherRelation + " limit 1;";
                ResultSet rs = stmt.executeQuery(sql);
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();
                TreeMap<String, DataType> parentPKsTypes = new TreeMap<>();
                TreeMap<String, DataType> childPKsTypes = new TreeMap<>();
                TreeMap<String, DataType> attributesTypes = new TreeMap<>();
                for (int i = 1; i <= parentPKs.size(); i++) {
                    String columnName = metaData.getColumnName(i);
                    DataType columnType = recognizeColumnType(metaData.getColumnType(i));
                    parentPKsTypes.put(columnName, columnType);
                }
                for (int i = 1 + parentPKs.size(); i <= parentPKs.size() + childPKs.size(); i++) {
                    String columnName = metaData.getColumnName(i);
                    DataType columnType = recognizeColumnType(metaData.getColumnType(i));
                    childPKsTypes.put(columnName, columnType);
                }
                for (int i = 1 + parentPKs.size() + childPKs.size(); i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i);
                    DataType columnType = recognizeColumnType(metaData.getColumnType(i));
                    attributesTypes.put(columnName, columnType);
                }
                if (attributesTypes.size() == 1) {
                    for (Map.Entry<String, DataType> entry : attributesTypes.entrySet()) {
                        if (entry.getValue().equals(DataType.Numeric)) {
                            recommendTypes.add(VisualisationType.TreeMap);
                            recommendTypes.add(VisualisationType.CirclePacking);
                            recommendTypes.add(VisualisationType.HierarchyTree);
                        }
                    }
                }
                break;
            }
            case "many-many": {
                TreeSet<String> firstPKs = new TreeSet<>();
                TreeSet<String> secondPKs = new TreeSet<>();
                if (relation.equals(anotherRelation)) {
                    ArrayList<RelationShip> relationShips = DotParser.relations.get(junctionRelation).getCompoundForeignKeys().get(relation);
                    RelationShip firstRelationShip = relationShips.get(0);
                    firstPKs.add(firstRelationShip.getAttribute1());
                    attrs.append(firstRelationShip.getAttribute1());
                    attrs.append(", ");
                    RelationShip secondRelationShip = relationShips.get(1);
                    secondPKs.add(secondRelationShip.getAttribute1());
                    attrs.append(secondRelationShip.getAttribute1());
                    attrs.append(", ");
                } else {
                    ArrayList<RelationShip> firstRelationShips = DotParser.relations.get(junctionRelation).getCompoundForeignKeys().get(relation);
                    for (RelationShip relationShip : firstRelationShips) {
                        firstPKs.add(relationShip.getAttribute1());
                        attrs.append(relationShip.getAttribute1());
                        attrs.append(", ");
                    }
                    ArrayList<RelationShip> secondRelationShips = DotParser.relations.get(junctionRelation).getCompoundForeignKeys().get(anotherRelation);
                    for (RelationShip relationShip : secondRelationShips) {
                        secondPKs.add(relationShip.getAttribute1());
                        attrs.append(relationShip.getAttribute1());
                        attrs.append(", ");
                    }
                }
                for (String attribute : attributes) {
                    attrs.append(attribute);
                    attrs.append(", ");
                }
                attrs.delete(attrs.length() - 2, attrs.length());
                String sql = "select " + attrs + " from " + junctionRelation + " limit 1;";
                ResultSet rs = stmt.executeQuery(sql);
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();
                TreeMap<String, DataType> parentPKsTypes = new TreeMap<>();
                TreeMap<String, DataType> childPKsTypes = new TreeMap<>();
                TreeMap<String, DataType> attributesTypes = new TreeMap<>();
                for (int i = 1; i <= firstPKs.size(); i++) {
                    String columnName = metaData.getColumnName(i);
                    DataType columnType = recognizeColumnType(metaData.getColumnType(i));
                    parentPKsTypes.put(columnName, columnType);
                }
                for (int i = 1 + firstPKs.size(); i <= firstPKs.size() + secondPKs.size(); i++) {
                    String columnName = metaData.getColumnName(i);
                    DataType columnType = recognizeColumnType(metaData.getColumnType(i));
                    childPKsTypes.put(columnName, columnType);
                }
                for (int i = 1 + firstPKs.size() + secondPKs.size(); i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i);
                    DataType columnType = recognizeColumnType(metaData.getColumnType(i));
                    attributesTypes.put(columnName, columnType);
                }
                if (attributesTypes.size() == 1) {
                    for (Map.Entry<String, DataType> entry : attributesTypes.entrySet()) {
                        if (entry.getValue().equals(DataType.Numeric)) {
                            recommendTypes.add(VisualisationType.Sankey);
                            recommendTypes.add(VisualisationType.Chord);
                        }
                    }
                }
                break;
            }
        }
        return recommendTypes;
    }

    public static DataType recognizeColumnType(int columnType) throws ParseException {
        if (columnType == Types.INTEGER || columnType == Types.SMALLINT || columnType == Types.DECIMAL || columnType == Types.NUMERIC) {
            return DataType.Numeric;
        } else if (columnType == Types.CHAR || columnType == Types.VARCHAR) {
            return DataType.Lexical;
        } else if (columnType == Types.DATE) {
            return DataType.Temporal;
        }
        throw new ParseException("Cannot recognise current Column type: " + columnType);
    }
}
