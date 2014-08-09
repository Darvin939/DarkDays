package darvin939.DarkDays.Commands.Handlers;

import darvin939.DarkDays.DarkDays;
import darvin939.DarkDays.Commands.Handler;
import darvin939.DarkDays.Commands.InvalidUsage;
import darvin939.DarkDays.Utils.Util;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginDescriptionFile;

public class About extends Handler {

   public About(DarkDays plugin) {
      super(plugin);
   }

   public boolean perform(CommandSender s, String[] args) throws InvalidUsage {
      PluginDescriptionFile des = this.plugin.getDescription();
      Util.Print(s, "&b================= &2DarkDays About &b================");
      Util.Print(s, "&6Version:&7 " + des.getVersion());
      Util.Print(s, "&6Author:&7 Darvin939 (Sergey Mashoshin. Russia, Moscow)");
      Util.Print(s, "&2Contacts:");
      Util.Print(s, "  &6Email:&7 darvin212@gmail.com");
      Util.Print(s, "  &6VK:&7 http://vk.com/darvin212");
      Util.Print(s, "  &6Skype:&7 darvin654");
      return true;
   }
}
