package com.cubes.cubesandroidsdk.smartextensions.controls;

import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cubes.cubesandroidsdk.R;
import com.cubes.cubesandroidsdk.schedulers.AbstractScheduler;
import com.cubes.cubesandroidsdk.schedulers.AdsShowingScheduler;
import com.cubes.cubesandroidsdk.schedulers.AdsShowingScheduler.IAdsChanger;
import com.sonyericsson.extras.liveware.extension.util.control.ControlExtension;
import com.sonyericsson.extras.liveware.extension.util.control.ControlObjectClickEvent;

public class AdsControlExtension extends ControlExtension implements
		IAdsChanger {

	private int containerImgId;
	private final int width;
	private final int height;
	private Bitmap mBackground;
	private int fakeCount;
	private AbstractScheduler scheduler;
	private static final Config BITMAP_CONFIG = Bitmap.Config.RGB_565;
	
	private static final long ADS_INTERVAL = 20000L;

	public AdsControlExtension(Context context, String hostAppPackageName) {
		super(context, hostAppPackageName);
		width = getBarWidth();
		height = getBarHeight();
		mBackground = Bitmap.createBitmap(width, height, BITMAP_CONFIG);
		mBackground.setDensity(DisplayMetrics.DENSITY_DEFAULT);
		scheduler = new AdsShowingScheduler(this, ADS_INTERVAL);
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
		drawBitmap(makeEmptyAd(mBackground.copy(BITMAP_CONFIG, true)));
		this.scheduler.start();
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
		super.onDestroy();
	}
	
	private void drawBitmap(Bitmap bitmap) {
		
		sendImage(containerImgId, bitmap);
	}
	
	private Bitmap makeTextAd(Bitmap bitmap) {
		
		return setTextIntoBitmap(bitmap, "Your ads can be here! LARGE TEXT");
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
	
	private Bitmap makeImgAd(Bitmap bitmap) {
		
		//XXX: Temporary solution
		bitmap = getBitmapFromAsset(mContext, "banner_bar.png");
		return bitmap;
	}
	
	private Bitmap makeEmptyAd(Bitmap bitmap) {

		return setTextIntoBitmap(bitmap, mContext.getString(R.string.ads_empty_banner_text));
	}
	
	/**
	 * Temporary solution!!!
	 * 
	 * @param context
	 * @param strName
	 * @return
	 */
	
	private Bitmap getBitmapFromAsset(Context context, String strName) {
	    AssetManager assetManager = context.getAssets();

	    InputStream istr;
	    Bitmap bitmap = null;
	    try {
	        istr = assetManager.open(strName);
	        bitmap = BitmapFactory.decodeStream(istr);
	    } catch (IOException e) {
	        return null;
	    }

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
			Toast.makeText(mContext, "Banner clicked", 1000).show();
		}
	}

	@Override
	public void onAdsMustChanged() {

		if(fakeCount++ % 2 == 0) {
			drawBitmap(makeImgAd(mBackground.copy(BITMAP_CONFIG, true)));
		} else {
			drawBitmap(makeTextAd(mBackground.copy(BITMAP_CONFIG, true)));
		}
	}
}
