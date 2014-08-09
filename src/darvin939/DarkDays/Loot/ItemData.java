package darvin939.DarkDays.Loot;


public class ItemData {

   String effect;
   String spawn;
   Integer durability;


   public ItemData(String effect, String spawn, Integer durability) {
      this.effect = effect;
      this.spawn = spawn;
      this.durability = durability;
   }

   public String getSpawn() {
      return this.spawn;
   }

   public String getEffect() {
      return this.effect;
   }

   public void setSpawn(String spawn) {
      this.spawn = spawn;
   }

   public void setEffect(String effect) {
      this.effect = effect;
   }

   public void setDurability(Integer durability) {
      this.durability = durability;
   }

   public Integer getDurability() {
      return this.durability;
   }
}
