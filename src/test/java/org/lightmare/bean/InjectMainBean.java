package org.lightmare.bean;

import javax.ejb.EJB;
import javax.ejb.Stateless;

@Stateless
public class InjectMainBean implements InjectMainBeanRemote {

    @EJB
    private ForInjectBeanRemote inject;

    @Override
    public String getFromInjection() {
	return inject.getForInject();
    }

}
