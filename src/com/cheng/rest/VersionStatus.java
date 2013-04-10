package com.cheng.rest;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/v1/version")
public class VersionStatus {
	
	private static final String version = "1.0.0.1";
	
	@GET
	@Path("/v1")
	@Produces(MediaType.TEXT_HTML)
	public String getVersion(){
		return "<b>The current api version is " + version + "</b>";
	}

}
