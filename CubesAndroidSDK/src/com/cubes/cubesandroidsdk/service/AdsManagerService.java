package com.cubes.cubesandroidsdk.service;

import java.util.List;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.cubes.cubesandroidsdk.adsmanager.AdsInstance;
import com.cubes.cubesandroidsdk.adsmanager.AdsManager;
import com.testflightapp.lib.TestFlight;

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
		adsManager = new AdsManager(this);
		TestFlight.log("Service started");
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return binder;
	}
	
	@Override
	public void onDestroy() {
		adsManager.dispose();
		TestFlight.log("Service stopped");
		TestFlight.sendsCrashes();
		TestFlight.sendsLogs();
		super.onDestroy();
	}
	
	@Override
	public boolean onUnbind(Intent intent) {
		Log.v("SDK", "on unbind");
		return super.onUnbind(intent);
	}

	public class AdsManagerServiceBinder extends Binder {
		
		/**
		 * Get list of actual ads. 
		 * @return
		 */
		public List<AdsInstance> getAdsList() {
			
			return adsManager.getAds();
		}
	}
}
