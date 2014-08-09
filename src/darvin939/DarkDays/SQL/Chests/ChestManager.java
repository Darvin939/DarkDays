package darvin939.DarkDays.SQL.Chests;

import darvin939.DarkDays.SQL.DBInitLite;
import darvin939.DarkDays.SQL.Chests.DDChest;
import darvin939.DarkDays.SQL.Chests.SQLChest;
import darvin939.DarkDays.Utils.Debug.Debug;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class ChestManager {

   private static final ChestManager instance = new ChestManager();
   public final Map<Location, DDChest> chests = new HashMap<Location, DDChest>();
   public static final Map<Location, Long> chestsID = new HashMap<Location, Long>();
   private final PreparedStatement insertChest = DBInitLite.prepareStatement("INSERT INTO `chests` (`x`,`y`,`z`,`world`) VALUES (?,?,?,?)", 1);


   public static ChestManager getInstance() {
      return instance;
   }

   public static void init() {
      ResultSet rs = DBInitLite.query("SELECT `x`,`y`,`z`,`world`,`id` FROM `chests`");

      try {
         while(rs.next()) {
            String e = rs.getString("world");
            if(e != null && !e.isEmpty()) {
               World world = Bukkit.getWorld(e);
               if(world != null) {
                  chestsID.put(new Location(world, rs.getDouble("x"), rs.getDouble("y"), rs.getDouble("z")), Long.valueOf(rs.getLong("id")));
               }
            }
         }

         rs.close();
      } catch (SQLException var3) {
         var3.printStackTrace();
         Debug.INSTANCE.severe(var3.toString());
      }

   }

   public DDChest addChest(Location loc) {
      this.addExtChest(loc);
      Long id = this.getChestID(loc);
      SQLChest chest = new SQLChest(loc, id);
      this.chests.put(loc, chest);
      return chest;
   }

   public void removeChest(Location loc) {
      ((DDChest)this.chests.get(loc)).removeChest();
      this.chests.remove(loc);
      chestsID.remove(loc);
   }

   public DDChest getChest(Location loc) {
      return (DDChest)this.chests.get(loc);
   }

   public Long getChestID(Location loc) {
      return (Long)chestsID.get(loc);
   }

   public Map<Location, Long> getChestsID() {
      return chestsID;
   }

   private void addExtChest(Location loc) {
      if(!chestsID.containsKey(loc)) {
         ResultSet rs = null;

         try {
            synchronized(this.insertChest) {
               this.insertChest.clearParameters();
               this.insertChest.setDouble(1, loc.getX());
               this.insertChest.setDouble(2, loc.getY());
               this.insertChest.setDouble(3, loc.getZ());
               this.insertChest.setString(4, loc.getWorld().getName());
               this.insertChest.executeUpdate();
               rs = this.insertChest.getGeneratedKeys();
               if(rs.next()) {
                  chestsID.put(loc, Long.valueOf(rs.getLong(1)));
               }

               if(rs != null) {
                  rs.close();
               }
            }
         } catch (SQLException var7) {
            var7.printStackTrace();
            if(rs != null) {
               try {
                  rs.close();
               } catch (SQLException var5) {
                  ;
               }
            }

            Debug.INSTANCE.severe(var7.toString());
         }
      }

   }
}
