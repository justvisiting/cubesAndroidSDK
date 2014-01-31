package com.cubes.cubesandroidsdk.service;

import java.util.List;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.cubes.cubesandroidsdk.adsmanager.AdsInstance;
import com.cubes.cubesandroidsdk.adsmanager.AdsManager;

/**
 * Service that provide interface to ads storage and keep long running operations and objects.
 * 
 * @author makarenko.s
 *
 */
public class AdsManagerService extends Service {
	
	private AdsManagerServiceBinder binder;
	private AdsManager adsManager;

	@Override
	public void onCreate() {
		
		super.onCreate();
		binder = new AdsManagerServiceBinder();
		adsManager = new AdsManager();
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return binder;
	}
	
	@Override
	public void onDestroy() {
		
		super.onDestroy();
	}
	
	public class AdsManagerServiceBinder extends Binder {
		
		/**
		 * Get list of actual ads. 
		 * @return
		 */
		public List<AdsInstance> getAdsList() {
			
			return adsManager.getAds();
		}
		
		/**
		 * Check that ads was updated. If new ads were loaded it set to true
		 * @return - true if ads were updated, false otherwise
		 */
		public boolean isAdsUPdated() {
			
			return adsManager.isAdsUpdated();
		}
	}
}
