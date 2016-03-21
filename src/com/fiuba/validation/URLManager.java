package com.fiuba.validation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class URLManager {

	private Map<String, File> files;
	private static URLManager inst = null;
	
	
	public static URLManager instance() {
		
		if (inst == null) {
			inst = new URLManager();
		}
		return inst;
	}
	
	private URLManager() {
		
		files = new HashMap<String, File>();

		files.put("a-e", new File("./data", "a-e.arc"));
		files.put("f-k", new File("./data", "f-k.arc"));
		files.put("l-p", new File("./data", "l-p.arc"));
		files.put("q-z", new File("./data", "q-z.arc"));
		

		try {
			
			new File("./data").mkdirs();
			files.get("a-e").createNewFile();
			files.get("f-k").createNewFile();
			files.get("l-p").createNewFile();
			files.get("q-z").createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public boolean isRepeated( String url ) {
		
		String name = url.replace("http://", "").replace("www.", "").replace("/", "");
		if (name.isEmpty()) return false;
		char key = name.substring(0, 1).toLowerCase().charAt(0);
		
		try ( Scanner scan = new Scanner(files.get(buildkey(key))); ) 
		{

			while( scan.hasNextLine() ) {
							
				String str = scan.findInLine(url);
				if (str != null){
					return true;
				}
				scan.nextLine();
			}
		} catch (FileNotFoundException e) {
			return false;
		} 
		
		return false;
	}
	
	public void saveURL(String url) {
		
		String name = url.replace("http://", "").replace("www.", "").replace("/", "");
		char key = name.substring(0, 1).toLowerCase().charAt(0);

		try ( 
			BufferedWriter bw = new BufferedWriter(new FileWriter(files.get(buildkey(key)),true));) 
		{
			bw.append(url);
			bw.newLine();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
	}

	private String buildkey(char key) {
		if ( key >= 'a' && key <= 'e') {
			return "a-e";
		}
		if ( key >='f' && key <='k') {
			return "f-k";
		}
		if ( key >='l' && key <='p') {
			return "l-p";
		}
		if ( key >='q' && key <='z') {
			return "q-z";
		}
		return "#";
	}
	
}
