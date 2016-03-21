package com.fiuba.controllers;

import java.util.concurrent.BlockingQueue;

import com.fiuba.pojos.MessageType;
import com.fiuba.pojos.URLMessage;
import com.fiuba.pojos.URLRegister;
import com.fiuba.utils.Utils;

public class RepeatedUrlDetector implements Runnable {

	private URLRegister urlRegister;
	private BlockingQueue<String> urlsQ;
	private BlockingQueue<URLMessage> urlsToProcessQ;
	
	public RepeatedUrlDetector(BlockingQueue<String> urls, URLRegister urlProcessed ) {
		this.urlsQ = urls;
		this.urlRegister = urlProcessed;
	}

	@Override
	public void run() {
		
		while (true) {
			
			try {
				String element = urlsQ.take();
				
				if (element.equals("-")) {
					break;
				}
				
				String pureUrl = element.substring(0, element.lastIndexOf("#") );
				System.out.println(pureUrl);
				
				if (!urlRegister.isRepeated(pureUrl)) {
					
					urlRegister.add(pureUrl);
					
					String n = element.substring(element.lastIndexOf("#")+1);
					URLMessage msg = new URLMessage();
					msg.setType(MessageType.URL_TYPE);
					msg.setUrl(pureUrl);
					msg.setRound( new Integer(n) );
					
					//TODO VER COMO ORGANIZAR LAS FOLDER
					msg.setFolder(Utils.getDefaultFolder());

					if (urlsToProcessQ.remainingCapacity() > 0)
						urlsToProcessQ.put(msg);
				}
				
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		
	}

	public void setUrlsToProcessQ(BlockingQueue<URLMessage> urlsToProcess) {
		this.urlsToProcessQ = urlsToProcess;
	}

}
