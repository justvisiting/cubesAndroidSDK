package com.cubes.cubesandroidsdk.networkloader.loader.requests;

import java.net.URL;

import com.cubes.cubesandroidsdk.networkloader.loader.HttpMethod;
import com.cubes.cubesandroidsdk.networkloader.loader.Protocol;

public class HttpGetRequest extends HttpRequest {

	public HttpGetRequest() {
		super();
		setProtocol(Protocol.HTTP);
		setMethod(HttpMethod.GET);
	}
	
	public HttpGetRequest(URL url) {
		super(url);
		setProtocol(Protocol.HTTP);
		setMethod(HttpMethod.GET);
	}
}
