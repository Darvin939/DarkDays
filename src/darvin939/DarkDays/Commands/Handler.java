package darvin939.DarkDays.Commands;

import darvin939.DarkDays.DarkDays;
import darvin939.DarkDays.Commands.InvalidUsage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class Handler {

   protected final DarkDays plugin;


   public Handler(DarkDays plugin) {
      this.plugin = plugin;
   }

   public abstract boolean perform(CommandSender var1, String[] var2) throws InvalidUsage;

   protected boolean hasPermission(Player p, String command, Boolean mess) {
      return this.plugin.hasPermission(p, command, mess);
   }

   protected void getHelp(Player p, String command) {
      this.plugin.getHelp(p, command);
   }
}
