package com.cubes.cubesandroidsdk.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.os.AsyncTask;

class RequestTask extends AsyncTask<String, String, String>{
	 String responseString = null;
	 ResponseParser parser;
	
    @Override
    protected String doInBackground(String... uri) {
        HttpClient httpclient = new DefaultHttpClient();
        HttpResponse response;
        try {
            response = httpclient.execute(new HttpGet(uri[0]));
            StatusLine statusLine = response.getStatusLine();
            if(statusLine.getStatusCode() == HttpStatus.SC_OK){
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                response.getEntity().writeTo(out);
                out.close();
                responseString = out.toString();
              
            } else{
                //Closes the connection.
                response.getEntity().getContent().close();
                throw new IOException(statusLine.getReasonPhrase());
            }
        } catch (ClientProtocolException e) {
            //TODO Handle problems..
        } catch (IOException e) {
            //TODO Handle problems..
        }
        return responseString;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        //Do anything with response..
        
    }
    
    
    public AdXmlElements getElements() {
    	try {
    		parser = new ResponseParser();
	    	SAXParserFactory factory=SAXParserFactory.newInstance();
	        SAXParser sp=factory.newSAXParser();
	        XMLReader reader=sp.getXMLReader();
	        reader.setContentHandler(parser);
	        
	      //Create an input source from the XML input stream
	        InputStream in = new ByteArrayInputStream(responseString.getBytes("UTF-8"));;
			InputSource source = new InputSource(in);
	
	        reader.parse(source);
	        return parser.get_ad();
    	}
        catch(Exception e){
        
        }
    	
    	return null;
    }
    
    
}
