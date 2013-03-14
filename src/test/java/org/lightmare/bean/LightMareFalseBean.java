package org.lightmare.bean;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

@Stateless
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class LightMareFalseBean implements LightMareFalseBeanRemote {

    @Override
    public boolean isFalse() {
	return true;
    }

}
