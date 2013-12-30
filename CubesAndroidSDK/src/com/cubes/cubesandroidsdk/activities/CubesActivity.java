package com.cubes.cubesandroidsdk.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.cubes.cubesandroidsdk.R;
import com.cubes.cubesandroidsdk.ui.CubesAdBannerImageView;
import com.cubes.cubesandroidsdk.ui.CubesAdInterstitialImageView;
import com.cubes.cubesandroidsdk.ui.CubesTextView;

public class CubesActivity extends Activity{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Set content view
		setContentView(getLayoutResId());
		
		// loadAdCubesTextView for test 
		loadAdCubesTextView(getResources().getString(R.string.text_ad), Gravity.CENTER, RelativeLayout.ALIGN_PARENT_BOTTOM);
	}

	// In your sample project just override getLayoutResId
	/**
	 * Returns the layout resource identifier to inflate when setting content view.
	 * @return the layout resource identifier.
	 */
	protected int getLayoutResId(){
		return R.layout.cubes_activity;
	}
	
	/**
	 * Show Ad TextView (text ad is hard coded)
	 */
	protected void loadAdCubesTextView(String text, int gravity, int gravityLayout){
		CubesTextView cubesTextView = (CubesTextView) findViewById(R.id.cubes_textView);
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
	
	/**
	 * Show Ad Banner ImageView (image ad banner is hard coded)
	 */
	protected void loadAdBannerCubesImageView(int resId, int gravity, int gravityLayout){
		CubesAdBannerImageView cubesAdBannerImageView = (CubesAdBannerImageView) findViewById(R.id.cubes_textView);
		if (cubesAdBannerImageView != null){
			LayoutParams lp = new RelativeLayout.LayoutParams(
					RelativeLayout.LayoutParams.MATCH_PARENT,
					RelativeLayout.LayoutParams.WRAP_CONTENT);
			lp.addRule(gravityLayout);
			cubesAdBannerImageView.setLayoutParams(lp);
			
			cubesAdBannerImageView.setImageResource(resId);
		}
	}
	
	/**
	 * Show Ad Interstitial ImageView (image ad  intertitial is hard coded)
	 */
	protected void loadAdInterstitialCubesImageView(int resId, int gravity){
		CubesAdInterstitialImageView cubesAdIntertitialImageView = (CubesAdInterstitialImageView) findViewById(R.id.cubes_textView);
		if (cubesAdIntertitialImageView != null){
			
			LayoutParams lp = new RelativeLayout.LayoutParams(
					RelativeLayout.LayoutParams.MATCH_PARENT,
					RelativeLayout.LayoutParams.MATCH_PARENT);
			cubesAdIntertitialImageView.setLayoutParams(lp);
			
			cubesAdIntertitialImageView.setImageResource(resId);
		}
	}
}
