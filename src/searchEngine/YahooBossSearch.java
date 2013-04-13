package searchEngine;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import extractor.Pair;

public class YahooBossSearch {	
	
	private String[] queries;
	private String entity;
	private static int count = 50;
	
	private static final String appid = "zJ7El53V34HnaWkt2PcB1D1qsylFuE91vfvbElW7euY1EBGAoZwr4x8PS6jWupCegT4z";
	
	public static String filePath = "C:\\cheng\\workspace\\CompetitorUI\\source";
		
	public YahooBossSearch(){
		this.entity = "";		
//		count = SearchMain.count;
		
	}
	
	// To Do... Generate quries according to linguistic patterns
	public void generatePatternQuery(){
		queries = new String[]{entity+" vs", "vs "+entity, entity+" or", "or "+entity, 
							   "such as "+entity+" and", 
							   "especially "+entity+" and", 
							   "including "+entity+" and" };
	}
	
	public void setEntity(String entity){
			this.entity = entity;
	}
	
	// Query via Yahoo Boss API and store the title and summary of top-<count> results.
	public void DoSearch() throws Exception {
		boolean isCreated = false;
		generatePatternQuery();
		File dirFile = new  File(filePath + "/" + entity);
        if ( dirFile.exists() )
        	isCreated = true;  
        
        if ( !isCreated ) {
			for (int p=0; p < queries.length; p++) {
				System.out.println("Start pattern " + (p+1) + "\n");
				
				String q = "%22" + queries[p] + "%22";		
				q = q.replace(" ", "%20");
						
				String request = "http://boss.yahooapis.com/ysearch/web/v1/" + q 
								+ "?appid=" + appid 
								+ "&format=xml" 
								+ "&count=" + count;
				
		        HttpClient client = new HttpClient();
		        GetMethod method = new GetMethod(request);
		        
		        // Send GET request
		        int statusCode = client.executeMethod(method);
		        
		        if (statusCode != HttpStatus.SC_OK) {
		        	System.err.println("Method failed: " + method.getStatusLine());
		        }
		        InputStream rstream = null;
		        
		        // Get the response body
		        rstream = method.getResponseBodyAsStream();
		    	
		        // Process response
		        Document response = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(rstream);
		        
		        XPathFactory factory = XPathFactory.newInstance();
		        XPath xPath=factory.newXPath();
		        
		        //Get all search Result nodes
		        NodeList nodes = (NodeList)xPath.evaluate("/ysearchresponse/resultset_web/result", response, XPathConstants.NODESET);
		        int nodeCount = nodes.getLength();
	//	        sr = new SearchResult[nodeCount];
	//			for (int i = 0; i < nodeCount; i++)	sr[i]=new SearchResult();		
		        
		        //iterate over search Result nodes
		        for (int i = 0; i < nodeCount; i++) {
		            //Get each xpath expression as a string
		        	String title = (String)xPath.evaluate("title", nodes.item(i), XPathConstants.STRING);
		        	String summary = (String)xPath.evaluate("abstract", nodes.item(i), XPathConstants.STRING);            
		        	String url = (String)xPath.evaluate("url", nodes.item(i), XPathConstants.STRING);
		            title = title.replaceAll("<b>", ""); 
		        	title = title.replaceAll("</b>", "");
		        	summary = summary.replaceAll("<b>", ""); 
		        	summary = summary.replaceAll("</b>", "");        	

		            if (!isCreated ) {
		            	System.out.print("Parsing page " + i + "...");
		            	readURL(url, queries[p]+"_"+i);
		            	System.out.println("Finish");
		            }
		        }
			}
        }
    }
	
	public void readURL(String inputUrl, String title){
		String request = inputUrl;		
		HttpClient client = new HttpClient();
        GetMethod method = new GetMethod(request);
        int statusCode;
        
        try {
        	statusCode = client.executeMethod(method);
        
        	if (statusCode != HttpStatus.SC_OK) {
        		System.err.println("Method failed: " + method.getStatusLine());
        	}
        InputStream rstream = null;        
        rstream = method.getResponseBodyAsStream();    	
        BufferedReader in = new BufferedReader(new InputStreamReader(rstream));        
 
		String inputLine;			
		File dirFile = new  File(filePath + "/" + entity);
        if ( !(dirFile.exists()) && !(dirFile.isDirectory()))  {
        	dirFile.mkdirs();
        }		
        File file = new File(filePath+"/" + entity + "/" + title + ".html"); 
        FileWriter filewriter = new FileWriter(file, true); 
        while ((inputLine = in.readLine()) != null)
        	filewriter.write(inputLine);
    	in.close(); 
        filewriter.close();
        }
        catch (Exception e) {
        	System.out.println("Exception!");
        }
	}
	
	public static int countHits(String name) throws Exception
	{
		name = name.replace(" ", "%20");
		String request = "http://boss.yahooapis.com/ysearch/web/v1/" + name 
		+ "?appid=" + appid 
		+ "&format=xml" ;
		
		HttpClient client = new HttpClient();
		GetMethod method = new GetMethod(request);
//		 Send GET request
        int statusCode = client.executeMethod(method);
        
        if (statusCode != HttpStatus.SC_OK) {
        	System.err.println("Method failed: " + method.getStatusLine());
        }
        InputStream rstream = null;
        
		rstream = method.getResponseBodyAsStream();
		// Process response
        Document response = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(rstream);
        
        XPathFactory factory = XPathFactory.newInstance();
        XPath xPath=factory.newXPath();
		String num = xPath.evaluate("ysearchresponse/resultset_web/@totalhits", response);
		Integer hits = new Integer(num);
		return hits.intValue();
	}
	
	public static Vector<Pair> getTargetURL(String target, int number) throws Exception{
		Vector<Pair> vec = new Vector<Pair>();
		target = target.replace(" ","%20");
		String request = "http://boss.yahooapis.com/ysearch/web/v1/" + target 
		+ "?appid=" + appid 
		+ "&format=xml" 
		+ "&count=" + count;
		
		HttpClient client = new HttpClient();
		GetMethod method = new GetMethod(request);
		int statusCode = client.executeMethod(method);
		if (statusCode != HttpStatus.SC_OK) {
	    	System.err.println("Method failed: " + method.getStatusLine());
	     }
		InputStream rstream = method.getResponseBodyAsStream();
		Document response = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(rstream);
        
        XPathFactory factory = XPathFactory.newInstance();
        XPath xPath=factory.newXPath();
		NodeList nodes = (NodeList)xPath.evaluate("/ysearchresponse/resultset_web/result", response, XPathConstants.NODESET);
		int nodeCount = nodes.getLength();
		if(nodeCount < number)
		{
			System.err.println("Can not retrieve enough results");
		}
		//iterate over search Result nodes
		for (int i = 0; i < number; i++) {
			//Get each xpath expression as a string
			String url = (String)xPath.evaluate("url", nodes.item(i), XPathConstants.STRING);
			String title = (String)xPath.evaluate("title", nodes.item(i), XPathConstants.STRING);
			title = title.replaceAll("<b>", ""); 
        	title = title.replaceAll("</b>", "");
			title =new String(title.getBytes(),"UTF-8");
			title = "<b>"+title+"</b>";
			Pair urlPair = new Pair(url,title);
			vec.add(urlPair);
		}
		return vec;
	}
}