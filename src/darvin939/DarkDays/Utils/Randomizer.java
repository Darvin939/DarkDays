package darvin939.DarkDays.Utils;

import java.util.Random;

public class Randomizer {

	private static Integer a = 3;
	private static Integer b = 2;

	public static boolean isCPercent(Integer chance) {
		int type = getCType(chance);
		System.out.println("======================");
		System.out.println("Chance: "+chance);
		Random r = new Random();
		int summ = 0;
		for (int i = 0; i < type; i++) {
			int s = r.nextInt(a);
			if (s < b)
				summ += 1;
		}
		System.out.println("Type: "+type);
		System.out.println("Summ: "+summ);
		if (summ == type) {
			return true;
		}
		return false;
	}

	public static Integer getCType(Integer chance) {
		int type;
		if (chance > 75)
			type = 1;
		else if (chance <= 75 && chance > 50)
			type = 2;
		else if (chance <= 50 && chance > 25)
			type = 3;
		else
			type = 4;
		return type;
	}

	public static Integer[] getPeriod(Integer chance) {
		int type = getCType(chance);
		Integer[] out = new Integer[] { 0, 100 };
		if (type == 1)
			out = new Integer[] { 75, 25 };
		if (type == 2)
			out = new Integer[] { 50, 25 };
		if (type == 3)
			out = new Integer[] { 25, 25 };
		if (type == 4)
			out = new Integer[] { 0, 25 };
		return out;
	}
}
