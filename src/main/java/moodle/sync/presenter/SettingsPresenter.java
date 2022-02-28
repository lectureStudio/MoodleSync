package moodle.sync.presenter;

import moodle.sync.config.DefaultConfiguration;
import moodle.sync.config.MoodleSyncConfiguration;
import moodle.sync.view.SettingsView;
import moodle.sync.view.StartView;
import moodle.sync.web.service.MoodleService;
import org.lecturestudio.core.app.ApplicationContext;
import org.lecturestudio.core.presenter.Presenter;
import org.lecturestudio.core.presenter.command.CloseApplicationCommand;
import org.lecturestudio.core.view.DirectoryChooserView;
import org.lecturestudio.core.view.ViewContextFactory;

import javax.inject.Inject;
import java.io.File;

import static java.util.Objects.nonNull;

public class SettingsPresenter extends Presenter<SettingsView> {

    private final ViewContextFactory viewFactory;

    @Inject
    SettingsPresenter(ApplicationContext context, SettingsView view,
                   ViewContextFactory viewFactory, MoodleService moodleService) {
        super(context, view);

        this.viewFactory = viewFactory;
    }

    @Override
    public void initialize() {

        MoodleSyncConfiguration config = (MoodleSyncConfiguration) context.getConfiguration();

        view.setOnExit(this::close);
        view.setMoodleToken(config.moodleTokenProperty());
        view.setSyncRootPath(config.syncRootPathProperty());
        view.setSelectSyncRootPath(this::selectRecordingPath);
    }

    private void selectRecordingPath() {
        MoodleSyncConfiguration config = (MoodleSyncConfiguration) context.getConfiguration();
        String syncPath = config.getSyncRootPath();
        if(syncPath == null || syncPath.isEmpty() || syncPath.isBlank()){
            DefaultConfiguration defaultConfiguration = new DefaultConfiguration();
            syncPath = defaultConfiguration.getSyncRootPath();
        }
        File initDirectory = new File(syncPath);

        DirectoryChooserView dirChooser = viewFactory.createDirectoryChooserView();
        dirChooser.setInitialDirectory(initDirectory);

        File selectedFile = dirChooser.show(view);

        if (nonNull(selectedFile)) {
            config.setSyncRootPath(selectedFile.getAbsolutePath());
        }
    }
}
