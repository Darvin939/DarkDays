package darvin939.DarkDays.Commands.Handlers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionType;

import darvin939.DarkDays.DarkDays;
import darvin939.DarkDays.Commands.Handler;
import darvin939.DarkDays.Commands.InvalidUsage;
import darvin939.DarkDays.Configuration.Config;
import darvin939.DarkDays.Loot.Data;
import darvin939.DarkDays.Loot.ItemData;
import darvin939.DarkDays.Loot.LootManager;
import darvin939.DarkDays.Loot.PotionData;
import darvin939.DarkDays.Utils.Util;
import darvin939.DeprecAPI.ItemAPI;

public class Loot extends Handler {

	private String[] args;
	private Player p;
	private Map<Player, Data> nameOfLoot = new HashMap<Player, Data>();

	public Loot(DarkDays plugin) {
		super(plugin);
	}

	@Override
	public boolean perform(CommandSender s, String[] args) throws InvalidUsage {
		this.args = args;
		if (s instanceof Player) {
			p = (Player) s;
			if (args.length > 1) {
				if (args[1].equalsIgnoreCase("new")) {
					if (hasPermissions(p, "loot.new", true))
						newLoot();
					return true;
				}
				if (args[1].equalsIgnoreCase("remove")) {
					if (hasPermissions(p, "loot.remove", true))
						remove();
					return true;
				}
				if (args[1].equalsIgnoreCase("help")) {
					getHelp(p, "loot");
					return true;
				}
				if (args[1].equalsIgnoreCase("list")) {
					if (hasPermissions(p, "loot.list", true))
						list();
					return true;
				}
				if (args[1].equalsIgnoreCase("item") || args[1].equalsIgnoreCase("flag") || args[1].equalsIgnoreCase("save") || args[1].equalsIgnoreCase("durability"))
					if (nameOfLoot.containsKey(p)) {
						if (args[1].equalsIgnoreCase("item")) {
							if (hasPermissions(p, "loot.item", true))
								item();
							return true;
						}
						if (args[1].equalsIgnoreCase("flag")) {
							if (hasPermissions(p, "loot.falg", true))
								flag();
							return true;
						}
						if (args[1].equalsIgnoreCase("save")) {
							if (hasPermissions(p, "loot.save", true))
								save();
							return true;
						}
						if (args[1].equalsIgnoreCase("durability")) {
							if (hasPermissions(p, "loot.durability", true))
								durability();
							return true;
						}
					} else {
						Util.PrintMSGPx(p, "loot_new_isempty");
						return true;
					}
				Util.unknownCmd(p, getClass(), new String[] { args[1], "new", "item", "flag", "save", "list", "remove", "durability" });
				return true;
			} else {
				getHelp(p, "loot");

			}
			return true;
		}
		s.sendMessage("You must be a Player to do this");
		return true;
	}

	private void save() {
		Data data = nameOfLoot.get(p);
		FileConfiguration cfg = Config.getLC().getCfg();
		ConfigurationSection section = cfg.createSection(Util.FCTU(data.getName())).createSection("items");
		for (Entry<Material, ItemData> s : data.getItems().entrySet()) {
			ConfigurationSection sec = section.createSection(String.valueOf(ItemAPI.get(s.getKey()).id()));
			if (s.getValue().getSpawn().isEmpty())
				Util.PrintMSG(p, "loot_flag_spawnnf", "item " + ItemAPI.get(s.getKey()).id());
			sec.set("spawn", s.getValue().getSpawn());
			sec.set("durability", s.getValue().getDurability());
			sec.set("effects", Arrays.asList(s.getValue().getEffect().split(";")));
		}
		PotionData potion = data.getPotion();
		if (potion != null)
			if (!potion.getSpawn().isEmpty() && !potion.getEffect().isEmpty()) {
				ConfigurationSection sec = section.createSection("potion");
				sec.set("spawn", potion.getSpawn());
				sec.set("effects", Arrays.asList(potion.getEffect().split(";")));
			} else
				Util.PrintMSG(p, "loot_flag_spawnnf", "Potion");
		Util.PrintMSG(p, "loot_save", Util.FCTU(data.getName()));
		Config.getLC().saveConfig();
	}

	private void durability() {
		Data data = nameOfLoot.get(p);
		String[] nargs = Util.newArgs(args);
		if (nargs.length > 1) {
			Integer dur = 0;
			if (LootManager.getMaterial(nargs[1]) != null && data.getItems().containsKey(LootManager.getMaterial(nargs[1]))) {
				if (nargs.length == 3) {
					try {
						dur = Integer.parseInt(nargs[2]);
						if (dur != 0 && dur > 0 && dur <= 100) {
							data.getItem(LootManager.getMaterial(nargs[1])).setDurability(dur);
							Util.PrintMSG(p, "loot_durability_set", nargs[1] + ";" + dur);
							return;
						}
					} catch (NumberFormatException e) {

					}
					Util.PrintMSGPx(p, "loot_durability_isEmpty");

				} else
					Util.PrintMSGPx(p, "loot_durability_isEmpty");
				return;
			}
			Util.PrintMSGPx(p, "loot_durability_nf");
		} else
			getHelp(p, "loot.durability");
	}

	private void flag() {
		Data data = nameOfLoot.get(p);
		String[] nargs = Util.newArgs(args);
		if (nargs.length > 1) {
			String spawn = "";
			String effects = "";
			if (nargs[1].equalsIgnoreCase("potion")) {
				if (nargs.length == 3) {
					if (nargs[2].startsWith("spawn="))
						spawn = spawnParser(nargs[2]);
					if (nargs[2].startsWith("effect="))
						effects = potionEffectParser(nargs[2]);
				}
				if (nargs.length == 4) {
					for (int i = 2; i < 4; i++) {
						if (nargs[i].startsWith("spawn="))
							spawn = spawnParser(nargs[i]);
						if (nargs[i].startsWith("effect="))
							effects = potionEffectParser(nargs[i]);
					}
				}
				if (spawn.isEmpty() && effects.isEmpty()) {
					Util.PrintMSGPx(p, "loot_flag_potion_isEmpty");
					return;
				}
				if (!spawn.isEmpty())
					data.setPotion("spawn", spawn);
				if (!effects.isEmpty())
					data.setPotion("effects", effects);
				Util.PrintMSG(p, "loot_flag_set", "Potion");
				return;
			}
			if (LootManager.getMaterial(nargs[1]) != null && data.getItems().containsKey(LootManager.getMaterial(nargs[1]))) {
				if (nargs.length == 3) {
					if (nargs[2].startsWith("spawn="))
						spawn = spawnParser(nargs[2]);
					if (nargs[2].startsWith("effect="))
						effects = itemEffectParser(nargs[2]);
				}
				if (nargs.length == 4) {
					for (int i = 2; i < 4; i++) {
						if (nargs[i].startsWith("spawn="))
							spawn = spawnParser(nargs[i]);
						if (nargs[i].startsWith("effect="))
							effects = itemEffectParser(nargs[i]);
					}
				}
				if (spawn.isEmpty() && effects.isEmpty()) {
					Util.PrintMSGPx(p, "loot_flag_item_isEmpty");
					return;
				}
				if (data.getItem(LootManager.getMaterial(nargs[1])) != null) {
					if (!spawn.isEmpty())
						data.getItem(LootManager.getMaterial(nargs[1])).setSpawn(spawn);
					if (!effects.isEmpty())
						data.getItem(LootManager.getMaterial(nargs[1])).setEffect(effects);
					Util.PrintMSG(p, "loot_flag_set", nargs[1]);
				}
				return;
			}
			Util.PrintMSGPx(p, "loot_flag_nf");
		} else
			getHelp(p, "loot.flag");
	}

	private String potionEffectParser(String arg) {
		int counter = 0;
		String e = arg.replace("effect=", "");
		String[] effects = e.split(";");
		for (String effect : effects) {
			Boolean b1 = false, b2 = false, b3 = false;
			String[] args = effect.split(",");
			if (args.length == 4) {
				if (PotionType.valueOf(args[0].toUpperCase()) != null)
					b1 = true;
				String[] range = args[1].split("-");
				if (range.length > 1)
					try {
						int min = Integer.parseInt(range[0]);
						int max = Integer.parseInt(range[1]);
						if (min <= max)
							b2 = true;
					} catch (NumberFormatException e1) {
					}
				if ((args[2].equalsIgnoreCase("true") || args[2].equalsIgnoreCase("false")) && (args[3].equalsIgnoreCase("true") || args[3].equalsIgnoreCase("false")))
					b3 = true;
			}
			if (b1 && b2 && b3)
				counter++;
		}
		if (counter == effects.length)
			return e;
		Util.PrintMSG(p, "loot_parser", "Potion ffect");
		return "";
	}

	private String itemEffectParser(String arg) {
		int counter = 0;
		String e = arg.replace("effect=", "");
		String[] effects = e.split(";");
		for (String effect : effects) {
			Boolean b1 = false, b2 = false, b3 = false;
			String[] args = effect.split(",");
			if (args.length == 3) {
				if (Enchantment.getByName(args[0].toUpperCase()) != null)
					b1 = true;
				String[] range = args[1].split("-");
				if (range.length > 1)
					try {
						int min = Integer.parseInt(range[0]);
						int max = Integer.parseInt(range[1]);
						if (min <= max)
							b2 = true;
					} catch (NumberFormatException e1) {
					}
				if (Util.isInteger(args[2])) {
					Integer i1 = Integer.parseInt(args[2]);
					if (i1 >= 0 && i1 <= 100)
						b3 = true;
				}
			}
			if (b1 && b2 && b3)
				counter++;
		}
		if (counter == effects.length)
			return e;
		Util.PrintMSG(p, "loot_parser", "Item effect");
		return "";
	}

	private String spawnParser(String arg) {
		Boolean b1 = false, b2 = false;
		String s = arg.replace("spawn=", "");
		String[] args = s.split(",");
		if (args.length == 2) {
			if (Util.isInteger(args[0])) {
				Integer i1 = Integer.parseInt(args[0]);
				if (i1 >= 0 && i1 <= 100)
					b1 = true;
			}
			String[] range = args[1].split("-");
			if (range.length > 1)
				try {
					int min = Integer.parseInt(range[0]);
					int max = Integer.parseInt(range[1]);
					if (min <= max)
						b2 = true;
				} catch (NumberFormatException e1) {
				}
		}
		if (b1 && b2)
			return s;
		Util.PrintMSG(p, "loot_parser", "Spawn");
		return "";
	}

	private void item() {
		String[] nargs = Util.newArgs(args);
		if (nargs.length > 1) {
			String items = "";
			String[] i = nargs[1].split(",");
			for (String item : i) {
				if (LootManager.getMaterial(item) != null) {
					nameOfLoot.get(p).addItem(LootManager.getMaterial(item), new ItemData("", "", 0));
					items = items.isEmpty() ? "&2" + item + "&f" : items + ", &2" + item + "&f";
				}
			}
			Util.PrintMSG(p, "loot_item_add", items);
		} else
			getHelp(p, "loot.item");
	}

	private void list() {
		Util.Print(p, "&b==================== &2LootList &b===================");
		for (String list : Config.getLC().getCfg().getKeys(false)) {
			List<String> itemList = LootManager.getItemList(list);
			String items = "";
			String potions = "";
			for (int i = 0; i < itemList.size(); i++) {
				String item = itemList.get(i);
				String[] itemData = item.split("\\|");
				Material material = LootManager.getMaterial(itemData[0]);

				if (material.equals(Material.POTION))
					potions = getPotions(new ArrayList<String>(LootManager.getEffects(list, item)));
				else {
					items = items.isEmpty() ? material.name() : items + ", " + material.name();
				}
			}
			Util.Print(p, "&2" + Util.FCTU(list) + ":");
			Util.Print(p, " &7Items: &6" + (items.isEmpty() ? "None" : items));
			Util.Print(p, " &7Potions: &6" + (potions.isEmpty() ? "None" : potions));
		}
	}

	private String getPotions(ArrayList<String> list) {
		String potions = "";
		for (int i = 0; i < list.size(); i++) {
			String[] properties = ((String) list.get(i)).split(",");
			potions = potions.isEmpty() ? Util.FCTU(properties[0]) : potions + ", " + Util.FCTU(properties[0]);
		}
		return potions;
	}

	private void remove() {
		String[] nargs = Util.newArgs(args);
		if (nargs.length > 1) {
			for (String list : Config.getLC().getCfg().getKeys(false)) {
				if (list.equalsIgnoreCase(nargs[1])) {
					Config.getLC().getCfg().set(Util.FCTU(nargs[1].toLowerCase()), null);
					Config.getLC().saveConfig();
					Util.PrintMSG(p, "loot_remove", Util.FCTU(nargs[1].toLowerCase()));
				}
			}
		} else
			getHelp(p, "loot.remove");
	}

	private void newLoot() {
		String[] nargs = Util.newArgs(args);
		if (nargs.length > 1) {
			nameOfLoot.put(p, new Data(nargs[1].toLowerCase()));
			Util.PrintMSG(p, "loot_new", Util.FCTU(nargs[1].toLowerCase()) + ";" + nargs[1].toLowerCase());
		} else
			getHelp(p, "loot.new");
	}

}
