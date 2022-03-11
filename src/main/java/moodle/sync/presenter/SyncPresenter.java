package moodle.sync.presenter;

import moodle.sync.config.MoodleSyncConfiguration;
import moodle.sync.javafx.UploadList;
import moodle.sync.util.UploadElement;
import moodle.sync.view.SyncView;
import moodle.sync.web.service.MoodleService;
import org.lecturestudio.core.app.ApplicationContext;
import org.lecturestudio.core.presenter.Presenter;
import org.lecturestudio.core.util.ListChangeListener;
import org.lecturestudio.core.util.ObservableList;
import org.lecturestudio.core.view.ViewContextFactory;
import org.lecturestudio.web.api.filter.RegexRule;

import javax.inject.Inject;
import java.util.List;

public class SyncPresenter extends Presenter<SyncView> {

    private final ViewContextFactory viewFactory;

    @Inject
    SyncPresenter(ApplicationContext context, SyncView view,
                      ViewContextFactory viewFactory, MoodleService moodleService) {
        super(context, view);

        this.viewFactory = viewFactory;
    }

    @Override
    public void initialize(){
        MoodleSyncConfiguration config = (MoodleSyncConfiguration) context.getConfiguration();
        UploadList uploadList = config.getUploadList();
        List<UploadElement> uploadElements = uploadList.getElements();

        view.setFiles(uploadElements);
        view.setOnSync(this::execute);
        view.returnList();
    }

    private void execute() {
        MoodleSyncConfiguration config = (MoodleSyncConfiguration) context.getConfiguration();
        UploadList uploadList = config.getUploadList();
        List<UploadElement> uploadElements = uploadList.getElements();

        view.returnList();
        for(int i = 0; i < uploadElements.size(); i++){
            //System.out.println(uploadElements.get(i).getPath());
            //System.out.println(uploadElements.get(i).getChecked());
        }
    }


    private void toggleFile(UploadElement uploadElement) {
        //uploadElement.setExecute(true);
    }

    private void execute(List<UploadElement> uploads) {
        for(int i = 0; i < uploads.size(); i++){
            System.out.println(uploads.get(i).getPath());
            System.out.println(uploads.get(i).getChecked());
        }
    }

}
