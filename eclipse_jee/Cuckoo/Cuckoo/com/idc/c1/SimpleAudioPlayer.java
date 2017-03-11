package com.idc.c1;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class SimpleAudioPlayer {
	private static final int EXTERNAL_BUFFER_SIZE = 128000;

	public static void main(String[] args) {
		(new SimpleAudioPlayer()).doTest();
	}

	private void doTest() {
		String strFile;
		strFile = "c:\\jvDwnlds\\Cuckoo\\boo.wav";
		strFile = "c:\\jvDwnlds\\Cuckoo\\spacemusic.au";
		strFile = "c:\\jvDwnlds\\Cuckoo\\jungle.rmf";
		strFile = "c:\\jvDwnlds\\Cuckoo\\flute+hrn+mrmba.aiff";		// ok
		strFile = "c:\\jvDwnlds\\Cuckoo\\trippygaia1.mid";
		strFile = "c:\\jvDwnlds\\Cuckoo\\bottle-open.wav";			// ok
		strFile = "c:\\jvDwnlds\\Cuckoo\\fart_z.wav";				// ok
//		strFile = "c:\\jvDwnlds\\Cuckoo\\cymbals.wav";
//		strFile = "c:\\jvDwnlds\\Cuckoo\\cuckoo_clock1_x.wav";
//		strFile = "c:\\jvDwnlds\\Cuckoo\\cuckoo_clock2_x.wav";
//		strFile = "c:\\jvDwnlds\\Cuckoo\\cuckoo-clock-one-chime-ANON.au";
		strFile = "c:\\jvDwnlds\\Cuckoo\\chime_up.wav";
		strFile = "c:\\jvDwnlds\\Cuckoo\\1.au";
		System.out.println("strFile :"+strFile+":");
		doSoundTest (new File(strFile));
	}

	private void doSoundTest (File file) {
		AudioInputStream audioInputStream = null;
		try {
			audioInputStream = AudioSystem.getAudioInputStream(file);	// read in the sound file
			AudioFormat	audioFormat = audioInputStream.getFormat();
			SourceDataLine line = null;
			DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
			line = (SourceDataLine) AudioSystem.getLine(info);
			line.open(audioFormat);		// open the line
			line.start();				// activate the line

			int	nBytesRead = 0;
			byte[] abData = new byte[EXTERNAL_BUFFER_SIZE];
			while (nBytesRead != -1) {
				nBytesRead = audioInputStream.read(abData, 0, abData.length);
				if (nBytesRead >= 0) {
					int	nBytesWritten = line.write(abData, 0, nBytesRead);
				}
			}
			line.drain();
			line.close();
		}
		catch (LineUnavailableException e) {
			e.printStackTrace();
			System.exit(1);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		System.exit(0);
	}
}
