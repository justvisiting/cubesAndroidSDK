package com.cubes.cubesandroidsdk.adsmanager;

/**
 * Global class for configuration of ads SDK.
 * Cannot be instantiated directly, use {@link Configuration#getInstance() for obtain object}
 * @author makarenko.s
 *
 */
public class Configuration {
	
	public static final String ACTION_CLICK_ADS_EVENT = "com.cubes.cubesandroidsdk.adsclickevent";
	
	private static Configuration instance;
	
	private static final long DEFAULT_ADS_UPDATE_INTERVAL = 20000L;
	private static final long INVALID_ADS_UPDATE_INTERVAL = -1L;
	private long adsUpdateInterval = INVALID_ADS_UPDATE_INTERVAL; 
	
	
	private static final long DEFAULT_ADS_BAR_CHANGE_INTERVAL = 20000L;
	private static final long INVALID_ADS_BAR_CHANGE_INTERVAL = -1L;
	private long adsBarChangeInterval = INVALID_ADS_BAR_CHANGE_INTERVAL;
	
	private Configuration() {
		//Class prevented from instantiating directly
	}
	
	/**
	 * 
	 * @return - object of {@link Configuration}.
	 */
	public static synchronized Configuration getInstance() {
		
		if(instance == null) {
			instance = new Configuration();
		}
		return instance;
	}
	
	/**
	 * 
	 * @return - long value of ads updating interval in milliseconds
	 */
	public long getAdsUpdateIntervalMillis() {
		return (adsUpdateInterval == INVALID_ADS_UPDATE_INTERVAL)? DEFAULT_ADS_UPDATE_INTERVAL: adsUpdateInterval;
	}
	
	/**
	 * 
	 * Set value of the ads updating interval
	 * @param adsUpdateInterval - ads updating interval in milliseconds (cannot be zero or negative value)
	 */
	public void setAdsUpdateIntervalMillis(long adsUpdateInterval) {
		checkIntervalArgument(adsUpdateInterval);
		this.adsUpdateInterval = adsUpdateInterval;
	}
	
	/**
	 * 
	 * @return - long value of ads bar changing interval in milliseconds
	 */
	public long getAdsBarChangeIntervalMillis() {
		
		return (adsBarChangeInterval == INVALID_ADS_BAR_CHANGE_INTERVAL)? DEFAULT_ADS_BAR_CHANGE_INTERVAL: adsBarChangeInterval;
	}
	
	/**
	 * Set value of the ads bar changing interval
	 * @param adsBarChangeInterval - ads bar changing interval (cannot be zero or negative value)
	 */
	public void setAdsBarChangeIntervalMillis(long adsBarChangeInterval) {
		checkIntervalArgument(adsBarChangeInterval);
		this.adsBarChangeInterval = adsBarChangeInterval;
	}
	
	private void checkIntervalArgument(long interval) {
		if(interval <= 0) {
			throw new IllegalArgumentException("Interval value cannot be zero or negative value");
		}
	}

}
