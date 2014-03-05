package com.cubes.cubesandroidsdk.adsmanager.updater;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

import com.cubes.cubesandroidsdk.adsmanager.AdsInstance;
import com.cubes.cubesandroidsdk.utils.AdXmlElements;

public class Utils {

	private static final String FULL_SCREEN_DATA = "full_screen_data";
	private static final String CLICK_DATA = "click_data";
	private static final String BAR_URI = "bar_uri";
	private static final String BAR_TEXT = "bar_text";
	private static final String CLICK_ACTION = "click_action";
	private static final String ADS_TYPE = "ads_type";

	public static AdsInstance convertXmlToAdsInstance(AdXmlElements xml) {

		return new AdsInstance(xml.get_adType(), xml.get_clickType(),
				xml.get_clickUrl(), null, null);
	}

	public static AdsInstance[] deserializeFromString(String str)
			throws JSONException {
		final JSONArray array = new JSONArray(str);
		final AdsInstance[] instances = new AdsInstance[array.length()];
		for (int i = 0; i < array.length(); i++) {
			final JSONObject object = array.getJSONObject(i);
			int adsType = object.getInt(ADS_TYPE);
			int clickAction = object.getInt(CLICK_ACTION);
			String barText = object.getString(BAR_TEXT);
			String barUri = object.getString(BAR_URI);
			String clickData = object.getString(CLICK_DATA);
			String files = object.getString(FULL_SCREEN_DATA);
			List<String> fullScreenUrls = new ArrayList<String>();
			if(!TextUtils.isEmpty(files) && !files.equals("[]")) {
				JSONArray fullScreenJson = new JSONArray(files);
				
				if (fullScreenJson.length() > 0) {
					for (int j = 0; j < fullScreenJson.length(); j++) {
						fullScreenUrls.add(fullScreenJson.getString(j));
					}
				}
			}
			
			AdsInstance instance = new AdsInstance(adsType, clickAction,
					clickData, barUri, barText);
			instance.setFullscreenAds(fullScreenUrls);
			instances[i] = instance;
		}
		return instances;
	}

	public static String serializeToString(AdsInstance[] instances)
			throws JSONException {
		final JSONArray array = new JSONArray();
		for (AdsInstance instance : instances) {
			final JSONObject object = new JSONObject();
			object.put(ADS_TYPE, instance.getAdsType());
			object.put(CLICK_ACTION, instance.getClickAction());
			object.put(BAR_TEXT, TextUtils.isEmpty(instance
					.getBarTextString()) ? "null" : instance.getBarTextString());
			object.put(BAR_URI,
					TextUtils.isEmpty(instance.getBarUriString()) ? "null"
							: instance.getBarUriString());
			object.put(CLICK_DATA, instance.getClickData());
			final List<String> fullScreen = instance.getFullscreenAds();
			final JSONArray fullScreenJson = new JSONArray();
			if (fullScreen != null && !fullScreen.isEmpty()) {
				for (int i = 0; i < fullScreen.size(); i++) {
					fullScreenJson.put(fullScreen.get(i));
				}
			}
			object.put(FULL_SCREEN_DATA, fullScreenJson.toString());
			array.put(object);
		}
		return array.toString();
	}
}
