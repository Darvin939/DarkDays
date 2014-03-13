package darvin939.DarkDays.Commands.Handlers;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.block.Chest;
import org.bukkit.command.CommandSender;

import darvin939.DarkDays.DarkDays;
import darvin939.DarkDays.Commands.Handler;
import darvin939.DarkDays.Commands.InvalidUsage;
import darvin939.DarkDays.Configuration.Config;
import darvin939.DarkDays.Loot.LootManager;
import darvin939.DarkDays.Utils.Util;
import darvin939.DeprecAPI.BlockAPI;

public class Chests extends Handler {

	private String[] args;
	private Player p;

	public Chests(DarkDays plugin) {
		super(plugin);
	}

	@Override
	public boolean perform(CommandSender s, String[] args) throws InvalidUsage {
		this.args = args;
		if (s instanceof Player) {
			p = (Player) s;
			if (args.length > 1) {
				if (args[1].equalsIgnoreCase("help")) {
					getHelp(p, "chest");
					return true;
				}
				if (args[1].equalsIgnoreCase("add")) {
					if (hasPermission(p, "chest.add", true))
						add();
					return true;
				}
				if (args[1].equalsIgnoreCase("set")) {
					if (hasPermission(p, "chest.set", true))
						set();
					return true;
				}
				if (args[1].equalsIgnoreCase("remove")) {
					if (hasPermission(p, "chest.remove", true))
						remove();
					return true;
				}
				Util.unknownCmd(p, "chest", new String[] { args[1], "add", "set", "remove", "help" });
				return true;
			} else {
				if (BlockAPI.getTargetBlock(p, 10).getType() == Material.CHEST && hasPermission(p, "chest", false))
					if (!Config.getCC().getChestInfo(p, BlockAPI.getTargetBlock(p, 10).getLocation()))
						Util.PrintMSGPx(p, "chest_normal");
					else {

					}

			}
			return true;
		}
		s.sendMessage("You must be a Player to do this");
		return true;
	}

	private void remove() {
		String[] nargs = Util.newArgs(args);
		if (nargs.length > 1) {
			if (nargs[1].equalsIgnoreCase("help"))
				getHelp(p, "chest.remove");
		} else {
			if (BlockAPI.getTargetBlock(p, 10).getType() == Material.CHEST) {
				if (Config.getCC().isChest(p) != null) {
					Location chestloc = BlockAPI.getTargetBlock(p, 10).getLocation();
					Config.getCC().removeChest(chestloc);
					if (chestloc.getBlock().getState() instanceof Chest) {
						((Chest) chestloc.getBlock().getState()).getInventory().clear();
						chestloc.getBlock().setType(Material.AIR);
						Util.PrintMSGPx(p, "chest_remove");
					}
				} else
					Util.PrintMSGPx(p, "chest_normal");
			}
		}
	}

	// есть баг с присвоением лута
	private void add() {
		String[] nargs = Util.newArgs(args);
		Location chestloc = BlockAPI.getTargetBlock(p, 10).getLocation();
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
						Util.PrintMSGPx(p, "chest_newWithID");
						LootManager.setLoot(p, Util.FCTU(nargs[1]));
						break;
					}
				if (!find) {
					Util.PrintMSGPx(p, "loot_nf");
					Util.PrintMSGPx(p, "chest_new");
				}
			}
		} else {
			Config.getCC().addChest(chestloc);
			chestloc.getBlock().setType(Material.CHEST);
			Util.PrintMSGPx(p, "chest_new");
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
				Util.PrintMSGPx(p, "loot_nf");
			}
		} else
			getHelp(p, "chest.set");
	}
}
