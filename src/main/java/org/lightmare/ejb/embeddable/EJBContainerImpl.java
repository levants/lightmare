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
 * Implementation for {@link javax.ejb.embeddable.EJBContainer} class for
 * lightmare ejb container
 * 
 * @author levan
 * 
 */
public class EJBContainerImpl extends EJBContainer {

    private MetaCreator creator;

    private static final Logger LOG = Logger.getLogger(EJBContainerImpl.class);

    protected EJBContainerImpl() {

	try {
	    this.creator = new MetaCreator.Builder().build();
	    this.creator.scanForBeans();

	} catch (IOException ex) {
	    LOG.error("Could not initialize EJBContainer", ex);
	}
    }

    protected EJBContainerImpl(Map<?, ?> properties) {

	try {

	    MetaCreator.Builder builder = new MetaCreator.Builder();
	    if (ObjectUtils.available(properties)) {
		for (Map.Entry<?, ?> entry : properties.entrySet()) {
		    builder.setProperty((String) entry.getKey(),
			    (String) entry.getValue());
		}
	    }
	    this.creator = builder.build();
	    this.creator.scanForBeans();

	} catch (IOException ex) {
	    LOG.error("Could not initialize EJBContainer", ex);
	}
    }

    @Override
    public Context getContext() {

	Context context = null;
	try {
	    JndiManager utils = new JndiManager();
	    context = utils.getContext();
	} catch (IOException ex) {
	    LOG.error("Could not initialize Context", ex);
	}
	return context;
    }

    @Override
    public void close() {

	if (ObjectUtils.notNull(creator)) {
	    creator.clear();
	}
	MetaCreator.close();
    }
}
