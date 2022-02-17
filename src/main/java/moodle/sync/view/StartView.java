package moodle.sync.view;

import org.lecturestudio.core.view.Action;
import org.lecturestudio.core.view.View;

public interface StartView extends View {

	void setOnExit(Action action);

	void setOnSync(Action action);

}
