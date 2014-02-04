package com.cubes.cubesandroidsdk.adsmanager;

import com.cubes.cubesandroidsdk.utils.AdXmlElements;

public class Utils {
	
	public static AdsInstance convertXmlToAdsInstance(AdXmlElements xml) {
		
		return new AdsInstance(xml.get_adType(), xml.get_clickType(), xml.get_clickUrl(), null, null, null);
		
	}
}
