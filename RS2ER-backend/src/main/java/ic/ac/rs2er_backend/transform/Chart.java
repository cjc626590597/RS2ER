package ic.ac.rs2er_backend.transform;

import ic.ac.rs2er_backend.common.VisualisationType;
import ic.ac.rs2er_backend.exception.DBConnectionException;
import ic.ac.rs2er_backend.exception.SQLParseException;
import ic.ac.rs2er_backend.util.DatabaseUtil;

import java.sql.*;
import java.util.*;

public class Chart {
    public static HashMap<String, String> getUniqueAttr(String type,
                                                        String anotherRelation,
                                                        String junctionRelation) throws DBConnectionException, SQLException {
        Connection conn = DatabaseUtil.conn;
        if (conn == null) {
            throw new DBConnectionException("The database is not yet connected!");
        }
        DatabaseMetaData dbMetaData = conn.getMetaData();
        String relation = null;
        if (type.equals("many-many")) {
            relation = junctionRelation;
        } else {
            relation = anotherRelation;
        }
        HashMap<String, String> uniqueAttrs = new HashMap<>();
        TreeSet<String> primaryKeys = DotParser.relations.get(relation).getPrimaryKeys();
        TreeSet<String> nonKeys = DotParser.relations.get(relation).getNonKeys();
        for (String pk : primaryKeys) {
            uniqueAttrs.put(pk, "primary key");
        }
        for (String key : nonKeys) {
            uniqueAttrs.put(key, "");
        }

        ResultSet indexInfo = dbMetaData.getIndexInfo(null, null, relation, false, false);
        while (indexInfo.next()) {
            String columnName = indexInfo.getString("COLUMN_NAME");
            boolean nonUnique = indexInfo.getBoolean("NON_UNIQUE");
            if (!nonUnique && !primaryKeys.contains(columnName)) {
                uniqueAttrs.put(columnName, "unique");
            }
        }
        return uniqueAttrs;
    }

    public static TreeMap getChartData(String type,
                                       String relation,
                                       String anotherRelation,
                                       TreeSet<String> attributes,
                                       String junctionRelation,
                                       VisualisationType vsType,
                                       ArrayList<HashMap<String, String>> conditions,
                                       String sortKey,
                                       String order,
                                       String limit,
                                       String identifier)
            throws DBConnectionException, SQLParseException, SQLException {
        Connection conn = DatabaseUtil.conn;
        if (conn == null) {
            throw new DBConnectionException("The database is not yet connected!");
        }
        Statement stmt = null;
        stmt = conn.createStatement();
        StringBuilder attrs = new StringBuilder();

        TreeMap<String, Object> res = new TreeMap<>();
        String sql = null;
        try {
            switch (type) {
                case "basic-entity": {
                    if (!vsType.equals(VisualisationType.ChoroplethMap)) {
                        TreeSet<String> primaryKeys = DotParser.relations.get(anotherRelation).getPrimaryKeys();
                        if (identifier != null && identifier.length() != 0 && !identifier.equals("default")) {
                            attrs.append(identifier);
                            attrs.append(", ");
                        } else {
                            for (String primaryKey : primaryKeys) {
                                attrs.append(primaryKey);
                                attrs.append(", ");
                            }
                        }
                        for (String attribute : attributes) {
                            attrs.append(attribute);
                            attrs.append(", ");
                        }
                        attrs.delete(attrs.length() - 2, attrs.length());
                        sql = "select " + attrs + " from " + anotherRelation
                                + addConditions(type, relation, anotherRelation, junctionRelation,
                                primaryKeys, conditions, sortKey, order, limit);
                        ResultSet rs = stmt.executeQuery(sql);
                        ArrayList<TreeMap<String, Object>> list = convertList(rs);
                        String pksName = null;
                        if (identifier != null && identifier.length() != 0 && !identifier.equals("default")) {
                            pksName = identifier;
                        } else {
                            pksName = concatKeysName(primaryKeys);
                            concatKeysData(list, primaryKeys);
                        }
                        res.put("pksName", pksName);
                        res.put("jsonData", list);
                    } else {
                        TreeSet<String> keys = new TreeSet<>();
                        keys.add("name");
                        attrs.append("name");
                        attrs.append(", ");
                        for (String attribute : attributes) {
                            attrs.append(attribute);
                            attrs.append(", ");
                        }
                        attrs.delete(attrs.length() - 2, attrs.length());
                        sql = "select " + attrs + " from " + anotherRelation
                                + addConditions(type, relation, anotherRelation, junctionRelation,
                                keys, conditions, sortKey, order, limit);
                        ;
                        ResultSet rs = stmt.executeQuery(sql);
                        ArrayList<TreeMap<String, Object>> list = convertList(rs);
                        res.put("pksName", "name");
                        res.put("jsonData", list);
                    }
                    break;
                }
                case "weak-entity":
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
                    sql = "select " + attrs + " from " + anotherRelation
                            + addConditions(type, relation, anotherRelation, junctionRelation,
                            parentPKs, conditions, sortKey, order, limit);
                    ResultSet rs = stmt.executeQuery(sql);
                    ArrayList<TreeMap<String, Object>> list = convertList(rs);
                    String parentPksName = concatKeysName(parentPKs);
                    String childPksName = concatKeysName(childPKs);
                    concatKeysData(list, parentPKs);
                    concatKeysData(list, childPKs);
                    res.put("parentPksName", parentPksName);
                    res.put("childPksName", childPksName);
                    res.put("jsonData", list);
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
                    sql = "select " + attrs + " from " + junctionRelation
                            + addConditions(type, relation, anotherRelation, junctionRelation,
                            firstPKs, conditions, sortKey, order, limit);
                    ResultSet rs = stmt.executeQuery(sql);
                    ArrayList<TreeMap<String, Object>> list = convertList(rs);
                    String firstPKsName = concatKeysName(firstPKs);
                    String secondPKsName = concatKeysName(secondPKs);
                    concatKeysData(list, firstPKs);
                    concatKeysData(list, secondPKs);
                    res.put("firstPKsName", firstPKsName);
                    res.put("secondPKsName", secondPKsName);
                    res.put("jsonData", list);
                    break;
                }
            }
        } catch (SQLException e) {
            throw new SQLParseException("Executed SQL: \"" + sql + "\"" + e.toString());
        }
        System.out.println(sql);
        return res;
    }

    public static String addConditions(String type,
                                       String relation,
                                       String anotherRelation,
                                       String junctionRelation,
                                       TreeSet<String> primaryKeys,
                                       ArrayList<HashMap<String, String>> conditions,
                                       String sortKey,
                                       String order,
                                       String limit) {
        StringBuilder sql = new StringBuilder();

        if (conditions.size() != 0) {
            HashMap<String, ArrayList<String>> keysMap = new HashMap<>();
            if (type.equals("many-many")) {
                ArrayList<RelationShip> relationShips = DotParser.relations.get(junctionRelation).getCompoundForeignKeys().get(relation);
                for (RelationShip relationShip : relationShips) {
                    if (!keysMap.containsKey(relationShip.getAttribute2())) {
                        ArrayList<String> list = new ArrayList<>();
                        list.add(relationShip.getAttribute1());
                        keysMap.put(relationShip.getAttribute2(), list);
                    } else {
                        keysMap.get(relationShip.getAttribute2()).add(relationShip.getAttribute1());
                    }
                }
            } else {
                ArrayList<RelationShip> relationShips = DotParser.relations.get(anotherRelation).getCompoundForeignKeys().get(relation);
                if (relationShips != null) {
                    for (RelationShip relationShip : relationShips) {
                        if (!keysMap.containsKey(relationShip.getAttribute2())) {
                            ArrayList<String> list = new ArrayList<>();
                            list.add(relationShip.getAttribute1());
                            keysMap.put(relationShip.getAttribute2(), list);
                        } else {
                            keysMap.get(relationShip.getAttribute2()).add(relationShip.getAttribute1());
                        }
                    }
                } else {
                    for (String primaryKey : primaryKeys) {
                        ArrayList<String> list = new ArrayList<>();
                        list.add(primaryKey);
                        keysMap.put(primaryKey, list);
                    }
                }
            }

            sql.append(" where 1=1");
            for (HashMap<String, String> condition : conditions) {
                if (condition.get("choice").equals("attributes")) {
                    sql.append(" and ");
                    sql.append(addCondition(condition));
                } else {
                    //condition.get("choice").equals("schema pattern")
                    String cAnotherRelation = condition.get("anotherRelation");
                    String cType = condition.get("type");
                    String cJunctionRelation = condition.get("junctionRelation");
                    switch (cType) {
                        case "basic-entity":
                        case "weak-entity":
                        case "one-many": {
                            ArrayList<RelationShip> cRelationShips = DotParser.relations.get(cAnotherRelation).getCompoundForeignKeys().get(relation);
                            for (RelationShip cRelationShip : cRelationShips) {
                                String cAttr = cRelationShip.getAttribute1();
                                ArrayList<String> filteredAttrs = keysMap.get(cRelationShip.getAttribute2());
                                for (String filteredAttr : filteredAttrs) {
                                    sql.append(" and ");
                                    sql.append(" " + filteredAttr + " in (select " + cAttr + " from " + cAnotherRelation + " where "
                                            + addCondition(condition) + ") ");
                                }
                            }
                            break;
                        }
                        case "many-many": {
                            ArrayList<RelationShip> cRelationShips = DotParser.relations.get(cJunctionRelation).getCompoundForeignKeys().get(relation);
                            for (RelationShip cRelationShip : cRelationShips) {
                                String cAttr = cRelationShip.getAttribute1();
                                ArrayList<String> filteredAttrs = keysMap.get(cRelationShip.getAttribute2());
                                for (String filteredAttr : filteredAttrs) {
                                    sql.append(" and ");
                                    sql.append(" " + filteredAttr + " in (select " + cAttr + " from " + cJunctionRelation + " where "
                                            + addCondition(condition) + ") ");
                                }
                            }
                            break;
                        }
                    }
                }
            }
        }
        if (sortKey != null && sortKey.length() != 0 && !sortKey.equals("default")) {
            sql.append(" order by ").append(sortKey);
        }
        if (order != null && order.length() != 0 && !order.equals("default")) {
            if (order.equals("descending")) {
                sql.append(" desc ");
            } else {
                sql.append(" asc ");
            }
        }
        if (limit != null && limit.length() != 0) {
            sql.append(" limit ").append(limit);
        }
        return sql.toString();
    }

    public static String addCondition(HashMap<String, String> condition) {
        String attribute = condition.get("attribute");
        String cd = condition.get("condition");
        String value = condition.get("value");
        StringBuilder sql = new StringBuilder();
        sql.append(attribute).append(" ");
        switch (cd) {
            case ">=":
            case ">":
            case "<=":
            case "<":
                sql.append(cd).append(" ");
                sql.append(value);
                break;
            case "=":
            case "!=":
                if (value.equals("null")) {
                    sql.append(" is not null ");
                } else {
                    sql.append(cd).append(" '");
                    sql.append(value).append("'");
                }
                break;
            case "contains":
                sql.append("like '%").append(value).append("%' ");
                break;
            case "does not contain":
                sql.append("not like '%").append(value).append("%' ");
                break;
            case "starts with":
                sql.append("like '").append(value).append("%' ");
                break;
            case "ends with":
                sql.append("like '%").append(value).append("' ");
                break;
        }
        return sql.toString();
    }

    public static ArrayList<TreeMap<String, Object>> convertList(ResultSet rs) throws SQLException {
        ArrayList<TreeMap<String, Object>> list = new ArrayList<>();
        ResultSetMetaData md = rs.getMetaData();
        int columnCount = md.getColumnCount();
        while (rs.next()) {
            TreeMap<String, Object> rowData = new TreeMap<>();
            for (int i = 1; i <= columnCount; i++) {
                rowData.put(md.getColumnName(i), rs.getObject(i));
            }
            list.add(rowData);
        }
        return list;
    }

    public static String concatKeysName(TreeSet<String> primaryKeys) {
        StringBuilder pksName = new StringBuilder();
        for (String pk : primaryKeys) {
            pksName.append(pk);
            pksName.append('-');
        }
        pksName.delete(pksName.length() - 1, pksName.length());
        return pksName.toString();
    }

    public static void concatKeysData(ArrayList<TreeMap<String, Object>> list, TreeSet<String> pks) {
        StringBuilder pksName = new StringBuilder();
        for (String pk : pks) {
            pksName.append(pk);
            pksName.append('-');
        }
        pksName.delete(pksName.length() - 1, pksName.length());
        for (Map<String, Object> map : list) {
            StringBuilder newPk = new StringBuilder();
            for (String pk : pks) {
                newPk.append(map.get(pk));
                newPk.append('-');
                map.remove(pk);
            }
            newPk.delete(newPk.length() - 1, newPk.length());
            map.put(pksName.toString(), newPk.toString());
        }
    }
}
