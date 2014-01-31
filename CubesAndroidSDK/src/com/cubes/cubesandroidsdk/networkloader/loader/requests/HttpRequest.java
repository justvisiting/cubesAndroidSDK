package com.cubes.cubesandroidsdk.networkloader.loader.requests;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.util.HashMap;

import com.cubes.cubesandroidsdk.networkloader.loader.HttpMethod;
import com.cubes.cubesandroidsdk.networkloader.loader.responses.IResponse;

/**
 * Class that represents abstract HTTp request (GET, POST etc).
 * You shouldn't use this class directly,
 *  use one from children of this class instead ({@link HttpGetRequest}, {@link HttpPostRequest})
 *  
 * @author makarenko.s
 *
 * @param <R>
 */
public class HttpRequest implements
		IRequest<IResponse> {

	private HashMap<String, String> params;
	private String serverName;
	private String httpMethod;
	private int protocol;
	private Proxy proxy;
	private IResponse response;
	private String path;
	private URL url;

	protected HttpRequest() {
		params = new HashMap<String, String>();
	}
	
	protected HttpRequest(URL url) {
		this();
		this.url = url;
	}
	
	public URL getUrl() {
		
		return url;
	}
	
	public boolean hasUrl() {
		
		return (url != null);
	}
	
	/**
	 * Name of remote server in form server.domain.
	 * 
	 */
	@Override
	public void setServer(String serverName) {

		this.serverName = serverName;
	}

	/**
	 * Key - value pair that will insert in request
	 */
	@Override
	public void addHeaderParam(String name, String value) {

		params.put(name, value);
	}

	/**
	 * One from {@link HttpMethod} parameters - name of HTTP method that will use
	 */
	@Override
	public void setMethod(String httpMethod) {

		this.httpMethod = httpMethod;
	}

	/**
	 * 
	 */
	@Override
	public void setProtocol(int protocol) {

		this.protocol = protocol;
	}

	@Override
	public void setProxy(String address, int port) {

		proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(address, port));
	}

	@Override
	public String getServer() {

		return serverName;
	}

	@Override
	public int getProtocol() {

		return protocol;
	}

	@Override
	public String getMethod() {

		return httpMethod;
	}

	@Override
	public HashMap<String, String> getParams() {

		return params;
	}

	@Override
	public boolean hasProxy() {
		return proxy != null;
	}

	@Override
	public Proxy getProxy() {

		return proxy;
	}

	@Override
	public IResponse getResponse() {

		return response;
	}

	@Override
	public void setResponse(IResponse response) {

		this.response = response;

	}

	@Override
	public void setPath(String path) {

		this.path = path;
	}

	@Override
	public String getPath() {

		return path;
	}

}
