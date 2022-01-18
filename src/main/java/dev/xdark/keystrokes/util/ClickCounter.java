package dev.xdark.keystrokes.util;

import net.minecraft.client.Minecraft;

import java.util.Arrays;

public final class ClickCounter {

	private long[] timestamps = new long[16];
	private int slot;
	private int cps;

	public void increment() {
		long[] timestamps = this.timestamps;
		int slot = this.slot++;
		if (slot == timestamps.length) {
			timestamps = this.timestamps = Arrays.copyOf(timestamps, slot * 2);
		}
		timestamps[slot] = Minecraft.getSystemTime() + 1000L;
	}

	public void update() {
		long now = Minecraft.getSystemTime();
		long[] timestamps = this.timestamps;
		int slot = this.slot;
		int pos;
		for (pos = 0; pos < slot && timestamps[pos] < now; pos++) ;
		int cps = slot - pos;
		if (cps > 0) {
			System.arraycopy(timestamps, pos, timestamps, 0, cps);
			this.slot = cps;
		}
		this.cps = cps;
	}

	public int getCps() {
		return cps;
	}
}
