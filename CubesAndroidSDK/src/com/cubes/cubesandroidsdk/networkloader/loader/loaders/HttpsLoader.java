package com.cubes.cubesandroidsdk.networkloader.loader.loaders;

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import android.util.Log;

import com.cubes.cubesandroidsdk.networkloader.loader.requests.IRequest;
import com.cubes.cubesandroidsdk.networkloader.loader.responses.AbstractParser;
import com.cubes.cubesandroidsdk.networkloader.loader.responses.IResponse;

public class HttpsLoader implements INetworkLoader {

	private AbstractParser parser;
	private IRequest<?> request;

	public HttpsLoader() {

	}

	@Override
	public INetworkLoader setRequest(IRequest<?> request) {
		// TODO Auto-generated method stub
		return this;
	}

	@Override
	public INetworkLoader setParser(AbstractParser parser) {

		this.parser = parser;
		return this;
	}

	@Override
	public IResponse execute() {

		try {
			KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
			String algorithm = TrustManagerFactory.getDefaultAlgorithm();
			TrustManagerFactory tmf = TrustManagerFactory
					.getInstance(algorithm); 
			tmf.init(keyStore);

			SSLContext sslContext = SSLContext.getInstance("SSL");
			sslContext.init(null, new TrustManager[] { new TrustManager() },
					new SecureRandom());
			URL url = new URL("https://192.168.0.108/ssl.php");
			HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
			conn.setDoOutput(true);
			DataOutputStream out = new DataOutputStream(conn.getOutputStream());
			out.writeBytes("test test test");
			out.flush();
			out.close();
			conn.setHostnameVerifier(new LoaderHostNameVerifier());
			conn.setSSLSocketFactory(sslContext.getSocketFactory());
//			if (parser != null) {
//				InputStream in = conn.getInputStream();
//				if (in != null) {
//					parser.parse(in, null);
//					in.close();
//				}
//			}
			Certificate[] serts  = conn.getServerCertificates();
//			BufferedReader dis = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//			String myString;
//				while ((myString = dis.readLine()) != null) {
//					Log.v("NET", myString);
////					response.setData(myString);
//				}
//				dis.close();
			Log.v("NET", "Parsed");
			conn.disconnect();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	public void connectKeystore(String path, String algorithm) throws Exception {
		TrustManagerFactory tmf = TrustManagerFactory.getInstance(algorithm);
		KeyStore keyStore = KeyStore.getInstance("BKS");
		InputStream in = new FileInputStream(path);

		keyStore.load(in, "mysecret".toCharArray());
		in.close();
		tmf.init(keyStore);
		SSLContext sslc = SSLContext.getInstance("TLS");
		sslc.init(null, tmf.getTrustManagers(), new SecureRandom());
	}

	private class LoaderHostNameVerifier implements HostnameVerifier {

		@Override
		public boolean verify(String hostname, SSLSession session) {

			Log.v("NET", "verify");
			return true;
		}

	}

	private class TrustManager implements X509TrustManager {

		@Override
		public void checkClientTrusted(X509Certificate[] chain, String authType)
				throws CertificateException {
			// TODO Auto-generated method stub
			Log.v("NET", "check client trusted");

		}

		@Override
		public void checkServerTrusted(X509Certificate[] chain, String authType)
				throws CertificateException {
			// TODO Auto-generated method stub
			Log.v("NET", "check server trusted");

		}

		@Override
		public X509Certificate[] getAcceptedIssuers() {
			// TODO Auto-generated method stub
			Log.v("NET", "get accepted issuers");
			return null;
		}

	}
}
