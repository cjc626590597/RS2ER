package ic.ac.rs2er_backend.controller;

import ic.ac.rs2er_backend.transform.DotParser;
import ic.ac.rs2er_backend.transform.Relation;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/erd")
public class ERDHandler {
    @GetMapping("table")
    public java.util.Collection<Relation> table() {

        long startTime = System.currentTimeMillis();
        DotParser.parse();
        DotParser.match();
        long endTime = System.currentTimeMillis();
        System.out.println("DotParser Time：" + (endTime - startTime) + "ms");

        return DotParser.relations.values();
    }
}
