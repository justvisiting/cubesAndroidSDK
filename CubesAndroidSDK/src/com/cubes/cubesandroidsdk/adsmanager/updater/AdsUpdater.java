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
import com.cubes.cubesandroidsdk.utils.AdXmlElements;
import com.cubes.cubesandroidsdk.utils.UrlManager;
import com.testflightapp.lib.TestFlight;

public class AdsUpdater implements IClientCallback {
	
	private IAdsUpdateCallback callback;
	private LoaderManager loaderManager;
	private List<AdsInstance> adsList;
	private Context context;
	
	public AdsUpdater(Context context, IAdsUpdateCallback callback) {
		
		this.context = context;
		this.callback =  callback;
		this.loaderManager = new LoaderManager();
		this.loaderManager.registerCallback(this);
		this.adsList = new ArrayList<AdsInstance>();
	}
	
	public void loadAd() {
		
		loadXmlAds();
	}

	private void loadXmlAds() {
		try {
			executeRequest(prepareRequest(getUrlString()), new AdsXmlParser());
			Log.v("SDK", "start load xml");
			TestFlight.passCheckpoint("Start load XML");
		} catch (MalformedURLException e) {
			// TODO: delivering error message
			e.printStackTrace();
		}
	}

	private void loadImageAds(Object data) {
		AdXmlElements xml = (AdXmlElements) data;
		
		try {
			Log.v("SDK", "start load image");
			TestFlight.passCheckpoint("start load image");
			executeRequest(prepareImageRequest(xml.get_bannerUrl(), Utils.convertXmlToAdsInstance(xml)), new AdsImageParser(context.getExternalCacheDir().getAbsolutePath()));
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private String getUrlString() {

		// TODO: implement getting of correct url
		return UrlManager.getBannerPtUrl(1);
//		return UrlManager.getInterstitialUrl();
	}

	public void dispose() {
		callback = null;
		loaderManager.unregisterCallback();
	}

	@Override
	public void onRequestFinished(int resultCode, IResponse response) {
		
		if (resultCode == ResultCode.RESULT_SUCCESSFULL) {
			Object data = response.getData();

			//TODO: Need to be refactored!!!
			if (data instanceof AdXmlElements) {
				Log.v("SDK", "xml loaded");
				TestFlight.passCheckpoint("xml loaded");
				loadImageAds(data);
			} else if (data instanceof AdsInstance) {

				AdsInstance instance = (AdsInstance) data;
				TestFlight.passCheckpoint("image loaded");
				Log.v("SDK", "image loaded");
				if(!adsList.contains(instance)) {
					TestFlight.passCheckpoint("send result to back");
					Log.v("SDK", "send result to back");
					adsList.add(instance);
					if(callback != null) {
						callback.onAdsUpdate(adsList);
					}
				}
			}
		}
	}

	private HttpRequest prepareRequest(String urlString)
			throws MalformedURLException {
			
		HttpGetRequest request = new HttpGetRequest(new URL(urlString));
		request.setResponse(new HttpResponse());
		return request;
	}
	
	private HttpRequest prepareImageRequest(String urlString, AdsInstance instance) throws MalformedURLException {
		
		HttpRequest request = prepareRequest(urlString);
		HttpResponse response = new HttpResponse();
		response.setData(instance);
		request.setResponse(response);
		return request;
	}

	private void executeRequest(IRequest<?> request, AbstractParser parser) {
		loaderManager.executeRequest(request, parser);
	}

	public interface IAdsUpdateCallback {

		void onAdsUpdate(List<AdsInstance> newAdsList);
	}
}
