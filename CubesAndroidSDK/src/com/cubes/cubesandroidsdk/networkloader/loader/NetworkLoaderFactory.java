package com.cubes.cubesandroidsdk.networkloader.loader;

import java.util.NoSuchElementException;

import com.cubes.cubesandroidsdk.networkloader.loader.loaders.HttpLoader;
import com.cubes.cubesandroidsdk.networkloader.loader.loaders.HttpsLoader;
import com.cubes.cubesandroidsdk.networkloader.loader.loaders.INetworkLoader;
import com.cubes.cubesandroidsdk.networkloader.loader.requests.IRequest;
import com.cubes.cubesandroidsdk.networkloader.loader.responses.AbstractParser;

public class NetworkLoaderFactory {
	
	/**
	 * Create new {@link INetworkLoader} based on the given request
	 * @param request - {@link IRequest} object
	 * @param parser - implementation of {@link AbstractParser} for parsing response
	 * @return - needed implementation of {@link INetworkLoader}
	 */
	public static INetworkLoader createNetworkLoader(IRequest<?> request, AbstractParser parser) {
		
		switch(request.getProtocol()) {
		case Protocol.HTTP:
			return new HttpLoader().setRequest(request).setParser(parser);
		case Protocol.HTTPS:
			return new HttpsLoader().setRequest(request).setParser(parser);
		case Protocol.SOCKET_TCP:
			throw new NoSuchElementException("Loader not implemented yet");
		default: return null;
		}
	}
}
