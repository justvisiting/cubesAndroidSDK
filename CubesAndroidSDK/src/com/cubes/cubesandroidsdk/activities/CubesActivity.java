package com.cubes.cubesandroidsdk.activities;

import android.os.Bundle;
import android.view.Gravity;
import android.widget.RelativeLayout;

import com.cubes.cubesandroidsdk.R;

// Activity Test
public class CubesActivity extends AbstractActivity{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// loadAdCubesTextView for test 
		loadAdCubesTextView(getResources().getString(R.string.text_ad), Gravity.CENTER, RelativeLayout.ALIGN_PARENT_BOTTOM);
	
		// loadAdCubesBannerImageView for test 
		loadAdBannerCubesImageView(R.drawable.ic_launcher, Gravity.CENTER, RelativeLayout.CENTER_IN_PARENT);
		
		// loadAdInterstitialCubesImageView for test
		loadAdInterstitialCubesImageView(R.drawable.ic_launcher, Gravity.CENTER, 3000);
	}

	@Override
	protected int getLayoutResId(){
		return R.layout.cubes_activity;
	}
}
