package com.cubes.cubesandroidsdk.smartextensions.controls;

import java.util.List;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.net.Uri;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ImageView;

import com.cubes.cubesandroidsdk.adsmanager.AdsInstance;
import com.cubes.cubesandroidsdk.adsmanager.ClickReceiver;
import com.cubes.cubesandroidsdk.config.AdsType;
import com.cubes.cubesandroidsdk.config.Configuration;
import com.cubes.cubesandroidsdk.schedulers.AbstractScheduler;
import com.cubes.cubesandroidsdk.schedulers.AdsShowingScheduler;
import com.cubes.cubesandroidsdk.schedulers.AdsShowingScheduler.IAdsChanger;
import com.cubes.cubesandroidsdk.service.AdsManagerService;
import com.cubes.cubesandroidsdk.service.AdsManagerService.AdsManagerServiceBinder;
import com.sonyericsson.extras.liveware.aef.control.Control;
import com.sonyericsson.extras.liveware.extension.util.control.ControlExtension;
import com.sonyericsson.extras.liveware.extension.util.control.ControlListItem;
import com.sonyericsson.extras.liveware.extension.util.control.ControlObjectClickEvent;
import com.testflightapp.lib.TestFlight;

public class AdsControlExtension extends ControlExtension implements
		IAdsChanger {

	private int containerImgId;
	private final int width;
	private final int height;
	private Bitmap mBackground;
	private int currentAdsCounter;
	private AbstractScheduler scheduler;
	private static final Config BITMAP_CONFIG = Bitmap.Config.RGB_565;

	private ServiceConnection adsServiceConnection;
	private AdsManagerServiceBinder serviceBinder;
	private BroadcastReceiver loaderCallback;

	private List<AdsInstance> adsList;
	
	private boolean mustInterceptActions;

	private FullScreenAdsControlExtension fullScreenControl;
	
	public AdsControlExtension(Context context, String hostAppPackageName) {
		super(context, hostAppPackageName);
		width = ExtensionDrawingHelper.getBarWidth(context);
		height = ExtensionDrawingHelper.getBarHeight(context);
		mBackground = Bitmap.createBitmap(width, height, BITMAP_CONFIG);
		mBackground.setDensity(DisplayMetrics.DENSITY_DEFAULT);
		scheduler = new AdsShowingScheduler(this, Configuration.getInstance()
				.getAdsBarChangeIntervalMillis());
		adsServiceConnection = prepareServiceConnection();
		fullScreenControl = new FullScreenAdsControlExtension(context,
				hostAppPackageName);
		bindToService();
	}
	
	public boolean mustInterceptActions() {
		
		return mustInterceptActions;
	}

	private void registerLoaderCallback() {
		loaderCallback = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				TestFlight.passCheckpoint("Got broadcast after loading");
				Log.v("SDK", "Got broadcast after loading");
				if (serviceBinder != null) {
					adsList = serviceBinder.getAdsList();
					resetCounter();
				}

				if (adsList != null && !adsList.isEmpty()) {
					Log.v("SDK",
							"loaded ads list with size - " + adsList.size());
					TestFlight.passCheckpoint("loaded ads list with size - "
							+ adsList.size());
					drawAdsInstance(adsList.get(getCounter()));
					moveCounter();
				} else {
					drawBitmap(makeEmptyAd(getDrawingArea()));
				}
			}
		};

		mContext.registerReceiver(loaderCallback, new IntentFilter(
				Configuration.ACTION_SEND_LOADER_CALLBACK));
	}

	private void unregisterLoaderCallback() {
		if(loaderCallback != null) {
			mContext.unregisterReceiver(loaderCallback);
		}
	}

	private ServiceConnection prepareServiceConnection() {

		return new ServiceConnection() {

			@Override
			public void onServiceConnected(ComponentName className,
					IBinder service) {
				if (service != null) {
					serviceBinder = (AdsManagerServiceBinder) service;
					registerLoaderCallback();
				}
			}

			@Override
			public void onServiceDisconnected(ComponentName arg0) {

			}
		};
	}

	/**
	 * Show bar with ads.
	 * 
	 * @param containerImgId
	 *            - index of container for hosting of bar. Must be
	 *            {@link ImageView} object with
	 *            {@link ImageView#setClickable(boolean)} set as true (for
	 *            delivering click event)
	 */
	protected void showAdsBar(int containerImgId) {

		this.containerImgId = containerImgId;
		
		if(adsList != null && !adsList.isEmpty()) {
			drawAdsInstance(adsList.get(getCounter()));
		} else {
			drawBitmap(makeEmptyAd(getDrawingArea()));
		}
		scheduler.start();

		TestFlight.log("ShowAdsBar invoked");
	}

	private void bindToService() {

		mContext.bindService(new Intent(mContext, AdsManagerService.class),
				adsServiceConnection, Service.BIND_AUTO_CREATE);
	}

	@Override
	public void onStart() {
		super.onStart();
		if (containerImgId != 0) {
			drawBitmap(makeEmptyAd(getDrawingArea()));
			scheduler.start();
			registerLoaderCallback();
		}
	}
	
	@Override
	public void onStop() {
		super.onStop();
		scheduler.stop();
		unregisterLoaderCallback();
	}

	@Override
	public void onDestroy() {

		scheduler.dispose();
		mBackground.recycle();
		mContext.unbindService(adsServiceConnection);
		Log.v("SDK", "View - ondestroy");
		super.onDestroy();
	}

	@Override
	public void onObjectClick(ControlObjectClickEvent event) {
		super.onObjectClick(event);

		int layoutReference = event.getLayoutReference();
		if (layoutReference == containerImgId) {
			performBarClick();
		}
	}
	
	@Override
	public void onListItemClick(ControlListItem listItem, int clickType,
			int itemLayoutReference) {
		if(fullScreenControl.isStarted()) {
			fullScreenControl.onListItemClick(listItem, clickType, itemLayoutReference);
		}
	}
	
	@Override
	public void onListItemSelected(ControlListItem listItem) {
		if(fullScreenControl.isStarted()) {
			fullScreenControl.onListItemSelected(listItem);
		}
	}

	private void performBarClick() {

		if (adsList != null && !adsList.isEmpty()) {
			final AdsInstance instance = adsList.get(getCounter());
			if (instance.isExpandable()) {
				ControlsManager.getInstance().putToStack(this);
				mustInterceptActions = true;
				fullScreenControl.showInstance(adsList.get(getCounter()));
				fullScreenControl.onStart();
				fullScreenControl.onResume();
				
			} else {
				mContext.sendBroadcast(new Intent(
						Configuration.ACTION_CLICK_ADS_EVENT).putExtra(
						ClickReceiver.INTENT_CLICK_ACTION,
						instance.getClickAction()).putExtra(
						ClickReceiver.INTENT_CLICK_DATA,
						instance.getClickData()));
			}
		}
	}
	
	@Override
	public void onRequestListItem(int layoutReference, int listItemPosition) {
		super.onRequestListItem(layoutReference, listItemPosition);
		
		fullScreenControl.onRequestListItem(layoutReference, listItemPosition);
	}
	
	@Override
	public void onKey(int action, int keyCode, long timeStamp) {

		if (action == Control.Intents.KEY_ACTION_RELEASE
				&& keyCode == Control.KeyCodes.KEYCODE_BACK) {

			if(fullScreenControl.isStarted()) {
				fullScreenControl.onPause();
				fullScreenControl.onStop();
				ControlsManager.getInstance().restore();
				mustInterceptActions = false;
			}
		}
		super.onKey(action, keyCode, timeStamp);
	}
	
	@Override
	public void onAdsMustChanged() {

		Log.v("SDK", "scheduled drawing banner");
		if (adsList != null && !adsList.isEmpty()) {
			moveCounter();
			drawAdsInstance(adsList.get(getCounter()));
		} else {
			drawBitmap(makeEmptyAd(getDrawingArea()));
		}
	}

	/**
	 * Drawing functions
	 * 
	 */

	private void drawBitmap(Bitmap bitmap) {

		sendImage(containerImgId, bitmap);
	}

	private Bitmap makeTextAd(Bitmap bitmap, String text) {

		return ExtensionDrawingHelper.setTextIntoBitmap(mContext, bitmap, text,
				width, height);
	}

	private Bitmap makeEmptyAd(Bitmap bitmap) {

		// return ExtensionDrawingHelper.setTextIntoBitmap(mContext, bitmap,
		// mContext.getString(R.string.ads_empty_banner_text), width,
		// height);
		return bitmap;
	}

	private void drawAdsInstance(AdsInstance instance) {

		switch (instance.getAdsType()) {

		case AdsType.MULTIPART:
			if (instance.hasTextBar()) {
				drawBitmap(makeTextAd(getDrawingArea(),
						instance.getBarTextString()));
			} else {
				try {
					drawBitmap(MediaStore.Images.Media.getBitmap(
							mContext.getContentResolver(),
							Uri.parse(instance.getBarUriString())));
				} catch (Exception e) {
					e.printStackTrace();
					drawBitmap(makeEmptyAd(getDrawingArea()));
				}
			}
			break;
		case AdsType.INTERSTITIAL:
			// TODO: Not implemented yet
			break;
		case AdsType.MULTIPART_LOGO:
			if (instance.hasTextBar()) {
				drawBitmap(makeTextAd(getDrawingArea(),
						instance.getBarTextString()));
			} else {
				try {
					drawBitmap(MediaStore.Images.Media.getBitmap(
							mContext.getContentResolver(),
							Uri.parse(instance.getBarUriString())));
				} catch (Exception e) {
					e.printStackTrace();
					drawBitmap(makeEmptyAd(getDrawingArea()));
				}
			}
			break;
		}
	}

	private Bitmap getDrawingArea() {

		return mBackground.copy(BITMAP_CONFIG, true);
	}

	private void moveCounter() {
		if (++currentAdsCounter == adsList.size()) {
			currentAdsCounter = 0;
		}
	}

	private void resetCounter() {
		currentAdsCounter = 0;
	}

	private int getCounter() {

		return currentAdsCounter;
	}
}
