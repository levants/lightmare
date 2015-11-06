package org.lightmare.criteria.entities;

import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class GeneralInfo {

    private String fullName;

    private String addrress;

    public String getFullName() {
	return fullName;
    }

    public void setFullName(String fullName) {
	this.fullName = fullName;
    }

    public String getAddrress() {
	return addrress;
    }

    public void setAddrress(String addrress) {
	this.addrress = addrress;
    }
}
