package com.cubes.cubesandroidsdk.networkloader.loader.asyncexecution;

import android.os.Handler;

import com.cubes.cubesandroidsdk.networkloader.loader.ResultCode;
import com.cubes.cubesandroidsdk.networkloader.loader.loaders.INetworkLoader;

public class NetworkLoaderTask extends AbstractAsyncTask {

	private INetworkLoader loader;

	public NetworkLoaderTask(INetworkLoader loader, Handler handler) {
		super(handler);
		if (loader == null) {

			throw new IllegalArgumentException("Parameters cannot be null");
		}
		this.loader = loader;
	}

	@Override
	protected void execute() {

		try {
			sendReport(ResultCode.RESULT_SUCCESSFULL, loader.execute());
		} catch (Exception ex) {
			sendReport(ResultCode.RESULT_FAILED, null);
		}
	}

}
