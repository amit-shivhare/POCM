package com.mt.cxml;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;

public class BuyerProfileData {
	
	public String ID = null;
	public String Name = null;
	public String Identity = null;
	public String SharedSecret = null;
	public String CustomResponse = null;
	public XPathExpression XIdentity = null;
	public XPathExpression XSharedSecret = null;
    
    BuyerProfileData (
			String ID_,
			String Name_,
			String Identity_,
			String SharedSecret_,
			String CustomResponse_
	) {
    
    	ID = ID_;
    	Name = Name_;
    	Identity = Identity_;
    	SharedSecret = SharedSecret_;
    	CustomResponse = CustomResponse_;

		XPath xPath =  XPathFactory.newInstance().newXPath();
		XPathExpression XIdentity_ = null;
		try {
			XIdentity_ = xPath.compile("boolean(" + Identity + ")");
			XIdentity = XIdentity_;
		}
		catch(XPathExpressionException e) {
			Identity = "ERROR";
		}
		
		XPathExpression XSharedSecret_ = null;
		try {
			XSharedSecret_ = xPath.compile("boolean(" + SharedSecret + ")");
			XSharedSecret = XSharedSecret_;
		}
		catch(XPathExpressionException e) {
			SharedSecret = "ERROR";
		}

    }
    
    public XPathExpression getXIdentity() {
    	return XIdentity;
    }
    
    public boolean isIdentityMatching(Document docxml) throws XPathExpressionException {
		String checkResult = XIdentity.evaluate(docxml);
		if(checkResult.equals("true")) return true;
		else return false;
    }
    
    public boolean isSharedSecretOK(Document docxml) throws XPathExpressionException {
		String checkResult = XSharedSecret.evaluate(docxml);
		if(checkResult.equals("true")) return true;
		else return false;
    }
    
}
