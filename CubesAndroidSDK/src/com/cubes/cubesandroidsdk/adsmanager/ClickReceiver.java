package com.cubes.cubesandroidsdk.adsmanager;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.webkit.URLUtil;

/**
 * Handle events of click on ads and perform appropriate action
 * 
 * @author makarenko.s
 * 
 */
public class ClickReceiver {

	private static final String DEFAULT_URL = "http://cubes13.com";

	public static void processClick(Context context, int clickAction,
			String clickData) {
		

		if (URLUtil.isValidUrl(clickData)) {
			context.startActivity(new Intent(Intent.ACTION_VIEW).setData(
					Uri.parse(clickData)).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
		} else if(isPhoneValid(clickData)) {
			context.startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(clickData))
			.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
		} else {
			context.startActivity(new Intent(Intent.ACTION_VIEW).setData(
					Uri.parse(DEFAULT_URL)).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
		}
		
//		switch (clickAction) {
//		case ClickAdsAction.BROWSER_ACTION:
//			openBrowser(context, clickData);
//			break;
//		case ClickAdsAction.CALL_ME_ACTION:
//			performCall(context, clickData);
//			break;
//		case ClickAdsAction.INITIATE_BUY_ACTION:
//			openBrowser(context, clickData);
//			break;
//		case ClickAdsAction.INITIATE_PHONE_CALL_ACTION:
//			performCall(context, clickData);
//			break;
//		case ClickAdsAction.PLAY_STORE_ACTION:
//			openBrowser(context, clickData);
//			break;
//		}
	}

	private static void openBrowser(Context context, String data) {

		if (URLUtil.isValidUrl(data)) {
			context.startActivity(new Intent(Intent.ACTION_VIEW).setData(
					Uri.parse(data)).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
		}
	}

	private static void performCall(Context context, String number) {

		if(!isPhoneValid(number)) {
			return;
		}
		context.startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(number))
				.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
	}

	private void initiateBuy() {
		// TODO: later milestone
	}

	private void initiateCall() {
		// TODO: later milestone
	}

	private void openPlayStore(String url) {
		// TODO: later milestone
	}
	
	private static boolean isPhoneValid(String phoneNo) {
		Log.v("SDK_tel", phoneNo);
		String expression = "^[tel:0-9-+]{9,15}$";
		CharSequence inputStr = phoneNo;
		Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(inputStr);
		return (matcher.matches()) ? true : false;
	}

}
