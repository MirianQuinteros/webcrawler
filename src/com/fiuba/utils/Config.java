package com.fiuba.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
 

public class Config {

	private InputStream inputStream;
	private Properties prop;
	private static Config instance;

	public static Config instance () {
		if (instance == null)
			instance = new Config();
		return instance;

	}

	private Config() {

		initPropFile();

	}

	private void initPropFile() {

		try {
			prop = new Properties();
			String propFileName = "resources/config.properties";
			
			inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);
			
			if (inputStream != null) {
				prop.load(inputStream);
			} else {
				throw new FileNotFoundException("property file '"
						+ propFileName + "' not found in the classpath");
			}

		} catch (Exception e) {
			System.out.println("Exception: " + e);
		} finally {
		
			try {
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public Integer getRawUrlQueueCap() {

		return new Integer(prop.getProperty("maxRawUrlsQueueCapacity", "100" ));

	}
	
	public Integer getUrlsQueueCap() {

		return new Integer(prop.getProperty("maxUrlQueueCapacity", "100" ));

	}

	public Integer getDownloadsQueueCap() {

		return new Integer(prop.getProperty("maxDownloadsQueueCapacity", "100" ));

	}
	
	public Integer getMaxAnalizers() {

		return new Integer(prop.getProperty("maxAnalyzers", "5" ));

	}

	public Integer getMaxDownloads() {

		return new Integer(prop.getProperty("maxDownloads", "20"));

	}

	public Integer getDownloadTimeout() {

		return new Integer(prop.getProperty("downloader.timeout", "3"));

	}

	public Integer getMaxLinkLength() {

		return new Integer(prop.getProperty("maxLinkLength", "100"));

	}
	
	public Integer getMaxLinks() {

		return new Integer(prop.getProperty("maxLinksPerPage", "10"));

	}
	
	public Integer getAnidLimit() {

		return new Integer(prop.getProperty("maxAnidation", "3"));

	}
	
	public Integer getMaxRepeatedDetectors() {

		return new Integer(prop.getProperty("maxDetectors", "10"));

	}
}
