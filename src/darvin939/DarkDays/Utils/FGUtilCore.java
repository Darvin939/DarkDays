package darvin939.DarkDays.Utils;

/*  
 *  FGUtilCore, Utilities class for Minecraft bukkit plugins
 *  
 *    (c)2012, fromgate, fromgate@gmail.com
 *  
 *      
 *  FGUtilCore is free software: you can redistribute it and/or modify
 *	it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  FGUtilCore is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with WeatherMan.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

/*
 * Modified for plugin DarkDays by Darvin939 (Sergey)
 * Contact email: darvin212@gmail.com
 * 
 */

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.xml.parsers.DocumentBuilderFactory;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.scheduler.BukkitTask;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import darvin939.DarkDays.DarkDays;

public abstract class FGUtilCore extends CipherUtil {

	protected DarkDays plg;
	public String px = "123";
	private boolean version_check = false;
	private String version_check_url = "";
	private String version_info_perm;
	private String language = "english";
	YamlConfiguration lng;
	protected HashMap<String, String> msg = new HashMap<String, String>();
	public char c1 = 'f';
	public char c2 = 'a';
	protected String msglist = "";
	public HashMap<String, Cmd> cmds = new HashMap<String, Cmd>();
	public String cmdlist = "";
	PluginDescriptionFile des;
	private double version_current = 0;
	private double version_new = 0;
	String version_new_str = "unknown";
	Random random = new Random();
	BukkitTask chId = null;
	private String permprefix;

	public FGUtilCore(DarkDays plg, boolean vcheck, String lng, String devbukkitname, String px) {
		this.plg = plg;
		this.des = plg.getDescription();
		this.version_current = Double.parseDouble(des.getVersion());
		this.px = px;
		this.version_check = vcheck;
		this.language = lng.toLowerCase();
		InitMsgFile();

		this.version_check_url = "http://dev.bukkit.org/server-mods/" + devbukkitname + "/files.rss";
		this.permprefix = devbukkitname.toLowerCase();
		this.version_info_perm = this.permprefix + "config";
		startUpdateTick();
		version_new = getNewVersion(version_current);
		UpdateMsg();
	}

	public DarkDays getPugin() {
		return plg;
	}

	public void UpdateMsg(Player p) {
		if (version_check && p.hasPermission(this.version_info_perm) && (version_new > version_current)) {
			PrintMSG(p, "msg_outdated", "&6" + des.getName() + " v" + version_current, 'e', '6');
			PrintMSG(p, "msg_pleasedownload", version_new_str, 'e', '6');
			Util.Print(p, "&3" + version_check_url.replace("files.rss", ""));
		}
	}

	public void UpdateMsg() {
		if ((version_new > version_current) && version_check) {
			Util.CSPx("v" + version_current + " is outdated!");
			Util.CSPx("Recommended version is v" + version_new);
			Util.CS("&3" + version_check_url.replace("files.rss", ""));
		}
	}

	public double getNewVersion(double currentVersion) {
		try {
			URL url = new URL(version_check_url);
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(url.openConnection().getInputStream());
			doc.getDocumentElement().normalize();
			NodeList nodes = doc.getElementsByTagName("item");
			Node firstNode = nodes.item(0);
			if (firstNode.getNodeType() == 1) {
				Element firstElement = (Element) firstNode;
				NodeList firstElementTagName = firstElement.getElementsByTagName("title");
				Element firstNameElement = (Element) firstElementTagName.item(0);
				NodeList firstNodes = firstNameElement.getChildNodes();
				version_new_str = firstNodes.item(0).getNodeValue().replace("DarkDays v", "").replace("(JAR only)", "").trim();
				return Double.parseDouble(firstNodes.item(0).getNodeValue().replace("DarkDays v", "").replace("(JAR only)", "").trim());
			}
		} catch (Exception e) {
			Util.CSPx("Failed to get updates");
		}
		return currentVersion;
	}

	private void startUpdateTick() {
		chId = plg.getServer().getScheduler().runTaskTimer(plg, new Runnable() {
			public void run() {
				version_new = getNewVersion(version_current);
			}
		}, 30 * 1200, 30 * 1200);
	}

	public void addCmd(String cmd, String perm, String desc) {
		cmds.put(cmd, new Cmd(this.permprefix + perm, desc));
		if (cmdlist.isEmpty())
			cmdlist = cmd;
		else
			cmdlist = cmdlist + ", " + cmd;
	}

	public class Cmd {
		String perm;
		public String desc;

		public Cmd(String perm, String desc) {
			this.perm = perm;
			this.desc = desc;
		}
	}

	public String getMsglist() {
		return msglist;
	}

	public void addMSG(String key, String txt) {
		msg.put(key, ChatColor.translateAlternateColorCodes('&', lng.getString(key, txt)));
		if (msglist.isEmpty())
			msglist = key;
		else
			msglist = msglist + "NEXTMESSAGE" + key;
	}

	@Deprecated
	public void InitMsgFileFromCipher() {
		try {
			Map<String, String> map = readMap(plg.getDataFolder() + File.separator + "locales", language + ".ddlang");
			lng = new YamlConfiguration();
			if (map.isEmpty()) {
				plg.getLogger().info("Language file not found. Making new...");
			} else {
				plg.getLogger().info("Initialize language file..");
			}
			for (Map.Entry<String, String> e : map.entrySet()) {
				lng.set((String) e.getKey(), e.getValue());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void InitMsgFile() {
		try {
			lng = new YamlConfiguration();
			File f = new File(plg.getDataFolder() + File.separator + "locales/" + language + ".lng");
			if (f.exists()) {
				plg.getLogger().info("Initialize language file..");
				lng.load(f);
			} else {
				plg.getLogger().info("Language file not found. Making new...");
				URL url = plg.getClass().getResource("/language/" + language + ".lng");
				if (!url.getFile().isEmpty()) {
					lng.load(url.getFile());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Deprecated
	public void SaveMSGToCipher() {
		String[] keys = msglist.split("NEXTMESSAGE");
		try {
			HashMap<String, String> map = new HashMap<String, String>();
			for (int i = 0; i < keys.length; i++) {
				map.put(keys[i], msg.get(keys[i]));
			}
			writeMap(plg.getDataFolder() + File.separator + "locales", language + ".ddlang", map);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void SaveMSG() {
		String[] keys = msglist.split("NEXTMESSAGE");
		try {
			File f = new File(plg.getDataFolder() + File.separator + "locales/" + language + ".lng");
			if (!f.exists()) {
				new File(plg.getDataFolder() + File.separator + "locales/").mkdirs();
				f.createNewFile();
			}
			YamlConfiguration cfg = new YamlConfiguration();
			for (int i = 0; i < keys.length; i++)
				cfg.set(keys[i], msg.get(keys[i]));
			cfg.save(f);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String MSG(String id) {
		return MSG(id, "", this.c1, this.c2);
	}

	public String MSG(String id, String keys) {
		return MSG(id, keys, this.c1, this.c2);
	}

	/*
	 * Расшифровка: id - ID строки, keys - заменитель %n% , c1 - цвет всей
	 * строки, c2 - цвет %n%
	 * 
	 * Пример: MSG("hlp_thishelp", "Test",'b', '2'); Вывод:
	 * "&b&2Test&b - this help"
	 */
	public String MSG(String id, String keys, char c1, char c2) {
		String str = "Unknown message: &a" + id;
		if (msg.containsKey(id)) {
			str = "&" + c1 + msg.get(id);
			String ln[] = keys.split(";");
			if (ln.length > 0)
				for (int i = 0; i < ln.length; i++) {
					str = str.replace("%" + Integer.toString(i + 1) + "%", "&" + c2 + ln[i] + "&" + c1);
				}
		}
		return ChatColor.translateAlternateColorCodes('&', str);
	}

	public String MSG(String id, char c1, char c2, Object... keys) {
		String str = "Unknown message: &a" + id;
		if (msg.containsKey(id)) {
			String clr1 = "";
			String clr2 = "";
			if (c1 != 'z')
				clr1 = "&" + c1;
			if (c2 != 'z')
				clr2 = "&" + c2;
			str = clr1 + msg.get(id);
			if (keys.length > 0)
				for (int i = 0; i < keys.length; i++)
					str.replace("%" + Integer.toString(i + 1) + "%", clr2 + keys[i].toString() + clr1);
		}
		return ChatColor.translateAlternateColorCodes('&', str);
	}

	public void PrintMSG(Player p, String msg_key, String keys, char c1, char c2) {
		p.sendMessage(MSG(msg_key, keys, c1, c2));
	}
}
