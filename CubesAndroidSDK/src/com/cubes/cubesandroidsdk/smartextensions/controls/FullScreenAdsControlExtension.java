package com.cubes.cubesandroidsdk.smartextensions.controls;

import java.io.FileNotFoundException;
import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;

import com.cubes.cubesandroidsdk.R;
import com.cubes.cubesandroidsdk.adsmanager.AdsInstance;
import com.sonyericsson.extras.liveware.aef.control.Control;
import com.sonyericsson.extras.liveware.extension.util.control.ControlExtension;
import com.sonyericsson.extras.liveware.extension.util.control.ControlObjectClickEvent;

public class FullScreenAdsControlExtension extends ControlExtension {

	private static final Config BITMAP_CONFIG = Bitmap.Config.RGB_565;
	private int width;
	private int height;
	private Bitmap mBackground;
	private AdsInstance instance;

	public FullScreenAdsControlExtension(Context context,
			String hostAppPackageName) {
		super(context, hostAppPackageName);
		width = ExtensionDrawingHelper.getBarWidth(context);
		height = ExtensionDrawingHelper.getBarHeight(context);
		mBackground = Bitmap.createBitmap(width, height, BITMAP_CONFIG);
		mBackground.setDensity(DisplayMetrics.DENSITY_DEFAULT);
	}

	public void showInstance(AdsInstance instance) {

		this.instance = instance;
		showLayout(R.layout.ads_full_screen, null);
		try {
			sendImage(
					R.id.ads_full_screen_container,
					MediaStore.Images.Media.getBitmap(
							mContext.getContentResolver(),
							Uri.parse(instance.getFullscreenAds().get(0))));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		start();
	}

	@Override
	public void onObjectClick(ControlObjectClickEvent event) {
		super.onObjectClick(event);
		if (event.getLayoutReference() == R.id.ads_full_screen_container) {
			// TODO:perform click event
		}
	}

	@Override
	public void onKey(int action, int keyCode, long timeStamp) {
		Log.v("SDK", "Ads full - on key");
		if (action == Control.Intents.KEY_ACTION_RELEASE
				&& keyCode == Control.KeyCodes.KEYCODE_BACK) {

			stop();
			destroy();
		} else {
			super.onKey(action, keyCode, timeStamp);
		}
	}
}
