package darvin939.DarkDays.Listeners;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.kitteh.tag.PlayerReceiveNameTagEvent;
import org.kitteh.tag.TagAPI;

import darvin939.DarkDays.DarkDays;
import darvin939.DarkDays.Loot;
import darvin939.DarkDays.Tasks;
import darvin939.DarkDays.Configuration.Config;
import darvin939.DarkDays.Configuration.Config.Nodes;
import darvin939.DarkDays.Configuration.PC;
import darvin939.DarkDays.Loadable.Effect;
import darvin939.DarkDays.Loadable.EffectManager;
import darvin939.DarkDays.Players.Memory.PlayerInfo;
import darvin939.DarkDays.Players.Memory.PlayerZombie;
import darvin939.DarkDays.Utils.Util;

public class PlayerListener implements Listener {
	DarkDays plg;
	public static ArrayList<Player> ptag = new ArrayList<Player>();
	private static ThreadGroup rootThreadGroup = null;

	public PlayerListener(DarkDays plg) {
		this.plg = plg;
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onInventoryCloseEvent(InventoryCloseEvent event) {
		Player p = (Player) event.getPlayer();
		if (event.getInventory().getHolder() instanceof Chest && event.getPlayer() instanceof Player) {
			if (Config.getCC().isChest(p))
				if (Nodes.chest_disappear.getBoolean() && Config.getCC().getLoot(p.getTargetBlock(null, 10).getLocation()) != null) {
					Chest chest = (Chest) event.getInventory().getHolder();
					if (Loot.isChestEmpty(p.getTargetBlock(null, 10))) {
						Block block = p.getTargetBlock(null, 10);
						chest.getInventory().clear();
						Location chestloc = new Location(block.getWorld(), block.getLocation().getBlockX(), block.getLocation().getBlockY(), block.getLocation().getBlockZ());
						chestloc.getBlock().setType(Material.AIR);
					}
				}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player p = event.getPlayer();
		Config.FGU.UpdateMsg(p);
		Config.getPC().initialize(p);

		boolean novice = (boolean) Config.getPC().getData(p, PC.DEATH);
		boolean death = (boolean) Config.getPC().getData(p, PC.NOVICE);
		if (novice || death) {
			resetPlayer(p);
			toSpawn(p);
		}
		if ((boolean) Config.getPC().getData(p, PC.SPAWNED)) {
			Tasks.player_hunger.put(p, (int) Config.getPC().getData(p, PC.HUNGER));
			Tasks.player_noise.put(p, 1);
			PlayerInfo.addPlayer(p);
			p.teleport(PC.fix(p));

			setupEffects(p);
		}
		for (Player op : plg.getServer().getOnlinePlayers()) {
			if (!op.equals(p)) {
				TagAPI.refreshPlayer(op, p);
			}
		}
	}

	public void setupEffects(Player p) {
		for (Entry<Method, Object> set : DarkDays.getEffectManager().getRunMethods().entrySet()) {
			for (String effect : Config.getPC().getEffects(p)) {
				if (((Effect) set.getValue()).getName().equalsIgnoreCase(effect)) {
					try {
						EffectManager.addTaskID(p, effect, Integer.parseInt(String.valueOf(set.getKey().invoke(set.getValue(), p))));
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		Player p = event.getPlayer();
		// Config.getPC().initialize(p);
		boolean novice = (boolean) Config.getPC().getData(p, PC.DEATH);
		boolean death = (boolean) Config.getPC().getData(p, PC.NOVICE);
		if (novice || death) {
			resetPlayer(p);
			event.setRespawnLocation(toSpawn(p));
		}
		if ((boolean) Config.getPC().getData(p, PC.SPAWNED)) {
			Tasks.player_hunger.put(p, (int) Config.getPC().getData(p, PC.HUNGER));
			Tasks.player_noise.put(p, 1);
			PlayerInfo.addPlayer(p);
			event.setRespawnLocation(PC.fix(p));
			return;
		}
		p.getInventory().clear();
		for (Player op : plg.getServer().getOnlinePlayers()) {
			if (!op.equals(p)) {
				TagAPI.refreshPlayer(op, p);
			}
		}
	}

	private Location toSpawn(Player p) {
		int x, y, z;
		if (plg.getConfig().isConfigurationSection("Spawns.Lobby")) {
			ConfigurationSection section = plg.getConfig().getConfigurationSection("Spawns.Lobby");
			x = section.getInt("x");
			y = section.getInt("y");
			z = section.getInt("z");
			Location loc = new Location(p.getWorld(), x, y, z);
			p.teleport(loc);
			return loc;
		}
		return p.getWorld().getSpawnLocation();
	}

	// This method doesn't work with the plugin AdminCmd!
	private void resetPlayer(Player p) {
		Tasks.resetHashMaps(p);
		Config.getPC().setData(p, PC.DEATH, false);
		Config.getPC().setData(p, PC.SPAWNED, false);
		Config.getPC().setData(p, PC.NOVICE, true);
		p.getInventory().clear();
		p.getInventory().setHelmet(null);
		p.getInventory().setChestplate(null);
		p.getInventory().setLeggings(null);
		p.getInventory().setBoots(null);
		p.setLevel(0);
		p.setExp(0);
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onHeal(PlayerInteractEntityEvent event) {
		if ((boolean) Config.getPC().getData(event.getPlayer(), PC.SPAWNED)) {
			if (((event.getRightClicked() instanceof Player)) && (event.getPlayer().getItemInHand().getTypeId() == Nodes.bandage_id.getInteger())) {
				Player e = (Player) event.getRightClicked();
				Player p = event.getPlayer();
				if (PlayerInfo.isPlaying(p) && PlayerInfo.isPlaying(e)) {
					if (e.getHealth() < 20) {
						if (e.getHealth() <= 20 - Nodes.bandage_health.getInteger())
							e.setHealth(e.getHealth() + Nodes.bandage_health.getInteger());
						else {
							e.setHealth(20);
						}
						if (p.getItemInHand().getAmount() > 1)
							p.setItemInHand(new ItemStack(Material.getMaterial(Nodes.bandage_id.getInteger()), p.getItemInHand().getAmount() - 1));
						else {
							p.setItemInHand(new ItemStack(Material.AIR, 0));
						}
						PlayerInfo.addPlayerHeal(p);
						if (Nodes.coloured_tegs.getBoolean()) {
							TagAPI.refreshPlayer(p);
						}
					} else {
						// msg (health of event.getRightClicked() is full)
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onNameTag(PlayerReceiveNameTagEvent event) {
		if (Nodes.coloured_tegs.getBoolean()) {
			Player p = event.getNamedPlayer();
			String name = p.getDisplayName();
			if (PlayerInfo.getPlayerHeals(p) < PlayerInfo.getPlayerKills(p)) {
				event.setTag(ChatColor.RED.toString() + name);
				p.sendMessage("RED" + PlayerInfo.getPlayerHeals(p) + " - " + PlayerInfo.getPlayerKills(p));
			} else if (PlayerInfo.getPlayerHeals(p) > PlayerInfo.getPlayerKills(p)) {
				event.setTag(ChatColor.GREEN.toString() + name);
				p.sendMessage("GREEN" + PlayerInfo.getPlayerHeals(p) + " - " + PlayerInfo.getPlayerKills(p));
			} else
				event.setTag(name);
			for (Player op : plg.getServer().getOnlinePlayers()) {
				if (ptag.contains(op)) {
					event.setTag(name);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerKick(PlayerKickEvent event) {
		Player p = event.getPlayer();
		Config.getPC().setData(p, PC.HUNGER, Tasks.player_hunger.get(p));
		Tasks.removeFromHashMaps(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player p = event.getPlayer();
		if (Tasks.player_hunger.containsKey(p))
			Config.getPC().setData(p, PC.HUNGER, Tasks.player_hunger.get(p));
		else
			Config.getPC().setData(p, PC.HUNGER, 209999);
		Tasks.removeFromHashMaps(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerKill(EntityDamageByEntityEvent event) {
		if (Nodes.coloured_tegs.getBoolean())
			if ((event.getDamager() instanceof Player)) {
				Player d = (Player) event.getDamager();
				if ((event.getEntity() instanceof Player)) {
					Player h = (Player) event.getEntity();
					if (event.getDamage() > h.getHealth()) {
						PlayerInfo.addPlayerKill(d);
						TagAPI.refreshPlayer(d);
						Util.msg(d, "You killed " + h.getName() + "!", 'p');
					}
				}
			}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerDeath(PlayerDeathEvent event) {
		Player p = event.getEntity();
		Config.getPC().setData(p, PC.DEATH, true);
		if (event.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent) {
			EntityDamageByEntityEvent dEvent = (EntityDamageByEntityEvent) event.getEntity().getLastDamageCause();

			if (dEvent.getDamager() instanceof Player && PlayerInfo.isPlaying(p)) {
				Player d = (Player) dEvent.getDamager();
				PlayerInfo.addPlayerKill(d);
				TagAPI.refreshPlayer(d);
				Util.msg(d, "You killed " + p.getName() + "!", 'p');
			}
			if (dEvent.getDamager() instanceof Zombie) {
				Location loc = p.getLocation();
				ArrayList<ItemStack> items = new ArrayList<ItemStack>();
				LivingEntity datZombie = (LivingEntity) loc.getWorld().spawnEntity(loc, EntityType.ZOMBIE);
				ItemStack item[] = p.getInventory().getContents();
				for (int i = 0; i < item.length; i++) {
					items.add(item[i]);
				}
				p.setLevel(0);
				event.setDroppedExp(0);
				for (Player op : plg.getServer().getOnlinePlayers()) {
					if (!op.equals(p)) {
						TagAPI.refreshPlayer(op, p);
					}
				}
				PlayerZombie.add(datZombie, items);
			}

		}
		PlayerInfo.removePlayer(p);
		event.getDrops().clear();
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerMove(PlayerMoveEvent event) {
		if ((boolean) Config.getPC().getData(event.getPlayer(), PC.SPAWNED)) {
			Player p = event.getPlayer();
			if (!event.getFrom().toVector().equals(event.getTo().toVector())) {
				float lold = (float) (((Tasks.player_noise.get(p)).intValue() - 1) * Tasks.maxExp);
				int movX = event.getFrom().getBlockX() - event.getTo().getBlockX();
				int movZ = event.getFrom().getBlockZ() - event.getTo().getBlockZ();
				if (Math.abs(movX) > 0 || Math.abs(movZ) > 0) {
					if (p.isSprinting() && p.getLocation().getY() > (int) p.getLocation().getY()) {
						Tasks.depleteThirst(p, 6);
						Tasks.player_noise.put(p, 6);
					} else if (p.getLocation().getY() > (int) p.getLocation().getY()) {
						Tasks.depleteThirst(p, 5);
						Tasks.player_noise.put(p, 5);
					} else if (p.isSprinting()) {
						Tasks.depleteThirst(p, 4);
						Tasks.player_noise.put(p, 4);
					} else if (p.isSneaking()) {
						Tasks.depleteThirst(p, 2);
						Tasks.player_noise.put(p, 2);
					} else if (Math.abs(movX) > 0 || Math.abs(movZ) > 0) {
						Tasks.depleteThirst(p, 3);
						Tasks.player_noise.put(p, 3);
					}
					float lnew = (float) (((Tasks.player_noise.get(p)).intValue() - 1) * Tasks.maxExp);
					if (lnew != lold) {
						smoothExp(lold, lnew, p);
						if (p.getExp() == lnew && getThread(p.getName()) != null)
							PlayerListener.getThread(p.getName()).interrupt();
					}
				}
			}
		}
	}

	public static void smoothExp(final float lold, final float lnew, final Player p) {
		new Thread(p.getName()) {
			public void run() {
				try {
					float max = Math.max(lold, lnew);
					float min = Math.min(lold, lnew);
					float inc = (max - min) / 10;
					if (lold < lnew)
						for (int i = 1; i <= 10; i++, Thread.sleep(25)) {
							p.setExp(min += inc);
						}
					else if (lold > lnew)
						for (int i = 10; i >= 0; i--, Thread.sleep(25)) {
							p.setExp(max -= inc);
						}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}
		}.start();
	}

	public static Thread getThread(final String name) {
		if (name == null)
			throw new NullPointerException("Null name");
		final Thread[] threads = getAllThreads();
		for (Thread thread : threads)
			if (thread.getName().equals(name))
				return thread;
		return null;
	}

	private static Thread[] getAllThreads() {
		final ThreadGroup root = getRootThreadGroup();
		final ThreadMXBean thbean = ManagementFactory.getThreadMXBean();
		int nAlloc = thbean.getThreadCount();
		int n = 0;
		Thread[] threads;
		do {
			nAlloc *= 2;
			threads = new Thread[nAlloc];
			n = root.enumerate(threads, true);
		} while (n == nAlloc);
		return java.util.Arrays.copyOf(threads, n);
	}

	private static ThreadGroup getRootThreadGroup() {
		if (rootThreadGroup != null)
			return rootThreadGroup;
		ThreadGroup tg = Thread.currentThread().getThreadGroup();
		ThreadGroup ptg;
		while ((ptg = tg.getParent()) != null)
			tg = ptg;
		return tg;
	}
}
