package darvin939.DarkDays.Configuration;

import darvin939.DarkDays.DarkDays;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class LootConfig {

   private FileConfiguration cfgLoot;
   private File cfgLootFile;


   public LootConfig(DarkDays plg) {
      this.cfgLootFile = new File(plg.getDataFolder() + "/loot.yml");
      this.cfgLoot = YamlConfiguration.loadConfiguration(this.cfgLootFile);
      this.saveConfig();
   }

   public void saveConfig() {
      try {
         this.cfgLoot.save(this.cfgLootFile);
      } catch (IOException var2) {
         var2.printStackTrace();
      }

   }

   public FileConfiguration getCfg() {
      try {
         this.cfgLoot.load(this.cfgLootFile);
      } catch (FileNotFoundException var2) {
         var2.printStackTrace();
      } catch (IOException var3) {
         var3.printStackTrace();
      } catch (InvalidConfigurationException var4) {
         var4.printStackTrace();
      }

      return this.cfgLoot;
   }
}
