package darvin939.DarkDays.Configuration;

import darvin939.DarkDays.DarkDays;
import darvin939.DarkDays.Utils.Util;
import java.io.File;
import java.io.IOException;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class SpawnConfig {

   private FileConfiguration cfgSpawn;
   private File cfgSpawnFile;


   public SpawnConfig(DarkDays plg) {
      this.cfgSpawnFile = new File(plg.getDataFolder() + "/spawns.yml");
      this.cfgSpawn = YamlConfiguration.loadConfiguration(this.cfgSpawnFile);
      this.saveConfig();
   }

   public void saveConfig() {
      try {
         this.cfgSpawn.save(this.cfgSpawnFile);
      } catch (IOException var2) {
         var2.printStackTrace();
      }

   }

   public FileConfiguration getCfg() {
      return this.cfgSpawn;
   }

   public void addSpawn(Location loc, String type) {
      this.add(loc, type);
   }

   public void addSpawn(Player p) {
      if(this.add(p.getLocation(), "Spawn")) {
         Util.PrintMSGPx(p, "spawn_new");
      } else {
         Util.PrintMSGPx(p, "spawn_error");
      }

   }

   public void addLobby(Player p) {
      if(this.add(p.getLocation(), "Lobby")) {
         Util.PrintMSGPx(p, "spawn_lobby_new");
      } else {
         Util.PrintMSGPx(p, "spawn_lobby_error");
      }

   }

   public Location getSpawnLoc(Player p) {
      String section = p.getWorld().getName() + ".Lobby";
      if(this.cfgSpawn.isConfigurationSection(section)) {
         double x = this.cfgSpawn.getDouble(section + ".x");
         double y = this.cfgSpawn.getDouble(section + ".y");
         double z = this.cfgSpawn.getDouble(section + ".z");
         Location loc = new Location(p.getWorld(), x, y, z);
         return loc;
      } else {
         return p.getWorld().getSpawnLocation();
      }
   }

   private boolean add(Location loc, String type) {
      if((new Location(loc.getWorld(), (double)loc.getBlockX(), (double)(loc.getBlockY() - 1), (double)loc.getBlockZ())).getBlock().getType() == Material.AIR) {
         return false;
      } else {
         double x = (double)loc.getBlockX() + 0.5D;
         double y = (double)loc.getBlockY();
         double z = (double)loc.getBlockZ() + 0.5D;
         String world = loc.getWorld().getName();
         if(!this.cfgSpawn.isConfigurationSection(world)) {
            this.cfgSpawn.createSection(world);
         }

         if(type.equalsIgnoreCase("lobby")) {
            String var13 = world + "." + Util.FCTU(type);
            if(!this.cfgSpawn.isConfigurationSection(var13)) {
               this.cfgSpawn.createSection(var13);
            }

            this.cfgSpawn.set(var13 + ".x", Double.valueOf(x));
            this.cfgSpawn.set(var13 + ".y", Double.valueOf(y));
            this.cfgSpawn.set(var13 + ".z", Double.valueOf(z));
            this.saveConfig();
            return true;
         } else {
            int spawnid = 0;

            int spawnidx;
            for(spawnidx = -1; this.cfgSpawn.contains(world + "." + "Spawn" + spawnid); spawnidx = spawnid++) {
               ;
            }

            ++spawnidx;
            String section = world + "." + "Spawn" + spawnidx;
            this.cfgSpawn.createSection(section);
            this.cfgSpawn.set(section + ".x", Double.valueOf(x));
            this.cfgSpawn.set(section + ".y", Double.valueOf(y));
            this.cfgSpawn.set(section + ".z", Double.valueOf(z));
            this.saveConfig();
            return true;
         }
      }
   }

   public boolean removeSpawn(World world, String name) {
      String section = world.getName() + "." + Util.FCTU(name.toLowerCase());
      if(this.cfgSpawn.isConfigurationSection(section)) {
         this.cfgSpawn.set(section, (Object)null);
         this.saveConfig();
         return true;
      } else {
         return false;
      }
   }
}
