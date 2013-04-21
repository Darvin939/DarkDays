package darvin939.DarkDays.Commands.Handlers;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.block.Chest;

import darvin939.DarkDays.DarkDays;
import darvin939.DarkDays.LootManager;
import darvin939.DarkDays.Commands.Handler;
import darvin939.DarkDays.Commands.InvalidUsage;
import darvin939.DarkDays.Configuration.Config;
import darvin939.DarkDays.Utils.Util;

public class Chests extends Handler {

	private String[] args;
	private Player p;

	public Chests(DarkDays plugin) {
		super(plugin);
	}

	@Override
	public boolean perform(Player p, String[] args) throws InvalidUsage {
		this.args = args;
		this.p = p;
		if (args.length > 1) {
			if (args[1].equalsIgnoreCase("help")) {
				getHelp(p, "chest");
				return true;
			}
			if (args[1].equalsIgnoreCase("add")) {
				if (hasPermissions(p, "chest.add", true))
					add();
				return true;
			}
			if (args[1].equalsIgnoreCase("set")) {
				if (hasPermissions(p, "chest.set", true))
					set();
				return true;
			}
			if (args[1].equalsIgnoreCase("remove")) {
				if (hasPermissions(p, "chest.remove", true))
					remove();
				return true;
			}
		} else {
			if (p.getTargetBlock(null, 10).getType() == Material.CHEST && hasPermissions(p, "chest", false))
				if (Config.getCC().getChestInfo(p, p.getTargetBlock(null, 10).getLocation())) {
				} else
					Util.PrintPxMSG(p, "chest_normal");
			return true;
		}
		return false;
	}

	private void remove() {
		String[] nargs = Util.newArgs(args);
		if (nargs.length > 1) {
			if (nargs[1].equalsIgnoreCase("help"))
				getHelp(p, "chest.remove");
		} else {
			if (p.getTargetBlock(null, 10).getType() == Material.CHEST) {
				if (Config.getCC().isChest(p) != null) {
					Location chestloc = p.getTargetBlock(null, 10).getLocation();
					Config.getCC().removeChest(chestloc);
					if (chestloc.getBlock().getState() instanceof Chest) {
						((Chest) chestloc.getBlock().getState()).getInventory().clear();
						chestloc.getBlock().setType(Material.AIR);
						Util.PrintPxMSG(p, "chest_remove");
					}
				} else
					Util.PrintPxMSG(p, "chest_normal");
			}
		}
	}

	private void add() {
		String[] nargs = Util.newArgs(args);
		Location chestloc = p.getTargetBlock(null, 10).getLocation();
		chestloc.setY(chestloc.getY() + 1);
		if (nargs.length > 1) {
			if (nargs[1].equalsIgnoreCase("help"))
				getHelp(p, "chest.add");
			else {
				Config.getCC().addChest(chestloc);
				chestloc.getBlock().setType(Material.CHEST);
				boolean find = false;
				for (String list : Config.getLC().getCfg().getKeys(false))
					if (nargs[1].equalsIgnoreCase(list)) {
						find = true;
						Util.PrintPxMSG(p, "chest_newWithID");
						LootManager.setLoot(p, Util.FCTU(nargs[1]));
						break;
					}
				if (!find) {
					Util.PrintPxMSG(p, "loot_nf");
					Util.PrintPxMSG(p, "chest_new");
				}
			}
		} else {
			Config.getCC().addChest(chestloc);
			chestloc.getBlock().setType(Material.CHEST);
			Util.PrintPxMSG(p, "chest_new");
		}
	}

	private void set() {
		String[] nargs = Util.newArgs(args);
		if (nargs.length > 1) {
			if (Config.getCC().isChest(p)) {
				for (String list : Config.getLC().getCfg().getKeys(false))
					if (nargs[1].equalsIgnoreCase(list)) {
						LootManager.setLoot(p, Util.FCTU(nargs[1]));
						return;
					}
				Util.PrintPxMSG(p, "loot_nf");
			}
		} else
			getHelp(p, "chest.set");
	}
}
