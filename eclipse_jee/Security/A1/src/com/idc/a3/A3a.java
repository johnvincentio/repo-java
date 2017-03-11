package com.idc.a3;

import java.util.Random;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class A3a {


	/*
	 * is there a maximum length for a get url parameter?
	 * 
	 * getRandomNumber between i and j (inclusive) getRandom alpha Numeric
	 * String of length j; construct csv string encrypt the string
	 * 
	 * get encrypted string decrypt the string separate the csv string get
	 * account number and country code.
	 * 
	 * 30-50 chars account number 20-30 chars country code 10-20 chars
	 * 
	 * test the max possible length; see if the server handles it.
	 */
	public static void main(String[] args) {
		A3a a3a = new A3a();
		a3a.test4();
	}

	private void test1() {
		doTest("1234567", "Some text to play with");
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

	// key length must be 8 characters
	private void doTest(String key, String text) {
		System.out.println(">>> doTest; key :" + key + ": text :" + text + ":");
		try {
			String encryptedText = encrypt(key, text);
			String decryptedText = decrypt(key, encryptedText);
			System.out.println("encryptedText :" + encryptedText + ":");
			System.out.println("decryptedText :" + decryptedText + ":");
			if (! text.equals(decryptedText)) {
				System.out.println("Trouble; different!");
				System.exit(1);
			}
		} catch (Exception ex) {
			System.out.println("Exception; " + ex.getMessage());
		}
		System.out.println("<<< doTest");
	}

	public static String encrypt (String key, String value) throws Exception {
		DESKeySpec desKeySpec = new DESKeySpec(key.getBytes());
		SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("DES");
		SecretKey secretKey = secretKeyFactory.generateSecret(desKeySpec);
		Cipher cipher = Cipher.getInstance("DES"); // create cipher
		cipher.init(Cipher.ENCRYPT_MODE, secretKey);
		byte[] encryptedBuffer = cipher.doFinal(value.getBytes("UTF8")); // encrypt
		return new BASE64Encoder().encode(encryptedBuffer); // encode
	}

	public static String decrypt (String key, String value) throws Exception {
		DESKeySpec desKeySpec = new DESKeySpec(key.getBytes());
		SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("DES");
		SecretKey secretKey = secretKeyFactory.generateSecret(desKeySpec);
		Cipher cipher = Cipher.getInstance("DES"); // create cipher
		cipher.init(Cipher.DECRYPT_MODE, secretKey);
		byte[] decodedBuffer = new BASE64Decoder().decodeBuffer(value); // decode the value
		byte[] decryptedBuffer = cipher.doFinal(decodedBuffer); // decrypt the value
		return new String(decryptedBuffer, "UTF8");
	}

	private static int getRandom (int from, int to) {
		Random random = new Random();
		return random.nextInt((to + 1) - from) + from;
	}

	public static StringBuffer generateRandomString (int length) {
		char[] values = {
				'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
				'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 
				'u', 'v', 'w', 'x', 'y', 'z', 
				'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
				'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 
				'U', 'V', 'W', 'X', 'Y', 'Z',
				'0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };
		StringBuffer buf = new StringBuffer();
		Random random = new Random();
		for (int i = 0; i < length; i++) {
			int idx = random.nextInt (values.length);
			buf.append (values[idx]);
		}
		return buf;
	}

	private static final String KEY = "maps9637";

// 30-50 chars, account number, 20-30 chars, country code, 10-20 chars
	private void test4() {
		String account = "4562346";
		String country = "HG";
		StringBuffer buf = new StringBuffer();
		buf.append (generateRandomString (getRandom (30, 50))).append (",");
		buf.append (account.trim()).append (",");
		buf.append (generateRandomString (getRandom (20, 30))).append (",");
		buf.append (country.trim()).append (",");
		buf.append (generateRandomString (getRandom (10, 20)));
		String orig = buf.toString();
		System.out.println("orig "+orig);

		try {
			String encryptedText = encrypt (KEY, orig);
			String decryptedText = decrypt (KEY, encryptedText);
			System.out.println("encryptedText :" + encryptedText + ":");
			System.out.println("decryptedText :" + decryptedText + ":");
			if (! orig.equals(decryptedText)) {
				System.out.println("Trouble; different!");
				System.exit(1);
			}

			Pattern pattern = Pattern.compile("\\,");
			String[] splitStrings = pattern.split (decryptedText);
			System.out.println("splitStrings "+splitStrings.length);
			String str1 = splitStrings[1].trim();
			String str2 = splitStrings[3].trim();
			System.out.println("str1 "+str1);
			System.out.println("str2 "+str2);
		}
		catch (Exception ex) {
			System.out.println("Exception; " + ex.getMessage());
		}
	}
	private void test5() {
		for (int cnt = 0; cnt < 20; cnt++) {
			int jv = getRandom (30, 50);
			System.out.println("cnt "+cnt+" jv "+jv);
		}
		for (int cnt = 10; cnt < 20; cnt++) {
			String str = generateRandomString (cnt).toString();
			System.out.println("cnt "+cnt+" str "+str);
		}
	}
}
