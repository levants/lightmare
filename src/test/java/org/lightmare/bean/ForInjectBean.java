package org.lightmare.bean;

import javax.ejb.Stateless;

@Stateless
public class ForInjectBean implements ForInjectBeanRemote {

    @Override
    public String getForInject() {

	return "Injection is success";
    }
}
