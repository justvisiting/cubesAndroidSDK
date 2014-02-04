package com.cubes.cubesandroidsdk.smartextensions.controls;

import java.util.List;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cubes.cubesandroidsdk.R;
import com.cubes.cubesandroidsdk.adsmanager.AdsInstance;
import com.cubes.cubesandroidsdk.adsmanager.ClickReceiver;
import com.cubes.cubesandroidsdk.adsmanager.Configuration;
import com.cubes.cubesandroidsdk.schedulers.AbstractScheduler;
import com.cubes.cubesandroidsdk.schedulers.AdsShowingScheduler;
import com.cubes.cubesandroidsdk.schedulers.AdsShowingScheduler.IAdsChanger;
import com.cubes.cubesandroidsdk.service.AdsManagerService;
import com.cubes.cubesandroidsdk.service.AdsManagerService.AdsManagerServiceBinder;
import com.sonyericsson.extras.liveware.extension.util.control.ControlExtension;
import com.sonyericsson.extras.liveware.extension.util.control.ControlObjectClickEvent;

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
	
	private List<AdsInstance> adsList;
	
	public AdsControlExtension(Context context, String hostAppPackageName) {
		super(context, hostAppPackageName);
		width = getBarWidth();
		height = getBarHeight();
		mBackground = Bitmap.createBitmap(width, height, BITMAP_CONFIG);
		mBackground.setDensity(DisplayMetrics.DENSITY_DEFAULT);
		scheduler = new AdsShowingScheduler(this, Configuration.getInstance().getAdsBarChangeIntervalMillis());
		adsServiceConnection = prepareServiceConnection();
	    bindToService();
	}
	
	private ServiceConnection prepareServiceConnection() {
		
		return new ServiceConnection() {

	        @Override
	        public void onServiceConnected(ComponentName className,
	                IBinder service) {
	        	if(service != null) {
	        		serviceBinder = (AdsManagerServiceBinder) service;
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
		drawBitmap(makeEmptyAd(getDrawingArea()));
		scheduler.start();
	}
	
	private void bindToService() {
		
		mContext.bindService(new Intent(mContext, AdsManagerService.class), adsServiceConnection, Service.BIND_AUTO_CREATE);
	}
	
	@Override
	public void onStart() {
		
		super.onStart();
		if(containerImgId != 0) {
			drawBitmap(makeEmptyAd(getDrawingArea()));
			scheduler.start();
		}
	}

	@Override
	public void onStop() {
		super.onStop();
		scheduler.stop();
	}
	
	@Override
	public void onDestroy() {

		scheduler.dispose();
		mBackground.recycle();
		mContext.unbindService(adsServiceConnection);
		super.onDestroy();
	}
	
	/**
	 * Drawing functions
	 * 
	 */
	
	private void drawBitmap(Bitmap bitmap) {
		
		sendImage(containerImgId, bitmap);
	}
	
	private Bitmap makeTextAd(Bitmap bitmap) {
		
		return setTextIntoBitmap(bitmap, "Your ads can be here!");
	}
	
	private Bitmap makeEmptyAd(Bitmap bitmap) {

		return setTextIntoBitmap(bitmap, mContext.getString(R.string.ads_empty_banner_text));
	}
	
	private Bitmap setTextIntoBitmap(Bitmap bitmap, String text) {

		final LinearLayout root = new LinearLayout(mContext);
		root.setLayoutParams(new LayoutParams(width, height));
		root.setGravity(Gravity.CENTER);
		final LinearLayout sampleLayout = (LinearLayout) LinearLayout.inflate(
				mContext, R.layout.ads_text_bar, root);
		((TextView) sampleLayout.findViewById(R.id.ads_bar_textview))
				.setText(text);
		sampleLayout.measure(width, height);
		sampleLayout.layout(0, 0, width, height);
		sampleLayout.draw(new Canvas(bitmap));
		return bitmap;
	}
	
	private int getBarHeight() {
		return mContext.getResources().getDimensionPixelSize(
				R.dimen.ads_multipart_bar_image_height);
	}

	private int getBarWidth() {
		return mContext.getResources().getDimensionPixelSize(
				R.dimen.ads_multipart_bar_image_width);
	}

	@Override
	public void onObjectClick(ControlObjectClickEvent event) {
		super.onObjectClick(event);
		if (event.getLayoutReference() == containerImgId) {
			if(adsList != null && !adsList.isEmpty()) {
				AdsInstance instance = adsList.get(getCounter());
				mContext.sendBroadcast(
						new Intent(Configuration.ACTION_CLICK_ADS_EVENT)
							.putExtra(ClickReceiver.INTENT_CLICK_ACTION, instance.getClickAction())
							.putExtra(ClickReceiver.INTENT_CLICK_DATA, instance.getClickData()));
			}
			
		}
	}
	
	private void drawAdsInstance(AdsInstance instance) {
		try {
			drawBitmap(MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), Uri.parse(instance.getBarUriString())));
		} catch (Exception e) {
			e.printStackTrace();
			makeEmptyAd(getDrawingArea());
		}
	}
	
	private Bitmap getDrawingArea() {
		
		return mBackground.copy(BITMAP_CONFIG, true);
	}
	
	private void moveCounter() {
		if(++currentAdsCounter  == adsList.size()) {
			currentAdsCounter = 0;
		}
	}
	
	private int getCounter() {
	 
		return currentAdsCounter;
	}

	@Override
	public void onAdsMustChanged() {
		
		if(serviceBinder != null && serviceBinder.isAdsUPdated()) {
			adsList = serviceBinder.getAdsList();
			currentAdsCounter = 0;
		}
		
		if(adsList != null && !adsList.isEmpty()) {
			moveCounter();
			drawAdsInstance(adsList.get(getCounter()));
		}
	}
}
