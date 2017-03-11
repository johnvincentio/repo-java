package com.idc.a1;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class App {
	private static final String UNICODE_FORMAT = "UTF8";

	public static void main(String[] args) {
		(new App()).doTest();
	}

	private void doTest() {
		String ciphertext = "someKey";
		try {
			byte[] cleartext = ciphertext.getBytes (UNICODE_FORMAT);
			BASE64Encoder base64encoder = new BASE64Encoder();
			String encryptedString = base64encoder.encode (cleartext);

			BASE64Decoder base64decoder = new BASE64Decoder();
			byte[] bytes = base64decoder.decodeBuffer (encryptedString);
			String decryptedString = bytes2String (bytes);
			System.out.println("decryptedString :" + decryptedString + ":");
		} catch (Exception ex) {
			System.out.println("Exception; " + ex.getMessage());
		}
	}

	private static String bytes2String (byte[] bytes) {
		StringBuffer stringBuffer = new StringBuffer();
		for (int i = 0; i < bytes.length; i++) {
			stringBuffer.append((char) bytes[i]);
		}
		return stringBuffer.toString();
	}
}
