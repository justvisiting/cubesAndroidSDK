/**
 * 
 */
package com.cubes.cubesandroidsdk.utils;

/**
 * @author 
 *
 */
public enum AdType {
	TextAd(1),
	BannerAd(2),
	LogoAd(3),
	MultiPartTextAd(4),
	MultiPartBannerAd(5),
	MultiPartLogoAd(6),
	InterstitialAd(7);
	 private int value;
	   private AdType(int value) {
	      this.value = value;
	   }
	   public int getValue() {
	      return value;
	   }
}
