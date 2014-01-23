package com.cubes.cubesandroidsdk.utils;


public enum ClickType {
	   Browse(1),
	   PlayStore(2),
	   Call(3),
	   Text(4);
	   private int value;
	   private ClickType(int value) {
	      this.value = value;
	   }
	   public int getValue() {
	      return value;
	   }
}
