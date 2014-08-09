package darvin939.DarkDays.SQL.Players;

import darvin939.DarkDays.Players.Memory.GameStatus;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public abstract class DDPlayer {

   protected final UUID uuid;
   protected Player player = null;


   protected DDPlayer(Player player) {
      this.uuid = player.getUniqueId();
      this.player = player;
   }

   public abstract Location getLoc();

   public abstract GameStatus getData();

   public abstract String getEffects();

   public abstract void addPlayer();

   public abstract void removePlayer();

   public abstract void addData(GameStatus var1);

   public abstract void removeData();

   public abstract void addEffects(String var1);

   public abstract void removeEffects();
}
