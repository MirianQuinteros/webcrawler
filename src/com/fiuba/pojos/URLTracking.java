package com.fiuba.pojos;

import java.util.Date;

public class URLTracking {

	private String url;
	private Integer resourcesCount;
	private Date startAnalisys;
	private Date stopAnalisys;
	private Date firstDownloadTimeStamp;
	private Date lastDownloadTimeStamp;
	
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public Integer getResourcesCount() {
		return resourcesCount;
	}
	public void setResourcesCount(Integer resourcesCount) {
		this.resourcesCount = resourcesCount;
	}
	public Date getStartAnalisys() {
		return startAnalisys;
	}
	public void setStartAnalisys(Date startAnalisys) {
		this.startAnalisys = startAnalisys;
	}
	public Date getStopAnalisys() {
		return stopAnalisys;
	}
	public void setStopAnalisys(Date stopAnalisys) {
		this.stopAnalisys = stopAnalisys;
	}
	public Date getFirstDownloadTimeStamp() {
		return firstDownloadTimeStamp;
	}
	public void setFirstDownloadTimeStamp(Date firstDownloadTimeStamp) {
		this.firstDownloadTimeStamp = firstDownloadTimeStamp;
	}
	public Date getLastDownloadTimeStamp() {
		return lastDownloadTimeStamp;
	}
	public void setLastDownloadTimeStamp(Date lastDownloadTimeStamp) {
		this.lastDownloadTimeStamp = lastDownloadTimeStamp;
	}
	
	//Falta lo de la espera
	
	
}
