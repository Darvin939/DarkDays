package darvin939.DarkDays.Listeners.Noise;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import darvin939.DarkDays.DarkDays;
import darvin939.DarkDays.Tasks;
import darvin939.DarkDays.Configuration.Config;
import darvin939.DarkDays.Configuration.PlayerConfig;

public class Noise implements Listener {
	DarkDays plg;

	private static ThreadGroup rootThreadGroup = null;

	public Noise(DarkDays plg) {
		this.plg = plg;
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerMove(PlayerMoveEvent event) {
		if ((boolean) Config.getPC().getData(event.getPlayer(), PlayerConfig.SPAWNED)) {
			Player p = event.getPlayer();
			if (!event.getFrom().toVector().equals(event.getTo().toVector())) {
				double lold = Tasks.getNoise(p);
				if (lold > 1.0)
					lold = 1.0;
				int movX = event.getFrom().getBlockX() - event.getTo().getBlockX();
				int movY = event.getFrom().getBlockY() - event.getTo().getBlockY();
				int movZ = event.getFrom().getBlockZ() - event.getTo().getBlockZ();
				if (Math.abs(movX) > 0 || Math.abs(movZ) > 0 || Math.abs(movY) > 0) {
					if (p.isSprinting() && p.getLocation().getY() > (int) p.getLocation().getY()) {
						Tasks.updateThirst(p, 13);
						Surface.setNoise(p, 'e');
					} else if (p.getLocation().getY() > (int) p.getLocation().getY()) {
						Tasks.updateThirst(p, 9);
						Surface.setNoise(p, 'd');
					} else if (p.isSprinting()) {
						Tasks.updateThirst(p, 6);
						Surface.setNoise(p, 'c');
					} else if (p.isSneaking()) {
						Tasks.updateThirst(p, 2);
						Surface.setNoise(p, 'a');
					} else {
						Tasks.updateThirst(p, 4);
						Surface.setNoise(p, 'b');
					}

					double lnew = Tasks.getNoise(p);
					if (lnew > 1.0)
						lnew = 1.0;
					if (lnew != lold) {
						smoothExp(lold, lnew, p);
					}
				}
			}
		}
	}

	public static void smoothExp(final double original, final double lnew, final Player p) {
		new Thread(p.getName()) {
			public void run() {
				try {
					double noise = Tasks.player_noise.get(p);
					double max = Math.max(original, lnew);
					double min = Math.min(original, lnew);
					double inc = (max - min) / 10;
					if (original < lnew)
						for (int i = 1; i <= 10; i++, Thread.sleep(20)) {
							if (Tasks.player_noise.get(p) != noise)
								break;
							p.setExp((float) (min += inc));
						}
					else if (original > lnew)
						for (int i = 10; i >= 0; i--, Thread.sleep(20)) {
							if (Tasks.player_noise.get(p) != noise)
								break;
							p.setExp((float) (max -= inc));
						}
					interrupt();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}
		}.start();
	}

	public static Thread getThread(final String name) {
		if (name == null)
			throw new NullPointerException("Null name");
		final Thread[] threads = getAllThreads();
		for (Thread thread : threads)
			if (thread.getName().equals(name))
				return thread;
		return null;
	}

	private static Thread[] getAllThreads() {
		final ThreadGroup root = getRootThreadGroup();
		final ThreadMXBean thbean = ManagementFactory.getThreadMXBean();
		int nAlloc = thbean.getThreadCount();
		int n = 0;
		Thread[] threads;
		do {
			nAlloc *= 2;
			threads = new Thread[nAlloc];
			n = root.enumerate(threads, true);
		} while (n == nAlloc);
		return java.util.Arrays.copyOf(threads, n);
	}

	private static ThreadGroup getRootThreadGroup() {
		if (rootThreadGroup != null)
			return rootThreadGroup;
		ThreadGroup tg = Thread.currentThread().getThreadGroup();
		ThreadGroup ptg;
		while ((ptg = tg.getParent()) != null)
			tg = ptg;
		return tg;
	}
}
