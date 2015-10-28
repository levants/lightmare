package org.lightmare.logger;

import org.apache.log4j.Appender;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;

public class Configure {

    public static void configure() {
	Logger logger = Logger.getRootLogger();
	Appender appender = logger.getAppender("CA");
	logger.removeAppender(appender);

	logger.info("Curr " + System.getProperty("user.dir"));
	logger.info("This is info should print to console");
	logger.debug("This is debug should print to console");

	SimpleLayout layout = new SimpleLayout();
	ConsoleAppender console = new ConsoleAppender(layout);

	logger.addAppender(console);
	logger.warn("This is warning should print to file and console");

	logger.setLevel(Level.INFO);
    }
}
