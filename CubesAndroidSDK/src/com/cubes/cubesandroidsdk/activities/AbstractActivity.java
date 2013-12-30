package com.cubes.cubesandroidsdk.activities;

import android.app.Activity;
import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.cubes.cubesandroidsdk.R;
import com.cubes.cubesandroidsdk.ui.CubesAdBannerImageView;
import com.cubes.cubesandroidsdk.ui.CubesAdInterstitialImageView;
import com.cubes.cubesandroidsdk.ui.CubesTextView;

public abstract class AbstractActivity extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Set content view
		setContentView(getLayoutResId());
	}

	// In your sample project just override getLayoutResId
	/**
	 * Returns the layout resource identifier to inflate when setting content view.
	 * @return the layout resource identifier.
	 */
	protected int getLayoutResId(){
		return 0;
	}

	// ----------------------------------------------------------------------------------
	// Basic SDK with text ad support (text ad is hard coded) and include it in sample app
	/**
	 * Show Ad TextView (text ad is hard coded)
	 */
	protected void loadAdCubesTextView(String text, int gravity, int gravityLayout){
		CubesTextView cubesTextView = (CubesTextView) findViewById(R.id.cubes_textview);
		if (cubesTextView != null){
			cubesTextView.setText(text);
			cubesTextView.setGravity(gravity);

			LayoutParams lp = new RelativeLayout.LayoutParams(
					RelativeLayout.LayoutParams.WRAP_CONTENT,
					RelativeLayout.LayoutParams.WRAP_CONTENT);
			lp.addRule(gravityLayout);
			cubesTextView.setLayoutParams(lp);
		}
	}
	// ----------------------------------------------------------------------------------

	// ----------------------------------------------------------------------------------
	// Add support for banner ad. 
	/**
	 * Show Ad Banner ImageView (image ad banner is hard coded)
	 */
	protected void loadAdBannerCubesImageView(int resId, int gravity, int gravityLayout){
		CubesAdBannerImageView cubesAdBannerImageView = (CubesAdBannerImageView) 
				findViewById(R.id.cubes_adbanner_imageview);
		if (cubesAdBannerImageView != null){
			LayoutParams lp = new RelativeLayout.LayoutParams(
					RelativeLayout.LayoutParams.MATCH_PARENT,
					RelativeLayout.LayoutParams.WRAP_CONTENT);
			lp.addRule(gravityLayout);
			cubesAdBannerImageView.setLayoutParams(lp);

			cubesAdBannerImageView.setImageResource(resId);
		}
	}
	// ----------------------------------------------------------------------------------

	// ----------------------------------------------------------------------------------
	// Add support of interstitial ad
	
	RelativeLayout mCubesRelativeLayout;
	
	/**
	 * Show Ad Interstitial ImageView (image ad  intertitial is hard coded)
	 */
	protected void loadAdInterstitialCubesImageView(final int resId, int gravity, final int time){
		mCubesRelativeLayout = (RelativeLayout) findViewById(R.id.cubes_layout);
		Thread thread = new Thread() {
		    @Override
		    public void run() {
		    	final CubesAdInterstitialImageView cubesAdIntertitialImageView = new CubesAdInterstitialImageView(AbstractActivity.this);
		        try {
		        	if (mCubesRelativeLayout != null){
		        		
		    			LayoutParams lp = new RelativeLayout.LayoutParams(
		    					RelativeLayout.LayoutParams.MATCH_PARENT,
		    					RelativeLayout.LayoutParams.MATCH_PARENT);
		    			cubesAdIntertitialImageView.setLayoutParams(lp);

		    			cubesAdIntertitialImageView.setImageResource(resId);
		    			mCubesRelativeLayout.addView(cubesAdIntertitialImageView);
		    		}
		            Thread.sleep(time);
		        } catch (InterruptedException e) {
		        }

		        runOnUiThread(new Runnable() {
		            @Override
		            public void run() {
		            	mCubesRelativeLayout.removeView(cubesAdIntertitialImageView);
		            }
		        });
		    }
		};
		thread.start();
		// TODO: add intertitial for some time
		
	}
	// ----------------------------------------------------------------------------------
}
