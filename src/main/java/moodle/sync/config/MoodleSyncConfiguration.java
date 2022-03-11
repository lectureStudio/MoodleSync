package moodle.sync.config;

import javafx.beans.property.ListProperty;
import moodle.sync.javafx.UploadList;
import moodle.sync.util.UploadElement;
import moodle.sync.web.json.Course;
import moodle.sync.web.json.Section;
import org.lecturestudio.core.app.configuration.Configuration;
import org.lecturestudio.core.beans.ObjectProperty;
import org.lecturestudio.core.beans.StringProperty;

public class MoodleSyncConfiguration extends Configuration {

	/** The path where the synchronized files are stored at. */
	private final StringProperty syncRootPath = new StringProperty();

	private final ObjectProperty<Course> recentCourse = new ObjectProperty<>();

	private final StringProperty moodleToken = new StringProperty();

	private final ObjectProperty<Section> recentSection = new ObjectProperty<>();

	private final ObjectProperty<UploadList> uploadList = new ObjectProperty<>();


	/**
	 * Get the path where the synchronized files are stored at.
	 *
	 * @return the sync root path.
	 */
	public String getSyncRootPath() {
		return syncRootPath.get();
	}

	/**
	 * Set the path where the synchronized files should be stored.
	 *
	 * @param path the root sync path to set.
	 */
	public void setSyncRootPath(String path) {
		this.syncRootPath.set(path);
	}

	/**
	 * Get the sync root path property.
	 *
	 * @return the sync root path property.
	 */
	public StringProperty syncRootPathProperty() {
		return syncRootPath;
	}


	public Course getRecentCourse() {
		return recentCourse.get();
	}

	public void setRecentCourse(Course course) {
		this.recentCourse.set(course);
	}

	public ObjectProperty<Course> recentCourseProperty() {
		return recentCourse;
	}

	public String getMoodleToken() {
		return moodleToken.get();
	}

	public void setMoodleToken(String token) {
		this.moodleToken.set(token);
	}

	public StringProperty moodleTokenProperty() {
		return moodleToken;
	}

	public Section getRecentSection() {
		return recentSection.get();
	}

	public void setRecentSection(Section section) {
		this.recentSection.set(section);
	}

	public ObjectProperty<Section> recentSectionProperty() {
		return recentSection;
	}

	public UploadList getUploadList() {
		return uploadList.get();
	}

	public void setUploadList(UploadList uploadList) {
		this.uploadList.set(uploadList);
	}

	public ObjectProperty<UploadList> uploadListProperty() {
		return uploadList;
	}
}
