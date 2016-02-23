package org.lightmare.criteria.entities.jdbc;

import org.lightmare.criteria.annotations.DBColumn;
import org.lightmare.criteria.annotations.DBTable;

@DBTable("PERSONS.PERSONS")
public class JdbcPerson {

    @DBColumn("PERSONAL_NO")
    private String personalNo;

    @DBColumn("LAST_NAME")
    private String lastName;

    @DBColumn("FIRST_NAME")
    private String firstName;

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
        return String.format("%s %s %s", personalNo, lastName, firstName);
    }
}
