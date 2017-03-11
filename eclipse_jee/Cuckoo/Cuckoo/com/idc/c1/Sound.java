package com.idc.c1;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class Sound {
	private static final int EXTERNAL_BUFFER_SIZE = 128000;

	public static void doSoundTest (File file) {
		try {
//			FileInputStream inputStream = new FileInputStream(file);
//			BufferedInputStream bufin = new BufferedInputStream(inputStream);
//			AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(bufin);	// read in the sound file

			AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file);	// read in the sound file
			AudioFormat	baseFormat = audioInputStream.getFormat();
			AudioFormat decodedFormat = new AudioFormat(
					AudioFormat.Encoding.PCM_SIGNED, // Encoding to use
					baseFormat.getSampleRate(),	  // sample rate (same as base format)
					16,				  // sample size in bits (thx to Javazoom)
					baseFormat.getChannels(),	  // # of Channels
					baseFormat.getChannels()*2,	  // Frame Size
					baseFormat.getSampleRate(),	  // Frame Rate
					false				  // Big Endian
			);
			show ("1", baseFormat);
			show ("2", decodedFormat);

			AudioInputStream decodedInput = AudioSystem.getAudioInputStream(decodedFormat, audioInputStream);

			DataLine.Info info = new DataLine.Info(SourceDataLine.class, decodedFormat);
			SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
			if (line != null) {
				line.open(decodedFormat);		// open the line
				line.start();					// activate the line

				byte[] abData = new byte[EXTERNAL_BUFFER_SIZE];
				int nBytesRead;
				while ((nBytesRead = decodedInput.read(abData, 0, abData.length)) != -1) {
					if (nBytesRead >= 0)
						line.write(abData, 0, nBytesRead);
				}
				line.drain();
				line.stop();
				line.close();
				decodedInput.close();
				audioInputStream.close();
			}
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
	private static void show (String msg, AudioFormat audioFormat) {
		System.out.println(">>> show; "+msg);
		System.out.println("Channels "+audioFormat.getChannels());
		System.out.println("FrameRate "+audioFormat.getFrameRate());
		System.out.println("FrameSize "+audioFormat.getFrameSize());
		System.out.println("SampleRate "+audioFormat.getSampleRate());
		System.out.println("SampleSizeInBits "+audioFormat.getSampleSizeInBits());
		System.out.println("Encoding "+audioFormat.getEncoding());
		System.out.println("<<< show; "+msg);
	}
}
