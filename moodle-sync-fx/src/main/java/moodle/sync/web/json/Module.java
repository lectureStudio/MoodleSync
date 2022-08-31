package moodle.sync.web.json;

import lombok.*;

import javax.annotation.Nullable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString

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
    private Integer visible;
    private String modname;
    private String availability;
    private List<Content> contents;


    public Module(List<Content> contents){
        this.contents = contents;
    }
}
