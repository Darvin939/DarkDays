package darvin939.DeprecAPI;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.scheduler.BukkitTask;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.UUID;
import java.util.logging.Level;

public class MetricsLite {

	private final static int REVISION = 6;
	private static final String BASE_URL = "http://mcstats.org";
	private static final String REPORT_URL = "/report/%s";
	private final static int PING_INTERVAL = 10;
	private final Plugin plugin;
	private final YamlConfiguration configuration;
	private final File configurationFile;
	private final String guid;
	private final boolean debug;
	private final Object optOutLock = new Object();
	private volatile BukkitTask task = null;

	public MetricsLite(Plugin plugin) throws IOException {
		if (plugin == null) {
			throw new IllegalArgumentException("Plugin cannot be null");
		}

		this.plugin = plugin;

		configurationFile = getConfigFile();
		configuration = YamlConfiguration.loadConfiguration(configurationFile);

		configuration.addDefault("opt-out", false);
		configuration.addDefault("guid", UUID.randomUUID().toString());
		configuration.addDefault("debug", false);

		if (configuration.get("guid", null) == null) {
			configuration.options().header("http://mcstats.org").copyDefaults(true);
			configuration.save(configurationFile);
		}

		guid = configuration.getString("guid");
		debug = configuration.getBoolean("debug", false);
	}

	public boolean start() {
		synchronized (optOutLock) {
			if (isOptOut()) {
				return false;
			}

			if (task != null) {
				return true;
			}

			task = plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, new Runnable() {

				private boolean firstPost = true;

				public void run() {
					try {
						synchronized (optOutLock) {
							if (isOptOut() && task != null) {
								task.cancel();
								task = null;
							}
						}

						postPlugin(!firstPost);

						firstPost = false;
					} catch (IOException e) {
						if (debug) {
							Bukkit.getLogger().log(Level.INFO, "[Metrics] " + e.getMessage());
						}
					}
				}
			}, 0, PING_INTERVAL * 1200);

			return true;
		}
	}

	public boolean isOptOut() {
		synchronized (optOutLock) {
			try {
				configuration.load(getConfigFile());
			} catch (IOException ex) {
				if (debug) {
					Bukkit.getLogger().log(Level.INFO, "[Metrics] " + ex.getMessage());
				}
				return true;
			} catch (InvalidConfigurationException ex) {
				if (debug) {
					Bukkit.getLogger().log(Level.INFO, "[Metrics] " + ex.getMessage());
				}
				return true;
			}
			return configuration.getBoolean("opt-out", false);
		}
	}

	public void enable() throws IOException {
		synchronized (optOutLock) {
			if (isOptOut()) {
				configuration.set("opt-out", false);
				configuration.save(configurationFile);
			}

			if (task == null) {
				start();
			}
		}
	}

	public void disable() throws IOException {
		synchronized (optOutLock) {
			if (!isOptOut()) {
				configuration.set("opt-out", true);
				configuration.save(configurationFile);
			}

			if (task != null) {
				task.cancel();
				task = null;
			}
		}
	}

	public File getConfigFile() {
		File pluginsFolder = plugin.getDataFolder().getParentFile();

		return new File(new File(pluginsFolder, "PluginMetrics"), "config.yml");
	}

	private void postPlugin(boolean isPing) throws IOException {
		PluginDescriptionFile description = plugin.getDescription();
		String pluginName = description.getName();
		boolean onlineMode = Bukkit.getServer().getOnlineMode();
		String pluginVersion = description.getVersion();
		String serverVersion = Bukkit.getVersion();
		int playersOnline = Bukkit.getServer().getOnlinePlayers().length;

		final StringBuilder data = new StringBuilder();

		data.append(encode("guid")).append('=').append(encode(guid));
		encodeDataPair(data, "version", pluginVersion);
		encodeDataPair(data, "server", serverVersion);
		encodeDataPair(data, "players", Integer.toString(playersOnline));
		encodeDataPair(data, "revision", String.valueOf(REVISION));

		String osname = System.getProperty("os.name");
		String osarch = System.getProperty("os.arch");
		String osversion = System.getProperty("os.version");
		String java_version = System.getProperty("java.version");
		int coreCount = Runtime.getRuntime().availableProcessors();

		if (osarch.equals("amd64")) {
			osarch = "x86_64";
		}

		encodeDataPair(data, "osname", osname);
		encodeDataPair(data, "osarch", osarch);
		encodeDataPair(data, "osversion", osversion);
		encodeDataPair(data, "cores", Integer.toString(coreCount));
		encodeDataPair(data, "online-mode", Boolean.toString(onlineMode));
		encodeDataPair(data, "java_version", java_version);

		if (isPing) {
			encodeDataPair(data, "ping", "true");
		}

		URL url = new URL(BASE_URL + String.format(REPORT_URL, encode(pluginName)));
		URLConnection connection;

		if (isMineshafterPresent()) {
			connection = url.openConnection(Proxy.NO_PROXY);
		} else {
			connection = url.openConnection();
		}

		connection.setDoOutput(true);

		final OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
		writer.write(data.toString());
		writer.flush();

		final BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		final String response = reader.readLine();

		writer.close();
		reader.close();

		if (response == null || response.startsWith("ERR")) {
			throw new IOException(response);
		}
	}

	private boolean isMineshafterPresent() {
		try {
			Class.forName("mineshafter.MineServer");
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private static void encodeDataPair(final StringBuilder buffer, final String key, final String value) throws UnsupportedEncodingException {
		buffer.append('&').append(encode(key)).append('=').append(encode(value));
	}

	private static String encode(final String text) throws UnsupportedEncodingException {
		return URLEncoder.encode(text, "UTF-8");
	}

}
