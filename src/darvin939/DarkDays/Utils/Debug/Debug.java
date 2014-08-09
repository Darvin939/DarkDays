package darvin939.DarkDays.Utils.Debug;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.common.io.Files;

public class Debug {

	public static File file;
	public static final Logger INSTANCE = Logger.getLogger("DarkDays");

	static {
		INSTANCE.setUseParentHandlers(false);
		INSTANCE.setLevel(Level.ALL);
	}

	public Debug(String path) {
		try {
			file = new File(path + File.separator + "debug.log");
			if (!file.exists()) {
				Files.createParentDirs(file);
			}

			writeToLog("=== " + (new SimpleDateFormat("yyyy.MM.dd HH:mm:ss")).format(new Date(System.currentTimeMillis())) + " ===");
			FileHandler e = new FileHandler(file.getPath(), true);
			INSTANCE.addHandler(e);
			e.setFormatter(new LogFormatter());
			INSTANCE.info("Logger created");
		} catch (SecurityException var3) {
			var3.printStackTrace();
		} catch (IOException var4) {
			var4.printStackTrace();
		}

	}

	public static void stopLogging() {
		for (Handler h : INSTANCE.getHandlers()) {
			h.close();
			INSTANCE.removeHandler(h);
		}
		writeToLog("\n");
	}

	private static void writeToLog(String string) {
		Throwable var1 = null;
		try {
			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file.getAbsoluteFile(), true)));

			try {
				out.println(string);
			} finally {
				if (out != null) {
					out.close();
				}

			}
		} catch (Throwable var11) {
			var1 = var11;

			try {
				throw var1;
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}

	}
}
