package com.cubes.cubesandroidsdk.networkloader.loader;

import java.util.ArrayDeque;
import java.util.Deque;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;

import com.cubes.cubesandroidsdk.networkloader.loader.asyncexecution.AsyncTaskExecutor;
import com.cubes.cubesandroidsdk.networkloader.loader.asyncexecution.IExecutionCallback;
import com.cubes.cubesandroidsdk.networkloader.loader.asyncexecution.NetworkLoaderTask;
import com.cubes.cubesandroidsdk.networkloader.loader.asyncexecution.ResultHandler;
import com.cubes.cubesandroidsdk.networkloader.loader.loaders.INetworkLoader;
import com.cubes.cubesandroidsdk.networkloader.loader.requests.IRequest;
import com.cubes.cubesandroidsdk.networkloader.loader.responses.AbstractParser;
import com.cubes.cubesandroidsdk.networkloader.loader.responses.IClientCallback;
import com.cubes.cubesandroidsdk.networkloader.loader.responses.IResponse;

public class LoaderManager implements IExecutionCallback {

	private final Deque<INetworkLoader> requestsQueue;
	private boolean isExecuting;
	private final AsyncTaskExecutor asyncExecutor;
	private final Handler resultHandler;
	private IClientCallback callback;
	
	public LoaderManager() {
		requestsQueue = new ArrayDeque<INetworkLoader>();
		asyncExecutor = new AsyncTaskExecutor();
		resultHandler = new ResultHandler(this);
	}
	
	/**
	 * Check if network connection available
	 * @param context
	 * @return true if connection is available, false otherwise
	 */
	public static boolean isNetworkAvailable(Context context) {

		final NetworkInfo activeNetworkInfo = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
	
	/**
	 * Put request into queue and execute it
	 * @param request
	 * @param parser
	 */
	public void executeRequest(IRequest<?> request, AbstractParser parser) {
		enqueueRequest(NetworkLoaderFactory.createNetworkLoader(request, parser));
		executeQueue();
	}
	
	private void enqueueRequest(INetworkLoader loader) {
		requestsQueue.addLast(loader);
	}
	
	/**
	 * Add callback for delivering result of request
	 * @param callback
	 */
	public void registerCallback(IClientCallback callback) {
		this.callback = callback;
	}
	
	/**
	 * Unsubscribe from delivering of results
	 */
	public void unregisterCallback() {
		callback = null;
	}
	
	private void executeQueue() {
		if(mustRun()) {
			executeNextRequest();
		}
	}
	
	private boolean mustRun() {
		if(requestsQueue.isEmpty()) {
			isExecuting = false;
			return false;
		}
		if(isExecuting) {
			return false;
		}
		return true;
	}
	
	private void executeNextRequest() {
		asyncExecutor.executeTask(new NetworkLoaderTask(requestsQueue.pollFirst(), resultHandler));
	}
	
	public void cancelCurrentTask() {
		asyncExecutor.stopCurrentTask();
	}

	@Override
	public void onExecutionFinished(int what, Object result) {
		executeQueue();
		sendResult(what, (IResponse) result);
	}
	
	private void sendResult(int what, IResponse response) {
		if(callback != null) {
			callback.onRequestFinished(what, response);
		}
	}
}
