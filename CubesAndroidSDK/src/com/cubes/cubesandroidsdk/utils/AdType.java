/**
 * 
 */
package com.cubes.cubesandroidsdk.utils;

/**
 * @author 
 *
 */
public enum AdType {
	Text(1),
	Banner(2),
	Logo(3),
	MultiPartText(4),
	MultiPartBanner(5),
	MultiPartLogo(6),
	Interstitial(7);
	 private int value;
	   private AdType(int value) {
	      this.value = value;
	   }
	   public int getValue() {
	      return value;
	   }
}
