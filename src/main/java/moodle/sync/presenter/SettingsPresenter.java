package moodle.sync.presenter;

import moodle.sync.config.DefaultConfiguration;
import moodle.sync.config.MoodleSyncConfiguration;
import moodle.sync.view.SettingsView;
import moodle.sync.view.StartView;
import moodle.sync.web.service.MoodleService;
import org.lecturestudio.core.app.ApplicationContext;
import org.lecturestudio.core.beans.StringProperty;
import org.lecturestudio.core.presenter.Presenter;
import org.lecturestudio.core.presenter.command.CloseApplicationCommand;
import org.lecturestudio.core.view.DirectoryChooserView;
import org.lecturestudio.core.view.ViewContextFactory;

import javax.inject.Inject;
import java.io.File;

import static java.util.Objects.nonNull;

public class SettingsPresenter extends Presenter<SettingsView> {

    private final ViewContextFactory viewFactory;

    private final MoodleService moodleService;

    @Inject
    SettingsPresenter(ApplicationContext context, SettingsView view,
                   ViewContextFactory viewFactory, MoodleService moodleService) {
        super(context, view);

        this.moodleService = moodleService;
        this.viewFactory = viewFactory;
    }

    @Override
    public void initialize() {

        MoodleSyncConfiguration config = (MoodleSyncConfiguration) context.getConfiguration();

        view.setOnExit(this::close);
        view.setMoodleField(config.moodleUrlProperty());
        view.setFormatsMoodle(config.formatsMoodleProperty());
        view.setFormatsFileserver(config.formatsFileserverProperty());
        view.setMoodleToken(config.moodleTokenProperty());
        view.setSyncRootPath(config.syncRootPathProperty());
        view.setSelectSyncRootPath(this::selectSyncPath);
        view.setFtpField(config.FileserverProperty());
        view.setFtpPort(config.portFileserverProperty());
        view.setFtpUser(config.userFileserverProperty());
        view.setFtpPassword(config.passwordFileserverProperty());
    }

    @Override
    public void close(){
        MoodleSyncConfiguration config = (MoodleSyncConfiguration) context.getConfiguration();
        moodleService.setApiUrl(config.getMoodleUrl());
        super.close();
    }

    private void selectSyncPath() {
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
