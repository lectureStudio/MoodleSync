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

public class Module {
    private Integer id;
    private String url;
    private String name;
    private Integer instance;  //instance id
    private Integer contextid;//Activity context id.
    private Boolean uservisible;//Is the module visible for the user?
    private String modname;
}
