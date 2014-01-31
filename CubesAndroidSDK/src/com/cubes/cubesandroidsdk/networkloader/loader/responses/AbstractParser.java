package com.cubes.cubesandroidsdk.networkloader.loader.responses;

import java.io.InputStream;

/**
 * Class that encapsulates work with response's stream. 
 * You must implement abstract method {@link AbstractParser#parse(InputStream, IResponse)}
 *  for getting data from server.
 * @author makarenko.s
 *
 */
public abstract class AbstractParser {

	/**
	 * Perform parsing answer from server here.
	 * @param stream - {@link InputStream} from network connection
	 * @param response - {@link IResponse} for delivery result or null (if no results needed)
	 * @return - {@link IResponse} object with data of answer or null. 
	 * Don't send large objects via this object, save such objects before responding and send reference to them
	 */
	public abstract IResponse parse(InputStream stream, IResponse response);
}
