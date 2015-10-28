package org.lightmare.entities;

import java.io.IOException;
import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.lightmare.annotations.UnitName;
import org.lightmare.rest.providers.RestProvider;

@Entity
@Table(name = "PHONE_NUMBERS", schema = "PERSONS")
@UnitName("testUnit")
public class PhoneNumber implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "org.lightmare.entities.PhoneNumber")
    @TableGenerator(name = "org.lightmare.entities.PhoneNumber", table = "ID_GENERATORS", pkColumnName = "TABLE_NAME", pkColumnValue = "PHONE_NUMBERS", valueColumnName = "KEY_VALUE", allocationSize = 20)
    @Column(name = "phone_number_id")
    private Integer phoneNumberId;

    @Column(name = "person_id")
    private Integer personId;

    @Column(name = "phone_number")
    private String phoneNumber;

    public Integer gerPersonId() {
	return personId;
    }

    public Integer getPhoneNumberId() {
	return phoneNumberId;
    }

    public String getPhoneNumber() {
	return phoneNumber;
    }

    public void setPersonId(Integer personId) {
	this.personId = personId;
    }

    public void setPhoneNumberId(Integer phoneNumberId) {
	this.phoneNumberId = phoneNumberId;
    }

    public void setPhoneNumber(String phoneNumber) {
	this.phoneNumber = phoneNumber;
    }

    public static PhoneNumber valueOf(String json) {

	PhoneNumber phone = null;
	try {
	    phone = RestProvider.convert(json, PhoneNumber.class);
	} catch (IOException ex) {
	    ex.printStackTrace();
	}

	return phone;
    }
}
