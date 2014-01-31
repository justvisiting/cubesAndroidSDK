package com.cubes.cubesandroidsdk.networkloader.loader.responses;


public interface IResponse {

	void setProtocol(int protocol);
	int getProtocol();
	
	void setMethod(String method);
	String getMethod();
	
	void setHttpResponseCode(int code);
	int getHttpResponseCode();
	
	void setData(Object data);
	Object getData();
}
