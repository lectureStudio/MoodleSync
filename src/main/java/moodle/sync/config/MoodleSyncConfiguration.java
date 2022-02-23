package moodle.sync.config;

import moodle.sync.web.json.Course;
import org.lecturestudio.core.app.configuration.Configuration;
import org.lecturestudio.core.beans.ObjectProperty;
import org.lecturestudio.core.beans.StringProperty;

public class MoodleSyncConfiguration extends Configuration {

	/** The path where the synchronized files are stored at. */
	private final StringProperty syncRootPath = new StringProperty();

	private final ObjectProperty<Course> recentCourse = new ObjectProperty<>();
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
}
