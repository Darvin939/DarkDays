package darvin939.DarkDays.Configuration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lib.PatPeter.SQLibrary.Database;

import org.bukkit.block.Biome;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import darvin939.DarkDays.DarkDays;
import darvin939.DarkDays.SQL.DBInitLite;
import darvin939.DarkDays.SQL.Chests.ChestManager;
import darvin939.DarkDays.SQL.Chests.SQLChest;
import darvin939.DarkDays.SQL.Players.PlayerManager;
import darvin939.DarkDays.SQL.Players.SQLPlayer;
import darvin939.DarkDays.Utils.FGUtilCore;

public class Config extends FGUtilCore {
	public static FGUtilCore FGU;
	private static PlayerConfig playerCfg;
	private static ChestConfig chestCfg;
	private static RegionConfig regionCfg;
	private static LootConfig lootCfg;
	private static SpawnConfig spawnCfg;

	public Config(DarkDays plg, boolean vcheck, String lng, String devbukkitname, String px) {
		super(plg, vcheck, lng, devbukkitname, px);
		setupMessages();
		setupRandomMessages();
		SaveMSG();
		FGU = this;
	}

	public static enum Nodes {
		language("Language", "english"), verCheck("Version-check", true), prefix("Prefix", "DarkDays"), disable_health_regen("DisableHealthRegen", false), wand_item("WandItem", 369), control_sitemst("ControlSItems", true), coloured_tegs("ColouredTags", true), enable_regions("EnableRegions", false), spawn_withBlindness(
				"SpawnWithBlindness", true), only_zombies("Zombie.OnlyZombies", true), zombie_speed("Zombie.Speed", 0.4), zombie_smoothness("Zombie.Smoothness", 15), attack_strength("Zombie.AttackStrength", 4), zombie_health("Zombie.Health", 24), zombie_pickup("Zombie.PickUpPlayerArmor", true), thirst_speed(
				"Thirst.Speed", 2), chest_empty("Chest.IfOnlyEmpty", true), chest_regen("Chest.RegenTime", 2), chest_click("Chest.Destroy.ClickTo", true), chest_disappear("Chest.Disappear", true), chest_spawnz("Chest.Destroy.SpawnZombie", true), chest_spawnzperc("Chest.Destroy.ZombiePercent", 50), MYSQL_USER(
				"MySQL.Username", "root"), MYSQL_PASS("MySQL.Password", "root"), MYSQL_HOST("MySQL.Hostname", "localhost"), MYSQL_PORT("MySQL.Port", 3306), MYSQL_DATABASE("MySQL.Database", "darkdays"), MYSQL_DBWRAPPER("MySQL.DataWrapper", "none"), noise_enable("Noise.EnableBlockData", true), noise_multiplier(
				"Noise.Multiplier", 0.3);

		String node;
		Object value;

		private Nodes(String node, Object value) {
			this.node = node;
			this.value = value;
		}

		public String getNode() {
			return node;
		}

		public Object getValue() {
			return this.value;
		}

		public Boolean getBoolean() {
			return (Boolean) value;
		}

		public Integer getInteger() {
			if (value instanceof Double)
				return ((Double) value).intValue();

			return (Integer) value;
		}

		public Double getDouble() {
			if (value instanceof Integer)
				return (double) ((Integer) value).intValue();

			return (Double) value;
		}

		public String getString() {
			return (String) value;
		}

		public Long getLong() {
			if (value instanceof Integer)
				return ((Integer) value).longValue();

			return (Long) value;
		}

		public void setValue(Object value) {
			this.value = value;
		}

		public String toString() {
			return String.valueOf(value);
		}
	}

	private void setupMessages() {
		// sys_
		addMSG("msg_outdated", "%1% is outdated!");
		addMSG("msg_pleasedownload", "Please download new version (%1%) from ");
		// loot_
		addMSG("loot_set", "LootID is set to %1%");
		addMSG("loot_error", "Error occurred with assigning LootID");
		addMSG("loot_nf", "LootID not found!");
		addMSG("loot_flag_potion_isEmpty", "Usage: &2..flag potion &7<spawn=[%,minCount-maxCount]> <effect=[type,minlvl-maxlvl,splash,extend]>");
		addMSG("loot_flag_item_isEmpty", "Usage: &2..flag &7[id] <spawn=[%,minCount-maxCount]> <effect=[type,minlvl-maxlvl,%]>");
		addMSG("loot_save", "Loot %1% successfully saved");
		addMSG("loot_remove", "Loot %1% successfully removed");
		addMSG("loot_new", "New loot &7%1%&f created. Enter &2/dd loot item &7[id,id,..]&f to add items or just setup the flags for potions");
		addMSG("loot_item_add", "Item(s) %1% added to your loot. Now, set the flags for ID of items &2/dd loot flag &7[id]&f ...");
		addMSG("loot_flag_set", "The flags are set for %1%. Now add durability, save or continue setup the flags for loot");
		addMSG("loot_new_isempty", "First add the new loot! Type &2/dd loot new &7[name]&f to create new loot");
		addMSG("loot_parser", "%1% Syntax error: check the entered data");
		addMSG("loot_flag_spawnnf", "The flag &7\"spawn\"&f  is not set for %1%! Set this flag otherwise this item will not spawn");
		addMSG("loot_flag_nf", "Item ID or Potion not found. Please check the entered command");
		addMSG("loot_durability_isEmpty", "Usage: &2..durability &7[id] [0-99]");
		addMSG("loot_durability_set", "Durability %2% set for %1%. Enter &2/dd loot save &f to save the loot or continue setup the flags");
		addMSG("loot_durability_nf", "Item ID not found. Please check the entered command");
		// chest_
		addMSG("chest_normal", "This is a normal chest");
		addMSG("chest_newWithID", "Created new looted chest with LootID");
		addMSG("chest_new", "Created new looted chest");
		addMSG("chest_remove", "Deleted loot chest");
		addMSG("chest_cantDestroy", "You can't destroy the loot chest!");
		// cmd_
		addMSG("cmd_indev", "This command in the process of developing");
		addMSG("cmd_occurred", "Error occurred when trying the command /%1%");
		addMSG("cmd_noperm", "You don't have the Permission(%1%) to do that!");
		addMSG("cmd_effectnf", "Effect &2%1% &fnot found on the server!");
		addMSG("cmd_unknown", "Unknown command: %1%");
		// game_
		addMSG("game_start", "You are in the world. Good luck!");
		addMSG("game_alrady", "You alrady spawned!");
		addMSG("game_need_water", "Need more water");
		addMSG("game_noplay", "You are not in the world. Type the %1% to start playing");
		// spawn_
		addMSG("spawn_lobby_new", "New lobby location created");
		addMSG("spawn_lobby_error", "Failed to create location of lobby");
		addMSG("spawn_new", "Added new spawn point");
		addMSG("spawn_error", "Failed to added new spawn point");
		addMSG("spawn_lobby_nf", "Lobby not found");
		addMSG("spawn_nf", "Spawns not found");
		addMSG("spawn_remove_success", "Spawn %1% successfully removed");
		addMSG("spawn_remove_fail", "An error occurred while removing spawn %1%");
		// tags_
		addMSG("tag_ends", "TAG's %1%");
		// hlp_
		addMSG("hlp_topic", "Type %1% to show help topic");
		addMSG("hlp_commands", "Command list:");
		// addMSG("hlp_nf", "Help for %1% command not found");
		// hlp_cmd_
		addMSG("hlp_cmd_status", "Show your progress");
		addMSG("hlp_cmd_help", "Show this help topic");
		addMSG("hlp_cmd_chest", "Show information about the chest (look at the chest)");
		addMSG("hlp_cmd_chest_add", "Create new looted chest. Type &2..create &7[name] &fto create chest with lootID");
		addMSG("hlp_cmd_chest_remove", "Remove looted chest");
		addMSG("hlp_cmd_chest_set", "Set the lootID for chest");
		addMSG("hlp_cmd_spawn", "Start playing");
		addMSG("hlp_cmd_spawn_set", "Add new spawn point. Type &2..set lobby &fto add new lobby location");
		addMSG("hlp_cmd_spawn_list", "Show list of all spawns and lobby locations");
		addMSG("hlp_cmd_spawn_remove", "Type &2..remove &7[name] &fto remove the spawn point by name");
		addMSG("hlp_cmd_tag", "Type&2 ..tag enable/disable &fto enable/disable colored names");
		addMSG("hlp_cmd_region", "Make a region of zombie spawn locations. Select region with WorldEdit Wand. Then type &2..region save &7[name] [parametrs]&f. Parametrs: &2s=&7[true/false]&f - can spawn, &2h=&7[true/false]&f - top-to-bottom");
		addMSG("hlp_cmd_loot", "Create, delete, assigning flags and durability for loot");
		addMSG("hlp_cmd_loot_new", "Type &2/dd loot new &7[name]&f to create new loot in server memory. Type &2/dd loot save&f to save your loot");
		addMSG("hlp_cmd_loot_remove", "Remove the loot for the specified name");
		addMSG("hlp_cmd_loot_list", "Show list of existing loot");
		addMSG("hlp_cmd_loot_item", "Add items to your loot");
		addMSG("hlp_cmd_loot_flag", "Set the flags for the items in your loot");
		addMSG("hlp_cmd_loot_save", "Save your loot");
		addMSG("hlp_cmd_about", "Show information about the plugin");
	}

	private void setupRandomMessages() {
		// Available prefixes
		// standart_
		addMSG("msg_standart_1", "Устал");
		addMSG("msg_standart_2", "Увидел бабочку :)");
		addMSG("msg_standart_3", "Услышал странные звуки");
		addMSG("msg_standart_4", "Укусил комар =(");
		addMSG("msg_standart_5", "Во рту пересохло");
		// beach_
		addMSG("msg_beach_1", "Можно и позагарать немного");
		addMSG("msg_beach_2", "Пляжный сезон. Даже тень бросить негде");
		// desert_
		addMSG("msg_desert_1", "Залюбовавшись миражом, прозевал оазис");
		addMSG("msg_desert_2", "Одиночество, это не один снаружи, это пустыня внутри");
		addMSG("msg_desert_3", "Легче всего место под солнцем уступается в пустыне");
		addMSG("msg_desert_4", "Если не можешь превратить пустыню в оазис, преврати ее в мираж");
		// hill_
		addMSG("msg_hill_1", "А тут высоко");
		addMSG("msg_hill_2", "Дует сильный ветер");
		addMSG("msg_hill_3", "Главное не оступиться");
		addMSG("msg_hill_4", "Чуть не упал, фуух");
		// extrimehill_
		addMSG("msg_extrimehill_1", "Тебе мешают не те горы впереди, на которые ты должен взобраться, тебе мешает камушек в твоих ботинках");
		addMSG("msg_extrimehill_2", "Где гора - там и долина");
		// forest_
		addMSG("msg_forest_1", "Что самое главное в лесу? — Туалетная бумага. Особенно в хвойном");
		addMSG("msg_forest_2", "Чем больше дров, тем реже лес");
		// frozen_
		addMSG("msg_frozen_1", "Судя по нашим дорогам, закончилась не зима, а война");
		addMSG("msg_frozen_2", "Зима настала, холодно стало");
		// hell_
		addMSG("msg_hell_1", "Жууть!");
		addMSG("msg_hell_2", "Тут очень страшно");
		addMSG("msg_hell_3", "Я бы не хотел здесь погибнуть");
		// ice_
		addMSG("msg_ice_1", "Холодновато что-то стало =(");
		addMSG("msg_ice_2", "Чуть не поскользнулся");
		addMSG("msg_ice_3", "Сейчас бы в снежки поиграть ;)");
		addMSG("msg_ice_4", "Найти бы шубку получше");
		// jungle_
		addMSG("msg_jungle_1", "Каждому Маугли свои джунгли!");
		addMSG("msg_jungle_2", "Ты нарушил закон джунглей?!");
		addMSG("msg_jungle_3", "В джунглях законов расцветает закон джунглей");
		addMSG("msg_jungle_4", "Интуиция - это компас в житейских джунглях");
		// mushroom_
		addMSG("msg_mushroom_1", "Хороший гриб нужно еще поискать, а вот поганка сама выставляет себя напоказ");
		addMSG("msg_mushroom_2", "Желаете СОЛЁНЫХ грибов - посолИте...");
		addMSG("msg_mushroom_3", "Грибы — они разные. Один тебя накормит, другой — кино покажет");
		addMSG("msg_mushroom_4", "Глядя на грибы, думаешь: трудно остаться чистым, если не ядовит");
		// ocean_
		addMSG("msg_ocean_1", "Сейчас бы порыбачить...");
		addMSG("msg_ocean_2", "Хочу стать таким же, как Джек Воробей");
		addMSG("msg_ocean_3", "А акул всё же боюсь");
		addMSG("msg_ocean_4", "Интересно, я умею плавать?");
		// plains_
		addMSG("msg_plains_1", "Что-то в этом есть");
		addMSG("msg_plains_2", "Видел какие-то развалины. Можно было бы и сходить к ним");
		addMSG("msg_plains_3", "Трава шелестит");
		addMSG("msg_plains_4", "Услышал чей-то голос");
		addMSG("msg_plains_5", "Хачется немного поесть");
		// river_
		addMSG("msg_river_1", "Там, где пересыхает источник радости, исчезает река счастья...");
		addMSG("msg_river_2", "Жизнь — это поток, это река: её настроения постоянно меняются");
		addMSG("msg_river_3", "Любовь, как река, в которую невозможно войти дважды…");
		addMSG("msg_river_4", "В одну и ту же реку нельзя войти дважды, но можно не выходить из этой реки");
		// sky_
		addMSG("msg_sky_1", "Предел для меня — только небо");
		addMSG("msg_sky_2", "А сейчас небо синее-синее, в нем пасутся овечки облаков, сделанные из сахарной ваты");
		addMSG("msg_sky_3", "Пальцами небо трогать не стоит — остаются пятна");
		addMSG("msg_sky_4", "Для некоторых только небо — предел. А кое-кого даже небо не остановит");
		// smallmountain_
		addMSG("msg_smallmountain_1", "Не так уж тут и высоко");
		addMSG("msg_smallmountain_1", "А снизу казалось выше");
		addMSG("msg_smallmountain_1", "Сделать бы тут пещерку");
		addMSG("msg_smallmountain_1", "Надо идти дальше...");
		// swampland_
		addMSG("msg_swampland_1", "Цели нужно достигать, шагая по дороге, а не в обход по болотам и по уши в грязи");
		addMSG("msg_swampland_2", "В тишине, да не в болоте.");
		addMSG("msg_swampland_3", "Тишь, да гладь");
		addMSG("msg_swampland_4", "Плыть по течению можно… Если только ты не в болоте");
		// taiga_
		addMSG("msg_taiga_1", "Эта тайга бесконечна =(");
		addMSG("msg_taiga_1", "Главное не заблудиться");
		addMSG("msg_taiga_1", "Видел оленя, но он быстро скрылся из виду");

	}

	public static Integer getBiomeMessageCount(String prefix) {
		String list = FGU.getMsglist();
		Pattern p = Pattern.compile(prefix);
		Matcher m = p.matcher(list);
		int count = 0;
		while (m.find()) {
			count += 1;
		}
		return count;
	}

	public static String BiomeMessages(Biome b) {
		switch (b.toString()) {
		case "BEACH":
			return "beach_";
		case "DESERT":
			return "desert_";
		case "DESERT_HILLS":
			return "hill_";
		case "EXTREME_HILLS":
			return "extrimehill_";
		case "FOREST":
			return "forest_";
		case "FOREST_HILLS":
			return "hill_";
		case "FROZEN_OCEAN":
			return "frozen_";
		case "FROZEN_RIVER":
			return "frozen_";
		case "HELL":
			return "hell_";
		case "ICE_MOUNTAINS":
			return "ice_";
		case "ICE_PLAINS":
			return "ice_";
		case "JUNGLE":
			return "jungle_";
		case "JUNGLE_HILLS":
			return "hill_";
		case "MUSHROOM_ISLAND":
			return "mushroom_";
		case "MUSHROOM_SHORE":
			return "mushroom_";
		case "OCEAN":
			return "ocean_";
		case "PLAINS":
			return "plains_";
		case "RIVER":
			return "river_";
		case "SKY":
			return "sky_";
		case "SMALL_MOUNTAINS":
			return "smallmountain_";
		case "SWAMPLAND":
			return "swampland_";
		case "TAIGA":
			return "taiga_";
		case "TAIGA_HILLS":
			return "hill_";
		}
		return "";
	}

	public void init() {
		if (isSqlWrapper()) {
			new DBInitLite();
			Database db = DBInitLite.DATABASE;
			if (db.open()) {
				SQLPlayer.createTables();
				SQLPlayer.initPrep();
				SQLChest.createTables();
				SQLChest.initPrep();

				PlayerManager.init();
				ChestManager.init();
			} else
				Nodes.MYSQL_DBWRAPPER.setValue("none");
		}

		playerCfg = new PlayerConfig(plg);
		lootCfg = new LootConfig(plg);
		chestCfg = new ChestConfig(plg);
		regionCfg = new RegionConfig(plg);
		spawnCfg = new SpawnConfig(plg);

	}

	public static SpawnConfig getSpawnCfg() {
		return spawnCfg;
	}

	public static LootConfig getLC() {
		return lootCfg;
	}

	public static RegionConfig getRC() {
		return regionCfg;
	}

	public static ChestConfig getCC() {
		return chestCfg;
	}

	public static PlayerConfig getPC() {
		return playerCfg;
	}

	public static boolean isSqlWrapper() {
		String wrapper = Nodes.MYSQL_DBWRAPPER.getString();
		return DarkDays.isSQLbrary() && (wrapper.equalsIgnoreCase("mysql") || wrapper.equalsIgnoreCase("sqlite") || wrapper.equalsIgnoreCase("h2"));
	}

	public static void extract(String[] names) {
		for (String name : names) {
			File actual = new File(DarkDays.getDataPath(), name);
			if (!actual.exists()) {
				InputStream input = DarkDays.class.getResourceAsStream("/" + name);
				if (input != null) {
					FileOutputStream output = null;
					try {
						output = new FileOutputStream(actual);
						byte[] buf = new byte[8192];
						int length = 0;
						while ((length = input.read(buf)) > 0) {
							output.write(buf, 0, length);
						}
					} catch (Exception e) {
					} finally {
						try {
							if (input != null)
								input.close();
						} catch (Exception e) {
						}
						try {
							if (output != null)
								output.close();
						} catch (Exception e) {
						}
					}
				}
			}
		}
	}

	public static void load(File file) {
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);
		for (Nodes n : Nodes.values())
			if (!n.getNode().isEmpty())
				if (config.get(n.getNode()) != null)
					n.setValue(config.get(n.getNode()));
	}

	public static void save(File file) {
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);
		for (Nodes n : Nodes.values())
			if (!n.getNode().isEmpty())
				config.set(n.getNode(), n.getValue());
		try {
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
