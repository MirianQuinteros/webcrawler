package com.fiuba.controllers;

import java.util.concurrent.BlockingQueue;

import com.fiuba.pojos.MessageType;
import com.fiuba.pojos.StatusInfo;
import com.fiuba.pojos.URLMessage;
import com.fiuba.utils.Config;

public class Finalizer implements Runnable {

	private BlockingQueue<String> queue1;
	private BlockingQueue<URLMessage> queue2;
	private BlockingQueue<URLMessage> queue3;
	private StatusInfo info;

	public Finalizer(StatusInfo info, BlockingQueue<String> urls,
			BlockingQueue<URLMessage> urlsQueue,
			BlockingQueue<URLMessage> downloads) {
		this.queue1 = urls;
		this.queue2=urlsQueue;
		this.queue3 = downloads;
		this.info = info;
	}

	@Override
	public void run() {
		try {	
			while (true) {
				Thread.sleep(5000);
				if (info.allIsDead() && queue1.isEmpty() 
						&& queue2.isEmpty() && queue3.isEmpty()) {
					for(int r = 0; r < Config.instance().getMaxRepeatedDetectors() ; r ++) {
						queue1.put("-");
					}
					for (int i = 0; i < Config.instance().getMaxAnalizers(); i++) {
						URLMessage msg = new URLMessage();
						msg.setType(MessageType.POISON_PILL);
						queue2.put(msg);
					}
					for (int i = 0; i < Config.instance().getMaxDownloads(); i++) {
						URLMessage msg = new URLMessage();
						msg.setType(MessageType.POISON_PILL);
						queue3.put(msg);
					}
					System.out.println("soy el finalizer, ya envie las poison pill");
					break;
				} else {
					System.out.println("fetching: " + info.fetching + "; processing: " + info.processing + " ; dowloading: " + info.downloading);
				}
			}
		} catch (InterruptedException e ) {
			e.printStackTrace();
		}
	}

}
