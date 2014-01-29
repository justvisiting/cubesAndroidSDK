package com.cubes.cubesandroidsdk.adsmanager;


public class AdsInstance {
	
	private int adsType;
	private int clickAction;
	private String clickData;
	private String bannerUriStrig;
	private String barUriString;
	private String barTextString;

	public String getBarUriString() {
		return barUriString;
	}

	public String getBarTextString() {
		return barTextString;
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

	public String getBannerUriString() {
		return bannerUriStrig;
	}
	
	public AdsInstance(int adsType, int clickAction, String clickData,
			String bannerUriStrig, String barUriString, String barTextString) {
		this.adsType = adsType;
		this.clickAction = clickAction;
		this.clickData = clickData;
		this.bannerUriStrig = bannerUriStrig;
		this.barUriString = barUriString;
		this.barTextString = barTextString;
	}
}
