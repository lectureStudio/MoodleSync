package moodle.sync.web.json;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

/**
 * Class representing a course-module.
 *
 * @author Daniel Schröter
 */
public class Module {
    private Integer id;
    private String url;
    private String name;
    private Integer instance;
    private Integer contextid;
    private Boolean uservisible;
    private String modname;
    private List<Content> contents;


    public Module(List<Content> contents){
        this.contents = contents;
    }
}
