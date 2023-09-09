package ic.ac.rs2er_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.TreeSet;

@Data
@AllArgsConstructor
public class GraphInfoRequest {
    String type;
    String relation;
    String anotherRelation;
    TreeSet<String> attributes;
    String junctionRelation;
}
