package moodle.sync.config;

import java.util.Locale;

public class DefaultConfiguration extends MoodleSyncConfiguration {

	public DefaultConfiguration() {
		setApplicationName("MoodleSync");
		setLocale(Locale.getDefault());
		setCheckNewVersion(true);
		setUIControlSize(9);
		setStartMaximized(false);
		setAdvancedUIMode(false);
		setSyncRootPath(System.getProperty("user.dir"));
	}

}
