package com.fiuba.validation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class URLFormatValidator {

	private static final String PATTERN = "((https?|ftp|gopher|telnet|file):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";

	private static final String IMAGE_PATTERN = "([^\\s]+(\\.(?i)(jpg|png|jpeg|gif|bmp))$)";

	private static final String AUDIO_VIDEO_PATTERN = "([^\\s]+(\\.(?i)(mp3|wav|ogg|wmv|swf|flv|avi|mp4|mpeg|mov))$)";
	
	private static final String COMPRESSED_PATTERN = "([^\\s]+(\\.(?i)(zip|gz|rar|bin|jar|apk|deb|gzip|7z|rpm|tar))$)";
	
	private static final String OTHER_MULTIMEDIA_PATTERN = "([^\\s]+(\\.(?i)(aac|exe|lib|hex|dat|doc|docx|xls|ppt|pdf|xml|war))$)";

	private static final String WEB_RES_PATTERN = "([^\\s]+(\\.(?i)(js|css|))$)";

	
	private static Pattern imageP = null;
	private static Pattern audioP = null;
	private static Pattern compressedP = null;
	private static Pattern otherP = null;
	private static Pattern generalP = null;
	private static Pattern webResP = null;
	
	public static boolean validate(String url) {
		
		if ( generalP == null ) {
			generalP = Pattern.compile(PATTERN, Pattern.CASE_INSENSITIVE);
		}

		Matcher m = generalP.matcher(url);
		return m.matches();

	}

	public static boolean validateImage(String res) {
		
		if (imageP == null) {
			imageP = Pattern.compile(IMAGE_PATTERN);
		}
		
		Matcher m = imageP.matcher(res);
		return m.matches();

	}

	
	public static boolean validateAudioVideo(String res) {
		
		if (audioP == null) {
			audioP = Pattern.compile(AUDIO_VIDEO_PATTERN);
		}
		
		Matcher m = audioP.matcher(res);
		return m.matches();

	}
	
	public static boolean validateCompressed(String res) {
		
		if (compressedP == null) {
			compressedP = Pattern.compile(COMPRESSED_PATTERN);
		}
		
		Matcher m = compressedP.matcher(res);
		return m.matches();
		
	}
	
	public static boolean validateOther(String res) {
		
		if (otherP == null) {
			otherP = Pattern.compile(OTHER_MULTIMEDIA_PATTERN);
		}
		
		Matcher m = otherP.matcher(res);
		return m.matches();
	}
	
	public static boolean validateWebResource(String res) {
		
		if (webResP == null) {
			webResP = Pattern.compile(WEB_RES_PATTERN);
		}
		
		Matcher m = webResP.matcher(res);
		return m.matches();
	}
	
	public static boolean validateMultimedia(String res) {
		
		return validate(res)
				&& (validateImage(res)
				|| validateAudioVideo(res)
				|| validateCompressed(res)
				|| validateOther(res));
	}
	
}
