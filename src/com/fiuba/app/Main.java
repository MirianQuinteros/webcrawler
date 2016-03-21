package com.fiuba.app;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import com.fiuba.controllers.Downloader;
import com.fiuba.controllers.Finalizer;
import com.fiuba.controllers.RepeatedUrlDetector;
import com.fiuba.controllers.URLFileReader;
import com.fiuba.controllers.WebAnalyzer;
import com.fiuba.pojos.StatusInfo;
import com.fiuba.pojos.URLMessage;
import com.fiuba.pojos.URLRegister;
import com.fiuba.utils.Config;

public class Main {
	
	public static void main(String[] args) {

		URLRegister register = new URLRegister();
		BlockingQueue<String> rawUrls = new ArrayBlockingQueue<String>(Config.instance().getRawUrlQueueCap());
		BlockingQueue<URLMessage> urlsQueue = new ArrayBlockingQueue<>(Config.instance().getUrlsQueueCap());
		BlockingQueue<URLMessage> downloadsQueue = new ArrayBlockingQueue<>(Config.instance().getDownloadsQueueCap());
		
		StatusInfo info = new StatusInfo();
				
		URLFileReader reader = new URLFileReader(rawUrls);
		
		new Thread(reader).start();
		
		for (int r = 0; r < Config.instance().getMaxRepeatedDetectors(); r++) {
			RepeatedUrlDetector detector = new RepeatedUrlDetector(rawUrls, register);
			detector.setUrlsToProcessQ(urlsQueue);
			new Thread(detector).start();
		}
		
		for (int i = 0; i < Config.instance().getMaxAnalizers() ; i++) {
			WebAnalyzer analyzer = new WebAnalyzer(i, urlsQueue, info);
			analyzer.setUrls(rawUrls);
			analyzer.setDownloads(downloadsQueue);
			new Thread(analyzer).start();
		}
		
		for (int i = 0; i < Config.instance().getMaxDownloads() ; i++) {
			Downloader downloader = new Downloader(i, info, downloadsQueue);
			new Thread(downloader).start();
		}
				
		Finalizer finalizer = new Finalizer(info, rawUrls, urlsQueue, downloadsQueue);
		new Thread(finalizer).start();

	}

}
