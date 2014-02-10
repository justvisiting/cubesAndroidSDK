package com.cubes.cubesandroidsdk.adsmanager.updater;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import android.net.Uri;

import com.cubes.cubesandroidsdk.adsmanager.AdsInstance;
import com.cubes.cubesandroidsdk.networkloader.loader.responses.AbstractParser;
import com.cubes.cubesandroidsdk.networkloader.loader.responses.IResponse;

/**
 * Load from stream image, save it into cache directory and put url string into {@link IResponse}
 * @author makarenko.s
 *
 */
public class AdsImageParser extends AbstractParser {

	private static final int BUFFER_SIZE = 1024;
	private String cachePath;
	
	public AdsImageParser(String cachePath) {
		this.cachePath = cachePath;
	}

	@Override
	public IResponse parse(InputStream stream, IResponse response) {

		try {
			final File file = new File(cachePath, String.valueOf(System.currentTimeMillis()));
			if(!file.exists()) {
				file.createNewFile();
			}
			final OutputStream output = new FileOutputStream(file);
			final byte[] buffer = new byte[BUFFER_SIZE];
			int bytesRead = 0;
			while ((bytesRead = stream.read(buffer, 0, buffer.length)) >= 0) {
				output.write(buffer, 0, bytesRead);
			}
			output.flush();
			output.close();
			final AdsInstance adsInstance = (AdsInstance) response.getData();
			adsInstance.setBarUriString(Uri.fromFile(file).toString());
			response.setData(adsInstance);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

}
