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
package org.lightmare.ejb.embeddable;

import java.io.IOException;
import java.util.Map;

import javax.ejb.embeddable.EJBContainer;
import javax.naming.Context;

import org.apache.log4j.Logger;
import org.lightmare.deploy.MetaCreator;
import org.lightmare.jndi.JndiManager;
import org.lightmare.utils.ObjectUtils;

/**
 * Extension of {@link javax.ejb.embeddable.EJBContainer} class for embedded EJB
 * container (runs only for java 7 and upper)
 *
 * @author Levan Tsinadze
 * @since 0.0.48
 */
public class EmbeddableContainer extends EJBContainer {

    // Initializes EJB container
    private MetaCreator creator;

    // Error messages
    private static final String CONTAINER_ERROR = "Could not initialize EJBContainer";

    private static final String CONTEXT_ERROR = "Could not initialize Context";

    private static final Logger LOG = Logger
	    .getLogger(EmbeddableContainer.class);

    /**
     * Initializes {@link MetaCreator.Builder} for passed properties
     *
     * @param properties
     * @return
     * @throws IOException
     */
    private MetaCreator.Builder initBuilder(Map<?, ?> properties)
	    throws IOException {

	MetaCreator.Builder builder;

	if (properties == null || properties.isEmpty()) {
	    builder = new MetaCreator.Builder();
	} else {
	    Map<Object, Object> configuration = ObjectUtils.cast(properties);
	    builder = new MetaCreator.Builder(configuration);
	}

	return builder;
    }

    /**
     * Constructor with specified configuration properties
     *
     * @param properties
     */
    protected EmbeddableContainer(Map<?, ?> properties) {

	try {
	    MetaCreator.Builder builder = initBuilder(properties);
	    this.creator = builder.build();
	    this.creator.scanForBeans();
	} catch (IOException ex) {
	    LOG.error(CONTAINER_ERROR, ex);
	}
    }

    /**
     * Default constructor without specified properties
     */
    protected EmbeddableContainer() {
	this(null);
    }

    @Override
    public Context getContext() {

	Context context;

	try {
	    context = JndiManager.getContext();
	} catch (IOException ex) {
	    context = null;
	    LOG.error(CONTEXT_ERROR, ex);
	}

	return context;
    }

    /**
     * Empties deployment cache
     */
    private void clearData() {

	if (ObjectUtils.notNull(creator)) {
	    creator.clear();
	}
    }

    @Override
    public void close() {

	try {
	    clearData();
	    MetaCreator.close();
	} catch (IOException ex) {
	    LOG.fatal(ex.getMessage(), ex);
	}
    }
}
