package org.lightmare.entities;

import java.io.IOException;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.lightmare.annotations.UnitName;
import org.lightmare.rest.utils.RestUtils;

@Entity
@Table(name = "EMAILS", schema = "PERSONS")
@UnitName("testUnit")
public class Email {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "org.lightmare.entities.Email")
    @TableGenerator(name = "org.lightmare.entities.Email", table = "ID_GENERATORS", pkColumnName = "TABLE_NAME", pkColumnValue = "EMAILS", valueColumnName = "KEY_VALUE", allocationSize = 20)
    @Column(name = "email_id")
    private Integer emailId;

    @Column(name = "person_id")
    private Integer personId;

    @Column(name = "email_address")
    private String emailAddress;

    public String getEmailAddress() {
	return emailAddress;
    }

    public Integer getEmailId() {
	return emailId;
    }

    public Integer gerPersonId() {
	return personId;
    }

    public void setEmailAddress(String emailAddress) {
	this.emailAddress = emailAddress;
    }

    public void setEmailId(Integer emailId) {
	this.emailId = emailId;
    }

    public void setPersonId(Integer personId) {
	this.personId = personId;
    }

    public static Email valueOf(String json) {

	Email email = null;
	try {
	    email = RestUtils.convert(json, Email.class);
	} catch (IOException ex) {
	    ex.printStackTrace();
	}

	return email;
    }
}
