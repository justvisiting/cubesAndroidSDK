package com.cubes.cubesandroidsdk.adsmanager.updater;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

import android.text.TextUtils;

import com.cubes.cubesandroidsdk.adsmanager.AdsInstance;

/**
 * Represents request for getting ads from server
 * @author makarenko.s
 *
 */
public class AdsRequest {
	
	private int status;
	private int requestType;
	private AdsInstance instance;
	private Deque<String> fullscreenUrlQueue;
	private String barUrl;
	private String xmlUrl;
	
	public String getXmlUrl() {
		return xmlUrl;
	}

	public void setXmlUrl(String xmlUrl) {
		this.xmlUrl = xmlUrl;
	}

	public static final int STATUS_PROGRESS = 300;
	public static final int STATUS_FINISHED = 301;
	
	public static final int TYPE_XML = 302;
	public static final int TYPE_IMAGE_FULLSCREEN = 303;
	public static final int TYPE_IMAGE_BAR = 304;
	
	public AdsRequest() {
		
		fullscreenUrlQueue = new ArrayDeque<String>();
	}
	
	public void setbarUrl(String barUrl) {
		this.barUrl = barUrl;
	}
	
	public String getbarUrl() {
		
		return barUrl;
	}
	
	public boolean hasBarUrl() {
		return !TextUtils.isEmpty(barUrl);
	}
	
	public boolean hasMoreUrls() {
		return !fullscreenUrlQueue.isEmpty();
	}
	
	public String getNextFullscreenUrl() {
		return fullscreenUrlQueue.poll();
	}
	
	public void setFullscreenUrls(List<String> urls) {
		fullscreenUrlQueue.addAll(urls);
	}
	
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getRequestType() {
		return requestType;
	}

	public void setRequestType(int requestType) {
		this.requestType = requestType;
	}

	public AdsInstance getInstance() {
		return instance;
	}

	public void setInstance(AdsInstance instance) {
		this.instance = instance;
	}
}
