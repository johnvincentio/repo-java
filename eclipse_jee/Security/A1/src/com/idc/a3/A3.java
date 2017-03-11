package com.idc.a3;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;

import javax.crypto.spec.DESKeySpec;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class A3 {
	public static void main(String[] args) {
		A3 a3 = new A3();
		a3.test3();
	}
	private void test1() {
		doTest("12345678", "Some text to play with");
		doTest("1234567890", "Some text to play with");
		doTest("12345678901234567890", "Some text to play with");
		doTest("alphaphi", "Some text to play with");
	}
	private void test2() {
		doTest("alphaphi", "HG,2824790");
		doTest("alphaphi", "HG,2824791");
		doTest("alphaphi", "HG,2824792");
		doTest("alphaphi", "HG,2824793");
		doTest("alphaphi", "HG,2824794");
		doTest("alphaphi", "HG,2824795");
	}
	private void test3() {
		doTest("alphaphi", "1234567890123456,HG,2824790");
		doTest("alphaphi", "4561234567890432,HG,2824791");
		doTest("alphaphi", "6345135874521459,HG,2824792");
		doTest("alphaphi", "2344554657235657,HG,2824793");
		doTest("alphaphi", "1565634236343545,HG,2824794");
		doTest("alphaphi", "4y93476945656534,HG,2824795");
	}

	private void doTest (String key, String text) {
		System.out.println(">>> doTest; key :"+key+": text :"+text+":");
		try {
			DESKeySpec desKeySpec = new DESKeySpec (key.getBytes());
			SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("DES");
			SecretKey secretKey = secretKeyFactory.generateSecret (desKeySpec);
			String encryptedText = encrypt (secretKey, text);
			String decryptedText = decrypt (secretKey, encryptedText);
			System.out.println("encryptedText :"+encryptedText+":");
			System.out.println("decryptedText :"+decryptedText+":");
			if (! text.equals (decryptedText)) {
				System.out.println("Trouble; different!");
				System.exit(1);
			}
		} catch (Exception ex) {
			System.out.println("Exception; " + ex.getMessage());
		}
		System.out.println("<<< doTest");
	}

	public static String encrypt (SecretKey secretKey, String value) throws Exception {
		Cipher cipher = Cipher.getInstance ("DES");							// create cipher
		cipher.init (Cipher.ENCRYPT_MODE, secretKey);
		byte[] encryptedBuffer = cipher.doFinal (value.getBytes ("UTF8"));	// encrypt
		value = new BASE64Encoder().encode (encryptedBuffer);				// encode
		return value;
	}

	public static String decrypt (SecretKey secretKey, String value) throws Exception {
		Cipher cipher = Cipher.getInstance ("DES");							// create cipher
		cipher.init (Cipher.DECRYPT_MODE, secretKey);
		byte[] decodedBuffer = new BASE64Decoder().decodeBuffer (value);		// decode the value
		byte[] decryptedBuffer = cipher.doFinal (decodedBuffer);			// decrypt the value
		value = new String (decryptedBuffer, "UTF8");
		return value;
	}
}
