package org.lightmare.criteria.entities;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

@Entity
@Table(name = "PERSONS")
public class Person extends GeneralInfo {

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

    @Column(name = "COMPARATOR_ID")
    private Long comparatorId;

    @Column(name = "FUNCTIONAL_ID")
    private Integer functionalId;

    @Column(name = "ESCAPE")
    private Character escape;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "PERSON_PHONES")
    private Set<Phone> phones;

    @Transient
    private Collection<Long> identifiers;

    @Embedded
    private PersonInfo info;

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

    public Long getComparatorId() {
        return comparatorId;
    }

    public void setComparatorId(Long comparatorId) {
        this.comparatorId = comparatorId;
    }

    public Integer getFunctionalId() {
        return functionalId;
    }

    public void setFunctionalId(Integer functionalId) {
        this.functionalId = functionalId;
    }

    public Character getEscape() {
        return escape;
    }

    public void setEscape(Character escape) {
        this.escape = escape;
    }

    public Set<Phone> getPhones() {
        return phones;
    }

    public void setPhones(Set<Phone> phones) {
        this.phones = phones;
    }

    public Collection<Long> getIdentifiers() {

        if (identifiers == null) {
            identifiers = new HashSet<>(
                    Arrays.asList(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L, 11L, 12L, 13L, 14L, 15L));
        }

        return identifiers;
    }

    public void setIdentifiers(Collection<Long> identifiers) {
        this.identifiers = identifiers;
    }

    public PersonInfo getInfo() {
        return info;
    }

    public void setInfo(PersonInfo info) {
        this.info = info;
    }

    @Override
    public String toString() {
        return String.format("%s %s %s %s %s %s", personId, personalNo, lastName, firstName, birthDate, middName);
    }
}
