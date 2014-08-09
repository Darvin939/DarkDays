package darvin939.DarkDays.Configuration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import darvin939.DarkDays.DarkDays;
import darvin939.DarkDays.SQL.Chests.ChestManager;
import darvin939.DarkDays.Utils.Util;
import darvin939.DeprecAPI.BlockAPI;

public class ChestConfig {

   public FileConfiguration cfgChest;
   private File cfgChestFile;
	private HashMap<Location, String> chests = new HashMap<Location, String>();


   public ChestConfig(DarkDays plg) {
      if(!Config.isSqlWrapper()) {
         this.cfgChestFile = new File(plg.getDataFolder() + "/chests.yml");
         this.cfgChest = YamlConfiguration.loadConfiguration(this.cfgChestFile);
         this.saveConfig();
      }

   }

   public void saveConfig() {
      try {
         this.cfgChest.save(this.cfgChestFile);
      } catch (IOException var2) {
         var2.printStackTrace();
      }

   }

   public void addChest(Location loc) {
      this.addChest(loc, "");
   }

	public void saveAll() {
		if (Config.isSqlWrapper()) {
			ChestManager cm = ChestManager.getInstance();
			for (Entry<Location, String> set : chests.entrySet())
				cm.getChest(set.getKey()).addLoot(Config.getLC().getCfg().contains(Util.FCTU(set.getValue())) ? set.getValue() : "");
		} else {
			ConfigurationSection s;
			for (Entry<Location, String> set : chests.entrySet()) {
				if (!cfgChest.isConfigurationSection("Chest-" + set.getKey().getWorld().getName() + "," + set.getKey().getBlockX() + "," + set.getKey().getBlockY() + "," + set.getKey().getBlockZ()))
					s = cfgChest.createSection("Chest-" + set.getKey().getWorld().getName() + "," + set.getKey().getBlockX() + "," + set.getKey().getBlockY() + "," + set.getKey().getBlockZ());
				else
					s = cfgChest.getConfigurationSection("Chest-" + set.getKey().getWorld().getName() + "," + set.getKey().getBlockX() + "," + set.getKey().getBlockY() + "," + set.getKey().getBlockZ());
				s.set("Location", set.getKey().getBlockX() + "," + set.getKey().getBlockY() + "," + set.getKey().getBlockZ());
				s.set("World", set.getKey().getWorld().getName());
				s.set("LootID", Config.getLC().getCfg().contains(Util.FCTU(set.getValue())) ? set.getValue() : "");
			}
			saveConfig();
		}
	}

	public void loadChests() {
		if (Config.isSqlWrapper()) {
			ChestManager cm = ChestManager.getInstance();
			for (Entry<Location, Long> set : cm.getChestsID().entrySet())
				chests.put(set.getKey(), cm.addChest(set.getKey()).getLoot());
		} else {
			for (String section : cfgChest.getKeys(false))
				if (getChestLoc(section) != null)
					chests.put(getChestLoc(section), cfgChest.getString(section + ".LootID", ""));
		}
	}

   private Location getChestLoc(String section) {
      if(this.cfgChest.isConfigurationSection(section)) {
         String sloc = this.cfgChest.getString(section + ".Location");
         String[] coords = sloc.split(",");
         double x = Double.valueOf(coords[0]).doubleValue();
         double y = Double.valueOf(coords[1]).doubleValue();
         double z = Double.valueOf(coords[2]).doubleValue();
         World world = Bukkit.getWorld(this.cfgChest.getString(section + ".World"));
         if(world != null) {
            return new Location(world, x, y, z);
         }
      }

      return null;
   }

   public void addChest(Location loc, String loot_id) {
      if(Config.isSqlWrapper()) {
         ChestManager.getInstance().addChest(loc).addLoot(loot_id);
      } else {
         ConfigurationSection s;
         if(!this.cfgChest.isConfigurationSection("Chest-" + loc.getWorld().getName() + "," + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ())) {
            s = this.cfgChest.createSection("Chest-" + loc.getWorld().getName() + "," + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ());
         } else {
            s = this.cfgChest.getConfigurationSection("Chest-" + loc.getWorld().getName() + "," + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ());
         }

         s.set("Location", loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ());
         s.set("World", loc.getWorld().getName());
         s.set("LootID", loot_id);
         this.saveConfig();
      }

      this.chests.put(loc, loot_id);
   }

   public void removeChest(Location loc) {
      if(Config.isSqlWrapper()) {
         ChestManager.getInstance().removeChest(loc);
      } else {
         this.cfgChest.set("Chest-" + loc.getWorld().getName() + "," + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ(), (Object)null);
      }

      this.chests.remove(loc);
   }

   public String getLoot(Location loc) {
      return this.chests.get(loc) != null && !((String)this.chests.get(loc)).isEmpty()?Util.FCTU((String)this.chests.get(loc)):null;
   }

   public void setLoot(Location loc, String loot) {
      this.chests.put(loc, loot);
   }

	public HashMap<Location, String> getChests() {
		return chests;
	}

   public Boolean isChest(Player p) {
      if(BlockAPI.getTargetBlock(p, 10).getType() == Material.CHEST) {
         Location l = BlockAPI.getTargetBlock(p, 10).getLocation();
         if(this.chests.containsKey(l)) {
            return Boolean.valueOf(true);
         }
      }

      return Boolean.valueOf(false);
   }

   public Boolean isChest(Block b) {
      if(b.getType() == Material.CHEST) {
         Location l = b.getLocation();
         if(this.chests.containsKey(l)) {
            return Boolean.valueOf(true);
         }
      }

      return Boolean.valueOf(false);
   }

   public boolean getChestInfo(Player p, Location l) {
      if(this.chests.containsKey(l)) {
         Util.Print(p, "&b===== &2Chest Info&b =====");
         Util.Print(p, "&7LootID:&6 " + this.getLoot(l));
         Util.Print(p, "&7Location:&6 x:" + l.getBlockX() + " y:" + l.getBlockY() + " z:" + l.getBlockZ());
         return true;
      } else {
         return false;
      }
   }
}
