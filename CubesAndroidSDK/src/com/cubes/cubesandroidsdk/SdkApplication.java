package com.cubes.cubesandroidsdk;

import android.app.Application;
import android.util.Log;

import com.testflightapp.lib.TestFlight;

public class SdkApplication extends Application {

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		 TestFlight.takeOff(this, "15e50a36-df9f-4622-84a9-b1707c980dc1");
		 TestFlight.log("Initialize");
		 Log.v("SDK", "application created");
	}
	
	

}
