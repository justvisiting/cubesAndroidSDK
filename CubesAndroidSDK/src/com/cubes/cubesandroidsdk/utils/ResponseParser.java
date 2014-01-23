package com.cubes.cubesandroidsdk.utils;
import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;



/*
 * 
<?xml version="1.0" encoding="UTF-8" ?>
<request type="bannerAd">
<img><![CDATA[http://smalladBannerUrl.com]]></img>
<clicktype>inapp</clicktype>
<clickurl><![CDATA[http://localhost:81/cubesad/md.click.php?zone_id=1&h=bb764a1e5fcb2326d9aca4726b029759&type=normal&campaign_id=1&ad_id=1&c=aHR0cDovLy9hZHYyY3JlYXRpdmUxLmNvbQ,,]]></clickurl>
<urltype>link</urltype>
<refresh>30</refresh>
<scale>no</scale>
<skippreflight>no</skippreflight>
</request>
 */
public class ResponseParser extends DefaultHandler {

	private StringBuilder _builder;
	private AdXmlElements _ad;
	public AdXmlElements get_ad() {
		return _ad;
	}


	private Boolean _isInExpandedElemnt;
	private List<String> _expandedUrlList;
	
	@Override
    public void startDocument() throws SAXException {
         
		_ad = new AdXmlElements();
		_isInExpandedElemnt = false;
    }

	@Override
    public void startElement(String uri, String localName, String qName,
            Attributes attributes) throws SAXException {
         
        /****  When New XML Node initiating to parse this function called *****/
         
        // Create StringBuilder object to store xml node value
        _builder=new StringBuilder();
         
        if(localName.equals("expand")){
             
            // Log.i("parse","----Expand----");
        	_isInExpandedElemnt = true;
        	_expandedUrlList = new ArrayList<String>();
        }
    }
     
     
     
     // Finished reading the login tag, add it to arraylist
     // @param uri
     // @param localName
     // @param qName
     // @throws SAXException
      
    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        
         
        if(localName.equals("expand")){
        	_isInExpandedElemnt = false; 
        	_ad.setExpandedUrl(_expandedUrlList);
        }
        else  if(localName.equalsIgnoreCase("img")){
             if (_isInExpandedElemnt)
             {
            	 _expandedUrlList.add(_builder.toString());
             }
             else
             {
            	 _ad.set_bannerUrl(_builder.toString());
             }
        }
        else  if(localName.equalsIgnoreCase("clickType")){  
        	ClickType clickType = ClickType.valueOf(_builder.toString());
        	_ad.set_clickType(clickType);
        }
        else if(localName.equalsIgnoreCase("clickUrl")){

            _ad.set_clickUrl(_builder.toString());
        }
        else if (localName.equalsIgnoreCase("Type")) {
        	AdType adType = AdType.valueOf(_builder.toString());
        	_ad.set_adType(adType);
        }
        // Log.i("parse",localName.toString()+"========="+builder.toString());
    }
 
    
     // Read the value of each xml NODE
     // @param ch
     // @param start
     // @param length
     // @throws SAXException
      
    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException {
             
        /******  Read the characters and append them to the buffer  ******/
        String tempString=new String(ch, start, length);
        _builder.append(tempString);
    }

}

