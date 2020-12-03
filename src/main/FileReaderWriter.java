package main;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class FileReaderWriter {
	
	private static final String SUFFIX = "-tftp";
	
	private final String file;
	private int mark;
	
	public FileReaderWriter(String file) {
		this.file = file;
		this.mark = 0;
	}
	
	public byte[] read() throws IOException {
		byte[] arr = Files.readAllBytes(Paths.get(file));
		return Arrays.copyOfRange(arr, mark, mark+511);
		// TODO: Älä lue koko tiedostoa ja poista kovakoodaus
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
	
	public void write(String data) {
		String filename = String.format("%s%s", file, SUFFIX);
		try (PrintStream fo = new PrintStream(new FileOutputStream(filename, true))) { 
			fo.print(data);
		} catch (FileNotFoundException e) {
			Main.onerror(e);
		}
	}

}
