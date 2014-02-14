package com.cubes.cubesandroidsdk.adsmanager.updater;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.net.Uri;

/**
 * Encapsulates work with cache directory.
 * @author makarenko.s
 *
 */
public class CacheManager {
	
	private Context context;
	private List<String> oldFilesList;
	private List<String> newFilesList;
	
	private final Object lock = new Object();  
	
	private static final int BUFFER_SIZE = 1024;
	
	public CacheManager(Context context) {
		
		this.context = context;
		
		this.newFilesList = new LinkedList<String>();
		this.oldFilesList = new LinkedList<String>();
	}
	
	/**
	 * save file to cache directory.
	 * @param stream
	 * @return {@link Uri} representation of saved file
	 * @throws IOException
	 */
	public String saveToFile(InputStream stream) throws IOException {
		
		final File file = new File(context.getExternalCacheDir().getAbsolutePath(), String.valueOf(System.currentTimeMillis()));
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
		
		final String fileUri = Uri.fromFile(file).toString();
		synchronized (lock) {
			newFilesList.add(file.getName());	
		}
		return fileUri;
	}
	
	/**
	 * Remove previously saved files from cache.
	 * Performs in working thread because cannot lead to ANR
	 */
	public void clearCache() {
		new Thread() {
			
			@Override
			public void run() {
				synchronized (lock) {
					if(!oldFilesList.isEmpty()) {
						for(String fileName: oldFilesList) {
							final File file = new File(context.getExternalCacheDir().getAbsolutePath(), fileName);
							if(file.exists() && file.isFile()) {
								file.delete();
							}
						}
						oldFilesList.clear();
					}
					oldFilesList.addAll(newFilesList);
					newFilesList.clear();
				}
			};
		}.start();
	}

}
