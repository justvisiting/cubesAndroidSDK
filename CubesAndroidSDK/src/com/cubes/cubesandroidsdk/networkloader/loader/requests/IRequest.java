package com.cubes.cubesandroidsdk.networkloader.loader.requests;

import java.net.Proxy;
import java.util.HashMap;

import com.cubes.cubesandroidsdk.networkloader.loader.responses.IResponse;

public interface IRequest<R extends IResponse> {

	void setServer(String serverName);
	String getServer();
	
	void setPath(String path);
	String getPath();
	
	void addHeaderParam(String name, String value);
	HashMap<String, String> getParams();
	
	void setMethod(String method);
	String getMethod();
	
	void setProtocol(int protocol);
	int getProtocol();
	
	void setProxy(String address, int port);
	boolean hasProxy();
	Proxy getProxy();
	
	R getResponse();
	void setResponse(R response);
	
}
