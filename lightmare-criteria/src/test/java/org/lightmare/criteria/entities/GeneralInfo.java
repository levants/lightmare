package org.lightmare.criteria.entities;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class GeneralInfo {

    @Column(name = "FULL_NAME")
    private String fullName;

    @Column(name = "ADDRESS")
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
