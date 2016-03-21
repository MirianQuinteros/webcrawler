package com.fiuba.controllers;

import java.util.concurrent.BlockingQueue;

import com.fiuba.pojos.MessageType;
import com.fiuba.pojos.StatusInfo;
import com.fiuba.pojos.StatusInfo.AppStatus;
import com.fiuba.pojos.URLMessage;

public class Watcher implements Runnable {

	private BlockingQueue<URLMessage> queue;
	private StatusInfo info;
	
	public Watcher(BlockingQueue<URLMessage> queue, StatusInfo info) {
		this.queue = queue;
		this.info = info;
	}
	
	@Override
	public void run() {
		try {

			while ( info.status == AppStatus.RUNNING ) {
				wait();
				if (info.allIsDead() && queue.isEmpty()) {
					info.status = AppStatus.STOPPEP;
					URLMessage msg = new URLMessage();
					msg.setType(MessageType.POISON_PILL);
					queue.put(msg);
				}
			}
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	

}
