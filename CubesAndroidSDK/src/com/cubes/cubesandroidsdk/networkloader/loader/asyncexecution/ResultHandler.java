package com.cubes.cubesandroidsdk.networkloader.loader.asyncexecution;

import android.os.Handler;
import android.os.Message;

public class ResultHandler extends Handler {
	
	private IExecutionCallback callback;
	
	public ResultHandler(IExecutionCallback callback) {
		
		this.callback = callback;
	}

	@Override
	public void handleMessage(Message msg) {
		super.handleMessage(msg);
		
		if(callback != null) {
			callback.onExecutionFinished(msg.what, msg.obj);
		}
	}
}
