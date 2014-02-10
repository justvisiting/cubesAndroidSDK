package com.cubes.cubesandroidsdk.schedulers;

import android.content.Context;

import com.cubes.cubesandroidsdk.adsmanager.AdsInstance;
import com.cubes.cubesandroidsdk.adsmanager.updater.AdsUpdater;
import com.cubes.cubesandroidsdk.adsmanager.updater.AdsUpdater.IAdsUpdateCallback;

/**
 * Perform update ads by predefined schedule. Result will deliver to client
 * through {@link IAdsUpdateCallback} as list of {@link AdsInstance}
 * 
 * @author makarenko.s
 * 
 */
public class AdsUpdateScheduler extends AbstractScheduler {

	private AdsUpdater updater;

	public AdsUpdateScheduler(IAdsUpdateCallback callback,
			long intervalInMillis, Context context) {
		super(intervalInMillis);
		updater = new AdsUpdater(context, callback);
	}

	@Override
	protected void onTick() {

		updater.loadAd();
	}
	
	@Override
	public void start() {

		updater.loadAd();
		super.start();
	}


	@Override
	public void dispose() {
		
		super.dispose();
		updater.dispose();
	}
}
