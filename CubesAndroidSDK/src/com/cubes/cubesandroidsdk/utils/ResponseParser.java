package com.cubes.cubesandroidsdk.utils;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.cubes.cubesandroidsdk.config.AdsType;
import com.cubes.cubesandroidsdk.config.ClickAdsAction;

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

	private StringBuilder builder;
	private AdXmlElements xml;

	public AdXmlElements get_ad() {
		return xml;
	}

	private Boolean _isInExpandedElemnt;
	private List<String> _expandedUrlList;

	@Override
	public void startDocument() throws SAXException {

		xml = new AdXmlElements();
		_isInExpandedElemnt = false;
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {

		/**** When New XML Node initiating to parse this function called *****/

		// Create StringBuilder object to store xml node value
		builder = new StringBuilder();

		if (localName.equals("expand")) {

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

		if (localName.equals("expand")) {
			
			_isInExpandedElemnt = false;
			xml.setExpandedUrl(_expandedUrlList);
			
		} else if (localName.equalsIgnoreCase("img")) {
			
			if (_isInExpandedElemnt) {
				_expandedUrlList.add(builder.toString());
			} else {
				xml.set_bannerUrl(builder.toString());
			}
			
		} else if (localName.equalsIgnoreCase("clickType")) {
			
//			ClickType clickType = ClickType.valueOf(builder.toString());
			String clickType = builder.toString();
			if(clickType.equalsIgnoreCase("Browse")) {
				xml.set_clickType(ClickAdsAction.BROWSER_ACTION);
			} else if(clickType.equalsIgnoreCase("PlayStore")) {
				xml.set_clickType(ClickAdsAction.PLAY_STORE_ACTION);
			} else if(clickType.equalsIgnoreCase("Call")) {
				xml.set_clickType(ClickAdsAction.INITIATE_PHONE_CALL_ACTION);
			}
			
		} else if (localName.equalsIgnoreCase("clickUrl")) {

			xml.set_clickUrl(builder.toString());
		} else if (localName.equalsIgnoreCase("Type")) {
			// AdType adType = AdType.valueOf(_builder.toString());

			if (builder.toString().equalsIgnoreCase("BannerAd")) {
				xml.set_adType(AdsType.MULTIPART);
			} else if(builder.toString().equalsIgnoreCase("MultiPartLogoAd")) {
				xml.set_adType(AdsType.MULTIPART_LOGO);
			} else if(builder.toString().equalsIgnoreCase("LogoAd")) {
				xml.set_adType(AdsType.LOGO_AD);
			} else if(builder.toString().equalsIgnoreCase("InterstitialAd")) {
				xml.set_adType(AdsType.INTERSTITIAL);
			}

		}
	}

	// Read the value of each xml NODE
	// @param ch
	// @param start
	// @param length
	// @throws SAXException

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {

		/****** Read the characters and append them to the buffer ******/
		String tempString = new String(ch, start, length);
		builder.append(tempString);
	}

}
