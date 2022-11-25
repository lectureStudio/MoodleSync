package moodle.sync.cli.inject;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Names;
import moodle.sync.core.config.DefaultConfiguration;
import moodle.sync.core.config.MoodleSyncConfiguration;
import moodle.sync.core.context.MoodleSyncContext;
import moodle.sync.core.web.service.MoodleService;
import moodle.sync.core.web.service.UploadService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lecturestudio.core.app.AppDataLocator;
import org.lecturestudio.core.app.ApplicationContext;
import org.lecturestudio.core.app.LocaleProvider;
import org.lecturestudio.core.app.configuration.Configuration;
import org.lecturestudio.core.app.configuration.ConfigurationService;
import org.lecturestudio.core.app.configuration.JsonConfigurationService;
import org.lecturestudio.core.app.dictionary.Dictionary;
import org.lecturestudio.core.audio.bus.AudioBus;
import org.lecturestudio.core.beans.StringProperty;
import org.lecturestudio.core.bus.ApplicationBus;
import org.lecturestudio.core.bus.EventBus;
import org.lecturestudio.core.util.AggregateBundle;
import org.lecturestudio.core.util.DirUtils;

import javax.inject.Singleton;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

public class ApplicationModule extends AbstractModule {

	@Provides
	@Singleton
	ResourceBundle createResourceBundle() throws Exception {
		LocaleProvider localeProvider = new LocaleProvider();
		Locale locale = localeProvider.getBestSupported(Locale.getDefault());

		return new AggregateBundle(locale, "resources.i18n.core", "resources.i18n.dict", "resources.i18n.cli");
	}

	@Provides
	@Singleton
	AggregateBundle createAggregateBundle(ResourceBundle resourceBundle) {
		return (AggregateBundle) resourceBundle;
	}

	@Provides
	@Singleton
	Dictionary provideDictionary(ResourceBundle resourceBundle) {
		return new Dictionary() {

			@Override
			public String get(String key) throws NullPointerException {
				return resourceBundle.getString(key);
			}

			@Override
			public boolean contains(String key) {
				return resourceBundle.containsKey(key);
			}
		};
	}

	@Provides
	@Singleton
	MoodleService createMoodleService(){
		return new MoodleService(new StringProperty("http://localhost/"));
	}



}
