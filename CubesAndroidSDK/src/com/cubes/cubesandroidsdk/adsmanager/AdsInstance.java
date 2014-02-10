package com.cubes.cubesandroidsdk.adsmanager;

import java.util.ArrayList;
import java.util.List;

import android.text.TextUtils;


public class AdsInstance {
	
	private int adsType;
	private int clickAction;
	private String clickData;
	private String barUriString;
	private String barTextString;
	private boolean isExpandable;
	private List<String> fullscreenAdsList;

	public boolean isExpandable() {
		return isExpandable;
	}

	public void setExpandable(boolean isExpandable) {
		this.isExpandable = isExpandable;
	}

	public String getBarUriString() {
		return barUriString;
	}

	public String getBarTextString() {
		return barTextString;
	}

	public void setBarUriString(String barUriString) {
		this.barUriString = barUriString;
	}
	
	public List<String> getFullscreenAds() {
		return fullscreenAdsList;
	}
	
	public void setFullscreenAds(List<String> list) {
		fullscreenAdsList = list;
	}

	public int getAdsType() {
		return adsType;
	}

	public int getClickAction() {
		return clickAction;
	}

	public String getClickData() {
		return clickData;
	}

	
	public AdsInstance(int adsType, int clickAction, String clickData,
			List<String> fullscreenAdsList, String barUriString, String barTextString) {
		this.adsType = adsType;
		this.clickAction = clickAction;
		this.clickData = clickData;
		this.barUriString = barUriString;
		this.barTextString = barTextString;
		this.fullscreenAdsList = new ArrayList<String>();
	}
	
	public boolean hasTextBar() {
		return (!TextUtils.isEmpty(barTextString));
	}

	public void setBarTextString(String barTextString) {
		this.barTextString = barTextString;
	}

	@Override
	public boolean equals(Object o) {
		
		if(!(o instanceof AdsInstance)) {
			return false;
		}
		AdsInstance instance = (AdsInstance) o;
		return (instance.getClickData().equalsIgnoreCase(clickData)) && (instance.getAdsType() == adsType);
	}
	
	
}
