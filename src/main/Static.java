package main;

import static org.junit.Assert.*;

import org.junit.*;

import ftp.*;

import java.io.*;
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
		return 0; // TODO: ???
	}
	
	public static byte[] intToBytes(int num) {
		return new byte[2]; // TODO: ???
	}
	
	public static class TestStatic {
		@Test
		public void testByteToInt() {
			String error = "Kokonaisluku muunnetaan väärin:";
			byte[] arr1 = new byte[] {(byte) 0, (byte) 1};
			byte[] arr2 = new byte[] {(byte) 155, (byte) 200};
			assertEquals(error, 1, bytesToInt(arr1));
			assertEquals(error, FTPClient.calcPort(155,200), bytesToInt(arr2));
		}
	}
	
}
