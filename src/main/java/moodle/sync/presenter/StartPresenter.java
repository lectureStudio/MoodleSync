package moodle.sync.presenter;

import javax.inject.Inject;

import moodle.sync.config.DefaultConfiguration;
import moodle.sync.config.MoodleSyncConfiguration;
import moodle.sync.util.FileService;
import moodle.sync.web.json.Course;
import moodle.sync.web.json.Coursecontent;
import moodle.sync.web.json.Module;
import moodle.sync.web.json.Section;

import org.lecturestudio.core.app.ApplicationContext;
import org.lecturestudio.core.beans.ObjectProperty;
import org.lecturestudio.core.presenter.Presenter;
import org.lecturestudio.core.presenter.command.CloseApplicationCommand;
import org.lecturestudio.core.presenter.command.ShowPresenterCommand;
import org.lecturestudio.core.view.NotificationType;
import org.lecturestudio.core.view.ViewContextFactory;

import moodle.sync.view.StartView;
import moodle.sync.web.service.MoodleService;
import org.lecturestudio.web.api.exception.MatrixUnauthorizedException;
import org.lecturestudio.web.api.service.DLZRoomService;

import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class StartPresenter extends Presenter<StartView> {

	private final ViewContextFactory viewFactory;

	private final MoodleService moodleService;

	private final MoodleSyncConfiguration config;

	private List<Section> coursecontent;



	@Inject
	StartPresenter(ApplicationContext context, StartView view,
			ViewContextFactory viewFactory, MoodleService moodleService) {
		super(context, view);

		this.viewFactory = viewFactory;
		this.moodleService = moodleService;
		this.config = (MoodleSyncConfiguration) context.getConfiguration();

	}

	@Override
	public void initialize() {
		String syncPath = config.getSyncRootPath();
		if(syncPath == null || syncPath.isEmpty() || syncPath.isBlank()){
			DefaultConfiguration defaultConfiguration = new DefaultConfiguration();
			config.setSyncRootPath(defaultConfiguration.getSyncRootPath());
		}
		view.setOnExit(this::onExit);
		view.setOnSync(this::onSync);
		view.setOnSettings(this::onSettings);
		view.setCourse(course());
		view.setCourses(courses());
		view.setSection(config.recentSectionProperty());
		view.setSections(sections());
		view.setOnCourseChanged(this::onCourseChanged);
	}

	private void onCourseChanged(Course course) {
		view.setSections(sections());
	}

	private void onSectionChanged(Section section) {
		view.setSections(sections());
	}

	private ObjectProperty<Course> course(){
		return config.recentCourseProperty();
	}

	private List<Course> courses() {
		List<Course> courses = List.of();
		try {
			courses = moodleService.getEnrolledCourses(config.getMoodleToken());
		} catch (Exception e) {
			config.setRecentCourse(null);
			handleException(e, "", e.getMessage());
		}
		return courses;
	}

	private List<Section> sections(){
		List<Section> content = moodleService.getCourseContent(config.getMoodleToken(), config.getRecentCourse().getId());
		coursecontent = content;
		return content;
	}

	private void onSettings() {
			context.getEventBus().post(new ShowPresenterCommand<>(SettingsPresenter.class));
	}

	private void onExit() {
		context.getEventBus().post(new CloseApplicationCommand());
	}


	private void onSync() {
		try {
			try{
				FileService fileService = new FileService();
				Path execute = Paths.get(config.getSyncRootPath() + "/" + config.recentCourseProperty().get().getDisplayname() + "/" + config.recentSectionProperty().get().getName());
				System.out.println(execute);
				fileService.DirectoryManager(execute);
				List<Module> choosenSectionModules = new ArrayList<Module>();
				for(int i = 0; i < config.getRecentSection().getModules().size(); i++){
					if(config.getRecentSection().getModules().get(i).getModname().equals("url") || config.getRecentSection().getModules().get(i).getModname().equals("resource")){
						choosenSectionModules.add(config.getRecentSection().getModules().get(i));
					}
				}
				System.out.println(choosenSectionModules.size());
				for(int i = 0; i < choosenSectionModules.size(); i++){
					System.out.println(choosenSectionModules.get(i).getName());
				}
			}
			catch (Throwable e){

			}
			/*File initialFile = new File("C:/Users/danie/OneDrive/Desktop/MoodleDocs.pdf");
			ByteArrayInputStream teste = new ByteArrayInputStream(FileUtils.readFileToByteArray(initialFile));
			//File result = new File("C:/Users/danie/OneDrive/Desktop/result");
			MoodleUploadService moodleUploadService = new MoodleUploadService("http://localhost/webservice/");
			String test = moodleUploadService.setFile(initialFile);*/
			/*OkHttpClient client = new OkHttpClient().newBuilder()
					.build();
			MediaType mediaType = MediaType.parse("text/plain");
			RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
					.addFormDataPart("filename","/C:/Users/danie/OneDrive/Desktop/Bachelorarbeit Anforderungsanalyse und Konkurrenzanalyse.pdf",
							RequestBody.create(MediaType.parse("application/octet-stream"),
									new File("/C:/Users/danie/OneDrive/Desktop/Bachelorarbeit Anforderungsanalyse und Konkurrenzanalyse.pdf")))
					.build();
			Request request = new Request.Builder()
					.url("http://localhost/webservice/upload.php?token=2e43a0cc7c9f536e26df55db90d2afdb")
					.method("POST", body)
					.build();
			Response response = client.newCall(request).execute();
			System.out.println(response.body().string());*/
		}
		catch (Throwable e) {
			logException(e, "Sync failed");

			showNotification(NotificationType.ERROR, "start.sync.error.title",
					"start.sync.error.message");
		}
	}

}
