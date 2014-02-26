package com.cubes.cubesandroidsdk.adsmanager;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.cubes.cubesandroidsdk.adsmanager.updater.AdsUpdater;
import com.cubes.cubesandroidsdk.adsmanager.updater.AdsUpdater.IAdsUpdateCallback;
import com.cubes.cubesandroidsdk.adsmanager.updater.CacheManager;
import com.cubes.cubesandroidsdk.adsmanager.updater.Utils;
import com.cubes.cubesandroidsdk.config.Configuration;
import com.cubes.cubesandroidsdk.schedulers.AdsUpdateScheduler;
import com.testflightapp.lib.TestFlight;

/**
 * Class that encapsulate business-logic of application - control of update, store and remove ads 
 * @author makarenko.s
 *
 */
public class AdsManager implements IAdsUpdateCallback {
	
	private static final String DEFAULT_ADS_PATH = "defaultAdsPath";
	
	private AdsUpdater adsUpdater;
	private List<AdsInstance> adsList;
	private AdsUpdateScheduler updateScheduler;
	private Context context;
	private volatile CacheManager cacheManager;
	
	public AdsManager(Context context) {
		this.adsList = new ArrayList<AdsInstance>();
		this.context = context;
		this.cacheManager = new CacheManager(context);
		this.adsUpdater = new AdsUpdater(context, cacheManager, this, !isDefaultValueSaved());
		this.updateScheduler = new AdsUpdateScheduler(this, Configuration.getInstance().getAdsUpdateIntervalMillis(), adsUpdater);
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
	public void onAdsUpdated(List<AdsInstance> newAdsList, boolean isDefault) {

		
		if(newAdsList != null && !newAdsList.isEmpty()) {
			if(isDefault) {
				try {
					String json = Utils.serializeToString(newAdsList.toArray(new AdsInstance[newAdsList.size()]));
					saveDefaultAds(json);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Log.v("sdk", "loaded default ads list " + newAdsList.size());
			} else {
				adsList.clear();
				adsList.addAll(newAdsList);
				context.sendStickyBroadcast(new Intent(Configuration.ACTION_SEND_LOADER_CALLBACK));
				cacheManager.clearCache();
				Log.v("sdk", "loaded ads list " + newAdsList.size());
				TestFlight.passCheckpoint("Ads loaded, callback sent!");
			}
		}
	}
	
	public AdsInstance[] getDefaultAd() {
		AdsInstance[] defaultAdsList = null;
		try {
			String json = getSavedDefaultAd();
			if(!TextUtils.isEmpty(json)) {
				defaultAdsList = Utils.deserializeFromString(json);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return defaultAdsList;
		
	}
	public boolean isDefaultValueSaved() {
		
		return PreferenceManager.getDefaultSharedPreferences(context).contains(DEFAULT_ADS_PATH);
	}
	
	public void dispose() {
		cacheManager.clearCacheDirectory();
		updateScheduler.stop();
		updateScheduler.dispose();
	}

	@Override
	public void onStartUpdate() {

		context.removeStickyBroadcast(new Intent(Configuration.ACTION_SEND_LOADER_CALLBACK));
	}
	
	private String getSavedDefaultAd() {
		
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(DEFAULT_ADS_PATH, "");
	}
	private void saveDefaultAds(String data) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = sp.edit();
		editor.putString(DEFAULT_ADS_PATH, data);
		editor.commit();
	}
}
