package moodle.sync.view;

import moodle.sync.util.UploadElement;
import org.lecturestudio.core.view.Action;
import org.lecturestudio.core.view.ConsumerAction;
import org.lecturestudio.core.view.View;


import java.nio.file.Path;
import java.util.List;

public interface SyncView extends View {

    void setOnSync(Action action);

    void setFiles(List<UploadElement> files);

    void returnList();
}
