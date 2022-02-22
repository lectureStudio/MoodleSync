package moodle.sync.web.json;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class MoodleFile {
    private String filename;
    private String filepath;
    private int filesize;
    private String fileurl;
    private int timemodified;
    private String mimetype;
    private boolean isexternalfile;
    private String repositorytype;
}
