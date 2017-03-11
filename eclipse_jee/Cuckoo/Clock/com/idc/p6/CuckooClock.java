package com.idc.p6;

public class CuckooClock extends Thread {
	private long time = -1;

	private Speaker speaker;

	private VirtualTime vt = new VirtualTime();

	public static void main(String[] args) {
		new CuckooClock().start();
	}

	public void run() {
		vt.reset();
		speaker = new Speaker(vt);
		for (;;) {
			vt.nanosleep(1000000000);
			tick();
		}
	}

	protected void tick() {
		++time;
		speaker.say("tick");
		System.out.println("Time is " + time);
		if (time % (60 * 60) == 0) {
			chirp(4);
			int hour = (int) (time / (60 * 60) % 12);
			cuckoo(hour == 0 ? 12 : hour);
		} else {
			if (time % (60 * 15) == 0)
				chirp((int) (time / 60 % 60 / 15));
			if (time % (60 * 30) == 0)
				cuckoo(1);
		}
	}

	protected void chirp(int n) {
		while (n-- > 0)
			speaker.say("chirp");
	}

	protected void cuckoo(int n) {
		while (n-- > 0)
			speaker.say("cuckoo");
	}
}
