package com.cubes.cubesandroidsdk.schedulers;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.cubes.cubesandroidsdk.adsmanager.AdsImageParser;
import com.cubes.cubesandroidsdk.adsmanager.AdsInstance;
import com.cubes.cubesandroidsdk.adsmanager.AdsXmlParser;
import com.cubes.cubesandroidsdk.adsmanager.Utils;
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

/**
 * Perform update ads by predefined schedule. Result will deliver to client
 * through {@link IAdsUpdateCallback} as list of {@link AdsInstance}
 * 
 * @author makarenko.s
 * 
 */
public class AdsUpdateScheduler extends AbstractScheduler implements
		IClientCallback {

	private IAdsUpdateCallback callback;
	private LoaderManager loaderManager;
	private Context context;
	private List<AdsInstance> adsList;

	public AdsUpdateScheduler(IAdsUpdateCallback callback,
			long intervalInMillis, Context context) {
		super(intervalInMillis);
		this.callback = callback;
		this.loaderManager = new LoaderManager();
		this.loaderManager.registerCallback(this);
		this.context = context;
		this.adsList = new ArrayList<AdsInstance>();
	}

	@Override
	protected void onTick() {

		loadXmlAds();
	}

	private void loadXmlAds() {
		try {
			executeRequest(prepareRequest(getUrlString()), new AdsXmlParser());
		} catch (MalformedURLException e) {
			// TODO: delivering error message
			e.printStackTrace();
		}
	}

	private void loadImageAds(Object data) {
		AdXmlElements xml = (AdXmlElements) data;
		
		try {
			executeRequest(prepareImageRequest(xml.get_bannerUrl(), Utils.convertXmlToAdsInstance(xml)), new AdsImageParser(context.getExternalCacheDir().getAbsolutePath()));
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private String getUrlString() {

		// TODO: implement getting of correct url
		return "http://iphonepackers.info/bannerad.xml";
	}

	@Override
	public void dispose() {
		super.dispose();
		callback = null;
		loaderManager.unregisterCallback();
	}

	@Override
	public void onRequestFinished(int resultCode, IResponse response) {
		
		if (resultCode == ResultCode.RESULT_SUCCESSFULL) {
			Object data = response.getData();

			if (data instanceof AdXmlElements) {
			
				loadImageAds(data);
			} else if (data instanceof AdsInstance) {

				adsList.add((AdsInstance) data);
				if(callback != null) {
					callback.onAdsUpdate(adsList);
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
