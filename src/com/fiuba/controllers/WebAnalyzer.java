package com.fiuba.controllers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.fiuba.pojos.MessageType;
import com.fiuba.pojos.StatusInfo;
import com.fiuba.pojos.URLMessage;
import com.fiuba.utils.Config;
import com.fiuba.utils.Utils;
import com.fiuba.validation.URLFormatValidator;


public class WebAnalyzer implements Runnable {

	private StatusInfo info;
	private BlockingQueue<String> urls;
	private BlockingQueue<URLMessage> queue;
	private BlockingQueue<URLMessage> downloads;
	private int id;
	
	public WebAnalyzer(int i, BlockingQueue<URLMessage> queue, StatusInfo info) {
		
		this.id = i;
		this.info = info;
		this.queue = queue;	
	}

	@Override
	public void run() {
		System.out.println("HOLA, SOY UN ANALYZER " + id);
		URLMessage msg;
		try {

			while ( true ) {
				
				//System.out.println("Esperando una url de la cola");
				msg = queue.take();
				if (msg.getType() == MessageType.POISON_PILL) {
					
					System.out
							.println("me tengo que morir " + id);
					break;
				}
				process(msg);
			}

		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	private void process(URLMessage msg) {

		fetchHTML(msg);
		
		// should run in thread
		processHTML(msg);
		
		// should run in thread
		downloadResources(msg);
	}

	private void downloadResources(URLMessage msg) {
		
		info.processing.incrementAndGet();
		
		this.searchForMultimedia(msg);
		
		this.searchWebResources(msg);
		
		info.processing.decrementAndGet();
	}

	private void processHTML(URLMessage msg) {
		
		System.out.println("ID " + id + " Processing URL");

		Integer newLevel = msg.getRound() + 1; 
		
		if (newLevel.equals(Config.instance().getAnidLimit())) {
			
			return;
		}

		info.processing.incrementAndGet();

		try {
			Document doc = Jsoup.connect(msg.getUrl()).get();
			
			Elements links = doc.select("a[href]");
						
			for (Element link : links) {
				
				String res = link.attr("abs:href");
				
				if ( URLFormatValidator.validate(res) 
						&& !URLFormatValidator.validateMultimedia(res) ) {
					//System.out.println("voy a poner una url de nivel " + (msg.getRound()+1));
					
					if (urls.remainingCapacity() > 0)
						urls.put(res.concat("#" + newLevel));
				}
			}
		} catch (SocketTimeoutException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		info.processing.decrementAndGet();
		
	}

	@SuppressWarnings("resource")
	private void fetchHTML(URLMessage msg) {
		
		System.out.println("ID " + id + " Fetching URL");
		info.fetching.incrementAndGet();
		
		try {
			String content = null;
			URLConnection connection = new URL(msg.getUrl()).openConnection();
			connection.setReadTimeout(6000);
			Scanner scanner;
			scanner = new Scanner( connection.getInputStream() );
			scanner.useDelimiter("\\Z");
			content = scanner.next();
		
			saveHtml(msg, content);
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (RuntimeException e) {
			e.printStackTrace();
		} catch (Exception e) {	
			e.printStackTrace();
		}
		
		info.fetching.decrementAndGet();
		
	}
	
	private void searchForMultimedia(URLMessage msg) {

		Document doc;
		try {
			
			doc = Jsoup.connect(msg.getUrl()).timeout(6000).get();

			Elements img = doc.getElementsByTag("img");
			Elements links = doc.select("a[href]");

			for (Element el : img) {

				String src = el.absUrl("src");

				src = Utils.prepareLink(src);
				
				if ( URLFormatValidator.validate(src) &&
						!urls.contains(src) ) {
					
					URLMessage downloadMsg = new URLMessage();
					downloadMsg.setUrl(src);
					downloadMsg.setType(MessageType.DOWNLOAD);
					downloadMsg.setParent(msg.getUrl());
					downloadMsg.setFolder(Utils.getDefaultFolder());
					if (downloads.remainingCapacity() > 0) {
						downloads.put(downloadMsg);
					}
				}
				
			}

			for (Element link : links) {

				String res = link.attr("abs:href");
				
				res = Utils.prepareLink(res);
				
				if ( URLFormatValidator.validateMultimedia(res)
						&& !urls.contains(res)) {
					
					URLMessage downloadMsg = new URLMessage();
					downloadMsg.setUrl(res);
					downloadMsg.setType(MessageType.DOWNLOAD);
					downloadMsg.setParent(msg.getUrl());
					downloadMsg.setFolder(Utils.getDefaultFolder());
					if (downloads.remainingCapacity() > 0)
						downloads.put(downloadMsg);
				}
			}
		} catch (HttpStatusException e) {
			e.printStackTrace();
		} catch (SocketTimeoutException se ) {
			se.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	private List<String> searchWebResources(URLMessage msg) {
		
		List<String> results = new ArrayList<String>();

		try {
			
			Document doc = Jsoup.connect(msg.getUrl()).timeout(6000).get();
			
			Elements scripts = doc.head().select("script");
			
			Elements csss = doc.head().select("link[type$=css]");
			
			results.addAll(searchScripts(msg, scripts));
			
			results.addAll(searchCustoms(msg, csss));
			
		} catch (HttpStatusException e) {
			e.printStackTrace();
			return results;
		} catch (SocketTimeoutException se) {
			se.printStackTrace();
			return results;
		} catch (IOException e) {
			e.printStackTrace();
			return results;
		} catch (Exception e) {
			e.printStackTrace();
			return results;
		}
		
		return results;
	}
	
	private Collection<? extends String> searchCustoms(URLMessage msg,
			Elements csss) {

		List<String> results = new ArrayList<String>();

		for (Element elc : csss) {
			try {
				String res = elc.attr("abs:href");

				res = Utils.prepareLink(res);

				if (URLFormatValidator.validate(res)) {
					URLMessage downloadMsg = new URLMessage();
					downloadMsg.setUrl(res);
					downloadMsg.setType(MessageType.DOWNLOAD);
					downloadMsg.setParent(msg.getUrl());
					downloadMsg.setFolder(Utils.getDefaultFolder());
					if (downloads.remainingCapacity() > 0)
						downloads.put(downloadMsg);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return results;
	}

	private List<String> searchScripts(URLMessage msg, Elements scripts) {

		List<String> results = new ArrayList<String>();

		for (Element els : scripts) {
			try {
				String res = els.attr("abs:src");

				res = Utils.prepareLink(res);

				if (URLFormatValidator.validate(res)) {
					URLMessage downloadMsg = new URLMessage();
					downloadMsg.setUrl(res);
					downloadMsg.setType(MessageType.DOWNLOAD);
					downloadMsg.setParent(msg.getUrl());
					downloadMsg.setFolder(Utils.getDefaultFolder());
					if (downloads.remainingCapacity() > 0)
						downloads.put(downloadMsg);

				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return results;
		
	}

	private void saveHtml(URLMessage msg, String content) {
		System.out.println( "ID " + id + " Guardando HTML");
		try {
			File folder = new File(msg.getFolder());
			folder.mkdirs(); 

			File file = new File(folder, Utils.cleanName(msg.getUrl()) + ".html");
			FileWriter fWriter = new FileWriter (file);
		    PrintWriter pWriter = new PrintWriter (fWriter);
		    pWriter.append(content);
		    pWriter.close();

		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void setDownloads(BlockingQueue<URLMessage> downloads) {
		this.downloads = downloads;
	}

	public void setUrls(BlockingQueue<String> urls) {
		this.urls = urls;
	}

}
