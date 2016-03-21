package com.fiuba.utils;

public class Utils {

	public static String buildUrlFolder(String url) {
		
		return "./pages/" + 
			 url.replace("http://", "")
				.replace("www.", "")
				.replace("/", "-")
				.replace(".com", "");
	}

	public static boolean isRepeated(String url) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public static String prepareLink(String resource) {
		
		if (resource.contains("../"))
			return resource.replace("../", "");
		
		if (resource.length() > Config.instance().getMaxLinkLength())
			return resource.substring(0, Config.instance().getMaxLinkLength());
		return resource;
	}

	public static String cleanName(String url) {
		return url.replace("http://", "")
				.replace("www.", "")
				.replace("/", "-")
				.replace(".com", "")
				.replace(".net", "")
				.replace(".ar", "");
	}

	public static String getDefaultFolder() {
		return "DownloadAllCrawler";
	}	

}
