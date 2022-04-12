package moodle.sync.util.UploadData;

import lombok.Getter;
import lombok.Setter;
import moodle.sync.util.UploadData.UploadData;

import java.nio.file.Path;
import java.util.List;

@Getter
@Setter
public class UploadFolderElement extends UploadData {

    private List<UploadData> content;

    private Path path;

    public UploadFolderElement(List<UploadData> elements, Path path){
        this.content = elements;
        this.path = path;
    }

    public UploadFolderElement(){
    }
}
