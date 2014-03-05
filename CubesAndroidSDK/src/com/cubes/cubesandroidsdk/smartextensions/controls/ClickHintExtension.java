package com.cubes.cubesandroidsdk.smartextensions.controls;

import android.content.Context;
import android.text.TextUtils;

import com.cubes.cubesandroidsdk.R;
import com.cubes.cubesandroidsdk.config.ClickAdsAction;
import com.sonyericsson.extras.liveware.extension.util.control.ControlExtension;

public class ClickHintExtension extends ControlExtension {

	private String actionName;
	private boolean isActive;
	
	public boolean isActive() {
		return isActive;
	}

	public ClickHintExtension(Context context, String hostAppPackageName) {
		super(context, hostAppPackageName);
	}
	
	public void setAction(int action) {

		switch (action) {
		case ClickAdsAction.BROWSER_ACTION:
			actionName = mContext.getString(R.string.ads_hint_browser);
			break;
		case ClickAdsAction.PLAY_STORE_ACTION:
			actionName = mContext.getString(R.string.ads_hint_play);
			break;
		case ClickAdsAction.INITIATE_PHONE_CALL_ACTION:
			actionName = mContext.getString(R.string.ads_hint_call);
			break;
		default:
			break;
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		showLayout(R.layout.ads_hint_screen_layout, null);
		if(TextUtils.isEmpty(actionName)) {
			actionName = mContext.getString(R.string.ads_hint_unknown);
		}
		sendText(R.id.ads_click_hint_action_text, actionName);
		isActive = true;
	}
	
	@Override
	public void onPause() {
		super.onPause();
		isActive = false;
	}
}
