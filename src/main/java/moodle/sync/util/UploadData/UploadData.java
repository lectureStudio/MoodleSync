package moodle.sync.util.UploadData;

import lombok.Getter;
import lombok.Setter;

import java.nio.file.Path;

@Getter
@Setter

/**
 * Class presenting the superclass needed for file-/ and directory support.
 *
 * @author Daniel Schröter
 */
public class UploadData {

    private Path path;

}
