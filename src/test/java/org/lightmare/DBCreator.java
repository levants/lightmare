package org.lightmare;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.lightmare.entities.Email;
import org.lightmare.entities.Person;
import org.lightmare.entities.PhoneNumber;

public class DBCreator {

	private EntityManager em;

	private DateFormat format = new SimpleDateFormat("yyyy-mm-dd");

	public DBCreator(EntityManager em) {
		this.em = em;
	}

	public Email addEmail(Integer emailId, String emailAddress, Integer personId) {
		Email email = new Email();
		// email.setEmailId(emailId);
		email.setEmailAddress(emailAddress);
		email.setPersonId(personId);
		return email;
	}

	public Person addPerson(Integer personId, String firstName,
			String lastName, String addressLine1, String addressLine2,
			String birthDate, Integer totalSalary, String gender)
			throws ParseException {

		Person person = new Person();
		// person.setPersonId(personId);
		person.setFirstName(firstName);
		person.setLastName(lastName);
		person.setAddressLine1(addressLine2);
		person.setAddressLine2(addressLine1);
		person.setBirthDate(format.parse(birthDate));
		person.setTotalSalary(totalSalary);
		person.setGender(gender);

		return person;

	}

	public PhoneNumber addPhoneNumber(Integer phoneNumberId, Integer personId,
			String phone) {

		PhoneNumber phoneNumber = new PhoneNumber();
		// phoneNumber.setPhoneNumberId(phoneNumberId);
		phoneNumber.setPersonId(personId);
		phoneNumber.setPhoneNumber(phone);
		return phoneNumber;
	}

	public void addEmailToPerson(Person person, Email... emails) {
		if (emails.length > 0 && person.getEmails() == null) {
			person.setEmails(new HashSet<Email>());
		}
		for (Email email : emails) {
			person.getEmails().add(email);
		}
	}

	public void addPhoneToPerson(Person person, PhoneNumber... phones) {
		if (phones.length > 0 && person.getPhoneNumbers() == null) {
			person.setPhoneNumbers(new HashSet<PhoneNumber>());
		}
		for (PhoneNumber phone : phones) {
			person.getPhoneNumbers().add(phone);
		}
	}

	public void addPersons(List<Person> persons) {
		for (Person person : persons) {
			em.merge(person);
		}
	}

	public Person personForIdGenerator() throws ParseException {
		Person person = new Person();
		person.setLastName("lastToAddId");
		person.setFirstName("firstToAddId");
		person.setAddressLine1("30 Umber Fown Cave Id");
		person.setAddressLine2("30 Umber Fown 30N30 Id");
		person.setBirthDate(format.parse("1930-11-30"));
		person.setTotalSalary(300000);
		person.setGender("m");

		return person;
	}

	public Person personForIdGeneratorNew() throws ParseException {
		Person person = new Person();
		person.setLastName("lastToAddIdNew");
		person.setFirstName("firstToAddIdNew");
		person.setAddressLine1("30 Umber Fown Cave Id New");
		person.setAddressLine2("30 Umber Fown 30N30 Id New");
		person.setBirthDate(format.parse("1930-11-30"));
		person.setTotalSalary(300000);
		person.setGender("m");

		return person;
	}

	public Person createPersonToAdd() throws ParseException {
		Person person = new Person();
		person.setLastName("lastToAdd");
		person.setFirstName("firstToAdd");
		person.setAddressLine1("30 Umber Fown Cave");
		person.setAddressLine2("30 Umber Fown 30N30");
		person.setBirthDate(format.parse("1930-11-30"));
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

	public Person createPersonToEdit() throws ParseException {
		Person person = new Person();
		person.setLastName("lastToEdit");
		person.setFirstName("firstToEdit");
		person.setAddressLine1("31 Umber Fown Cave");
		person.setAddressLine2("31 Umber Fown 30N30");
		person.setBirthDate(format.parse("1931-11-31"));
		person.setTotalSalary(310000);
		person.setGender("m");

		Set<PhoneNumber> phones = new HashSet<PhoneNumber>();

		PhoneNumber phone1 = new PhoneNumber();
		phone1.setPhoneNumber("3030329");
		phones.add(phone1);

		PhoneNumber phone2 = new PhoneNumber();
		phone2.setPhoneNumber("313130");
		phones.add(phone2);

		PhoneNumber phone3 = new PhoneNumber();
		phone3.setPhoneNumber("323231");
		phones.add(phone3);

		PhoneNumber phone4 = new PhoneNumber();
		phone4.setPhoneNumber("343432");
		phones.add(phone4);

		PhoneNumber phone5 = new PhoneNumber();
		phone5.setPhoneNumber("353533");
		phones.add(phone5);

		person.setPhoneNumbers(phones);

		Set<Email> emails = new HashSet<Email>();

		Email email1 = new Email();
		email1.setEmailAddress("lastToEdit1@test.org");
		emails.add(email1);

		Email email2 = new Email();
		email2.setEmailAddress("lastToEdit2@test.org");
		emails.add(email2);

		Email email3 = new Email();
		email3.setEmailAddress("lastToEdit3@test.org");
		emails.add(email3);

		person.setEmails(emails);

		return person;
	}

	public void createDB() throws ParseException {
		List<Person> persons = new ArrayList<Person>();
		Person person1 = addPerson(1, "Richard", "White",
				"598 Umber Fawn Cove  ", "F6047", "1979-12-22", 6200, "m");
		persons.add(person1);
		Person person2 = addPerson(2, "David", "Smith",
				"422 Stony Bridge Bend", "N12819", "1977-08-22", 4100, "m");
		persons.add(person2);
		Person person3 = addPerson(3, "William", "Johnson",
				"376 Orange Barn Ridge", "G47247", "1992-01-25", 9500, "m");
		persons.add(person3);
		Person person4 = addPerson(4, "Michael", "Williams",
				"807 Blue Pioneer Orchard", "P69942", "1973-02-26", 7500, "m");
		persons.add(person4);
		Person person5 = addPerson(5, "James", "Jones", "533 Blue Nest Lagoon",
				"L65188", "1978-01-09", 8100, "m");
		persons.add(person5);
		Person person6 = addPerson(6, "John", "Brown", "778 Hidden Axe Pike",
				"W4737373", "1990-09-30", 7900, "m");
		persons.add(person6);
		Person person7 = addPerson(7, "Robert", "Davis",
				"698 Merry Chestnut Villa", "R7485880", "1982-08-14", 5100, "m");
		persons.add(person7);
		Person person8 = addPerson(8, "Donald", "Thomas", "942 Quaint Twist",
				"L2464", "1973-09-16", 9800, "m");
		persons.add(person8);
		Person person9 = addPerson(9, "Mark", "Anderson",
				"506 Foggy River Orchard", "S5585", "1990-08-01", 5700, "m");
		persons.add(person9);
		Person person10 = addPerson(10, "Patricia", "Taylor", "631 Goat Stead",
				"F6358", "1987-04-29", 7100, "f");
		persons.add(person10);
		Person person11 = addPerson(11, "Daniel", "Moore",
				"825 Lone Berry Isle", "X9447", "1989-07-26", 8500, "m");
		persons.add(person11);
		Person person12 = addPerson(12, "Christopher", "Willson",
				"970 Big Bird Villa", "B84309", "1988-07-07", 2200, "m");
		persons.add(person12);
		Person person13 = addPerson(13, "Thomas", "Miller",
				"689 Dusty Elk Isle", "W21253", "1986-05-05", 2500, "m");
		persons.add(person13);
		Person person14 = addPerson(14, "Thomas", "Taylor",
				"649 Indian Branch Trek", "Q746887", "1973-07-05", 4700, "m");
		persons.add(person14);
		Person person15 = addPerson(15, "John", "Taylor",
				"213 Cotton Leaf Court", "Y27897", "1990-04-07", 5900, "m");
		persons.add(person15);
		Person person16 = addPerson(16, "Michael", "Moore", "938 Oak Dell",
				"F389376", "1975-02-10", 5700, "m");
		persons.add(person16);
		Person person17 = addPerson(17, "Paul", "Davis",
				"91 Orange Barn Ridge", "U120", "1965-12-15", 7600, "m");
		persons.add(person17);
		Person person18 = addPerson(18, "Mary", "Williams", "46 Oak Dell",
				"F34297", "1986-02-04", 3400, "f");
		persons.add(person18);
		Person person19 = addPerson(19, "Linda", "Johnson", "271 Goat Stead",
				"S971288", "1972-09-13", 1200, "f");
		persons.add(person19);
		Person person20 = addPerson(20, "last1", "first1", "271 Goat Stead",
				"S971288", "1972-09-13", 1200, "f");
		persons.add(person20);
		Person person21 = addPerson(21, "last2", "first2", "271 Goat Stead",
				"S971288", "1972-09-13", 1200, "f");
		persons.add(person21);
		Person person22 = addPerson(22, "last3", "first3", "271 Goat Stead",
				"S971288", "1972-09-13", 1200, "f");
		persons.add(person22);
		Person person23 = addPerson(23, "last4", "first4", "271 Goat Stead",
				"S971288", "1972-09-13", 1200, "f");
		persons.add(person23);
		Person person24 = addPerson(24, "last5", "first5", "271 Goat Stead",
				"S971288", "1972-09-13", 1200, "f");
		persons.add(person24);
		Person person25 = addPerson(25, "last", "first", "271 Goat Stead",
				"S971288", "1972-09-13", 1200, "f");
		persons.add(person25);

		Email email1 = addEmail(1, "richard@example.com", 1);
		addEmailToPerson(person1, email1);
		Email email2 = addEmail(2, "david@example.com", 2);
		addEmailToPerson(person2, email2);
		Email email3 = addEmail(3, "william@example.com", 3);
		addEmailToPerson(person3, email3);
		Email email4 = addEmail(4, "w.johnson@example.com", 3);
		addEmailToPerson(person3, email4);
		Email email5 = addEmail(5, "john@example.org", 6);
		addEmailToPerson(person6, email5);

		Email email6 = addEmail(6, "last1@example.org", 20);
		addEmailToPerson(person20, email6);
		Email email7 = addEmail(7, "last2@example.org", 21);
		addEmailToPerson(person21, email7);
		Email email8 = addEmail(8, "last3@example.org", 22);
		addEmailToPerson(person22, email8);
		Email email9 = addEmail(9, "last4@example.org", 23);
		addEmailToPerson(person23, email9);
		Email email10 = addEmail(10, "last5@example.org", 24);
		addEmailToPerson(person24, email10);
		Email email11 = addEmail(11, "last@example.org", 25);
		addEmailToPerson(person25, email11);

		PhoneNumber phone1 = addPhoneNumber(1, 1, "+6071632774");
		addPhoneToPerson(person1, phone1);
		PhoneNumber phone2 = addPhoneNumber(2, 1, "+7042471748");
		addPhoneToPerson(person1, phone2);
		PhoneNumber phone3 = addPhoneNumber(3, 2, "+1607739101");
		addPhoneToPerson(person2, phone3);
		PhoneNumber phone4 = addPhoneNumber(4, 3, "+1239662770");
		addPhoneToPerson(person3, phone4);
		PhoneNumber phone5 = addPhoneNumber(5, 4, "+8891410317");
		addPhoneToPerson(person4, phone5);
		PhoneNumber phone6 = addPhoneNumber(6, 5, "+1032826010");
		addPhoneToPerson(person5, phone6);
		PhoneNumber phone7 = addPhoneNumber(7, 6, "+1972593092");
		addPhoneToPerson(person6, phone7);
		PhoneNumber phone8 = addPhoneNumber(8, 7, "+8672182012");
		addPhoneToPerson(person7, phone8);
		PhoneNumber phone9 = addPhoneNumber(9, 7, "+4305998522");
		addPhoneToPerson(person7, phone9);
		PhoneNumber phone10 = addPhoneNumber(10, 8, "+2094418141");
		addPhoneToPerson(person8, phone10);
		PhoneNumber phone11 = addPhoneNumber(11, 8, "+5146273773");
		addPhoneToPerson(person8, phone11);
		PhoneNumber phone12 = addPhoneNumber(12, 8, "+8525638805");
		addPhoneToPerson(person8, phone12);
		PhoneNumber phone13 = addPhoneNumber(13, 9, "+5985000896");
		addPhoneToPerson(person9, phone13);
		PhoneNumber phone14 = addPhoneNumber(14, 11, "+6331647513");
		addPhoneToPerson(person11, phone14);
		PhoneNumber phone15 = addPhoneNumber(15, 12, "+4111794910");
		addPhoneToPerson(person12, phone15);
		PhoneNumber phone16 = addPhoneNumber(16, 14, "+6269759870");
		addPhoneToPerson(person14, phone16);
		PhoneNumber phone17 = addPhoneNumber(17, 15, "+8460150903");
		addPhoneToPerson(person15, phone17);
		PhoneNumber phone18 = addPhoneNumber(18, 16, "+5461245532");
		addPhoneToPerson(person16, phone18);
		PhoneNumber phone19 = addPhoneNumber(19, 16, "+6199855888");
		addPhoneToPerson(person16, phone19);
		PhoneNumber phone20 = addPhoneNumber(20, 17, "+5612038838");
		addPhoneToPerson(person17, phone20);
		PhoneNumber phone21 = addPhoneNumber(21, 18, "+6864956263");
		addPhoneToPerson(person18, phone21);
		PhoneNumber phone22 = addPhoneNumber(22, 19, "+5372325721");
		addPhoneToPerson(person19, phone22);

		PhoneNumber phone23 = addPhoneNumber(23, 20, "+5372325722");
		addPhoneToPerson(person20, phone23);
		PhoneNumber phone24 = addPhoneNumber(24, 21, "+5372325723");
		addPhoneToPerson(person21, phone24);
		PhoneNumber phone25 = addPhoneNumber(25, 22, "+5372325724");
		addPhoneToPerson(person22, phone25);
		PhoneNumber phone26 = addPhoneNumber(26, 23, "+5372325725");
		addPhoneToPerson(person23, phone26);
		PhoneNumber phone27 = addPhoneNumber(27, 24, "+5372325726");
		addPhoneToPerson(person24, phone27);
		PhoneNumber phone28 = addPhoneNumber(28, 25, "+5372325727");
		addPhoneToPerson(person25, phone28);
		EntityTransaction transaction = em.getTransaction();
		transaction.begin();

		transaction.commit();
		em.close();
	}
}
