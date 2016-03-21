package com.fiuba.pojos;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;

public class StatusInfo {

	public StatusInfo() {
		this.processing = new AtomicInteger(0);
		this.downloading = new AtomicInteger(0);
		this.fetching = new AtomicInteger(0);
		this.urlCount = new AtomicInteger(0);
	}
	
	public enum ContentType {
		AUDIO, IMAGE, VIDEO, JS, CSS, PHP, TEXT, ZIPRAR, OTHER;
	}
	
	public enum AppStatus {
		RUNNING, STOPPEP, FINISHED;
	}
	
	public AtomicInteger processing;
	public AtomicInteger fetching;
	public AtomicInteger downloading;
	public AtomicInteger urlCount;
	public AtomicIntegerArray resourcesCounter;
	public volatile AppStatus status;

	public synchronized String buildStatus() {
		return "";
	}
	
	public Boolean allIsDead() {
		
		return processing.get()==0 && downloading.get()==0
				&& fetching.get()==0;
	}

}
