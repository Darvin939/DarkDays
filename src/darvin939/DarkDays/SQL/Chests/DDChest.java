package darvin939.DarkDays.SQL.Chests;

import org.bukkit.Location;

public abstract class DDChest {

   protected Location loc;
   private int hashCode;


   protected DDChest(Location loc) {
      this.loc = loc;
      byte result = 7;
      int result1 = 41 * result + (loc == null?0:loc.hashCode());
      this.hashCode = result1;
   }

   public abstract Location getLoc();

   public abstract String getLoot();

   public abstract void addChest(String var1);

   public abstract void addChest();

   public abstract void removeChest();

   public abstract void addLoot(String var1);

   public String getName() {
      return "Chest-" + this.loc.getWorld().getName() + "," + this.loc.getBlockX() + "," + this.loc.getBlockY() + "," + this.loc.getBlockZ();
   }

   public int hashCode() {
      return this.hashCode;
   }
}
