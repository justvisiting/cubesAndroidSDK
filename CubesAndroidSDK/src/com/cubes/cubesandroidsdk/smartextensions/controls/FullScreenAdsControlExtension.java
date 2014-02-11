package com.cubes.cubesandroidsdk.smartextensions.controls;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.os.Bundle;
import android.util.DisplayMetrics;

import com.cubes.cubesandroidsdk.R;
import com.cubes.cubesandroidsdk.adsmanager.AdsInstance;
import com.sonyericsson.extras.liveware.aef.control.Control;
import com.sonyericsson.extras.liveware.extension.util.control.ControlExtension;
import com.sonyericsson.extras.liveware.extension.util.control.ControlListItem;

public class FullScreenAdsControlExtension extends ControlExtension {

	private static final Config BITMAP_CONFIG = Bitmap.Config.RGB_565;
	private int width;
	private int height;
	private Bitmap mBackground;
	private AdsInstance instance;
	private boolean isStarted;

	public FullScreenAdsControlExtension(Context context,
			String hostAppPackageName) {
		super(context, hostAppPackageName);
		width = ExtensionDrawingHelper.getBarWidth(context);
		height = ExtensionDrawingHelper.getBarHeight(context);
		mBackground = Bitmap.createBitmap(width, height, BITMAP_CONFIG);
		mBackground.setDensity(DisplayMetrics.DENSITY_DEFAULT);
	}
	
	public boolean isStarted() {
		
		return isStarted;
	}
	
	@Override
	public void onStart() {
		
		super.onStart();
		showLayout(R.layout.ads_full_screen_layout, null);
		sendListCount(R.id.ads_fullscreen_gallery, instance.getFullscreenAds().size());
		sendListPosition(R.id.ads_fullscreen_gallery, 0);
		isStarted = true;
	}
	
	@Override
	public void onStop() {

		isStarted = false;
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
	
	@Override
	public void onListItemClick(ControlListItem listItem, int clickType,
			int itemLayoutReference) {
		super.onListItemClick(listItem, clickType, itemLayoutReference);
	}
	
	@Override
	public void onListItemSelected(ControlListItem listItem) {
		super.onListItemSelected(listItem);
	}
	
	private ControlListItem createControlListItem(int position) {

        ControlListItem item = new ControlListItem();
        item.layoutReference = R.id.ads_fullscreen_gallery;
        item.dataXmlLayout = R.layout.ads_full_screen;
        item.listItemId = position;
        item.listItemPosition = position;

        Bundle contentBundle = new Bundle();
        contentBundle.putInt(Control.Intents.EXTRA_LAYOUT_REFERENCE, R.id.ads_full_screen_container);
        contentBundle.putString(Control.Intents.EXTRA_DATA_URI, instance.getFullscreenAds().get(position));

        item.layoutData = new Bundle[1];
        item.layoutData[0] = contentBundle;

        return item;
    }

	public void showInstance(AdsInstance instance) {

		this.instance = instance;
	}

	@Override
	public void onKey(int action, int keyCode, long timeStamp) {

	}
}
