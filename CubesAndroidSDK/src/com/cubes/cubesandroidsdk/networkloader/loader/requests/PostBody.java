package com.cubes.cubesandroidsdk.networkloader.loader.requests;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.util.Map;
import java.util.Map.Entry;

import android.text.TextUtils;
import android.webkit.MimeTypeMap;

/**
 * Represents body of POST request.
 * 
 * @author makarenko.s
 * 
 */
public class PostBody {

	private final Map<String, File> contentItems;
	private final String delimiter;
	private static final String CRLF = "\r\n";
	private static final String TWO_HYPHENS = "--";
	protected static final int BUFFER_SIZE = 1024;

	public PostBody(Map<String, File> contentItems, String delimiter) {
		this.contentItems = contentItems;
		this.delimiter = delimiter;
	}

	/**
	 * 
	 * @param stream
	 *            - {@link OutputStream} that is provided by network connection.
	 *            You must write into this stream your data. You shouldn't close
	 *            stream because it does by parent class.
	 * 
	 */
	public void writeInto(OutputStream stream) throws IOException {
		if (contentItems.isEmpty()) {

			return;
		} else {

			final DataOutputStream outStream = new DataOutputStream(stream);
			FileInputStream fStream = null;
			final byte[] buffer = new byte[BUFFER_SIZE];
			int length = -1;
			outStream.writeBytes(getPartDelimiter());
			for (Entry<String, File> entry : contentItems.entrySet()) {

				final File file = entry.getValue();
				if (file != null && file.exists() && file.isFile()) {
					outStream.writeBytes(getContentDisposition(entry.getKey(),
							file.getName()));
					outStream.writeBytes(getMimeType(file.toURI().toURL()
							.toString()));
					outStream.writeBytes(CRLF);
					fStream = new FileInputStream(file);
					while ((length = fStream.read(buffer)) != -1) {
						outStream.write(buffer, 0, length);
					}
					outStream.writeBytes(CRLF);
					outStream.writeBytes(getPartDelimiter());

					fStream.close();
				}

			}
			outStream.writeBytes(getTrailer());
			outStream.flush();
			outStream.close();
		}
	}

	/**
	 * 
	 * @return true if POST body contain multiple parts, false otherwise
	 */
	public boolean isMultipart() {
		return true;
	}

	private String getTrailer() {
		return new StringBuilder().append(TWO_HYPHENS).append(CRLF)
				.append(CRLF).toString();
	}

	private String getPartDelimiter() {

		return new StringBuilder().append(TWO_HYPHENS).append(delimiter)
				.append(CRLF).toString();
	}

	private String getContentDisposition(String name, String fileName) {
		return new StringBuilder()
				.append("Content-Disposition:form-data;name=\"").append(name)
				.append("\"; filename=\"").append(fileName).append("\"")
				.append(CRLF).toString();
	}

	private String getMimeType(String url) throws MalformedURLException {
		final StringBuilder sb = new StringBuilder().append("Content-Type:");
		final MimeTypeMap mimeTypeResolver = MimeTypeMap.getSingleton();
		final String extension = MimeTypeMap.getFileExtensionFromUrl(url);
		if (!TextUtils.isEmpty(extension)
				&& mimeTypeResolver.hasExtension(extension)) {
			sb.append(mimeTypeResolver.getMimeTypeFromExtension(extension));
		} else {
			sb.append("application/x-www-form-urlencoded");
		}
		sb.append(CRLF);
		return sb.toString();
	}

}
