package com.example.descracker;

import junit.framework.TestCase;

public class DES_Test_Case extends TestCase {

	public void testParseBytes() {
		byte[] byteCell = DES.parseBytes("74657374696e67");
		assertTrue("Test Successfully", DES.hex(byteCell).equals("74 65 73 74 69 6E 67 "));
		assertFalse("Test Failure", !DES.hex(byteCell).equals("74 65 73 74 69 6E 67 "));

	}

	public void testHex() {
		byte[] byteCell = DES.parseBytes("74657374696e67");
		String hexa = DES.hex(byteCell);
		assertTrue("Test Successfully", hexa.equals("74 65 73 74 69 6E 67 "));
		assertFalse("Test Failure", !hexa.equals("74 65 73 74 69 6E 67 "));
	}

	public void testConvertStringToHex() {
		String hexa = DES.convertStringToHex("testing");
		assertTrue("Test Successfully", hexa.equals("74657374696e67"));
		assertFalse("Test Failure", !hexa.equals("74657374696e67"));
	}

	public void testConvertHexToString() {
		String text = DES.convertHexToString("74657374696e67");
		assertTrue("Test Successfully", text.equals("testing"));
		assertFalse("Test Failure", !text.equals("testing"));
	}

	public void testDecryptCBC() {
		String hex = DES.convertStringToHex("Testing_Cipher");
		String hexAfterAppend = DES.appendZero(hex);
		byte[] testBytes = DES.parseBytes(hexAfterAppend);
		
		String decKey = DES.convertStringToHex("Testing_Key");
		byte[] keyByte = DES.parseBytes(decKey);
		String encryptResult = DES.hex(DES.encryptCBC(testBytes, keyByte));
		byte[] cipherByteTest = DES.parseBytes(encryptResult);
		String decryptResult = DES.hex(DES.decryptCBC(cipherByteTest, keyByte));	
		String temp = decryptResult.replace(" ", "");
		String finalDecrypt = temp.substring(0, (temp.length() - DES.getConcatCount()));
		String finalResult = DES.convertHexToString(finalDecrypt);
		System.out.println(finalResult);
		
		
		assertTrue("Test Successfully", finalResult.equals("Testing_Cipher"));
		assertFalse("Test Failure", !finalResult.equals("Testing_Cipher"));
		
	}

}
