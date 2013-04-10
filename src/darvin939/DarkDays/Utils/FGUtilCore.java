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
 * */

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import javax.xml.parsers.DocumentBuilderFactory;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import darvin939.DarkDays.DarkDays;

public abstract class FGUtilCore extends CipherUtil {

	JavaPlugin plg;
	public String px = "";
	private String permprefix = DarkDays.premPrefix + "fgutilcore.";
	private boolean version_check = false;
	private String version_check_url = "";
	private String version_info_perm = permprefix + "config";
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

	public FGUtilCore(DarkDays plg, boolean vcheck, String lng, String devbukkitname, String px) {
		this.plg = plg;
		this.des = plg.getDescription();
		this.version_current = Double.parseDouble(des.getVersion());
		this.px = px;
		this.version_check = vcheck;
		this.language = lng;
		InitMsgFile();
		initStdMsg();

		if (devbukkitname.isEmpty())
			this.version_check = false;
		else {
			this.version_check_url = "http://dev.bukkit.org/server-mods/" + devbukkitname + "/files.rss";
			this.permprefix = devbukkitname.toLowerCase() + ".";
			startUpdateTick();
			version_new = getNewVersion(version_current);
			UpdateMsg();
		}
	}

	private void initStdMsg() {
		addMSG("msg_outdated", "%1% is outdated!");
		addMSG("msg_pleasedownload", "Please download new version (%1%) from ");
		addMSG("hlp_commands", "Command list:");
		addMSG("cmd_unknown", "Unknown command: %1%");
	}

	public void UpdateMsg(Player p) {
		if (version_check && p.hasPermission(this.version_info_perm) && (version_new > version_current)) {
			PrintMSG(p, "msg_outdated", "&6" + des.getName() + " v" + version_current, 'e', '6');
			PrintMSG(p, "msg_pleasedownload", version_new_str, 'e', '6');
			PrintMsg(p, "&3" + version_check_url.replace("files.rss", ""));
		}
	}

	public void UpdateMsg() {
		if ((version_new > version_current) && version_check) {
			SC(des.getName() + " v" + version_current + " is outdated!");
			SC("Recommended version is v" + version_new);
			SC("&3" + version_check_url.replace("files.rss", ""));
		}
	}

	public double getNewVersion(double currentVersion) {
		try {
			URL url = new URL(version_check_url);
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(url.openConnection().getInputStream());
			doc.getDocumentElement().normalize();
			NodeList nodes = doc.getElementsByTagName("item");
			Node firstNode = nodes.item(1);
			if (firstNode.getNodeType() == 1) {
				Element firstElement = (Element) firstNode;
				NodeList firstElementTagName = firstElement.getElementsByTagName("title");
				Element firstNameElement = (Element) firstElementTagName.item(0);
				NodeList firstNodes = firstNameElement.getChildNodes();
				version_new_str = firstNodes.item(0).getNodeValue().replace("DarkDays v", "").replace("(JAR only)", "").trim();
				return Double.parseDouble(firstNodes.item(0).getNodeValue().replace("DarkDays v", "").replace("(JAR only)", "").trim());
			}
		} catch (Exception localException) {
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

	public void SC(String msg) {
		plg.getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', px + msg));
	}

	public class Cmd {
		String perm;
		public String desc;

		public Cmd(String perm, String desc) {
			this.perm = perm;
			this.desc = desc;
		}
	}

	public void PrintMsg(Player p, String msg) {
		p.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
	}

	public void PrintPxMsg(Player p, String msg) {
		p.sendMessage(ChatColor.translateAlternateColorCodes('&', px + " " + msg));
	}

	public void addMSG(String key, String txt) {
		msg.put(key, ChatColor.translateAlternateColorCodes('&', lng.getString(key, txt)));
		if (msglist.isEmpty())
			msglist = key;
		else
			msglist = msglist + "NEXTMESSAGE" + key;
	}

	public void InitMsgFile() {
		try {
			Map<String, String> map = readMap(plg.getDataFolder() + File.separator + "locales", language + ".ddlang");
			lng = new YamlConfiguration();
			if (map.isEmpty())
				plg.getLogger().info("Language file not found. Making new...");
			else
				plg.getLogger().info("Initialize language file");	
			for (Entry<String, String> e : map.entrySet()) {
				lng.set(e.getKey(), e.getValue());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void SaveMSG() {
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

	public String MSG(String id) {
		return MSG(id, "", this.c1, this.c2);
	}

	public String MSG(String id, char c) {
		return MSG(id, "", c, this.c2);
	}

	public String MSG(String id, String keys) {
		return MSG(id, keys, this.c1, this.c2);
	}

	public String MSG(String id, String keys, char c) {
		return MSG(id, keys, this.c1, c);
	}

	/*
	 * Расшифровка: id - ID строки, keys - заменитель %n% ("test;bal bla bla"),
	 * c1 - цвет всей строки, c2 - цвет %n%
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

	public void PrintMSG(Player p, String msg_key, String keys) {
		p.sendMessage(MSG(msg_key, keys, this.c1, this.c2));
	}

	public void PrintMSG(Player p, String msg_key, String keys, char c1, char c2) {
		p.sendMessage(MSG(msg_key, keys, c1, c2));
	}

	public void PrintMSG(Player p, String msg_key) {
		p.sendMessage(MSG(msg_key));
	}
}
