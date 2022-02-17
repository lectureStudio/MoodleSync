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

public class Draft {
    private String component;
    private long contextid;
    private long userid;
    private String filearea;
    private long itemid;
    private List<String> warnings;

}
