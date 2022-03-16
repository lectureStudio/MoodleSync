package moodle.sync.view;

import moodle.sync.util.UploadElement;
import moodle.sync.web.json.Course;
import org.lecturestudio.core.view.Action;
import org.lecturestudio.core.view.ConsumerAction;
import org.lecturestudio.core.view.View;
import org.lecturestudio.web.api.filter.RegexRule;


import java.nio.file.Path;
import java.util.List;

public interface SyncView extends View {

    void setOnClose(Action action);

    void setOnSync(Action action);

    void setFiles(List<UploadElement> files);



}
