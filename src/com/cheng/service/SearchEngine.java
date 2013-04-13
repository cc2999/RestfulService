package com.cheng.service;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;

public class SearchEngine {
	
	//using google custom search rest API
	private static final String GOOGLE_SEARCH_REQUEST_HEADER = "https://www.googleapis.com/customsearch/v1?";
	private static final String API_KEY = "key=AIzaSyDzfM8KNxPMOAlweM7D8NlSOWqgQCmYOLk";
	private static final String CSE = "cx=002884194429174078789:mhlvrbug6ai";
	private static final String RETURN_FORMAT = "alt=json";
	private static final String SLASH = "/";
	private static final String AND = "&";
	
	private static final String exmpleURL = "https://www.googleapis.com/customsearch/v1"
		+ "?key=AIzaSyDzfM8KNxPMOAlweM7D8NlSOWqgQCmYOLk"
		+ "&cx=000455696194071821846:reviews"
		+ "&q=flowers"
		+ "&alt=atom";
	
	public String doSimpleSearch(String searchKey) throws Exception{
		String restRequest = this.constructSimpleSearchRequest(searchKey);
		return getSearchResults(restRequest);
	}
	
	public String getSearchResults(String searchRequest) throws Exception{
		HttpClient client = new HttpClient();
		GetMethod method = new GetMethod(searchRequest);
		//send GET http request
		int statusCode = client.executeMethod(method);
		
		if (statusCode != HttpStatus.SC_OK) {
        	System.err.println("Method failed: " + method.getStatusLine());
        }
		//get http reponse
		
		return null;
	}
	
	/*
	 * Construct a simple search request only using the search key word
	 * @param	searchKey 	the string search key 
	 * @return 				the request kicking off Google search 
	 */
	private String constructSimpleSearchRequest(String searchKey) {
		StringBuffer sBuffer = new StringBuffer();
		sBuffer.append(getRequestRequiredPrefix());
		sBuffer.append("&q=" + searchKey);
		sBuffer.append(AND + RETURN_FORMAT);
		return sBuffer.toString();
	}
	
	/*
	 * Construct a simple search request only using the search key word
	 * @param	searchParams 	the params contain search key and extra conditions
	 * @return 					the request kicking off Google search 
	 */
	private String constructComplexSearchRequest(Map<String, String> searchParams) {
		//TO DO
		return null;
	}
	
	private String getRequestRequiredPrefix() {
		return GOOGLE_SEARCH_REQUEST_HEADER
			 + API_KEY
			 + AND
			 + CSE;
	}
	
	public static void main(String[] args) {
		System.out.println(new SearchEngine().getRequestRequiredPrefix());
		System.out.println(new SearchEngine().constructSimpleSearchRequest("GOOGLE"));
	}
}
