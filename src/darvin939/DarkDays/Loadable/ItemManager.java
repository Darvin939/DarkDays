package darvin939.DarkDays.Loadable;

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

import darvin939.DarkDays.DarkDays;

public class ItemManager {
	private DarkDays plugin;
	private Map<String, File> itemFiles;
	public HashMap<String, Item> items = new HashMap<String, Item>();
	private final File dir;
	private final ClassLoader classLoader;

	public ItemManager(DarkDays plugin) {
		this.plugin = plugin;
		itemFiles = new HashMap<String, File>();
		dir = new File(plugin.getDataFolder(), "items");
		dir.mkdir();
		List<URL> urls = new ArrayList<URL>();
		for (String itemFile : dir.list()) {
			if (itemFile.endsWith(".jar")) {
				File file = new File(dir, itemFile);
				String name = itemFile.toLowerCase().replace(".jar", "").replace("item", "");
				if (itemFiles.containsKey(name)) {
					// DayZ.db("Duplicate item jar found! Please remove " +
					// itemFile + " or " + itemFiles.get(name).getName());
					continue;
				}
				itemFiles.put(name, file);
				try {
					urls.add(file.toURI().toURL());
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
			}
		}

		ClassLoader cl = plugin.getClass().getClassLoader();
		classLoader = URLClassLoader.newInstance(urls.toArray(new URL[urls.size()]), cl);
		loadItems();
	}

	public HashMap<String, Item> getItems() {
		return items;
	}

	public void loadItems() {
		try {
			for (Entry<String, File> s : itemFiles.entrySet()) {

				JarFile jarFile = new JarFile(s.getValue());
				Enumeration<JarEntry> entries = jarFile.entries();
				String mainClass = null;
				while (entries.hasMoreElements()) {
					JarEntry element = entries.nextElement();
					if (element.getName().equalsIgnoreCase("item.info")) {
						BufferedReader reader = new BufferedReader(new InputStreamReader(jarFile.getInputStream(element)));
						mainClass = reader.readLine().substring(12);
						break;
					}
				}
				jarFile.close();
				if (mainClass != null) {
					Class<?> clazz = Class.forName(mainClass, true, classLoader);
					Constructor<?> cnst = clazz.getConstructor(DarkDays.class);
					Item item = (Item) cnst.newInstance(plugin);
					items.put(item.getName(), item);
				} else {
					throw new Exception();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			// DayZ.log(Level.INFO, "The item " + file.getName() +
			// " failed to load");
		}
	}

}
