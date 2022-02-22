package moodle.sync.web.json;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class MoodleUpload {
    private String component;
    private Integer contextid;
    private Integer userid;
    private Integer itemid;
}
