package darvin939.DarkDays.Commands.Handlers;

import darvin939.DarkDays.DarkDays;
import darvin939.DarkDays.Commands.Handler;
import darvin939.DarkDays.Commands.InvalidUsage;
import darvin939.DarkDays.Utils.Util;
import org.bukkit.command.CommandSender;

public class Help extends Handler {

   public Help(DarkDays plugin) {
      super(plugin);
   }

   public boolean perform(CommandSender s, String[] args) throws InvalidUsage {
      String[] cmds = this.plugin.Commands.getCommands();
      Util.Print(s, "&b=================== &2DarkDays &b===================");
      String[] var7 = cmds;
      int var6 = cmds.length;

      for(int var5 = 0; var5 < var6; ++var5) {
         String cmd = var7[var5];
         Util.Print(s, "&6/dd " + cmd + " &f: " + this.plugin.Commands.getHelp(cmd));
      }

      Util.Print(s, "&7For more help of command type &6/dd &4<command> &6help");
      return true;
   }
}
