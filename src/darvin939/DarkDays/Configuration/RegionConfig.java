package darvin939.DarkDays.Configuration;

import darvin939.DarkDays.DarkDays;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Logger;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class RegionConfig {

   private FileConfiguration cfgRegions;
   private File cfgRegionsFile;
   private Logger log = Logger.getLogger("Minecraft");


   public RegionConfig(DarkDays plg) {
      this.cfgRegionsFile = new File(plg.getDataFolder() + "/regions.yml");
      this.cfgRegions = YamlConfiguration.loadConfiguration(this.cfgRegionsFile);
      this.saveConfig();
   }

   public void saveConfig() {
      try {
         this.cfgRegions.save(this.cfgRegionsFile);
      } catch (IOException var2) {
         var2.printStackTrace();
      }

   }

   public FileConfiguration getCfg() {
      return this.cfgRegions;
   }

   public void setParam(String reg, String param, Object value) {
      if(!this.cfgRegions.isConfigurationSection(reg)) {
         this.cfgRegions.createSection(reg);
      }

      ConfigurationSection section = this.cfgRegions.getConfigurationSection(reg);
      section.set(param, Arrays.asList(new Object[]{value}));
      this.saveConfig();
   }

   public Object getParam(String reg, String value) {
      try {
         this.cfgRegions.load(this.cfgRegionsFile);
      } catch (Exception var4) {
         var4.printStackTrace();
      }

      if(this.cfgRegions.isConfigurationSection(reg)) {
         ConfigurationSection section = this.cfgRegions.getConfigurationSection(reg);
         return section.getList(value).toString();
      } else {
         this.log.severe(DarkDays.getConsolePfx() + "Error of receiving parameter from regions.yml");
         return null;
      }
   }
}
