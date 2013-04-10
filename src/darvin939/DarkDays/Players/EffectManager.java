package darvin939.DarkDays.Players;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.bukkit.entity.Player;

import darvin939.DarkDays.DarkDays;
import darvin939.DarkDays.Configuration.Config;

public class EffectManager extends Manager {

	private DarkDays plugin;
	public static HashMap<String, Integer> runnableTasks = new HashMap<String, Integer>();
	private Map<String, File> effectFiles;
	public HashMap<String, Effect> effects = new HashMap<String, Effect>();
	private final File dir;
	private final ClassLoader classLoader;

	public EffectManager(DarkDays plugin) {
		this.plugin = plugin;
		effectFiles = new HashMap<String, File>();
		dir = new File(plugin.getDataFolder(), "effects");
		dir.mkdir();
		List<URL> urls = new ArrayList<URL>();
		for (String effectFile : dir.list()) {
			if (effectFile.endsWith(".jar")) {
				File file = new File(dir, effectFile);
				String name = effectFile.toLowerCase().replace(".jar", "").replace("effect", "");
				if (effectFiles.containsKey(name)) {
					// DayZ.db("Duplicate effect jar found! Please remove " +
					// effectFile + " or " + effectFiles.get(name).getName());
					continue;
				}
				effectFiles.put(name, file);
				try {
					urls.add(file.toURI().toURL());
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
			}
		}
		ClassLoader cl = plugin.getClass().getClassLoader();
		classLoader = URLClassLoader.newInstance(urls.toArray(new URL[urls.size()]), cl);
		loadEffects();
	}

	public HashMap<String, Effect> getEffects() {
		return effects;
	}

	public void loadEffects() {
		try {
			for (Entry<String, File> s : effectFiles.entrySet()) {
				JarFile jarFile = new JarFile(s.getValue());
				Enumeration<JarEntry> entries = jarFile.entries();
				String mainClass = null;
				while (entries.hasMoreElements()) {
					JarEntry element = entries.nextElement();
					if (element.getName().equalsIgnoreCase("effect.info")) {
						BufferedReader reader = new BufferedReader(new InputStreamReader(jarFile.getInputStream(element)));
						mainClass = reader.readLine().substring(12);
						break;
					}
				}
				jarFile.close();
				if (mainClass != null) {
					Class<?> clazz = Class.forName(mainClass, true, classLoader);
					Constructor<?> cnst = clazz.getConstructor(DarkDays.class);
					Effect effect = (Effect) cnst.newInstance(plugin);
					effects.put(effect.getName(), effect);
				} else {
					throw new Exception();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			// DayZ.log(Level.INFO, "The effect " + file.getName() +
			// " failed to load");
		}
	}

	public static String getEffect(Player p, String eff) {
		if (Config.getPC().getEffects(p) != null) {
			String[] effects = Config.getPC().getEffects(p);
			if (effects.length > 0)
				for (String effect : effects) {
					if (effect.equalsIgnoreCase(eff)) {
						return effect;
					}
				}
		}
		return null;
	}

	public static void setEffect(Player p, String effect) {
		Config.getPC().addEffect(p, effect);
	}

	public static HashMap<String, Integer> getTasks() {
		return runnableTasks;
	}

	public void cancelEffect(Player p, String effect) {
		Config.getPC().removeEffect(p, effect);
		Integer count = 0;
		for (Player player : plugin.getServer().getOnlinePlayers()) {
			if (getEffect(player, effect) != null)
				count += 1;
		}
		if (count == 0 && getTasks().containsKey(effect)) {
			plugin.getServer().getScheduler().cancelTask(getTasks().get(effect));
			getTasks().remove(effect);
		}
	}
}
