package moodle.sync.presenter;

import moodle.sync.view.SettingsView;
import moodle.sync.view.StartView;
import moodle.sync.web.service.MoodleService;
import org.lecturestudio.core.app.ApplicationContext;
import org.lecturestudio.core.presenter.Presenter;
import org.lecturestudio.core.presenter.command.CloseApplicationCommand;
import org.lecturestudio.core.view.ViewContextFactory;

import javax.inject.Inject;

public class SettingsPresenter extends Presenter<SettingsView> {

    @Inject
    SettingsPresenter(ApplicationContext context, SettingsView view,
                   ViewContextFactory viewFactory, MoodleService moodleService) {
        super(context, view);

        //this.viewFactory = viewFactory;
        //this.moodleService = moodleService;
    }

    @Override
    public void initialize() {
        view.setOnExit(this::close);
        //view.setOnSync(this::onSync);
    }

}
