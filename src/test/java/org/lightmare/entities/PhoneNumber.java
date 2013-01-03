package org.lightmare.entities;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.lightmare.annotations.UnitName;

@Entity
@Table(name = "PHONE_NUMBERS", schema = "PERSONS")
@UnitName("testUnit")
public class PhoneNumber {

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "ge.gov.mia.lightmare.entities.PhoneNumber")
	@TableGenerator(name = "ge.gov.mia.lightmare.entities.PhoneNumber", table = "ID_GENERATORS", pkColumnName = "TABLE_NAME", pkColumnValue = "PHONE_NUMBERS", valueColumnName = "KEY_VALUE", allocationSize = 20)
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

}
