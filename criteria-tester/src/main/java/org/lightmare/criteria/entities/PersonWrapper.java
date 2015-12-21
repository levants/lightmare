package org.lightmare.criteria.entities;

import org.lightmare.criteria.utils.StringUtils;

public class PersonWrapper {

    private String personalNo;

    private String lastName;

    private String firstName;

    public PersonWrapper(String personalNo, String lastName, String firstName) {
        this.personalNo = personalNo;
        this.lastName = lastName;
        this.firstName = firstName;
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

    @Override
    public String toString() {
        return StringUtils.concat(personalNo, StringUtils.SPACE, lastName, StringUtils.SPACE, firstName);
    }
}
