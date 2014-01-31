package com.cubes.cubesandroidsdk.networkloader.loader.loaders;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map.Entry;

import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;

import com.cubes.cubesandroidsdk.networkloader.loader.HttpMethod;
import com.cubes.cubesandroidsdk.networkloader.loader.Protocol;
import com.cubes.cubesandroidsdk.networkloader.loader.requests.HttpGetRequest;
import com.cubes.cubesandroidsdk.networkloader.loader.requests.HttpPostRequest;
import com.cubes.cubesandroidsdk.networkloader.loader.requests.IRequest;
import com.cubes.cubesandroidsdk.networkloader.loader.responses.AbstractParser;
import com.cubes.cubesandroidsdk.networkloader.loader.responses.IResponse;

public class HttpLoader implements INetworkLoader {

	private IRequest<?> request;
	private AbstractParser parser;
	private static final String SCHEME_HTTP = "http";
	private static final String SCHEME_HTTPS = "https";

	public HttpLoader() {

	}

	@Override
	public INetworkLoader setRequest(IRequest<?> request) {
		
		this.request = request;
		return this;
	}

	@Override
	public INetworkLoader setParser(AbstractParser parser) {

		this.parser = parser;
		return this;
	}

	@Override
	public IResponse execute() throws Exception {

		if (request.getMethod().equals(HttpMethod.GET)) {
			return executeGet();
		} else if (request.getMethod().equals(HttpMethod.POST)) {
			return executePost();
		}

		return null;
	}

	private IResponse executeGet() throws Exception {

		HttpGetRequest getRequest = (HttpGetRequest) request;
		HttpURLConnection connection = null;
		if(getRequest.hasUrl()) {
			connection = openConnection(getRequest.getUrl());
		} else {
			connection = openConnection(buildUrl(
					getScheme(), getRequest.getServer(), getRequest.getPath(),
					getRequest.getParams()));
		}
		setGetParameters(connection);
		final IResponse response = prepareResponse(connection);

		connection.disconnect();
		return response;
	}

	private IResponse executePost() throws Exception {
		
		HttpPostRequest postRequest = (HttpPostRequest) request;
		HttpURLConnection connection = null;
		if(postRequest.hasUrl()) {
			connection = openConnection(postRequest.getUrl());
		} else {
			connection = openConnection(buildUrl(
					getScheme(), postRequest.getServer(), postRequest.getPath(),
					postRequest.getParams()));
		}
		setPostParameters(connection, postRequest);
		if (postRequest.hasBody()) {
			final OutputStream outStream = connection.getOutputStream();
			postRequest.getSerializedBody().writeInto(outStream);
			outStream.close();
		}
		
		final IResponse response = prepareResponse(connection);
		connection.disconnect();
		return response;
	}

	private void setGetParameters(HttpURLConnection connection) {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.FROYO) {
			System.setProperty("http.keepAlive", "false");
		} else {
			connection.setRequestProperty("Connection", "Keep-Alive");
		}
		connection.setRequestProperty("Charset", "UTF-8");
	}

	private void setPostParameters(HttpURLConnection connection,
			HttpPostRequest request) {
		connection.setDoOutput(true);
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.FROYO) {
			System.setProperty("http.keepAlive", "false");
		} else {
			connection.setRequestProperty("Connection", "Keep-Alive");
		}

		connection.setRequestProperty("Charset", "UTF-8");
		if(request.isMultipart()) {
			connection
			.setRequestProperty(
					"Content-Type",
					"multipart/form-data;boundary="
							+ request.getContentDelimiter());
		}
	}

	private HttpURLConnection openConnection(URL url) throws Exception {

		if (request.hasProxy()) {
			return (HttpURLConnection) url.openConnection(request.getProxy());
		} else {
			return (HttpURLConnection) url.openConnection();
		}
	}

	private IResponse prepareResponse(HttpURLConnection connection)
			throws IOException {
		final IResponse response = request.getResponse();
		response.setProtocol(request.getProtocol());
		response.setMethod(request.getMethod());
		response.setHttpResponseCode(connection.getResponseCode());
		if (parser != null) {
			final InputStream inputStream = connection.getInputStream();
			if (inputStream != null) {
				parser.parse(inputStream, response);
				inputStream.close();
			}
		}
		return response;
	}

	private URL buildUrl(String scheme, String server, String path,
			HashMap<String, String> params) throws Exception {

		final Uri.Builder builder = new Uri.Builder();
		builder.scheme(scheme).authority(server);
		if (!TextUtils.isEmpty(path)) {
			builder.appendEncodedPath(path);
		}
		if (params != null && !params.isEmpty()) {
			for (Entry<String, String> entry : params.entrySet()) {
				builder.appendQueryParameter(entry.getKey(), entry.getValue());
			}
		}
		return new URI(builder.build().toString()).toURL();
	}

	private String getScheme() {
		switch (request.getProtocol()) {
		case Protocol.HTTP:
			return SCHEME_HTTP;
		case Protocol.HTTPS:
			return SCHEME_HTTPS;
		default:
			return "";
		}
	}

}
