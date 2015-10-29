package org.lightmare.linq.entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "PERSONS")
public class Person implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "org.lightmare.linq.entities.Person")
    @TableGenerator(name = "org.lightmare.linq.entities.Person", table = "ID_GENERATORS", pkColumnName = "TABLE_NAME", pkColumnValue = "PERSONS", valueColumnName = "KEY_VALUE", allocationSize = 20)

    @Column(name = "PERSON_ID")
    private Long personId;

    @Column(name = "PERSONAL_NO")
    private String personalNo;

    @Column(name = "LAST_NAME")
    private String lastName;

    @Column(name = "FIRST_NAME")
    private String firstName;

    @Column(name = "BIRTH_DATE")
    @Temporal(TemporalType.DATE)
    private Date birthDate;

    @Column(name = "MIDD_NAME")
    private String middName;

    public Long getPersonId() {
	return personId;
    }

    public void setPersonId(Long personId) {
	this.personId = personId;
    }

    public String getPersonalNo() {
	return personalNo;
    }

    public void setPersonalNo(String personalNo) {
	this.personalNo = personalNo;
    }

    public String getLastName() {
	return lastName;
    }

    public void setLastName(String lastName) {
	this.lastName = lastName;
    }

    public String getFirstName() {
	return firstName;
    }

    public void setFirstName(String firstName) {
	this.firstName = firstName;
    }

    public Date getBirthDate() {
	return birthDate;
    }

    public void setBirthDate(Date birthDate) {
	this.birthDate = birthDate;
    }

    public String getMiddName() {
	return middName;
    }

    public void setMiddName(String middName) {
	this.middName = middName;
    }

    @Override
    public String toString() {
	return String.format("%s %s %s %s %s %s", personId, personalNo, lastName, firstName, birthDate, middName);
    }
}
