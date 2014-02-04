package com.cubes.cubesandroidsdk.adsmanager;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.cubes.cubesandroidsdk.schedulers.AdsUpdateScheduler;
import com.cubes.cubesandroidsdk.schedulers.AdsUpdateScheduler.IAdsUpdateCallback;

/**
 * Class that encapsulate business-logic of application - control of update, store and remove ads 
 * @author makarenko.s
 *
 */
public class AdsManager implements IAdsUpdateCallback {
	
	private boolean isAdsUpdated;
	private List<AdsInstance> adsList;
	private AdsUpdateScheduler updateScheduler;
	private Context context;
	
	public AdsManager(Context context) {
		this.adsList = new ArrayList<AdsInstance>();
		this.context = context;
		this.updateScheduler = new AdsUpdateScheduler(this, Configuration.getInstance().getAdsUpdateIntervalMillis(), this.context);
		this.updateScheduler.start();
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

	@Override
	public void onAdsUpdate(List<AdsInstance> newAdsList) {
		if(newAdsList != null && !newAdsList.isEmpty()) {
			adsList.clear();
			adsList.addAll(newAdsList);
			isAdsUpdated = true;
		}
	}
	
	public void dispose() {
		updateScheduler.stop();
		updateScheduler.dispose();
	}
}
