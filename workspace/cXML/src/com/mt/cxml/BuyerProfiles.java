package com.mt.cxml;

import com.mt.cxml.BuyerProfileData;
import com.mt.data.DataStore;
import java.util.ArrayList;
import org.w3c.dom.Document;
import javax.xml.xpath.XPathExpressionException;

public class BuyerProfiles {
	
	private ArrayList<BuyerProfileData> al = new ArrayList<BuyerProfileData>();
	
	public BuyerProfiles(String host) throws Exception {
		
		DataStore ds_BuyerProfile = new DataStore("BuyerProfile", host);
		DataStore ds_Identity = new DataStore("Identity", host);
		DataStore ds_SharedSecret = new DataStore("SharedSecret", host);
		DataStore ds_CustomResponse = new DataStore("CustomResponse", host);
		
		ArrayList<String> BuyerProfile = ds_BuyerProfile.getRows();
		
		for(int i=0; i<BuyerProfile.size(); i++) {
			String line = BuyerProfile.get(i);
			int separatorIndex = line.indexOf("->");

			String ID = line.substring(0, separatorIndex);
			String Name = line.substring(separatorIndex+2);
			String Identity = ds_Identity.getValueForKey(ID);
			String SharedSecret = ds_SharedSecret.getValueForKey(ID);
			String CustomResponse = ds_CustomResponse.getValueForKey(ID);
			
			if(Identity==null) Identity = "ERROR";
			if(SharedSecret==null) SharedSecret = "ERROR";
			
			al.add(new BuyerProfileData(ID, Name, Identity, SharedSecret, CustomResponse));
		}
	}
	
	public ArrayList<BuyerProfileData> getBuyerProfiles() {
		return al;
	}
	
	public BuyerProfileData getMatchingBuyerProfile(Document docxml) throws XPathExpressionException {
		BuyerProfileData bpd = null;
		for(int i=0; i<al.size(); i++) {
			bpd = al.get(i);
			if(bpd.isIdentityMatching(docxml)) return bpd;
		}
		return null;
	}
	
}
