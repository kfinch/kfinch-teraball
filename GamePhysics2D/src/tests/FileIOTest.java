package tests;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Scanner;

public class FileIOTest {

	public static void main(String args[]){
		writeToFile("testfile.txt", "DO NOT BE ALARMED. THIS IS A KINDNESS.");
		readFromFile("testfile.txt");
	}
	
	
	private static void writeToFile(String fileName, String toWrite){
		Writer writer = null;

		try {
		    writer = new BufferedWriter(new OutputStreamWriter(
		          new FileOutputStream(fileName), "utf-8"));
		    writer.write(toWrite);
		} catch (IOException ex) {
		  System.out.println(ex.getMessage());
		} finally {
		   try {writer.close();} catch (Exception ex) {}
		}
	}
	
	private static void readFromFile(String fileName){
		Scanner fileScanner = null;
		try {
			fileScanner = new Scanner(new File(fileName));
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
			return;
		}
		while(fileScanner.hasNext())
			System.out.println(fileScanner.next());
		fileScanner.close();
	}
}
