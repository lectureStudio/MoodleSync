package moodle.sync.config;

import java.util.Locale;

/**
 * This Class offers the possibility to generate a default configuration.
 */
public class DefaultConfiguration extends MoodleSyncConfiguration {

	public DefaultConfiguration() {
		setApplicationName("MoodleSync");
		setLocale(Locale.getDefault());
		setCheckNewVersion(true);
		setUIControlSize(9);
		setStartMaximized(false);
		setAdvancedUIMode(false);
		setSyncRootPath(System.getProperty("user.dir"));
		setFormatsFileserver("mp4");
		setFormatsMoodle("pdf,png,pptx,docx");
		setPortFileserver("21");
		setMoodleUrl("http://localhost");
	}

}
