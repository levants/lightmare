package org.lightmare.ejb.embeddable;

import java.util.Map;

import javax.ejb.EJBException;
import javax.ejb.embeddable.EJBContainer;
import javax.ejb.spi.EJBContainerProvider;

/**
 * Implementation for {@link javax.ejb.spi.EJBContainerProvider} interface for
 * lightmare EJB container
 * 
 * @author levan
 * 
 */
public class EJBContainerProviderImpl implements EJBContainerProvider {

    @Override
    public EJBContainer createEJBContainer(Map<?, ?> properties)
	    throws EJBException {

	return new EJBContainerImpl(properties);
    }
}
