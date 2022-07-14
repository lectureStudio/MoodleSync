package moodle.sync.view;

import moodle.sync.util.UploadData.UploadData;
import moodle.sync.util.UploadData.UploadElement;
import org.lecturestudio.core.view.Action;
import org.lecturestudio.core.view.View;


import java.util.List;

/**
 * Interface defining the functions of the "sync-page".
 *
 * @author Daniel Schröter
 */
public interface SyncView extends View {

    void setOnClose(Action action);

    void setOnSync(Action action);

    void setFiles(List<UploadData> files);

}
