package com.cubes.cubesandroidsdk.adsmanager;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that encapsulate business-logic of application - control of update, store and remove ads 
 * @author makarenko.s
 *
 */
public class AdsManager {
	
	private boolean isAdsUpdated;
	private List<AdsInstance> adsList;
	
	public AdsManager() {
		this.adsList = new ArrayList<AdsInstance>();
	}
	
	public boolean isAdsUpdated() {
		
		return isAdsUpdated;
	}

	/**
	 * 
	 * @return - all ads that application must show
	 */
	public List<AdsInstance> getAds() {
		isAdsUpdated = false;
		return adsList;
	}
}
