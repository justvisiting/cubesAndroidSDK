package com.cubes.cubesandroidsdk.networkloader.loader.loaders;

import com.cubes.cubesandroidsdk.networkloader.loader.requests.IRequest;
import com.cubes.cubesandroidsdk.networkloader.loader.responses.AbstractParser;
import com.cubes.cubesandroidsdk.networkloader.loader.responses.IResponse;

public interface INetworkLoader {

	INetworkLoader setRequest(IRequest<?> request);
	INetworkLoader setParser(AbstractParser parser);
	IResponse execute() throws Exception;
}
