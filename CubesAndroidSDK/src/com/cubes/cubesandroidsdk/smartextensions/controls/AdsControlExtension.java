package com.cubes.cubesandroidsdk.smartextensions.controls;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cubes.cubesandroidsdk.R;
import com.sonyericsson.extras.liveware.extension.util.control.ControlExtension;
import com.sonyericsson.extras.liveware.extension.util.control.ControlObjectClickEvent;

public class AdsControlExtension extends ControlExtension {

	private int containerImgId;
	
	public AdsControlExtension(Context context, String hostAppPackageName) {
		super(context, hostAppPackageName);
	}
	
	/**
	 * Show bar with ads. 
	 * @param containerImgId - index of container for hosting of bar. Must be {@link ImageView} object 
	 * 	with {@link ImageView#setClickable(boolean)} set as true (for delivering click event) 
	 */
	protected void showAdsBar(int containerImgId) {
		
		this.containerImgId = containerImgId;
		final int width = getBarWidth();
		final int height = getBarHeight();
        final Bitmap mBackground = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        
        mBackground.setDensity(DisplayMetrics.DENSITY_DEFAULT);

        final LinearLayout root = new LinearLayout(mContext);
        root.setLayoutParams(new LayoutParams(width, height));
        

        final LinearLayout sampleLayout = (LinearLayout)LinearLayout.inflate(mContext,
                R.layout.ads_text_bar, root);
        ((TextView)sampleLayout.findViewById(R.id.ads_bar_textview)).setText("this is put");
        sampleLayout.measure(width, height);
        sampleLayout.layout(0, 0, sampleLayout.getMeasuredWidth(),
                sampleLayout.getMeasuredHeight());
        sampleLayout.draw(new Canvas(mBackground));
        sendImage(containerImgId, mBackground);
	}
	
	private int getBarHeight() {
		return mContext.getResources().getDimensionPixelSize(R.dimen.ads_multipart_bar_image_height);
	}
	
	private int getBarWidth() {
		return mContext.getResources().getDimensionPixelSize(R.dimen.ads_multipart_bar_image_width);
	}

	@Override
	public void onObjectClick(ControlObjectClickEvent event) {
		super.onObjectClick(event);
		Log.v("ads", "on click");
		if(event.getLayoutReference() == containerImgId) {
			Toast.makeText(mContext, "Click", 1000).show();
		}
		
	}
}
