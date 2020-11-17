package main;

import static org.junit.Assert.*;

import java.util.*;
import java.util.regex.*;

import org.junit.*;

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
			sb.append(str[i]);
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
	
	public static String extractAddress(String str) {
		StringBuilder sb = new StringBuilder();
		Integer[] arr = extractNumbers(str);
		
		append(sb, arr[1]);
		append(sb, '.', arr[2]);
		append(sb, '.', arr[3]);
		append(sb, '.', arr[4]);
		
		return sb.toString();
	}
	
	public static int extractPort(String str) {
		Integer[] arr = extractNumbers(str);
		int last = arr.length - 1;
		return calcPort(arr[last-1], arr[last]);
	}

    public static int calcPort(int p1, int p2) {
        return (p1 * 256) + p2;
    }
    
    public static String extractFile(String prefix, String str) {
    	String[] arr = str.split(prefix);
    	StringBuilder sb = new StringBuilder();
    	for (int i = 1; i < arr.length; i++) {
    		sb.append(arr[i].trim());
    	}
		return sb.toString();
    }
    
    public static class StaticTest {
    	private static final  String PASV = 
    			"227 Entering Passive Mode (193,166,3,2,155,200)";
    	@Test
    	public void testCalcPort() {
    		String error = "Portti lasketaan väärin:";
    		
    		assertEquals(error, 0, Static.calcPort(0,0));
    		assertEquals(error, 1, Static.calcPort(0,1));
    		assertEquals(error, 256, Static.calcPort(1,0));
    		assertEquals(error, 257, Static.calcPort(1,1));
    	}
    	@Test
    	public void testExtractNumber() {
    		String error = "Numerot parsitaan väärin:";
    		Integer[] result = new Integer[] {227,193,166,3,2,155,200};
    		Integer[] test = Static.extractNumbers(PASV);
    		assertEquals(error, true, Static.compare(test, result));
    	}
    	@Test
    	public void testExtractPort() {
    		String error = "Portti parsitaan väärin:";
    		assertEquals(error, Static.calcPort(155,200), Static.extractPort(PASV));
    	}
    	@Test
    	public void testExtractAddress() {
    		String error = "IP-osoite parsitaan väärin:";
    		assertEquals(error, "193.166.3.2", Static.extractAddress(PASV));
    	}
    	@Test
    	public void testExtractFile() {
    		String error = "Tiedostonimi parsitaan väärin:";
    		assertEquals(error, "file", Static.extractFile("RETR", "RETR file"));
    		assertEquals(error, "", Static.extractFile("RETR", "RETR"));
    	}
    }
	
}
