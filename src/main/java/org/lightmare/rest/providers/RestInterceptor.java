package org.lightmare.rest.providers;

import java.io.IOException;
import java.io.InputStream;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.ReaderInterceptor;
import javax.ws.rs.ext.ReaderInterceptorContext;

@Provider
public class RestInterceptor implements ReaderInterceptor {

    @Override
    public Object aroundReadFrom(ReaderInterceptorContext context)
	    throws IOException, WebApplicationException {

	InputStream stream = context.getInputStream();
	System.out.println(stream.available());
	context.setInputStream(stream);

	return context.proceed();
    }

}
