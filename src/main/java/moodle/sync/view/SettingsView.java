package moodle.sync.view;

import org.lecturestudio.core.beans.StringProperty;
import org.lecturestudio.core.view.Action;
import org.lecturestudio.core.view.View;

public interface SettingsView extends View {

    void setOnExit(Action action);

    void setMoodleToken(StringProperty moodleToken);

    void setSyncRootPath(StringProperty path);

    void setSelectSyncRootPath(Action action);
}
