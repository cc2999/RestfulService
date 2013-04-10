package com.cheng.rest;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/v1/message")
public class WelcomeMessage {
	
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String getMessage(){
		return "Hello World!";
	}
	
	public void foo(){}
}
