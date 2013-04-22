package darvin939.DarkDays.Loot;

public class ItemData {
	String effect;
	String spawn;

	public ItemData(String effect, String spawn) {
		this.effect = effect;
		this.spawn = spawn;
	}

	public String getSpawn() {
		return spawn;
	}

	public String getEffect() {
		return effect;
	}

	public void setSpawn(String spawn) {
		this.spawn = spawn;
	}

	public void setEffect(String effect) {
		this.effect = effect;
	}
}
