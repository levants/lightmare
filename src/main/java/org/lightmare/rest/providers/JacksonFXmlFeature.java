package org.lightmare.rest.providers;

import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;

import org.lightmare.utils.StringUtils;

import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;

/**
 * Class to register new JSON <a
 * href="https://github.com/FasterXML/jackson-databind">Jackson</a> provider
 * instead of old for REST services
 * 
 * @author Levan Tsinadze
 * @since 0.0.52-SNAPSHOT
 */
public class JacksonFXmlFeature implements Feature {

    // Key prefix to disable MOXy JSON library
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
