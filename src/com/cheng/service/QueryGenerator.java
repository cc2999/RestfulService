package com.cheng.service;

import java.util.HashSet;
import java.util.Set;

import com.sun.enterprise.util.StringUtils;

/*
 * This class is used to generate query set based on target query
 */
public class QueryGenerator {
	
	public static final String TARGET = "?";
	private String originalQuery;
	
	public QueryGenerator(String query) {
		this.originalQuery = query;
	}
	
	public Set<String> generateQuerySet() {
		Set<String> querySet = new HashSet<String>();
		for(QueryPattern pattern : QueryPattern.values()){
			String pString = StringUtils.replace(pattern.getValue(), QueryGenerator.TARGET, originalQuery);
			querySet.add(pString);
		}
		return querySet;
	}

	public static void main(String[] args){
		QueryGenerator qg = new QueryGenerator("cc");
		for(String s : qg.generateQuerySet()){
			System.out.println(s);
		}
	}
}

enum QueryPattern{
	PATTERN1("? vs"),
	PATTERN2("vs ?"),
	PATTERN3("? or"),
	PATTERN4("or ?"),
	PATTERN5("such as ? and"),
	PATTERN6("especially ? and"),
	PATTERN7("including ? and");
	
	private final String value;
	private QueryPattern(String value){
		this.value = value;
	}
	public String getValue(){
		return this.value;
	}
}
