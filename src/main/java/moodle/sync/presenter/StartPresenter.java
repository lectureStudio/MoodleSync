package moodle.sync.presenter;

import javax.inject.Inject;

import moodle.sync.web.client.MultipartBody;
import moodle.sync.web.json.*;
import moodle.sync.web.service.MoodleFileService;
import moodle.sync.web.service.MoodleUploadService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.lecturestudio.core.app.ApplicationContext;
import org.lecturestudio.core.presenter.Presenter;
import org.lecturestudio.core.presenter.command.CloseApplicationCommand;
import org.lecturestudio.core.presenter.command.ShowPresenterCommand;
import org.lecturestudio.core.view.NotificationType;
import org.lecturestudio.core.view.ViewContextFactory;

import moodle.sync.view.StartView;
import moodle.sync.web.service.MoodleService;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.StandardCopyOption;

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
		view.setOnSettings(this::onSettings);
	}

	private void onSettings() {
			context.getEventBus().post(new ShowPresenterCommand<>(SettingsPresenter.class));
	}

	private void onExit() {
		context.getEventBus().post(new CloseApplicationCommand());
	}

	private void onSync() {
		try {
			/*MoodleFileService moodleFileService = new MoodleFileService("http://localhost/webservice/pluginfile.php/86/mod_resource/content/0/Web%20service%20API%20functions%20-%20MoodleDocs.pdf?forcedownload=1");
			InputStream test = moodleFileService.getDownload();

			File file = new File("C:/Users/danie/OneDrive/Desktop/testen.pdf");

			FileUtils.copyInputStreamToFile(test, file);*/
			File initialFile = new File("C:/Users/danie/OneDrive/Desktop/MoodleDocs.pdf");
			//InputStream testen = FileUtils.openInputStream(initialFile);
			ByteArrayInputStream teste = new ByteArrayInputStream(FileUtils.readFileToByteArray(initialFile));
			MoodleUploadService moodleUploadService = new MoodleUploadService("http://localhost/webservice/");
			String test = moodleUploadService.setFile(teste);
		}
		catch (Throwable e) {
			logException(e, "Sync failed");

			showNotification(NotificationType.ERROR, "start.sync.error.title",
					"start.sync.error.message");
		}
	}
}
