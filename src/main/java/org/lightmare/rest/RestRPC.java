package org.lightmare.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

@Path("")
@Produces("application/json;charset=utf-8")
@Consumes("application/json;charset=utf-8")
public class RestRPC {

	public String call(@QueryParam("param") String param) {

		String response;
		return null;
	}
}
