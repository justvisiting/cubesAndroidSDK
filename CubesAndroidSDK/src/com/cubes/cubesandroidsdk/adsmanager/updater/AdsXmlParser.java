package com.cubes.cubesandroidsdk.adsmanager.updater;

import java.io.InputStream;
import java.util.List;

import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import com.cubes.cubesandroidsdk.networkloader.loader.responses.AbstractParser;
import com.cubes.cubesandroidsdk.networkloader.loader.responses.IResponse;
import com.cubes.cubesandroidsdk.utils.AdXmlElements;
import com.cubes.cubesandroidsdk.utils.ResponseParser;

public class AdsXmlParser extends AbstractParser {

	@Override
	public IResponse parse(InputStream stream, IResponse response) {
		AdsRequest request = (AdsRequest) response.getData();
		AdXmlElements xml = getElements(stream);
		request.setInstance(Utils.convertXmlToAdsInstance(xml));
		request.setbarUrl(xml.get_bannerUrl());
		List<String> urls = xml.getExpandedUrl();
		if(urls != null	&& !urls.isEmpty()) {
			request.setFullscreenUrls(urls);
		}
		request.setStatus(AdsRequest.STATUS_FINISHED);
		response.setData(request);
		return response;
	}
	
    public AdXmlElements getElements(InputStream input) {
    	try {
    		final ResponseParser parser = new ResponseParser();
    		
	        XMLReader reader=SAXParserFactory.newInstance().newSAXParser().getXMLReader();
	        reader.setContentHandler(parser);
	
	        reader.parse(new InputSource(input));
	        return parser.get_ad();
    	}
        catch(Exception e){
        
        	e.printStackTrace();
        }
    	
    	return null;
    }
}
