package com.cubes.cubesandroidsdk.adsmanager.updater;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
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
 * 
 * @author makarenko.s
 * 
 */
public class AdsUpdater implements IClientCallback {

	private IAdsUpdateCallback callback;
	private LoaderManager loaderManager;
	private List<AdsInstance> adsList;
	private List<AdsInstance> defaultAdsList;
	private Context context;
	private Deque<AdsRequest> requestsQueue;

	private AbstractParser imageParser;

	private boolean needDefaultLoading;

	public AdsUpdater(Context context, CacheManager cacheManager,
			IAdsUpdateCallback callback, boolean needDefaultLoading) {

		this.context = context;
		this.callback = callback;
		this.loaderManager = new LoaderManager();
		this.loaderManager.registerCallback(this);
		this.adsList = new ArrayList<AdsInstance>();
		this.defaultAdsList = new ArrayList<AdsInstance>();
		this.requestsQueue = new ArrayDeque<AdsRequest>();
		this.imageParser = new AdsImageParser(cacheManager);
		this.needDefaultLoading = needDefaultLoading;
	}

	/**
	 * Perform loading all available ads from server
	 */
	public void loadAd() {

		if (!LoaderManager.isNetworkAvailable(context)) {
			return;
		}
		if (callback != null) {
			callback.onStartUpdate();
		}
		adsList.clear();
		loadXmlAds();
	}

	public void initialLoading() {

		if (needDefaultLoading) {
			defaultAdsList.clear();
			for (String url : getDefaultUrlsArray()) {
				final AdsRequest request = new AdsRequest();
				request.setRequestType(AdsRequest.TYPE_XML);
				request.setStatus(AdsRequest.STATUS_PROGRESS);
				request.setXmlUrl(url);
				request.setDefault(true);
				requestsQueue.add(request);
			}
		}
		loadXmlAds();
	}

	private void loadXmlAds() {

		for (String url : getUrlsArray()) {
			final AdsRequest request = new AdsRequest();
			request.setRequestType(AdsRequest.TYPE_XML);
			request.setStatus(AdsRequest.STATUS_PROGRESS);
			request.setXmlUrl(url);
			request.setDefault(false);
			requestsQueue.add(request);
		}

		executeNextRequest();
		Log.v("sdk_updater", "start load xml");
		TestFlight.passCheckpoint("Start load XMLs");
	}

	private boolean executeNextRequest() {

		final AdsRequest request = requestsQueue.poll();
		if (request != null) {
			try {
				executeRequest(prepareRequest(request.getXmlUrl(), request),
						new AdsXmlParser());
				return true;
			} catch (MalformedURLException e) {
				e.printStackTrace();
				return executeNextRequest();
			}
		} else {
			return false;
		}
	}

	private void loadImageAds(String url, AdsRequest request) {

		try {
			Log.v("sdk_updater", "start load image");
			TestFlight.passCheckpoint("start load image");
			executeRequest(prepareRequest(url, request), imageParser);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	private String[] getDefaultUrlsArray() {
		return new String[] { UrlManager.getDefaultBannerUrl(1),
				UrlManager.getDefaultInterstitialUrl() };
	}

	private String[] getUrlsArray() {

		// TODO: implement getting of correct url
		return new String[] { UrlManager.getLogoPtUrl(2),
				UrlManager.getBannerPtUrl(2), UrlManager.getInterstitialUrl() };
		/*
		 * return new String[] { "http://iphonepackers.info/bannerad.xml",
		 * "http://iphonepackers.info/bannerad2.xml",
		 * "http://iphonepackers.info/LogoAd.xml",
		 * "http://iphonepackers.info/MultiPartLogoAd.xml" };
		 */
	}

	public void dispose() {
		callback = null;
		loaderManager.unregisterCallback();
	}

	@Override
	public void onRequestFinished(int resultCode, IResponse response) {

		if (resultCode == ResultCode.RESULT_SUCCESSFULL) {
			final AdsRequest adsRequest = (AdsRequest) response.getData();

			if (adsRequest.getRequestType() == AdsRequest.TYPE_XML
					&& adsRequest.getStatus() == AdsRequest.STATUS_FINISHED) {
				Log.v("sdk_updater", "xml loaded");
				TestFlight.passCheckpoint("xml loaded");

				if (adsRequest.hasBarUrl()) {
					loadBarImage(adsRequest);
				} else if (adsRequest.hasMoreUrls()) {
					loadFullScreenImage(adsRequest);
				} else {
					saveInstance(adsRequest.getInstance(),
							adsRequest.isDefault());
				}
			} else if (adsRequest.getRequestType() == AdsRequest.TYPE_IMAGE_BAR
					&& adsRequest.getStatus() == AdsRequest.STATUS_FINISHED) {

				if (adsRequest.hasMoreUrls()) {
					loadFullScreenImage(adsRequest);
				} else {
					saveInstance(adsRequest.getInstance(),
							adsRequest.isDefault());
				}
			} else if (adsRequest.getRequestType() == AdsRequest.TYPE_IMAGE_FULLSCREEN) {
				TestFlight.passCheckpoint("image loaded");
				Log.v("sdk_updater", "image loaded");

				if (adsRequest.hasMoreUrls()) {
					loadFullScreenImage(adsRequest);
				} else {
					saveInstance(adsRequest.getInstance(),
							adsRequest.isDefault());
				}
			}
		} else {
			if (!executeNextRequest()) {
				deliveryResult();
			}
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
		loadImageAds(adsRequest.getNextFullscreenUrl(), adsRequest);
	}

	private void saveInstance(AdsInstance instance, boolean isDefault) {

		if (isDefault) {
			putInstanceToList(defaultAdsList, instance);
		} else {
			putInstanceToList(adsList, instance);
		}
		if (!executeNextRequest()) {
			deliveryResult();

			TestFlight.passCheckpoint("send result to back");
			Log.v("sdk_updater", "send result to back");
		}
	}

	private void putInstanceToList(List<AdsInstance> list, AdsInstance instance) {
		if (!list.contains(instance)) {
			list.add(instance);
		} else {
			list.remove(instance);
			list.add(instance);
		}
	}

	private void deliveryResult() {

		if (callback != null) {
			if (adsList != null && !adsList.isEmpty()) {
				callback.onAdsUpdated(adsList, false);
			}
			if (defaultAdsList != null && !defaultAdsList.isEmpty()) {
				callback.onAdsUpdated(defaultAdsList, true);
			}
		}
	}

	private HttpRequest prepareRequest(String urlString, AdsRequest request)
			throws MalformedURLException {

		final HttpGetRequest networkRequest = new HttpGetRequest(new URL(
				urlString));
		final HttpResponse response = new HttpResponse();
		response.setData(request);
		networkRequest.setResponse(response);
		return networkRequest;
	}

	private void executeRequest(IRequest<?> request, AbstractParser parser) {
		loaderManager.executeRequest(request, parser);
	}

	/**
	 * Interface for delivering result of loading ads to subscriber
	 * 
	 * @author makarenko.s
	 * 
	 */
	public interface IAdsUpdateCallback {

		void onAdsUpdated(List<AdsInstance> newAdsList, boolean isDefault);

		void onStartUpdate();
	}
}
