package darvin939.DarkDays.Commands.Handlers;

import darvin939.DarkDays.DarkDays;
import darvin939.DarkDays.Commands.Handler;
import darvin939.DarkDays.Commands.InvalidUsage;
import darvin939.DarkDays.Players.Memory.PlayerData;
import darvin939.DarkDays.Utils.Util;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Status extends Handler {

   public Status(DarkDays plugin) {
      super(plugin);
   }

   public boolean perform(CommandSender s, String[] args) throws InvalidUsage {
      if(s instanceof Player) {
         Player p = (Player)s;
         if(this.hasPermission(p, "status", Boolean.valueOf(true))) {
            if(PlayerData.isPlaying(p)) {
               Util.Print(p, "&b================= &2Your Progress &b=================");
               Util.Print(p, "Current session:");
               Util.Print(p, "  &7Players you bandaged:&6 " + PlayerData.getPlayerHeals(p));
               Util.Print(p, "  &7Killed players:&6 " + PlayerData.getPlayerKills(p));
               Util.Print(p, "  &7Killed zombies:&6 " + PlayerData.getZombieKills(p));
            } else {
               Util.PrintMSGPx(p, "game_noplay", "/dd spawn");
            }
         }

         return true;
      } else {
         s.sendMessage("You must be a Player to do this");
         return true;
      }
   }
}
