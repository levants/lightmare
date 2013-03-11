package org.lightmare.entities;

import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.lightmare.annotations.UnitName;

@Entity
@Table(name = "PERSONS", schema = "PERSONS")
@UnitName("testUnit")
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "org.lightmare.entities.Person")
    @GenericGenerator(name = "org.lightmare.entities.Person", strategy = "org.lightmare.jpa.TableGeneratorIml", parameters = {
	    @Parameter(name = "table_name", value = "ID_GENERATORS"),
	    @Parameter(name = "segment_column_name", value = "TABLE_NAME"),
	    @Parameter(name = "segment_value", value = "PERSONS"),
	    @Parameter(name = "value_column_name", value = "KEY_VALUE")
    // ,@Parameter(name = "increment_size", value = "20")
    })
    // @TableGenerator(name = "ge.gov.mia.lightmare.entities.Person", table =
    // "ID_GENERATORS", pkColumnName = "TABLE_NAME", pkColumnValue = "PERSONS",
    // valueColumnName = "KEY_VALUE", allocationSize = 20)
    @Column(name = "person_id", nullable = true)
    private Integer personId;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "address_line1")
    private String addressLine1;

    @Column(name = "address_line2")
    private String addressLine2;

    @Column(name = "birth_date")
    private Date birthDate;

    @Column(name = "total_salary")
    private Integer totalSalary;

    @Column(name = "gender")
    private String gender;

    @OneToMany(cascade = CascadeType.ALL, targetEntity = Email.class)
    @JoinColumn(name = "PERSON_ID", referencedColumnName = "PERSON_ID")
    private Set<Email> emails;

    @OneToMany(cascade = CascadeType.ALL, targetEntity = PhoneNumber.class)
    @JoinColumn(name = "PERSON_ID", referencedColumnName = "PERSON_ID")
    private Set<PhoneNumber> phoneNumbers;

    public String getAddressLine1() {
	return addressLine1;
    }

    public String getAddressLine2() {
	return addressLine2;
    }

    public Date getBirthDate() {
	return birthDate;
    }

    public Set<Email> getEmails() {
	return emails;
    }

    public String getFirstName() {
	return firstName;
    }

    public String getGender() {
	return gender;
    }

    public String getLastName() {
	return lastName;
    }

    public Integer getPersonId() {
	return personId;
    }

    public Set<PhoneNumber> getPhoneNumbers() {
	return phoneNumbers;
    }

    public Integer getTotalSalary() {
	return totalSalary;
    }

    public void setAddressLine1(String addressLine1) {
	this.addressLine1 = addressLine1;
    }

    public void setAddressLine2(String addressLine2) {
	this.addressLine2 = addressLine2;
    }

    public void setBirthDate(Date birthDate) {
	this.birthDate = birthDate;
    }

    public void setEmails(Set<Email> emails) {
	this.emails = emails;
    }

    public void setFirstName(String firstName) {
	this.firstName = firstName;
    }

    public void setGender(String gender) {
	this.gender = gender;
    }

    public void setLastName(String lastName) {
	this.lastName = lastName;
    }

    public void setPersonId(Integer personId) {
	this.personId = personId;
    }

    public void setPhoneNumbers(Set<PhoneNumber> phoneNumbers) {
	this.phoneNumbers = phoneNumbers;
    }

    public void setTotalSalary(Integer totalSalary) {
	this.totalSalary = totalSalary;
    }

}
