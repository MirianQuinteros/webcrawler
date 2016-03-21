package com.fiuba.utils;

import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import com.fiuba.pojos.StatusInfo;
import com.fiuba.pojos.StatusInfo.AppStatus;


public class Informer implements Runnable {
	
	StatusInfo info;
	
	public Informer(StatusInfo info) {
		this.info = info;
	}

	@Override
	public void run() {

		try {
			FileHandler fh = new FileHandler("status.log");
			Logger logger = Logger.getLogger("Status");
			logger.addHandler(fh);
			SimpleFormatter formatter = new SimpleFormatter();  
			fh.setFormatter(formatter);  
        
			while ( info.status == AppStatus.RUNNING ) {
				
				Thread.sleep(3000);
		        logger.info("Obteniendo HTML: " + info.fetching);
		        logger.info("Procesando HTML: " + info.processing);
		        //TODO MORE DATA
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (Exception e) {
			
		}

	}

}
