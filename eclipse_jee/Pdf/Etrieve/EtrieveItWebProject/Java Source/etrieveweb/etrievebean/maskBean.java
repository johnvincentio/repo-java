// *****************************************************************************************************************
// Copyright (C) 2005 The Hertz Corporation, All Rights Reserved.  Unpublished.       
//                                                                                              
// The information contained herein is confidential and proprietary to the Hertz Corporation
// and may not be duplicated, disclosed to third parties, or used for any purpose not       
// expressly authorized by it.                                                              
// Any unauthorized use, duplication, or disclosure is prohibited by law.                   
// ****************************************************************************************************************

package etrieveweb.etrievebean;

import javax.crypto.*;
import javax.crypto.interfaces.*;
import javax.crypto.spec.*;
import java.security.*;
import java.security.spec.*;
import java.io.*;
import java.util.*;

public class maskBean {

        Cipher ecipher;
        Cipher dcipher;
    
       // Begin by creating a random salt of 64 bits (8 bytes)
       byte[] salt = {
            (byte)0xc7, (byte)0x9B, (byte)0x21, (byte)0x32,
            (byte)0x56, (byte)0x35, (byte)0xE3, (byte)0x99
        };

        // Iteration count
        int iterationCount = 19;
    
        public maskBean() {

            super();

            try {

                   PBEKeySpec pbeKeySpec;
                   PBEParameterSpec pbeParamSpec;
                   SecretKeyFactory keyFac;

                   // Create PBE parameter set
                   pbeParamSpec = new PBEParameterSpec(salt, iterationCount);

                  // The password that is taken is to be converted into a SecretKey object, using a PBE key factory
                  String passPhrase = "s1*raft523%yd5=@fhu!mcu38cmwuid#s1ld$";
                  pbeKeySpec = new PBEKeySpec(passPhrase.toCharArray());
                  keyFac = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
                  SecretKey pbeKey = keyFac.generateSecret(pbeKeySpec);

                  // Create the PBE Cipher.  
                  // ecipher = Cipher.getInstance("PBEWithMD5AndDES");
                  // dcipher = Cipher.getInstance("PBEWithMD5AndDES");

                  ecipher = Cipher.getInstance("PBEWithMD5AndTripleDES");
                  dcipher = Cipher.getInstance("PBEWithMD5AndTripleDES");
    
                // Prepare the parameter to the ciphers
                AlgorithmParameterSpec paramSpec = new PBEParameterSpec(salt, iterationCount);
    
                // Create the ciphers
                ecipher.init(Cipher.ENCRYPT_MODE, pbeKey, paramSpec);
                dcipher.init(Cipher.DECRYPT_MODE, pbeKey, paramSpec);

            } catch (java.security.InvalidAlgorithmParameterException e) {
            } catch (java.security.spec.InvalidKeySpecException e) {
            } catch (javax.crypto.NoSuchPaddingException e) {
            } catch (java.security.NoSuchAlgorithmException e) {
            } catch (java.security.InvalidKeyException e) {
            }
        }
    
        public String encrypt(String str) {

            try {
                // Encode the string into bytes using utf-8
                byte[] utf8 = str.getBytes("UTF8");
    
                // Encrypt
                byte[] enc = ecipher.doFinal(utf8);
    
                // Encode bytes to base64 to get a string
                return new sun.misc.BASE64Encoder().encode(enc);
            } catch (javax.crypto.BadPaddingException e) {
            } catch (IllegalBlockSizeException e) {
            } catch (UnsupportedEncodingException e) {
            } catch (java.io.IOException e) {
            }
            return "";
        }
    
        public String decrypt(String str) {
            try {
                // Decode base64 to get bytes
                byte[] dec = new sun.misc.BASE64Decoder().decodeBuffer(str);
    
                // Decrypt
                byte[] utf8 = dcipher.doFinal(dec);
    
                // Decode using utf-8
                return new String(utf8, "UTF8");
            } catch (javax.crypto.BadPaddingException e) {
            } catch (IllegalBlockSizeException e) {
            } catch (UnsupportedEncodingException e) {
            } catch (java.io.IOException e) {
            }
            return "";
        }
    }


