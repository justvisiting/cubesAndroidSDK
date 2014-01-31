package com.cubes.cubesandroidsdk.networkloader.loader.requests;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.cubes.cubesandroidsdk.networkloader.loader.HttpMethod;
import com.cubes.cubesandroidsdk.networkloader.loader.Protocol;

/**
 * Represents http POST request. 
 * @author makarenko.s
 *
 */
public class HttpPostRequest extends HttpRequest {
	
	private Map<String, File> contentItems;
	private String contentDelimiter;
	private PostBody customBody;
	
	public HttpPostRequest() {
		super();
		setProtocol(Protocol.HTTP);
		setMethod(HttpMethod.POST);
		contentItems = new HashMap<String, File>();
		contentDelimiter = generateDelimiter();
	}
	
	public HttpPostRequest(URL url) {
		super(url);
		setProtocol(Protocol.HTTP);
		setMethod(HttpMethod.POST);
		contentItems = new HashMap<String, File>();
		contentDelimiter = generateDelimiter();
	}

	/**
	 * Add item for multipart POST request
	 * @param name 
	 * @param value
	 */
	public void addBodyItem(String name, File value) {

		contentItems.put(name, value);
	}

	/**
	 * Add item for multipart POST request.
	 *  If you add this item you shouldn't use {@link HttpPostRequest#setCustomRequestBody(PostBody)}
	 * @param name
	 * @param value
	 */
	public void addBodyItem(String name, URI value) {

		addBodyItem(name, new File(value));
	}
	
	/**
	 * Return {@link PostBody} object that encapsulates work with POST body.
	 * @return
	 */
	public PostBody getSerializedBody() {

		return customBody == null? new PostBody(contentItems, contentDelimiter): customBody;
	}
	
	/**
	 * Set user-defined body of POST request.
	 * You shouldn't set custom body and  {@link HttpPostRequest#addBodyItem(String, File)} 
	 * 	or {@link HttpPostRequest#addBodyItem(String, URI)} simultaneously.
	 *   
	 * @param customBody
	 */
	public void setCustomRequestBody(PostBody customBody) {
		this.customBody = customBody;
	}
	
	private static String generateDelimiter() {

		return UUID.randomUUID().toString();
	}
	
	/**
	 * 
	 * @return - {@link String} that use as boundary in multiparts requests
	 */
	public String getContentDelimiter() {
		return contentDelimiter;
	}
	
	/**
	 * 
	 * @return - true if this request has data that must be sent as POST body, false otherwise
	 */
	public boolean hasBody() {
		return (contentItems.size() > 0) || (customBody != null);
	}
	
	/**
	 * 
	 * @return - true if this request's "Content-Type" parameter must be set to "multipart/form-data"
	 */
	public boolean isMultipart() {
		return (contentItems.size() > 1) || (customBody != null && customBody.isMultipart());
	}
}
