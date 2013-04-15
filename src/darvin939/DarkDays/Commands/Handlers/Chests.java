package darvin939.DarkDays.Commands.Handlers;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.block.Chest;

import darvin939.DarkDays.DarkDays;
import darvin939.DarkDays.Loot;
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
			if (args[1].equalsIgnoreCase("loot")) {
				if (hasPermissions(p, "chest.loot", true))
					loot();
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
					Config.FGU.PrintPxMsg(p, Config.FGU.MSG("chest_normal"));
			return true;
		}
		return false;
	}

	private void remove() {
		String[] nargs = Util.newArgs(args);
		if (nargs.length > 1)
			getHelp(p, "chest.remove");
		else {
			if (p.getTargetBlock(null, 10).getType() == Material.CHEST) {
				if (Config.getCC().isChest(p) != null) {
					Location chestloc = p.getTargetBlock(null, 10).getLocation();
					Config.getCC().removeChest(chestloc);
					if (chestloc.getBlock().getState() instanceof Chest) {
						((Chest) chestloc.getBlock().getState()).getInventory().clear();
						chestloc.getBlock().setType(Material.AIR);
						Config.FGU.PrintPxMsg(p, Config.FGU.MSG("chest_remove"));
					}
				} else
					Config.FGU.PrintPxMsg(p, Config.FGU.MSG("chest_normal"));
			}
		}
	}

	public void add() {
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
						Config.FGU.PrintPxMsg(p, Config.FGU.MSG("chest_newWithID"));
						Loot.setLoot(p, Util.FCTU(nargs[1]));
						break;
					}
				if (!find) {
					Config.FGU.PrintPxMsg(p, Config.FGU.MSG("loot_nf"));
					Config.FGU.PrintPxMsg(p, Config.FGU.MSG("chest_new"));
				}
			}
		} else {
			Config.getCC().addChest(chestloc);
			chestloc.getBlock().setType(Material.CHEST);
			Config.FGU.PrintPxMsg(p, Config.FGU.MSG("chest_new"));
		}
	}

	public void loot() {
		String[] nargs = Util.newArgs(args);
		if (nargs.length > 1) {
			if (Config.getCC().isChest(p)) {
				if (nargs[1].equalsIgnoreCase("new")) {
					Config.FGU.PrintPxMsg(p, Config.FGU.MSG("cmd_indev"));
				}
				if (nargs[1].equalsIgnoreCase("set")) {
					if (nargs.length > 2) {
						for (String list : Config.getLC().getCfg().getKeys(false))
							if (nargs[2].equalsIgnoreCase(list)) {
								Loot.setLoot(p, Util.FCTU(nargs[2]));
								return;
							}
						Config.FGU.PrintPxMsg(p, Config.FGU.MSG("loot_nf"));
					}
				}
			}
			if (nargs[1].equalsIgnoreCase("help")) {
				getHelp(p, "chest.loot");
			}
		}
	}
}
