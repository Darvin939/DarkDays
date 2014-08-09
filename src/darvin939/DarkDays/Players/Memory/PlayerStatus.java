package darvin939.DarkDays.Players.Memory;

import java.util.UUID;

public class PlayerStatus {

   private UUID playerId;
   private Integer zombieKills = Integer.valueOf(0);
   private Integer playerKills = Integer.valueOf(0);
   private Integer playerHeals = Integer.valueOf(0);


   public PlayerStatus(UUID playerId) {
      this.playerId = playerId;
   }

   public UUID getPlayerId() {
      return this.playerId;
   }

   public void setPlayerId(UUID playerId) {
      this.playerId = playerId;
   }

   public Integer getZombieKills() {
      return this.zombieKills;
   }

   public void setZombieKills(Integer zombieKills) {
      this.zombieKills = zombieKills;
   }

   public void addZombieKill() {
      this.zombieKills = Integer.valueOf(this.zombieKills.intValue() + 1);
   }

   public Integer getPlayerKills() {
      return this.playerKills;
   }

   public void setPlayerKills(Integer playerKills) {
      this.playerKills = playerKills;
   }

   public void addPlayerKill() {
      this.playerKills = Integer.valueOf(this.playerKills.intValue() + 1);
   }

   public Integer getPlayerHeals() {
      return this.playerHeals;
   }

   public void setPlayerHeals(Integer playerHeals) {
      this.playerHeals = playerHeals;
   }

   public void addPlayerHeal() {
      this.playerHeals = Integer.valueOf(this.playerHeals.intValue() + 1);
   }
}
