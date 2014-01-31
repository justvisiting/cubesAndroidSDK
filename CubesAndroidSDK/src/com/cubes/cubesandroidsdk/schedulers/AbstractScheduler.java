package com.cubes.cubesandroidsdk.schedulers;

import android.os.Handler;

public abstract class AbstractScheduler {

	private long intervalInMillis;
	private Handler handler;
	private Runnable callback;
	private boolean isStarted;

	/**
	 * 
	 * @param intervalInMillis
	 *            - value of interval between ticking on timer in milliseconds.
	 */
	protected AbstractScheduler(long intervalInMillis) {

		if (intervalInMillis <= 0) {
			throw new IllegalArgumentException(
					"Timer interval cannot be zero or negative value");
		}
		this.intervalInMillis = intervalInMillis;
		this.handler = new Handler();
		this.callback = new Runnable() {

			@Override
			public void run() {
				onTick();
				nextTick();
			}
		};
	}

	/**
	 * start timer. After each interval invokes
	 * {@link AbstractScheduler#onTick()}
	 */
	public void start() {
		
		if(isStarted) {
			return;
		}
		isStarted = true;
		nextTick();
	}

	/**
	 * Stop timer's work
	 */
	public void stop() {
		if(!isStarted) {
			return;
		}
		isStarted = false;
		handler.removeCallbacks(callback);
	}

	private void nextTick() {
		handler.postDelayed(callback, intervalInMillis);
	}

	/**
	 * This method invokes after every interval;
	 */
	protected abstract void onTick();

	/**
	 * Release all resourses that is used by this object.
	 */
	public void dispose() {
		// Implementation could be in child classes
	}

}
