package moodle.sync.web.json;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Course {
    private Integer id;
    private String shortname;
    private String fullname;
    private String displayname;
    private Integer enrolledusercount;
    private String idnumber ;
    private Integer visible;
    private String summary;
    private Integer summaryformat;
    private String format;
    private Boolean showgrades;
    private String lang;
    private Boolean enablecompletion;
    private Boolean completionhascriteria;
    private Boolean completionusertracked;
    private Integer category;
    private Double progress;
    private Boolean completed;
    private Integer startdate;
    private Integer enddate;
    private Integer marker;
    private Integer lastaccess;
    private Boolean isfavourite;
    private Boolean hidden;
    //private List<File> overviewfiles; //Da muss als Type der List noch Files rein
    //private Boolean showactivitydates;
    //private Boolean showcompletionconditions;


    @Override
    public String toString() {
        return displayname;
    }
}
