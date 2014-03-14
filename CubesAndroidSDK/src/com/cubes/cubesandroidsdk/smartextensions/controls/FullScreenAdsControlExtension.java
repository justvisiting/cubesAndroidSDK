package com.cubes.cubesandroidsdk.smartextensions.controls;


import java.io.ByteArrayOutputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;

import com.cubes.cubesandroidsdk.R;
import com.cubes.cubesandroidsdk.adsmanager.AdsInstance;
import com.sonyericsson.extras.liveware.aef.control.Control;
import com.sonyericsson.extras.liveware.extension.util.control.ControlExtension;
import com.sonyericsson.extras.liveware.extension.util.control.ControlListItem;
import com.testflightapp.lib.TestFlight;

public class FullScreenAdsControlExtension extends ControlExtension {

	private static final Config BITMAP_CONFIG = Bitmap.Config.RGB_565;
	public static final long SHOWING_INTERVAL = 10000;
	private int width;
	private int height;
	private Bitmap mBackground;
	private AdsInstance instance;
	private boolean isStarted;
	private int counter = 11;
	private String timerPrefics;

	public FullScreenAdsControlExtension(Context context,
			String hostAppPackageName) {
		super(context, hostAppPackageName);
		width = ExtensionDrawingHelper.getBarWidth(context);
		height = ExtensionDrawingHelper.getBarHeight(context);
		mBackground = Bitmap.createBitmap(width, height, BITMAP_CONFIG);
		mBackground.setDensity(DisplayMetrics.DENSITY_DEFAULT);
		timerPrefics = mContext.getString(R.string.ads_timer_prefics);
	}
	
	public void updateTimer() {
//		sendText(R.id.ads_fullscreen_timer_value, timerPrefics + String.valueOf(--counter));
	}
	
	public boolean isActive() {
		
		return isStarted;
	}
	
	@Override
	public void onStart() {
		
		super.onStart();
		TestFlight.passCheckpoint("STart full screen");
		isStarted = true;
		counter = 11;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		showLayout(R.layout.ads_full_screen_layout, null);
		sendListCount(R.id.ads_fullscreen_gallery, instance.getFullscreenAds().size());
		sendListPosition(R.id.ads_fullscreen_gallery, 0);
	}
	
	@Override
	public void onStop() {

		isStarted = false;
		TestFlight.passCheckpoint("STop full screen");
		super.onStop();
	}
	
	@Override
	public void onRequestListItem(int layoutReference, int listItemPosition) {
		super.onRequestListItem(layoutReference, listItemPosition);
		
		if (layoutReference != -1 && listItemPosition != -1 && layoutReference == R.id.ads_fullscreen_gallery) {
            ControlListItem item = createControlListItem(listItemPosition);
            if (item != null) {
                sendListItem(item);
            }
        }
	}
	
	public AdsInstance getInstance() {
		
		return instance;
	}
	
	private ControlListItem createControlListItem(int position) {

        final ControlListItem item = new ControlListItem();
        item.layoutReference = R.id.ads_fullscreen_gallery;
        item.dataXmlLayout = R.layout.ads_full_screen;
        item.listItemId = position;
        item.listItemPosition = position;

        byte[] buffer = null;
		try {
			final Bitmap bmp = MediaStore.Images.Media.getBitmap(
					mContext.getContentResolver(),
					Uri.parse(instance.getFullscreenAds().get(position)));
			final ByteArrayOutputStream os = new ByteArrayOutputStream(256);
	        bmp.compress(CompressFormat.PNG, 100, os);
	        buffer = os.toByteArray();
	        os.flush();
	        os.close();
	        bmp.recycle();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
        
        final Bundle contentBundle = new Bundle();
        contentBundle.putInt(Control.Intents.EXTRA_LAYOUT_REFERENCE, R.id.ads_full_screen_container);
        contentBundle.putByteArray(Control.Intents.EXTRA_DATA, buffer);

        item.layoutData = new Bundle[1];
        item.layoutData[0] = contentBundle;

        return item;
    }

	public void showInstance(AdsInstance instance) {

		this.instance = instance;
	}
}
