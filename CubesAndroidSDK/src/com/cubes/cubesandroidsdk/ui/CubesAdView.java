package com.cubes.cubesandroidsdk.ui;


import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;


public class CubesAdView extends FrameLayout {

	public ImageView mBannerView;
	private Context mContext;
	public CubesAdView(final Context context, final AttributeSet attributes){
			super(context, attributes);
			mContext = context;
			if (attributes != null) {
				int count = attributes.getAttributeCount();
				for (int i = 0; i < count; i++) {
	//				String name = attributes.getAttributeName(i);
/*					if (name.equals("publisherId")) {
						this.publisherId = attributes.getAttributeValue(i);
					} else if (name.equals("request_url")) {
						this.requestURL = attributes.getAttributeValue(i);
					} else if (name.equals("animation")) {
						this.animation = attributes.getAttributeBooleanValue(i, false);
					} else if (name.equals("includeLocation")) {
						this.includeLocation = attributes.getAttributeBooleanValue(i, false);
					}
				}*/
			}
			//initialize(context);
		}
		showContent();
	}
	
	private void showContent(){
		if(mBannerView!=null){
			this.removeView(mBannerView);
		}
		mBannerView = new ImageView(mContext);
		mBannerView.setContentDescription("test");
		//Resources res = mContext.getResources();
		
		//mBannerView.setImageResource(resId);
		this.addView(mBannerView);
	}
}
