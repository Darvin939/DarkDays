package darvin939.DarkDays.Loadable;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
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

public class EffectManager {

	private DarkDays plugin;
	private Map<String, File> effectFiles;
	public HashMap<String, Effect> effects = new HashMap<String, Effect>();
	private HashMap<Method, Object> runMethods = new HashMap<Method, Object>();
	private static Map<Player, Map<String, Integer>> tasksID = new HashMap<Player, Map<String, Integer>>();
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
					// DarkDays.db("Duplicate effect jar found! Please remove "
					// +
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

	public HashMap<Method, Object> getRunMethods() {
		return runMethods;
	}

	public HashMap<String, Effect> getEffects() {
		return effects;
	}

	public static void addTaskID(Player p, String name, Integer id) {
		if (tasksID.containsKey(p))
			tasksID.get(p).put(name, id);
		else {
			Map<String, Integer> map = new HashMap<String, Integer>();
			map.put(name, id);
			tasksID.put(p, map);
		}
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

					Object cnst = clazz.getConstructor(DarkDays.class).newInstance(plugin);
					Effect effect = (Effect) cnst;
					effects.put(effect.getName(), effect);
					runMethods.put(clazz.getDeclaredMethod("run", Player.class), cnst);
				} else {
					throw new Exception();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			// DarkDays.log(Level.INFO, "The effect " + file.getName() +
			// " failed to load");
		}
	}

	public static String isEffect(Player p, String eff) {
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

	public static void setEffect(Player p, String effect, Integer id) {
		Config.getPC().addEffect(p, effect);
		addTaskID(p, effect, id);
	}

	public void pauseEffects(Player p) {
		if (tasksID.containsKey(p))
			for (Entry<String, Integer> s : tasksID.get(p).entrySet()) {
				plugin.getServer().getScheduler().cancelTask(s.getValue());
			}
	}

	public void cancelEffects(Player p) {
		if (tasksID.containsKey(p))
			for (Entry<String, Integer> s : tasksID.get(p).entrySet()) {
				plugin.getServer().getScheduler().cancelTask(s.getValue());
				if (isEffect(p, s.getKey()) != null)
					Config.getPC().removeEffect(p, s.getKey());
			}
	}

	public void cancelEffect(Player p, String effect) {
		if (isEffect(p, effect) != null)
			Config.getPC().removeEffect(p, effect);
		if (tasksID.containsKey(p)) {
			for (Entry<String, Integer> s : tasksID.get(p).entrySet()) {
				if (s.getKey().equalsIgnoreCase(effect)) {
					plugin.getServer().getScheduler().cancelTask(s.getValue());
					break;
				}
			}
		}
	}
}
