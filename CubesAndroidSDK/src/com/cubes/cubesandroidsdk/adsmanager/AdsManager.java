package com.cubes.cubesandroidsdk.adsmanager;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;

import com.cubes.cubesandroidsdk.adsmanager.updater.AdsUpdater.IAdsUpdateCallback;
import com.cubes.cubesandroidsdk.adsmanager.updater.CacheManager;
import com.cubes.cubesandroidsdk.config.Configuration;
import com.cubes.cubesandroidsdk.schedulers.AdsUpdateScheduler;
import com.testflightapp.lib.TestFlight;

/**
 * Class that encapsulate business-logic of application - control of update, store and remove ads 
 * @author makarenko.s
 *
 */
public class AdsManager implements IAdsUpdateCallback {
	
	private List<AdsInstance> adsList;
	private AdsUpdateScheduler updateScheduler;
	private Context context;
	private volatile CacheManager cacheManager;
	
	public AdsManager(Context context) {
		this.adsList = new ArrayList<AdsInstance>();
		this.context = context;
		this.cacheManager = new CacheManager(context);
		this.updateScheduler = new AdsUpdateScheduler(this, Configuration.getInstance().getAdsUpdateIntervalMillis(), cacheManager, context);
		this.updateScheduler.start();
	}
	
	/**
	 * 
	 * @return - all ads that application must show
	 */
	public List<AdsInstance> getAds() {
		return adsList;
	}
	
	@Override
	public void onAdsUpdated(List<AdsInstance> newAdsList) {
		if(newAdsList != null && !newAdsList.isEmpty()) {
			adsList.clear();
			adsList.addAll(newAdsList);
			context.sendStickyBroadcast(new Intent(Configuration.ACTION_SEND_LOADER_CALLBACK));
			cacheManager.clearCache();
			TestFlight.passCheckpoint("Ads loaded, callback sent!");
		}
	}
	
	public void dispose() {
		cacheManager.clearCache();
		updateScheduler.stop();
		updateScheduler.dispose();
	}

	@Override
	public void onStartUpdate() {

		context.removeStickyBroadcast(new Intent(Configuration.ACTION_SEND_LOADER_CALLBACK));
	}
}
