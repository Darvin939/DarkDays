package darvin939.DarkDays.Loot;


public class PotionData {

   String spawn;
   String effects;


   public PotionData(String spawn, String effects) {
      this.spawn = spawn;
      this.effects = effects;
   }

   public String getSpawn() {
      return this.spawn;
   }

   public String getEffect() {
      return this.effects;
   }
}
