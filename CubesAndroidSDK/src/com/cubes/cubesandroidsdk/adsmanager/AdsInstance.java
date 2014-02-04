package com.cubes.cubesandroidsdk.adsmanager;


public class AdsInstance {
	
	private int adsType;
	private int clickAction;
	private String clickData;
	private String bannerUriString;
	private String barUriString;
	private String barTextString;

	public String getBarUriString() {
		return barUriString;
	}

	public String getBarTextString() {
		return barTextString;
	}

	public void setBarUriString(String barUriString) {
		this.barUriString = barUriString;
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
		return bannerUriString;
	}
	
	public AdsInstance(int adsType, int clickAction, String clickData,
			String bannerUriStrig, String barUriString, String barTextString) {
		this.adsType = adsType;
		this.clickAction = clickAction;
		this.clickData = clickData;
		this.bannerUriString = bannerUriStrig;
		this.barUriString = barUriString;
		this.barTextString = barTextString;
	}
	
	public AdsInstance() {
		
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
