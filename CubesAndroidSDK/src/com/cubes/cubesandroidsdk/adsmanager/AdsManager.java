package com.cubes.cubesandroidsdk.adsmanager;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;

import com.cubes.cubesandroidsdk.schedulers.AdsUpdateScheduler;
import com.cubes.cubesandroidsdk.schedulers.AdsUpdateScheduler.IAdsUpdateCallback;
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
	
	public AdsManager(Context context) {
		this.adsList = new ArrayList<AdsInstance>();
		this.context = context;
		this.updateScheduler = new AdsUpdateScheduler(this, Configuration.getInstance().getAdsUpdateIntervalMillis(), this.context);
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
	public void onAdsUpdate(List<AdsInstance> newAdsList) {
		if(newAdsList != null && !newAdsList.isEmpty()) {
			adsList.clear();
			adsList.addAll(newAdsList);
			context.sendBroadcast(new Intent(Configuration.ACTION_SEND_LOADER_CALLBACK));
			TestFlight.passCheckpoint("Ads loaded, callback sent!");
		}
	}
	
	public void dispose() {
		updateScheduler.stop();
		updateScheduler.dispose();
	}
}
