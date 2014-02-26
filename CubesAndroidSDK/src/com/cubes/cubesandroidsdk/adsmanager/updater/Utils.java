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
			int adsType = object.getInt("ads_type");
			int clickAction = object.getInt("click_action");
			String barText = object.getString("bar_text");
			String barUri = object.getString("bar_uri");
			String clickData = object.getString("click_data");
			String files = object.getString("full_screen_data");
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
			object.put("ads_type", instance.getAdsType());
			object.put("click_action", instance.getClickAction());
			object.put("bar_text", TextUtils.isEmpty(instance
					.getBarTextString()) ? "null" : instance.getBarTextString());
			object.put("bar_uri",
					TextUtils.isEmpty(instance.getBarUriString()) ? "null"
							: instance.getBarUriString());
			object.put("click_data", instance.getClickData());
			final List<String> fullScreen = instance.getFullscreenAds();
			final JSONArray fullScreenJson = new JSONArray();
			if (fullScreen != null && !fullScreen.isEmpty()) {
				for (int i = 0; i < fullScreen.size(); i++) {
					fullScreenJson.put(fullScreen.get(i));
				}
			}
			object.put("full_screen_data", fullScreenJson.toString());
			array.put(object);
		}
		return array.toString();
	}
}
