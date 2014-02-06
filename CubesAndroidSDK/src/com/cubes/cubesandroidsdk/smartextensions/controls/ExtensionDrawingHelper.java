package com.cubes.cubesandroidsdk.smartextensions.controls;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.Gravity;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cubes.cubesandroidsdk.R;
import com.cubes.cubesandroidsdk.adsmanager.AdsType;

/**
 * Helper class for drawing {@link AdsControlExtension}
 * @author makarenko.s
 *
 */
class ExtensionDrawingHelper {

	/**
	 * 
	 * @param context
	 * @return - height of ads bar ({@link AdsType#MULTIPART}) in pixels
	 */
	public static int getBarHeight(Context context) {
		return context.getResources().getDimensionPixelSize(
				R.dimen.ads_multipart_bar_image_height);
	}

	/**
	 * 
	 * @param context
	 * @return - width of ads bar ({@link AdsType#MULTIPART}) in pixels
	 */
	public static int getBarWidth(Context context) {
		return context.getResources().getDimensionPixelSize(
				R.dimen.ads_multipart_bar_image_width);
	}
	
	/**	  
	 * Prepare text container for text and draw given text on center of bitmap.
	 * 
	 * @param context
	 * @param bitmap
	 * @param text
	 * @param width
	 * @param height
	 * @return
	 */
	public static Bitmap setTextIntoBitmap(Context context, Bitmap bitmap, String text, int width, int height) {

		final LinearLayout root = new LinearLayout(context);
		root.setLayoutParams(new LayoutParams(width, height));
		root.setGravity(Gravity.CENTER);
		final LinearLayout sampleLayout = (LinearLayout) LinearLayout.inflate(
				context, R.layout.ads_text_bar, root);
		((TextView) sampleLayout.findViewById(R.id.ads_bar_textview))
				.setText(text);
		sampleLayout.measure(width, height);
		sampleLayout.layout(0, 0, width, height);
		sampleLayout.draw(new Canvas(bitmap));
		
		return bitmap;
	}

}
