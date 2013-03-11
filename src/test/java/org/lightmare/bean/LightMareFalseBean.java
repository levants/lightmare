package org.lightmare.bean;

import javax.ejb.Stateless;

@Stateless
public class LightMareFalseBean implements LightMareFalseBeanRemote {

    @Override
    public boolean isFalse() {
	return true;
    }

}
