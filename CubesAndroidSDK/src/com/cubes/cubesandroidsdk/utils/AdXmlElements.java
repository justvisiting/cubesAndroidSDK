package com.cubes.cubesandroidsdk.utils;

import java.util.List;



public class AdXmlElements {
	private String _clickUrl;
	private String _bannerUrl;
	private ClickType _clickType;
	private AdType _adType;
	private List<String> ExpandedUrl;
	
	public List<String> getExpandedUrl() {
		return ExpandedUrl;
	}
	public void setExpandedUrl(List<String> expandedUrl) {
		ExpandedUrl = expandedUrl;
	}
	public String get_clickUrl() {
		return _clickUrl;
	}
	public void set_clickUrl(String _clickUrl) {
		this._clickUrl = _clickUrl;
	}
	public String get_bannerUrl() {
		return _bannerUrl;
	}
	public void set_bannerUrl(String _bannerUrl) {
		this._bannerUrl = _bannerUrl;
	}
	public ClickType get_clickType() {
		return _clickType;
	}
	public void set_clickType(ClickType _clickType) {
		this._clickType = _clickType;
	}
	public AdType get_adType() {
		return _adType;
	}
	public void set_adType(AdType _adType) {
		this._adType = _adType;
	}
}
