package ic.ac.rs2er_backend.controller;

import ic.ac.rs2er_backend.common.VisualisationType;
import ic.ac.rs2er_backend.dto.ChartInfoRequest;
import ic.ac.rs2er_backend.dto.GraphInfoRequest;
import ic.ac.rs2er_backend.transform.Chart;
import ic.ac.rs2er_backend.transform.DotParser;
import ic.ac.rs2er_backend.transform.Recommender;
import ic.ac.rs2er_backend.transform.Relation;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.*;

@RestController
@RequestMapping("/graph")
public class GraphHandler {
    @GetMapping("init")
    public java.util.TreeMap<String, Relation> init() {
        return DotParser.relations;
    }

    @PutMapping("recommend")
    public Serializable recommend(@RequestBody GraphInfoRequest request) {
        String type = request.getType();
        String relation = request.getRelation();
        String anotherRelation = request.getAnotherRelation();
        TreeSet<String> attributes = request.getAttributes();
        String junctionRelation = request.getJunctionRelation();
        try {
            TreeMap<String, Object> res = new TreeMap<>();
            TreeSet<VisualisationType> recommendTypes = Recommender.recommend(type, relation, anotherRelation, attributes, junctionRelation);
            HashMap<String, String> uniqueAttrs = Chart.getUniqueAttr(type, anotherRelation, junctionRelation);
            res.put("recommendTypes", recommendTypes);
            res.put("isUniqueAttrs", uniqueAttrs);
            return res;
        } catch (Exception e) {
            return e.toString();
        }
    }

    @PutMapping("chart")
    public Serializable chart(@RequestBody ChartInfoRequest request) {
        String type = request.getType();
        String relation = request.getRelation();
        String anotherRelation = request.getAnotherRelation();
        TreeSet<String> attributes = request.getAttributes();
        String junctionRelation = request.getJunctionRelation();
        VisualisationType vsType = request.getVsType();
        String conditionsString = request.getConditions();
        String sortKey = request.getSortKey();
        String order = request.getOrder();
        String limit = request.getLimit();
        String identifier = request.getIdentifier();

        ArrayList<HashMap<String, String>> conditions = new ArrayList<>();
        if (!conditionsString.equals("[]")) {
            conditionsString = conditionsString.replace("\"", "");
            conditionsString = conditionsString.replace("[", "").replace("]", "").replace("},", "}#");
            String[] conditionsArr = conditionsString.split("#");
            for (int i = 0; i < conditionsArr.length; i++) {
                HashMap<String, String> map = new HashMap<>();
                conditionsArr[i] = conditionsArr[i].replace("{", "").replace("}", "");
                String[] kvs = conditionsArr[i].split(",");
                for (int j = 0; j < kvs.length; j++) {
                    String[] kv = kvs[j].split(":");
                    if (kv.length == 1) {
                        map.put(kv[0], "");
                    } else {
                        map.put(kv[0], kv[1]);
                    }
                }
                conditions.add(map);
            }
        }
        try {
            return Chart.getChartData(type, relation, anotherRelation, attributes, junctionRelation, vsType,
                    conditions, sortKey, order, limit, identifier);
        } catch (Exception e) {
            return e.toString();
        }
    }
}
