package metadata.util;

import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Log {

	private static Logger logger = Logger.getLogger("MyLog");
	private FileHandler fh = null;

	private Log() {
		try {
			fh = new FileHandler(Config.pathConfig + "credito.log");
			logger.addHandler(fh);
			SimpleFormatter formatter = new SimpleFormatter();
			fh.setFormatter(formatter);

			logger.info("Creaci�n Log");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Logger newInstance() {
		if (logger == null) {
			new Log();
		}
		return logger;
	}

}
