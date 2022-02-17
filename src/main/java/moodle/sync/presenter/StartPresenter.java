package moodle.sync.presenter;

import javax.inject.Inject;

import moodle.sync.web.json.Course;
import moodle.sync.web.json.Courselist;
import moodle.sync.web.json.Draft;
import org.lecturestudio.core.app.ApplicationContext;
import org.lecturestudio.core.presenter.Presenter;
import org.lecturestudio.core.presenter.command.CloseApplicationCommand;
import org.lecturestudio.core.view.NotificationType;
import org.lecturestudio.core.view.ViewContextFactory;

import moodle.sync.view.StartView;
import moodle.sync.web.service.MoodleService;

import java.util.List;

public class StartPresenter extends Presenter<StartView> {

	private final ViewContextFactory viewFactory;

	private final MoodleService moodleService;


	@Inject
	StartPresenter(ApplicationContext context, StartView view,
			ViewContextFactory viewFactory, MoodleService moodleService) {
		super(context, view);

		this.viewFactory = viewFactory;
		this.moodleService = moodleService;
	}

	@Override
	public void initialize() {
		view.setOnExit(this::onExit);
		view.setOnSync(this::onSync);
	}

	private void onExit() {
		context.getEventBus().post(new CloseApplicationCommand());
	}

	private void onSync() {
		try {
			Draft test = moodleService.getDraft();
			System.out.println(test.getItemid());
			List<Course> testen = moodleService.getEnrolledCourses();
		}
		catch (Throwable e) {
			logException(e, "Sync failed");

			showNotification(NotificationType.ERROR, "start.sync.error.title",
					"start.sync.error.message");
		}
	}
}
