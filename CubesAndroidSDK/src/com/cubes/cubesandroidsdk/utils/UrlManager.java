package com.cubes.cubesandroidsdk.utils;

import java.util.Random;

public class UrlManager {
	static String[] bannerUrls = {"http://iphonepackers.info/bannerad.xml"
			,"http://iphonepackers.info/bannerad2.xml"
		};
	
	static String[] logoUrls = {
		 "http://iphonepackers.info/LogoAd.xml"
		, "http://iphonepackers.info/MultiPartLogoAd.xml"
	};
	
	static Random random;
	static {
		random = new Random(System.currentTimeMillis());
	}
	
	/*
	 * increase counter in argument as things are implemented
	 *  1 => banner ad only
	 *  2 => above + multipart banner ad
	 */
	public static String getBannerPtUrl(int implementationLevel) {
		return bannerUrls[random.nextInt(implementationLevel)];
	}
	

	/*
	 * increase counter in argument as things are implemented
	 *  1 => logo ad only
	 *  2 => above + multipart logo ad
	 */
	public static String getLogoPtUrl(int implementationLevel) {
		return logoUrls[random.nextInt(implementationLevel)];
	}
	
	public static String getInterstitialUrl() {	
		return "http://iphonepackers.info/interstitial.xml";	
	}
	
	public static String getDefaultBannerUrl(int implementationLevel) {
		return bannerUrls[random.nextInt(implementationLevel)];
	}
	
	public static String getDefaultLogoUrl(int implementationLevel) {
		return logoUrls[random.nextInt(implementationLevel)];
	}
	
	
	/*
	 * 
	 */
	public static String getDefaultInterstitialUrl() {
		return "http://iphonepackers.info/interstitial.xml";	
	}
}
