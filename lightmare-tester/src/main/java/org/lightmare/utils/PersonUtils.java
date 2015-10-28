package org.lightmare.utils;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Set;

import org.lightmare.entities.Email;
import org.lightmare.entities.Person;
import org.lightmare.entities.PhoneNumber;

public class PersonUtils {

    public static Person createPersonToAdd() throws IOException {
	Person person = new Person();
	person.setLastName("lastToAdd");
	person.setFirstName("firstToAdd");
	person.setAddressLine1("30 Umber Fown Cave");
	person.setAddressLine2("30 Umber Fown 30N30");
	try {
	    person.setBirthDate(new SimpleDateFormat("yyyy-MM-dd")
		    .parse("1930-11-30"));
	} catch (ParseException ex) {
	    throw new IOException(ex);
	}
	person.setTotalSalary(300000);
	person.setGender("m");

	Set<PhoneNumber> phones = new HashSet<PhoneNumber>();

	PhoneNumber phone1 = new PhoneNumber();
	phone1.setPhoneNumber("303030");
	phones.add(phone1);

	PhoneNumber phone2 = new PhoneNumber();
	phone2.setPhoneNumber("313131");
	phones.add(phone2);

	PhoneNumber phone3 = new PhoneNumber();
	phone3.setPhoneNumber("323232");
	phones.add(phone3);

	PhoneNumber phone4 = new PhoneNumber();
	phone4.setPhoneNumber("343434");
	phones.add(phone4);

	PhoneNumber phone5 = new PhoneNumber();
	phone5.setPhoneNumber("353535");
	phones.add(phone5);

	person.setPhoneNumbers(phones);

	Set<Email> emails = new HashSet<Email>();

	Email email1 = new Email();
	email1.setEmailAddress("lastToAdd1@test.org");
	emails.add(email1);

	Email email2 = new Email();
	email2.setEmailAddress("lastToAdd2@test.org");
	emails.add(email2);

	Email email3 = new Email();
	email3.setEmailAddress("lastToAdd3@test.org");
	emails.add(email3);

	person.setEmails(emails);

	return person;
    }
}
