package moodle.sync.config;

import moodle.sync.web.json.Course;
import moodle.sync.web.json.Section;
import org.lecturestudio.core.app.configuration.Configuration;
import org.lecturestudio.core.beans.IntegerProperty;
import org.lecturestudio.core.beans.ObjectProperty;
import org.lecturestudio.core.beans.StringProperty;

public class MoodleSyncConfiguration extends Configuration {

	/** The path where the synchronized files are stored at. */
	private final StringProperty syncRootPath = new StringProperty();

	private final ObjectProperty<Course> recentCourse = new ObjectProperty<>();

	private final StringProperty moodleToken = new StringProperty();

	private final ObjectProperty<Section> recentSection = new ObjectProperty<>();

	private final StringProperty moodleUrl = new StringProperty();

	private final StringProperty formatsMoodle = new StringProperty();

	private final StringProperty formatsFileserver = new StringProperty();

	private final StringProperty ftpserver = new StringProperty();

	private final StringProperty ftpuser = new StringProperty();

	private final StringProperty ftppassword = new StringProperty();

	private final StringProperty ftpport = new StringProperty();

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

	public String getMoodleUrl() {
		return moodleUrl.get();
	}

	public void setMoodleUrl(String url) {
		this.moodleUrl.set(url);
	}

	public StringProperty moodleUrlProperty() {
		return moodleUrl;
	}

	public String getFormatsMoodle() {
		return formatsMoodle.get();
	}

	public void setFormatsMoodle(String formats) {
		this.formatsMoodle.set(formats);
	}

	public StringProperty formatsMoodleProperty() {
		return formatsMoodle;
	}

	public String getFormatsFileserver() {
		return formatsFileserver.get();
	}

	public void setFormatsFileserver(String formats) {
		this.formatsFileserver.set(formats);
	}

	public StringProperty formatsFileserverProperty() {
		return formatsFileserver;
	}

	public String getFileserver() {
		return ftpserver.get();
	}

	public void setFileserver(String fileserver) {
		this.ftpserver.set(fileserver);
	}

	public StringProperty FileserverProperty() {
		return ftpserver;
	}

	public String getUserFileserver() {
		return ftpuser.get();
	}

	public void setUserFileserver(String user) {
		this.ftpuser.set(user);
	}

	public StringProperty userFileserverProperty() {
		return ftpuser;
	}

	public String getPasswordFileserver() {
		return ftppassword.get();
	}

	public void setPasswordFileserver(String formats) {
		this.ftppassword.set(formats);
	}

	public StringProperty passwordFileserverProperty() {
		return ftppassword;
	}

	public String getPortFileserver() {
		return ftpport.get();
	}

	public void setPortFileserver(String port) {
		this.ftpport.set(port);
	}

	public StringProperty portFileserverProperty() {
		return ftpport;
	}
}
