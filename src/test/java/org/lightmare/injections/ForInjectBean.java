package org.lightmare.injections;

import javax.ejb.Stateless;

@Stateless
public class ForInjectBean implements ForInjectBeanRemote {

    @Override
    public String getForInject() {

	return "Injection is success";
    }
}
