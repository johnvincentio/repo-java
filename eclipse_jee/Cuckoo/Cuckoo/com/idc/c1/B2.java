package com.idc.c1;

import java.io.File;

public class B2 {

	public static void main(String[] args) {
		(new B2()).doTest();
	}

	private void doTest() {
		String strFile;
		strFile = "c:\\jvDwnlds\\Cuckoo\\boo.wav";

		strFile = "c:\\jvDwnlds\\Cuckoo\\jungle.rmf";
		strFile = "c:\\jvDwnlds\\Cuckoo\\flute+hrn+mrmba.aiff";		// ok
		strFile = "c:\\jvDwnlds\\Cuckoo\\trippygaia1.mid";
		strFile = "c:\\jvDwnlds\\Cuckoo\\bottle-open.wav";			// ok
//		strFile = "c:\\jvDwnlds\\Cuckoo\\fart_z.wav";				// ok
//		strFile = "c:\\jvDwnlds\\Cuckoo\\cymbals.wav";
		strFile = "c:\\jvDwnlds\\Cuckoo\\cuckoo_clock1_x.wav";
		strFile = "c:\\jvDwnlds\\Cuckoo\\cuckoo_clock2_x.wav";
//		strFile = "c:\\jvDwnlds\\Cuckoo\\cuckoo-clock-one-chime-ANON.au";	// ok
//		strFile = "c:\\jvDwnlds\\Cuckoo\\1.au";
//		strFile = "c:\\jvDwnlds\\Cuckoo\\cheering.wav";
//		strFile = "c:\\jvDwnlds\\Cuckoo\\spacemusic.au";			// ok
//		strFile = "c:\\jvDwnlds\\Cuckoo\\chime_up.wav";
		strFile = "c:\\jvDwnlds\\Cuckoo\\cow.wav";
		strFile = "c:\\jvDwnlds\\Cuckoo\\bat-chirp.wav";
		strFile = "c:\\jvDwnlds\\Cuckoo\\chirp.au";
		strFile = "c:\\jvDwnlds\\Cuckoo\\bird002.au";
		System.out.println("strFile :"+strFile+":");
		Sound.doSoundTest (new File(strFile));
	}
}
/*
strFile :c:\jvDwnlds\Cuckoo\bottle-open.wav:
>>> show; 1
Channels 1
FrameRate 44100.0
FrameSize 2
SampleRate 44100.0
SampleSizeInBits 16
Encoding PCM_SIGNED
<<< show; 1
>>> show; 2
Channels 1
FrameRate 44100.0
FrameSize 2
SampleRate 44100.0
SampleSizeInBits 16
Encoding PCM_SIGNED
<<< show; 2
*/
/*
strFile :c:\jvDwnlds\Cuckoo\1.au:
>>> show; 1
Channels 1
FrameRate 8000.0
FrameSize 1
SampleRate 8000.0
SampleSizeInBits 8
Encoding ULAW
<<< show; 1
>>> show; 2
Channels 1
FrameRate 8000.0
FrameSize 2
SampleRate 8000.0
SampleSizeInBits 16
Encoding PCM_SIGNED
<<< show; 2
*/
/*
strFile :c:\jvDwnlds\Cuckoo\cuckoo-clock-one-chime-ANON.au:
>>> show; 1
Channels 1
FrameRate 8000.0
FrameSize 1
SampleRate 8000.0
SampleSizeInBits 8
Encoding ULAW
<<< show; 1
>>> show; 2
Channels 1
FrameRate 8000.0
FrameSize 2
SampleRate 8000.0
SampleSizeInBits 16
Encoding PCM_SIGNED
<<< show; 2
*/
