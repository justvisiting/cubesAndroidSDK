package com.cubes.cubesandroidsdk.schedulers;

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
			long intervalInMillis, AdsUpdater updater) {
		super(intervalInMillis);
		this.updater = updater;
	}

	@Override
	protected void onTick() {

		updater.loadAd();
	}
	
	@Override
	public void start() {

		updater.initialLoading();
		super.start();
	}


	@Override
	public void dispose() {
		
		super.dispose();
		updater.dispose();
	}
}
