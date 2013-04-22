package darvin939.DarkDays.Loot;

public class PotionData {
	String spawn;
	String effects;

	public PotionData(String spawn, String effects) {
		this.spawn = spawn;
		this.effects = effects;
	}
	public String getSpawn() {
		return spawn;
	}

	public String getEffect() {
		return effects;
	}
}
