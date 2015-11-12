/*
 * Lightmare, Lightweight embedded EJB container (works for stateless session beans) with JPA / Hibernate support
 *
 * Copyright (c) 2013, Levan Tsinadze, or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
package org.lightmare.rest.providers;

import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;

import org.lightmare.utils.StringUtils;

import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;

/**
 * Class to register new JSON
 * <a href="https://github.com/FasterXML/jackson-databind">Jackson</a> provider
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

        boolean valid = Boolean.TRUE;

        String runtimeType = context.getConfiguration().getRuntimeType().name().toLowerCase();
        String disableMoxy = StringUtils.concat(DISABLE_JSON_KEY, runtimeType);
        context.property(disableMoxy, valid);
        Class<?>[] ios = new Class[] { MessageBodyReader.class, MessageBodyWriter.class };
        context.register(JacksonJaxbJsonProvider.class, ios);

        return valid;
    }
}
