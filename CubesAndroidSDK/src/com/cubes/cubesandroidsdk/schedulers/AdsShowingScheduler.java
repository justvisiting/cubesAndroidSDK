package com.cubes.cubesandroidsdk.schedulers;


/**
 * Class performs periodically invoking of {@link IAdsChanger#onAdsMustChanged()} 
 * @author makarenko.s
 *
 */
public class AdsShowingScheduler extends AbstractScheduler {

	private IAdsChanger callback;
	private int timerCode;
	
	/**
	 * Initiates new instance of scheduler. After initiating call {@link AbstractScheduler#start()} for start timer work
	 * @param callback - {@link IAdsChanger} object that must be implemented by client's class. If its null result will not deliver.
	 * @param timerCode - code that use for dividing multiple timers 
	 * @param intervalInMillis - interval between callback's invocations (in milliseconds). Cannot be 0 or negative value.
	 */
	public AdsShowingScheduler(IAdsChanger callback, long intervalInMillis, int timerCode) {
		super(intervalInMillis);
		this.callback = callback;
		this.timerCode = timerCode;
	}

	@Override
	protected void onTick() {
	
		if(callback != null) {
			callback.onAdsMustChanged(timerCode);
		}
	}
	
	@Override
	public void dispose() {
		callback = null;
	}
	
	/**
	 * Interface for clients of {@link AdsShowingScheduler}.
	 * @author makarenko.s
	 *
	 */
	public interface IAdsChanger {
		
		void onAdsMustChanged(int timerCode);
	}
}
