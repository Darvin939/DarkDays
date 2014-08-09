package darvin939.DarkDays.Players.Memory;


public class GameStatus {

   private Integer hunger;
   private Boolean death;
   private Boolean novice;
   private Boolean spawned;


   public GameStatus(Integer hunger, Boolean death, Boolean novice, Boolean spawned) {
      this.hunger = hunger;
      this.death = death;
      this.novice = novice;
      this.spawned = spawned;
   }

   public Integer getHunger() {
      return this.hunger;
   }

   public Boolean isDeath() {
      return this.death;
   }

   public Boolean isNovice() {
      return this.novice;
   }

   public Boolean isSpawned() {
      return this.spawned;
   }

   public void setHunger(Integer value) {
      this.hunger = value;
   }

   public void setDeath(Boolean value) {
      this.death = value;
   }

   public void setNovice(Boolean value) {
      this.novice = value;
   }

   public void setSpawned(Boolean value) {
      this.spawned = value;
   }

   public Object get(String data) {
      switch(data.hashCode()) {
      case -2011558566:
         if(data.equals("spawned")) {
            return this.spawned;
         }
         break;
      case -1206104397:
         if(data.equals("hunger")) {
            return this.hunger;
         }
         break;
      case -1039630442:
         if(data.equals("novice")) {
            return this.novice;
         }
         break;
      case 95457908:
         if(data.equals("death")) {
            return this.death;
         }
      }

      return null;
   }

   public void set(String data, Object value) {
      switch(data.hashCode()) {
      case -2011558566:
         if(data.equals("spawned")) {
            this.spawned = (Boolean)value;
         }
         break;
      case -1206104397:
         if(data.equals("hunger")) {
            this.hunger = (Integer)value;
         }
         break;
      case -1039630442:
         if(data.equals("novice")) {
            this.novice = (Boolean)value;
         }
         break;
      case 95457908:
         if(data.equals("death")) {
            this.death = (Boolean)value;
         }
      }

   }
}
