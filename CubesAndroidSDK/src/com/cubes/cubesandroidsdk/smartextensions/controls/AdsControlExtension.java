package com.cubes.cubesandroidsdk.smartextensions.controls;

import java.util.ArrayList;
import java.util.Arrays;
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
import android.os.CountDownTimer;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ImageView;

import com.cubes.cubesandroidsdk.R;
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
import com.sonyericsson.extras.liveware.extension.util.control.ControlTouchEvent;
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

	private boolean isLoaderCallbackRegistered;

	private FullScreenAdsControlExtension fullScreenControl;
	private CountDownTimer fullScreenCloseTimer;

	private List<AdsInstance> defaultAdsList;

	private static final int TC_BAR_SHOW = 100;

	private ClickHintExtension hintExtension;

	public AdsControlExtension(Context context, String hostAppPackageName) {
		super(context, hostAppPackageName);
		width = ExtensionDrawingHelper.getBarWidth(context);
		height = ExtensionDrawingHelper.getBarHeight(context);
		mBackground = Bitmap.createBitmap(width, height, BITMAP_CONFIG);
		mBackground.setDensity(DisplayMetrics.DENSITY_DEFAULT);
		scheduler = new AdsShowingScheduler(this, Configuration.getInstance()
				.getAdsBarChangeIntervalMillis(), TC_BAR_SHOW);
		adsServiceConnection = prepareServiceConnection();
		defaultAdsList = new ArrayList<AdsInstance>();
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
					Log.v("SDK_control", "loaded ads list with size - "
							+ adsList.size());

					TestFlight.passCheckpoint("loaded ads list with size - "
							+ adsList.size());
					drawAdsInstance(adsList.get(getCounter()));
				} else {
					drawBitmap(makeEmptyAd(getDrawingArea()));
				}
			}
		};
		fullScreenControl = new FullScreenAdsControlExtension(context,
				hostAppPackageName);
		fullScreenCloseTimer = new CountDownTimer(11000, 1000) {

			@Override
			public void onTick(long millisUntilFinished) {
				fullScreenControl.updateTimer();
			}

			@Override
			public void onFinish() {
				backFromFullScreen();
			}
		};
		bindToService();
	}

	public boolean mustInterceptActions() {

		return mustInterceptActions;
	}

	private void registerLoaderCallback() {
		if (!isLoaderCallbackRegistered) {
			mContext.registerReceiver(loaderCallback, new IntentFilter(
					Configuration.ACTION_SEND_LOADER_CALLBACK));
			isLoaderCallbackRegistered = true;
		}
	}

	private void unregisterLoaderCallback() {
		if (loaderCallback != null && isLoaderCallbackRegistered) {
			mContext.unregisterReceiver(loaderCallback);
			isLoaderCallbackRegistered = false;
		}
	}

	@Override
	public void onTouch(ControlTouchEvent event) {
		super.onTouch(event);
		Log.v("sdk_click", "touch action " + event.getAction());
		if (fullScreenControl.isActive()) {
			if (event.getX() > 184 && event.getY() < 50) {
				backFromFullScreen();
			}
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
					AdsInstance[] instances = serviceBinder.getDefaultAd();
					if (instances != null) {
						defaultAdsList = Arrays.asList(instances);
					}
				}
			}

			@Override
			public void onServiceDisconnected(ComponentName arg0) {

				// Nothing to do
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

		if (adsList != null && !adsList.isEmpty()) {
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
		TestFlight.passCheckpoint("Start ads extension");
		if (containerImgId != 0) {
			drawBitmap(makeEmptyAd(getDrawingArea()));
			scheduler.start();
			registerLoaderCallback();
		}
	}

	@Override
	public void onStop() {
		super.onStop();
		TestFlight.passCheckpoint("STart ads extension");
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
		} else if (layoutReference == R.id.ads_fullscreen_close_btn) {
			backFromFullScreen();
		}
	}

	private void backFromFullScreen() {

//		fullScreenCloseTimer.cancel();
		fullScreenControl.onPause();
		fullScreenControl.onStop();
		ControlsManager.getInstance().restore();
		mustInterceptActions = false;
	}

	@Override
	public void onListItemClick(ControlListItem listItem, int clickType,
			int itemLayoutReference) {
		Log.v("sdk_click", "onlistitem click");
		if (fullScreenControl.isActive()) {

			final AdsInstance instance = fullScreenControl.getInstance();
			ClickReceiver.processClick(mContext, instance.getClickAction(),
					instance.getClickData());
			showHint(instance.getClickAction());
		}
	}

	private void performBarClick() {

		if (adsList != null && !adsList.isEmpty()) {
			final AdsInstance instance = adsList.get(getCounter());
			Log.v("SDK_ads", " clicked - " + instance.getClickData()
					+ ", full screen" + instance.getFullscreenAds().size()
					+ ",  counter - " + getCounter());
			if (instance.isExpandable()) {
				gotToFullScreen(instance);
			} else {
				showHint(instance.getClickAction());
				ClickReceiver.processClick(mContext, instance.getClickAction(),
						instance.getClickData());
			}
		}
	}

	private void showHint(int clickAction) {

		ControlsManager.getInstance().putToStack(this);
		mustInterceptActions = true;
		hintExtension = new ClickHintExtension(mContext, mHostAppPackageName);
		hintExtension.setAction(clickAction);
		hintExtension.onStart();
		hintExtension.onResume();
	}

	private void gotToFullScreen(AdsInstance instance) {

		ControlsManager.getInstance().putToStack(this);
		mustInterceptActions = true;
		fullScreenControl.showInstance(instance);
		fullScreenControl.onStart();
		fullScreenControl.onResume();
//		fullScreenCloseTimer.start();
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

			if (hintExtension != null && hintExtension.isActive()) {
				hideHint();
				return;
			}

			if (fullScreenControl != null && fullScreenControl.isActive()) {
				backFromFullScreen();
			}
		}
		super.onKey(action, keyCode, timeStamp);
	}

	private void hideHint() {
		hintExtension.onPause();
		hintExtension.onStop();
		hintExtension.onDestroy();
		ControlsManager.getInstance().restore();
		mustInterceptActions = false;
	}

	@Override
	public void onAdsMustChanged(int timerCode) {

		Log.v("SDK", "scheduled drawing banner");
		switch (timerCode) {
		case TC_BAR_SHOW:
			if (adsList != null && !adsList.isEmpty()) {
				moveCounter();
				drawAdsInstance(adsList.get(getCounter()));
				Log.v("SDK_ads", "show instance - " + getCounter() + ", "
						+ adsList.get(getCounter()).getClickData());
			} else {
				drawBitmap(makeEmptyAd(getDrawingArea()));
			}
			break;
		}
	}

	public void showInterstitial() {

		if (adsList != null) {
			for (AdsInstance instance : adsList) {
				if (instance.getAdsType() == AdsType.INTERSTITIAL) {
					gotToFullScreen(instance);
					return;
				}
			}
		}
	}

	/**
	 * Drawing functions
	 * 
	 */

	private void drawBitmap(Bitmap bitmap) {

		final Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, 220, 36, false);
		sendImage(containerImgId,
				scaledBitmap);
		scaledBitmap.recycle();
		bitmap.recycle();
	}

	private Bitmap makeTextAd(Bitmap bitmap, String text) {

		return ExtensionDrawingHelper.setTextIntoBitmap(mContext, bitmap, text,
				width, height);
	}

	private Bitmap makeEmptyAd(Bitmap bitmap) {

		if (defaultAdsList == null) {
			return bitmap;
		}
		for (AdsInstance instance : defaultAdsList) {
			if (instance.getAdsType() == AdsType.MULTIPART) {
				try {
					return MediaStore.Images.Media.getBitmap(
							mContext.getContentResolver(),
							Uri.parse(instance.getBarUriString()));
				} catch (Exception e) {
					return bitmap;
				}
			}
		}
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
		case AdsType.LOGO_AD:
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

		if(mBackground == null) {
			mBackground = Bitmap.createBitmap(width, height, BITMAP_CONFIG);
			mBackground.setDensity(DisplayMetrics.DENSITY_DEFAULT);
		} else if(mBackground.isRecycled()) {
			mBackground = Bitmap.createBitmap(width, height, BITMAP_CONFIG);
			mBackground.setDensity(DisplayMetrics.DENSITY_DEFAULT);
		}
		return mBackground;
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
