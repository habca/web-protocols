package main;

import static org.junit.Assert.*;

import org.junit.*;

import ftp.*;

import java.util.*;
import java.util.regex.*;

/**
 * Staattinen apufunktio-kirjasto
 * 
 * @author Harri Linna
 * @version 17.11.2020
 */
public final class Static {
	
	private Static() {
		super();
	}
	
	// ei toimi primitiivi taulukoille, käytä Arrays.equals
	public static <T> boolean compare(T[] arr1, T[] arr2) {
    	if (arr1.length != arr2.length) return false;
    	if (arr1 == null || arr2 == null) return false;
    	
    	for (int i = 0; i < arr1.length; i++) {
    		if (!arr1[i].equals(arr2[i])) return false;
    	}
    	
    	return true;
	}
	
	@SafeVarargs
	public static <T> void append(StringBuilder sb, T... str) {
		for (int i = 0; i < str.length; i++) {
			sb.append(str[i].toString());
		}
	}
	
	public static Integer[] extractNumbers(String str) {
		Pattern pattern = Pattern.compile("\\d+");
		Matcher matcher = pattern.matcher(str);
		
		ArrayList<String> matches = new ArrayList<String>();
		while (matcher.find()) {
			matches.add(matcher.group());
		}
		
		Integer[] arr = new Integer[matches.size()];
		for (int i = 0; i < arr.length; i++) {
			arr[i] = Integer.parseInt(matches.get(i));
		}
		
		return arr;
	}
	
	public static int bytesToInt(byte[] arr) {
		int a = arr[0] & 0xFF;
		int b = arr[1] & 0xFF;
		return FTPClient.calcPort(a, b);
	}
	
	public static byte[] intToBytes(int num, int size) {
		// TODO: taulukon ekat tavut aina 0, korjaa
		byte[] arr = new byte[size];
		arr[arr.length-1] = (byte) num;
		return arr;
	}
	
	public static class TestStatic {
		
		@Test
		public void testBytesToInt() {
			String error = "Kokonaisluku muunnetaan väärin:";
			
			byte[] arr1 = new byte[] {0, 1};
			byte[] arr2 = new byte[] {4, 5};
			
			assertEquals(error, 1, bytesToInt(arr1));
			assertEquals(error, FTPClient.calcPort(4,5), bytesToInt(arr2));
		}
		
		@Test
		public void testIntToBytes() {
			String error = "Tavut muunnetaan väärin:";
			
			int in = FTPClient.calcPort(0, 5);
			byte[] out = new byte[] {(byte) 0, (byte) 5};
			
			assertTrue(error, Arrays.equals(intToBytes(in, 2), out));
		}
		
	}
	
}
