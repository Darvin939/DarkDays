package darvin939.DarkDays.Commands.Handlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import darvin939.DarkDays.DarkDays;
import darvin939.DarkDays.LootManager;
import darvin939.DarkDays.Commands.Handler;
import darvin939.DarkDays.Commands.InvalidUsage;
import darvin939.DarkDays.Configuration.Config;
import darvin939.DarkDays.Utils.Util;

public class Loot extends Handler {

	private String[] args;
	private Player p;
	private Map<Player, String> nameOfLoot = new HashMap<Player, String>();

	public Loot(DarkDays plugin) {
		super(plugin);
	}

	@Override
	public boolean perform(Player p, String[] args) throws InvalidUsage {
		this.args = args;
		this.p = p;
		if (args.length > 1) {
			if (args[1].equalsIgnoreCase("new")) {
				if (hasPermissions(p, "chest.add", true))
					newLoot();
				return true;
			}
			if (args[1].equalsIgnoreCase("remove")) {
				if (hasPermissions(p, "chest.set", true))
					remove();
				return true;
			}
			if (args[1].equalsIgnoreCase("list")) {
				if (hasPermissions(p, "chest.remove", true))
					list();
				return true;
			}
			if (args[1].equalsIgnoreCase("item")) {
				if (hasPermissions(p, "chest.remove", true))
					item();
				return true;
			}
			if (args[1].equalsIgnoreCase("flag")) {
				if (hasPermissions(p, "chest.remove", true))
					flag();
				return true;
			}
			if (args[1].equalsIgnoreCase("save")) {
				if (hasPermissions(p, "chest.remove", true))
					save();
				return true;
			}
		} else {
			getHelp(p, "loot");
			return true;
		}
		return false;
	}

	private void save() {

	}

	private void flag() {

	}

	private void item() {

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
					Util.Print(p, "Loot " + Util.FCTU(nargs[1].toLowerCase()) + " successfully removed");
				}
			}
		} else
			getHelp(p, "loot.remove");
	}

	private void newLoot() {
		String[] nargs = Util.newArgs(args);
		if (nargs.length > 1) {
			nameOfLoot.put(p, nargs[1].toLowerCase());
			Util.Print(p, "A New loot " + Util.FCTU(nargs[1].toLowerCase()) + " created. Type /dd loot save " + nargs[1].toLowerCase() + " to save this loot");
		} else
			getHelp(p, "loot.new");
	}

}
