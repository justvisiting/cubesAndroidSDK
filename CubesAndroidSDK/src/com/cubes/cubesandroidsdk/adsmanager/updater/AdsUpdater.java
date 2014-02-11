package com.cubes.cubesandroidsdk.adsmanager.updater;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;

import com.cubes.cubesandroidsdk.adsmanager.AdsInstance;
import com.cubes.cubesandroidsdk.networkloader.loader.LoaderManager;
import com.cubes.cubesandroidsdk.networkloader.loader.ResultCode;
import com.cubes.cubesandroidsdk.networkloader.loader.requests.HttpGetRequest;
import com.cubes.cubesandroidsdk.networkloader.loader.requests.HttpRequest;
import com.cubes.cubesandroidsdk.networkloader.loader.requests.IRequest;
import com.cubes.cubesandroidsdk.networkloader.loader.responses.AbstractParser;
import com.cubes.cubesandroidsdk.networkloader.loader.responses.HttpResponse;
import com.cubes.cubesandroidsdk.networkloader.loader.responses.IClientCallback;
import com.cubes.cubesandroidsdk.networkloader.loader.responses.IResponse;
import com.cubes.cubesandroidsdk.utils.UrlManager;
import com.testflightapp.lib.TestFlight;

/**
 * Class that perform updating ads from server
 * @author makarenko.s
 *
 */
public class AdsUpdater implements IClientCallback {

	private IAdsUpdateCallback callback;
	private LoaderManager loaderManager;
	private List<AdsInstance> adsList;
	private Context context;
	private int adsMustBeLoaded;
	private int processedAdsCount;

	public AdsUpdater(Context context, IAdsUpdateCallback callback) {

		this.context = context;
		this.callback = callback;
		this.loaderManager = new LoaderManager();
		this.loaderManager.registerCallback(this);
		this.adsList = new ArrayList<AdsInstance>();
	}

	/**
	 * Perform loading all available ads from server
	 */
	public void loadAd() {

		loadXmlAds();
	}

	private void loadXmlAds() {
		try {
			for (String url : getUrlsArray()) {
				AdsRequest request = new AdsRequest();
				request.setRequestType(AdsRequest.TYPE_XML);
				request.setStatus(AdsRequest.STATUS_PROGRESS);
				executeRequest(prepareRequest(url, request), new AdsXmlParser());
				adsMustBeLoaded++;
			}
			Log.v("SDK", "start load xml");
			TestFlight.passCheckpoint("Start load XML");
		} catch (MalformedURLException e) {
			// TODO: delivering error message
			e.printStackTrace();
		}
	}

	private void loadImageAds(String url, AdsRequest request) {

		try {
			Log.v("SDK", "start load image");
			TestFlight.passCheckpoint("start load image");
			executeRequest(prepareRequest(url, request), new AdsImageParser(
					context.getExternalCacheDir().getAbsolutePath()));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	private String[] getUrlsArray() {

		// TODO: implement getting of correct url
		return new String[] { UrlManager.getLogoPtUrl(2), UrlManager.getBannerPtUrl(2) };
	}

	public void dispose() {
		callback = null;
		loaderManager.unregisterCallback();
	}

	@Override
	public void onRequestFinished(int resultCode, IResponse response) {

		if (resultCode == ResultCode.RESULT_SUCCESSFULL) {
			AdsRequest adsRequest = (AdsRequest) response.getData();

			if (adsRequest.getRequestType() == AdsRequest.TYPE_XML
					&& adsRequest.getStatus() == AdsRequest.STATUS_FINISHED) {
				Log.v("SDK", "xml loaded");
				TestFlight.passCheckpoint("xml loaded");
				if (adsRequest.hasBarUrl()) {
					loadBarImage(adsRequest);
				} else if(adsRequest.hasMoreUrls()) {
					loadFullScreenImage(adsRequest);
				} else {
					putInstanceIntoList(adsRequest.getInstance());
				}
			} else if (adsRequest.getRequestType() == AdsRequest.TYPE_IMAGE_BAR
					&& adsRequest.getStatus() == AdsRequest.STATUS_FINISHED) {
				
				if (adsRequest.hasMoreUrls()) {
					loadFullScreenImage(adsRequest);
				} else {
					putInstanceIntoList(adsRequest.getInstance());
				}
			} else if (adsRequest.getRequestType() == AdsRequest.TYPE_IMAGE_FULLSCREEN) {
				TestFlight.passCheckpoint("image loaded");
				Log.v("SDK", "image loaded");

				if (adsRequest.hasMoreUrls()) {
					loadFullScreenImage(adsRequest);
				} else {
					putInstanceIntoList(adsRequest.getInstance());
				}
			}
		} else {
			processedAdsCount++;
		}
	}
	
	private void loadBarImage(AdsRequest adsRequest) {
		adsRequest.setRequestType(AdsRequest.TYPE_IMAGE_BAR);
		adsRequest.setStatus(AdsRequest.STATUS_PROGRESS);
		loadImageAds(adsRequest.getbarUrl(), adsRequest);
	}
	
	private void loadFullScreenImage(AdsRequest adsRequest) {
		adsRequest.setRequestType(AdsRequest.TYPE_IMAGE_FULLSCREEN);
		adsRequest.setStatus(AdsRequest.STATUS_PROGRESS);
		loadImageAds(adsRequest.getNextFullscreenUrl(),adsRequest);
	}

	private void putInstanceIntoList(AdsInstance instance) {
		if (!adsList.contains(instance)) {
			TestFlight.passCheckpoint("send result to back");
			Log.v("SDK", "send result to back");
			adsList.add(instance);
			processedAdsCount++;
			if(adsMustBeLoaded == processedAdsCount) {
				deliveryResult();
			}
		}
	}
	
	private void deliveryResult() {
		if (callback != null) {
			callback.onAdsUpdate(adsList);
		}
		adsMustBeLoaded = 0;
		processedAdsCount = 0;
	}

	private HttpRequest prepareRequest(String urlString, AdsRequest request)
			throws MalformedURLException {

		HttpGetRequest getRequest = new HttpGetRequest(new URL(urlString));
		HttpResponse response = new HttpResponse();
		response.setData(request);
		getRequest.setResponse(response);
		return getRequest;
	}

	private void executeRequest(IRequest<?> request, AbstractParser parser) {
		loaderManager.executeRequest(request, parser);
	}

	/**
	 * Interface for delivering result of loading ads to subscriber 
	 * @author makarenko.s
	 *
	 */
	public interface IAdsUpdateCallback {

		void onAdsUpdate(List<AdsInstance> newAdsList);
	}
}
