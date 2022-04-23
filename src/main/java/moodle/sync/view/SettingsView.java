package moodle.sync.view;

import org.lecturestudio.core.beans.BooleanProperty;
import org.lecturestudio.core.beans.StringProperty;
import org.lecturestudio.core.view.Action;
import org.lecturestudio.core.view.View;

public interface SettingsView extends View {

    void setOnExit(Action action);

    void setMoodleField(StringProperty moodleURL);

    void setFormatsMoodle(StringProperty moodleformats);

    void setFormatsFileserver(StringProperty fileserverformats);

    void setFtpField(StringProperty ftpURL);

    void setFtpPort(StringProperty ftpPort);

    void setFtpUser(StringProperty ftpUser);

    void setFtpPassword(StringProperty ftpPassword);

    void setMoodleToken(StringProperty moodleToken);

    void setSyncRootPath(StringProperty path);

    void setSelectSyncRootPath(Action action);

    void setShowUnknownFormats(BooleanProperty unknownFormats);
}
