package com.cubes.cubesandroidsdk.networkloader.loader.responses;


public class HttpResponse implements IResponse {
	
	private int protocol;
	private String method;
	private int httpResponseCode;
	private Object data;

	@Override
	public void setProtocol(int protocol) {
		
		this.protocol = protocol;
	}

	@Override
	public int getProtocol() {

		return protocol;
	}

	@Override
	public void setMethod(String method) {

		this.method = method;
	}

	@Override
	public String getMethod() {

		return method;
	}

	@Override
	public void setHttpResponseCode(int code) {

		httpResponseCode = code;
	}

	@Override
	public int getHttpResponseCode() {
		
		return httpResponseCode;
	}

	@Override
	public void setData(Object data) {
		
		this.data = data;
	}

	@Override
	public Object getData() {
		
		return data;
	}
}
