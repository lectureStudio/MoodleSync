package moodle.sync.javafx;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import moodle.sync.util.UploadElement;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
public class UploadList {

    private List<UploadElement> elements;

    public UploadList(List<UploadElement> elements) {
        this.elements=elements;
    }
}
