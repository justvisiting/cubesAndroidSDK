package com.cubes.cubesandroidsdk.networkloader.loader.responses;

import com.cubes.cubesandroidsdk.networkloader.loader.ResultCode;

/**
 * Interface of callback that will be used for delivery result 
 * of request execution. 
 * @author makarenko.s
 *
 */
public interface IClientCallback {

	/**
	 * 
	 * @param resultCode - {@link ResultCode}
	 * @param response - {@link IResponse} object with results of request execution or null
	 */
	void onRequestFinished(int resultCode, IResponse response);
}
