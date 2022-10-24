package moodle.sync.core.model.json;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

/**
 * Class representing a Moodle-Course.
 *
 * @author Daniel Schröter
 */
public class Course {
    private Integer id;
    private String shortname;
    private String displayname;
    private String idnumber ;
    private Integer visible;
    private Integer enddate;

    @Override
    public String toString() {
        return shortname;
    }
}
