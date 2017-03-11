package com.idc.cuckoo;

import java.io.*;
import javax.sound.sampled.*;

/**
 * An example .wav player.
 * 
 * @author Jonathan Bredin
 * @version 2007.11.16
 */
public class WavPlayer {
	private Clip clip;

	public static final String USAGE = "WavPlayer <filename>";

	/** The constructor to load a .wav file from disk. */
	public WavPlayer(String fileName) throws UnsupportedAudioFileException,
			LineUnavailableException, IOException {
		System.out.println("fileName :"+fileName+":");
		File f = new File(fileName);
		AudioInputStream ais = AudioSystem.getAudioInputStream(f);
		clip = AudioSystem.getClip();
		clip.open(ais);
	} // constructor (String)

	/** Play our sound on the default audio system. */
	public void play() {
		if (clip != null) {
			clip.loop(1);
		}
	}

	/**
	 * Free up the resources associated with the player. XXX Seems to freeze on
	 * OSX Java 1.5.0_07-164
	 */
	public void close() {
		clip.close();
		clip = null;
	}

	/** Return the duration in microseconds of the audio clip. */
	public long getMicrosecondLength() {
		return clip.getMicrosecondLength();
	}

	/**
	 * Demonstrate how to use the WavPlayer class. Run on the command line as:
	 * java WavPlayer <audioFile.wav>
	 */
	public static void main(String[] args) throws InterruptedException {
		/*
		if (args.length != 1) // check usage
		{
			System.err.println(USAGE);
			System.exit(-1);
		}
*/
		String strFile;
		strFile = "c:\\jvDwnlds\\Cuckoo\\boo.wav";
		strFile = "c:\\jvDwnlds\\Cuckoo\\spacemusic.au";
		strFile = "c:\\jvDwnlds\\Cuckoo\\jungle.rmf";
		strFile = "c:\\jvDwnlds\\Cuckoo\\flute+hrn+mrmba.aiff";		// ok
		strFile = "c:\\jvDwnlds\\Cuckoo\\trippygaia1.mid";
		strFile = "c:\\jvDwnlds\\Cuckoo\\bottle-open.wav";			// ok
		strFile = "c:\\jvDwnlds\\Cuckoo\\fart_z.wav";				// ok
		strFile = "c:\\jvDwnlds\\Cuckoo\\cymbals.wav";
		strFile = "c:\\jvDwnlds\\Cuckoo\\cuckoo_clock1_x.wav";
		strFile = "c:\\jvDwnlds\\Cuckoo\\cuckoo_clock2_x.wav";
		strFile = "c:\\jvDwnlds\\Cuckoo\\1.au";
		try {
//			WavPlayer player = new WavPlayer(args[0]);
			WavPlayer player = new WavPlayer(strFile);
			player.play();
			// Sleep so that Java can play our sound.
			// XXX not all .wav files have specified durations.
			Thread.sleep(player.getMicrosecondLength() / 1000);
			// player.close (); // XXX see close() note above
		} catch (IOException e) {
			System.err.println("error opening file: " + strFile);
		} catch (UnsupportedAudioFileException e) {
			System.err.println(".wav format not supported by JVM/SPI");
		} catch (LineUnavailableException e) {
			System.err.println(".wav format not supported");
		}

	} // main
} // class WavPlayer

