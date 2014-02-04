package com.cubes.cubesandroidsdk;

import android.app.Application;
import android.util.Log;

import com.testflightapp.lib.TestFlight;

public class SdkApplication extends Application {

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		 TestFlight.takeOff(this, "53ed8eaf-c41e-4c23-8aec-723dc5119d0e");
		 TestFlight.log("Initialize");
		 TestFlight.passCheckpoint("Application started");
		 Log.v("SDK", "application created");
	}
	
	

}
