package ic.ac.rs2er_backend.dto;

import ic.ac.rs2er_backend.common.VisualisationType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.TreeSet;

@Data
@AllArgsConstructor
public class ChartInfoRequest {
    String choice;
    String type;
    String relation;
    String anotherRelation;
    TreeSet<String> attributes;
    String junctionRelation;
    VisualisationType vsType;
    String conditions;
    String sortKey;
    String order;
    String limit;
    String identifier;
}
