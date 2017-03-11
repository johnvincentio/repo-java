package com.idc.j1;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class Tone {
	public static void sound (int hz, int msecs, double vol)
			throws IllegalArgumentException, LineUnavailableException {

		if (vol > 1.0 || vol < 0.0)
			throw new IllegalArgumentException("Volume out of range 0.0 - 1.0");

		byte[] buf = new byte[msecs * 8];

		for (int i = 0; i < buf.length; i++) {
			double angle = i / (8000.0 / hz) * 2.0 * Math.PI;
			buf[i] = (byte) (Math.sin(angle) * 127.0 * vol);
		}

		// shape the front; arid back ends of the wave form.
		for (int i = 0; i < 20 && i < buf.length / 2; i++) {
			buf[i] = (byte) (buf[i] * i / 20);
			buf[buf.length - 1 - i] = (byte) (buf[buf.length - 1 - i] * i / 20);
		}

		AudioFormat af = new AudioFormat(8000f, 8, 1, true, false);
		SourceDataLine sdl = AudioSystem.getSourceDataLine(af);
		sdl.open(af);
		sdl.start();
		sdl.write(buf, 0, buf.length);
		sdl.drain();
		sdl.close();
	}

	public static void main(String[] args) throws LineUnavailableException {
		Tone.sound(2000, 500, 1.0);
	}
}