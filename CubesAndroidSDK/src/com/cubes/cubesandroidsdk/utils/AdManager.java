package com.cubes.cubesandroidsdk.utils;

public class AdManager {

	//TODO:...
	public static AdXmlElements getAd() {
		new RequestTask().execute("http://iphonepackers.info/bannerad.xml");
		return null;
	}
}
