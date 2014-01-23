package com.cubes.cubesandroidsdk.utils;

import java.util.Random;

public class UrlManager {
	static String[] urls = {"http://iphonepackers.info/bannerad.xml"
			,"http://iphonepackers.info/bannerad2.xml"
			," http://iphonepackers.info/interstitial.xml"
			, "http://iphonepackers.info/multipartBannerAd.xml"
			, "http://iphonepackers.info/MultiPartLogoAd.xml"};
	
	static int counter = 0;
	static Random random;
	static {
		random = new Random(System.currentTimeMillis());
		counter = random.nextInt(urls.length);
	}
	
	/*
	 * increase counter in argument as things are implemented
	 *  1 => banner ad only
	 *  2 => above + interstitial ad
	 *  3 => above + multipart banner ad
	 *  4 => above + logo ad
	 */
	public static String getUrl(int implementationLevel) {
		
		return urls[random.nextInt(implementationLevel)];
		
	}
}
