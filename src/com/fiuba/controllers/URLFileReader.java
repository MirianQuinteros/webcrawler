package com.fiuba.controllers;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;


public class URLFileReader implements Runnable {
	
	private static String URL_FILE = "urls";
	private BlockingQueue<String> urlsQueue;
	
	public URLFileReader(BlockingQueue<String> queue ) {
		this.urlsQueue = queue;
	}
	
	@SuppressWarnings("resource")
	private List<String> readURLs() {

		List<String> result = new ArrayList<String>();
		
		BufferedReader in;
		
		try {

			in = new BufferedReader(new FileReader(URL_FILE));

			String url;

			while ((url = in.readLine()) != null) {

				result.add(url);

			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	
	}

	@Override
	public void run() {
		try {
		
		for (String url : readURLs()) {
			
				//System.out.println("Voy a poner una url en la cola");
				urlsQueue.put(url.concat("#0"));
		}
		
		} catch (InterruptedException e) {
				
		} catch (Exception e) {
				
		}
		
		
		
		
	}

}
