package com.cubes.cubesandroidsdk.adsmanager.updater;

import java.io.InputStream;

import com.cubes.cubesandroidsdk.networkloader.loader.responses.AbstractParser;
import com.cubes.cubesandroidsdk.networkloader.loader.responses.IResponse;

/**
 * Load from stream image, save it into cache directory and put url string into {@link IResponse}
 * @author makarenko.s
 *
 */
public class AdsImageParser extends AbstractParser {

	private volatile CacheManager cacheManager;
	
	public AdsImageParser(CacheManager cacheManager) {
		this.cacheManager = cacheManager;
	}

	@Override
	public IResponse parse(InputStream stream, IResponse response) {

		try {
			final String uri = cacheManager.saveToFile(stream);
			final AdsRequest request = (AdsRequest) response.getData();
			
			if(request.getRequestType() == AdsRequest.TYPE_IMAGE_BAR) {
				request.getInstance().setBarUriString(uri);
				request.setStatus(AdsRequest.STATUS_FINISHED);
				
			} else if(request.getRequestType() == AdsRequest.TYPE_IMAGE_FULLSCREEN) {
				request.getInstance().getFullscreenAds().add(uri);
			}
			response.setData(request);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}
}
