package com.cubes.cubesandroidsdk.networkloader.loader.asyncexecution;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class AsyncTaskExecutor {

	private ExecutorService executor;
	private static final int THREAD_POOL_SIZE = 1;
	
	public AsyncTaskExecutor() {
		executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
	}
	
	public void executeTask(AbstractAsyncTask task) {
		executor.execute(task);
	}
	
	public void stopCurrentTask() {
		executor.shutdownNow();
	}
}
