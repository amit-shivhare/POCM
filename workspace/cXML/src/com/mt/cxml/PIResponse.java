package com.mt.cxml;

public class PIResponse {
	
	private String Content = "";
	private int Code = 0;
	
	public PIResponse (String content, int code) {
		Content = content;
		Code = code;
	}
	
	public int getCode() {
		return Code;
	}
	
	public String getContent() {
		return Content;
	}
	
	public String getCodeString() {
		return Integer.toString(Code);
	}
}
