package org.lightmare.deploy;

import java.io.IOException;
import java.util.Map;

import javax.ejb.embeddable.EJBContainer;
import javax.naming.Context;

import org.apache.log4j.Logger;
import org.lightmare.jndi.NamingUtils;
import org.lightmare.utils.ObjectUtils;

/**
 * Implementation for {@link javax.ejb.embeddable.EJBContainer} class for
 * lightmare ejb container
 * 
 * @author levan
 * 
 */
public class EJBConteainerImpl extends EJBContainer {

    private MetaCreator creator;

    private static final Logger LOG = Logger.getLogger(EJBConteainerImpl.class);

    public static EJBContainer createEjbContainer() {

	EJBConteainerImpl container = new EJBConteainerImpl();

	try {
	    container.creator = new MetaCreator.Builder().build();
	    container.creator.scanForBeans();

	} catch (IOException ex) {
	    LOG.error("Could not initialize EJBContainer", ex);
	}

	return container;

    }

    public static EJBContainer createEjbContainer(Map<?, ?> properties) {

	EJBConteainerImpl container = new EJBConteainerImpl();

	try {

	    MetaCreator.Builder builder = new MetaCreator.Builder();
	    for (Map.Entry<?, ?> entry : properties.entrySet()) {
		builder.setProperty((String) entry.getKey(),
			(String) entry.getValue());
	    }
	    container.creator = builder.build();
	    container.creator.scanForBeans();

	} catch (IOException ex) {
	    LOG.error("Could not initialize EJBContainer", ex);
	}

	return container;

    }

    @Override
    public Context getContext() {

	Context context = null;
	try {
	    NamingUtils utils = new NamingUtils();
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
