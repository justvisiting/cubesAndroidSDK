package com.cubes.cubesandroidsdk.adsmanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.webkit.URLUtil;

import com.cubes.cubesandroidsdk.config.ClickAdsAction;

/**
 * Handle events of click on ads and perform appropriate action
 * @author makarenko.s
 *
 */
public class ClickReceiver extends BroadcastReceiver {
	
	public static final String INTENT_CLICK_ACTION = "intentClickAction";
	public static final String INTENT_CLICK_DATA = "intentClickData";
	private static final int INVALID_CLICK_ACTION = -1;

	@Override
	public void onReceive(Context context, Intent intent) {
		
		if(intent != null && intent.hasExtra(INTENT_CLICK_ACTION) && intent.hasExtra(INTENT_CLICK_DATA)) {
			int clickAction = intent.getIntExtra(INTENT_CLICK_ACTION, INVALID_CLICK_ACTION);
			switch(clickAction) {
			case ClickAdsAction.BROWSER_ACTION:
				openBrowser(context, intent.getStringExtra(INTENT_CLICK_DATA));
				break;
			case ClickAdsAction.CALL_ME_ACTION:
				openBrowser(context, intent.getStringExtra(INTENT_CLICK_DATA));
				break;
			case ClickAdsAction.INITIATE_BUY_ACTION:
				openBrowser(context, intent.getStringExtra(INTENT_CLICK_DATA));
				break;
			case ClickAdsAction.INITIATE_PHONE_CALL_ACTION:
				openBrowser(context, intent.getStringExtra(INTENT_CLICK_DATA));
				break;
			case ClickAdsAction.PLAY_STORE_ACTION:
				openBrowser(context, intent.getStringExtra(INTENT_CLICK_DATA));
				break;
			}
		}
	}
	
	private void openBrowser(Context context, String url) {
		
		if(URLUtil.isValidUrl(url)) {
			
			context.startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(url)).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
		}
	}
	
	private void performCall(Context context, String number) {
		
        context.startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(number)).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
	}
	
	private void initiateBuy() {
		//TODO: later milestone
	}
	
	private void initiateCall() {
		//TODO: later milestone
	}
	
	private void openPlayStore(String url) {
		//TODO: later milestone
	}

}
