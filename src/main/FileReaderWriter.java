package main;

import java.io.*;
import java.util.*;

/**
 * Reads and writes from/to a file
 * @author Harri Linna
 * @version 4.12.2020
 */
public class FileReaderWriter extends File implements Iterator<byte[]> {
	
	private static final long serialVersionUID = 1L;
	
	private byte[] data;
	private int step;
	
	public FileReaderWriter(String file, int step) {
		super(file);
		
		this.step = step;
		
		// read file into a byte array
		data = new byte[(int) this.length()];
		
		if (isFile()) {
			try (InputStream is = new FileInputStream(this)) {
				is.read(data);
			} catch (IOException e) {
		    	Main.onerror(e);
		    }
		}
	}
	
	/*
	public void read() {
		try (Scanner fi = new Scanner(new FileInputStream(new File(file)))) {
			while (fi.hasNext()) {
				Main.onmessage(fi.nextLine());
			}
		} catch (FileNotFoundException e) {
			Main.onerror(e);
        }
	}
	*/
	
	public static void write(String filename, String data) {
		try (PrintStream fo = new PrintStream(
				new FileOutputStream(new File(filename), true))) { 
			fo.print(data);
		} catch (FileNotFoundException e) {
			Main.onerror(e);
		}
	}

	@Override
	public boolean hasNext() {
		return data.length > 0;
	}

	@Override
	public byte[] next() {
		if (data.length < step) {
			byte[] arr = Arrays.copyOf(data, data.length);
			data = new byte[0];
			return arr;
		}
		
		byte[] arr = Arrays.copyOf(data, step);
		data = Arrays.copyOfRange(data, step, data.length);
		return arr;
	}
	
}
