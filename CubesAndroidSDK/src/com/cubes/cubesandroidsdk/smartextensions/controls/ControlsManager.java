package com.cubes.cubesandroidsdk.smartextensions.controls;

import java.util.Stack;

import com.sonyericsson.extras.liveware.extension.util.control.ControlExtension;

public class ControlsManager {

	public static ControlsManager instance;

	private Stack<ControlExtension> controlsStack;

	private ControlsManager() {
		controlsStack = new Stack<ControlExtension>();
	}

	public static synchronized ControlsManager getInstance() {

		if (instance == null) {
			instance = new ControlsManager();
		}
		return instance;
	}

	public void putToStack(ControlExtension extension) {
		controlsStack.push(extension);
	}

	public boolean restore() {
		if (!controlsStack.isEmpty()) {
			ControlExtension previous = controlsStack.pop();
			previous.onStart();
			previous.onResume();
			return true;
		} else {
			return false;
		}
	}
}
