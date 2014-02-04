package com.cubes.cubessdkexample;

import android.app.Application;

import com.testflightapp.lib.TestFlight;

public class ExtensionApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		TestFlight.takeOff(this, "53ed8eaf-c41e-4c23-8aec-723dc5119d0e");
	}

	
}
