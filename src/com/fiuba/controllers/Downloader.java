package com.fiuba.controllers;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.ClosedByInterruptException;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.fiuba.pojos.MessageType;
import com.fiuba.pojos.StatusInfo;
import com.fiuba.pojos.URLMessage;

public class Downloader implements Runnable {
	
	private Integer id;
	private StatusInfo info;
	private BlockingQueue<URLMessage> queue;
	private ExecutorService executor;
	
	public Downloader(Integer id, StatusInfo info, BlockingQueue<URLMessage> queue) {
		this.id = id;
		this.info = info;
		this.queue = queue;
		this.executor = Executors.newSingleThreadExecutor();
	}

	@Override
	public void run() {
		
		while (true) {
			try {
				URLMessage msg = queue.take();
				
				if (msg.getType().equals(MessageType.POISON_PILL)) {
					executor.shutdown();
					if ( !executor.awaitTermination(5, TimeUnit.MINUTES) ) {
						executor.shutdownNow();
					}
					System.out.println("Soy el downloader " + id + " y me voy a morir.");
					break;
				}
			
				download(msg);
			
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}

	private void download(URLMessage msg) {
		
		info.downloading.incrementAndGet();
		try {

//			System.out.println("deSCARGANDO RECURSO " + msg.getUrl());

			executor.invokeAll(Arrays.asList(new DownloadTask(msg)), 10, TimeUnit.MINUTES);
		
		} catch (InterruptedException e ) {
			e.printStackTrace();			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		info.downloading.decrementAndGet();
	}

	private class DownloadTask implements Callable<Integer> {

		private URLMessage msg;
		
		public DownloadTask(URLMessage msg) {
			this.msg = msg;
		}
		
		@Override
		public Integer call() throws Exception {
			
			try {
				saveResource(msg);
				//Thread.sleep( 5000 +(long) (Math.random()* 85000));
			} catch (InterruptedException e ) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return 0;
		}
		
		private void saveResource(URLMessage msg) throws Exception {

			String path = msg.getFolder()+ "/"+ msg.getUrl().replace("/",  "");
			
			OutputStream outStream = null;
	        URLConnection uCon = null;

	        InputStream is = null;
	        try {
	            URL url;
	            byte[] buf;
	            int byteRead = 0;
	            url = new URL(msg.getUrl());
	            outStream = new BufferedOutputStream(new FileOutputStream(path));

	            uCon = url.openConnection();
	            uCon.setReadTimeout(600000);
	            is = uCon.getInputStream();
	            buf = new byte[1024];
	            while ((byteRead = is.read(buf)) != -1) {
	                outStream.write(buf, 0, byteRead);
	            }
	        } catch (ClosedByInterruptException e) {
	        	e.printStackTrace();
	        } catch (Exception e) {
	            e.printStackTrace();
	        } finally {
	            try {
	                is.close();
	                outStream.close();
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        }
		}
		
	}

}
