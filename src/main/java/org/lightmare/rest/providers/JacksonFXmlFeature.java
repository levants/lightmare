package org.lightmare.rest.providers;

import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;

import org.lightmare.utils.StringUtils;

import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;

/**
 * Class to register new JSON <a
 * href="https://github.com/FasterXML/jackson-databind">jackson</a> provider
 * instead of old
 * 
 * @author Levan
 * 
 */
public class JacksonFXmlFeature implements Feature {

    private static final String DISABLE_JSON_KEY = "jersey.config.disableMoxyJson.";

    @Override
    public boolean configure(FeatureContext context) {

	String runtimeType = context.getConfiguration().getRuntimeType().name()
		.toLowerCase();
	String disableMoxy = StringUtils.concat(DISABLE_JSON_KEY, runtimeType);
	context.property(disableMoxy, Boolean.TRUE);
	Class<?>[] ios = new Class[] { MessageBodyReader.class,
		MessageBodyWriter.class };
	context.register(JacksonJaxbJsonProvider.class, ios);

	return Boolean.TRUE;
    }
}
