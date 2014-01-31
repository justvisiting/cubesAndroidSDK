package com.cubes.cubesandroidsdk.networkloader.loader.asyncexecution;

import android.os.Handler;

import com.cubes.cubesandroidsdk.networkloader.loader.ResultCode;

public abstract class AbstractAsyncTask implements Runnable {
	
	private Handler handler;
	
	public AbstractAsyncTask(Handler handler) {
		this.handler = handler;
	}

	@Override
	public void run() {
		execute();
	}
	
	/**
	 * Send result to called its thread 
	 * @param what - {@link ResultCode}
	 * @param result - {@link Object} that contain result. Can be null if some error occurred
	 * or no response needed
	 */
	protected void sendReport(int what, Object result) {
		handler.sendMessage(handler.obtainMessage(what, result));
	}
	
	protected abstract void execute();
}
