package com.fiuba.pojos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class URLRegister {

	private Map<String, List<String>> map;
	
	public URLRegister() {
		map = new HashMap<String, List<String>>();
	}
	
	public boolean isRepeated(String url) {
		
		String firstLetter = url.replace("http://", "").replace("www.", "").substring(0, 1);
		if ( map.get(firstLetter) == null) {
			return false;
		}
		
		return search(map.get(firstLetter), url);
	}
	
	private synchronized boolean search(List<String> list, String url) {
		for(String s : list) {
			if (s.equals(url)) {
				return true;
			}
		}
		return false;
	}

	public synchronized void add( String url ) {
		
		String firstLetter = url.replace("http://", "").replace("www.", "").substring(0, 1);
		
		if ( map.get(firstLetter) == null ) {
			List<String> urls = new ArrayList<String>();
			map.put(firstLetter, urls);
		}
		
		map.get(firstLetter).add(url);
		
	}
	
}
