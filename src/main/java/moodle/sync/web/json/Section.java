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

public class Section {
    private Integer id;
    private String name;
    private Integer visible;
    private String summary;
    private Integer summaryformat;
    private Integer section; //Sectionnummer innerhalb des Kurses
    private Integer hiddenbynumsections;
    private Boolean uservisible;
    private List<Module> modules;
}
