package org.lightmare.criteria.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

@Entity
@Table(name = "PHONES")
public class Phone implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "org.lightmare.linq.entities.Person")
    @TableGenerator(name = "org.lightmare.linq.entities.Person", table = "ID_GENERATORS", pkColumnName = "TABLE_NAME", pkColumnValue = "PERSONS", valueColumnName = "KEY_VALUE", allocationSize = 20)
    @Column(name = "PHONE_ID")
    private Long phoneId;

    @Column(name = "PERATOR_ID")
    private Long operatorId;

    @Column(name = "PHONE_NUMBER")
    private String phoneNumber;

    public Long getPhoneId() {
	return phoneId;
    }

    public void setPhoneId(Long phoneId) {
	this.phoneId = phoneId;
    }

    public Long getOperatorId() {
	return operatorId;
    }

    public void setOperatorId(Long operatorId) {
	this.operatorId = operatorId;
    }

    public String getPhoneNumber() {
	return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
	this.phoneNumber = phoneNumber;
    }
}
