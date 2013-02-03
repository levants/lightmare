package org.lightmare.rest;

import java.io.IOException;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.apache.log4j.Logger;
import org.lightmare.utils.RpcUtils;

@Path("")
@Produces("application/json;charset=utf-8")
@Consumes("application/json;charset=utf-8")
public class RestRPC {

	private static Logger LOG = Logger.getLogger(RestRPC.class);

	@GET
	public String call(@QueryParam("param") String param) {

		String response = "not yet implemented";
		try {
			response = RpcUtils.write(response);
		} catch (IOException ex) {
			LOG.error("Could not serialize response", ex);
			response = String.format("{error:'%s'}", ex.getMessage());
		}

		return response;
	}

	@GET
	public String publish() {

		RestPublisher publisher = new RestPublisher();
		String beansDescriptor = publisher.publishAll();

		return beansDescriptor;
	}
}
