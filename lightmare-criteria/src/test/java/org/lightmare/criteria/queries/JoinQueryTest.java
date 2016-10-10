package org.lightmare.criteria.queries;

import javax.persistence.EntityManager;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.lightmare.criteria.entities.GeneralInfo;
import org.lightmare.criteria.entities.Person;
import org.lightmare.criteria.entities.Phone;
import org.lightmare.criteria.query.providers.jpa.JpaQueryConsumer;
import org.lightmare.criteria.query.providers.jpa.JpaQueryProvider;
import org.lightmare.criteria.query.providers.jpa.JpaQueryStream;
import org.lightmare.criteria.runorder.RunOrder;
import org.lightmare.criteria.runorder.SortedRunner;

@RunWith(SortedRunner.class)
public class JoinQueryTest extends SubQueryTest {

    @Test
    @RunOrder(200)
    public void innerJoinTest() {

        EntityManager em = emf.createEntityManager();
        try {
            JpaQueryStream<Person> stream = JpaQueryProvider.select(em, Person.class).where(q -> q
                    .equal(GeneralInfo::getAddrress, "address").like(Person::getLastName, "lname")
                    .join(Person::getPhones, c -> c.equal(Phone::getPhoneNumber, "100100")).leftJoin(Person::getPhones,
                            c -> c.equal(Phone::getPhoneId, Person::getPersonId).equal(Phone::getPhoneId, 100L)));
            Person person = stream.getFirst();
            String sql = stream.sql();
            printParameters(stream);
            System.out.println(sql);
            System.out.println(person);
        } finally {
            em.close();
        }
    }

    @Test
    @RunOrder(200.1)
    public void innerJoinOnTest() {

        EntityManager em = emf.createEntityManager();
        try {
            JpaQueryStream<Person> stream = JpaQueryProvider.select(em, Person.class).where(q -> q
                    .equal(GeneralInfo::getAddrress, "address").like(Person::getLastName, "lname")
                    .join(Person::getPhones, c -> c.equal(Phone::getPhoneNumber, "100100")).leftJoin(Person::getPhones,
                            o -> o.equal(Phone::getPhoneId, Person::getPersonId),
                            c -> c.equal(Phone::getPhoneId, Person::getPersonId).equal(Phone::getPhoneId, 100L)));
            String sql = stream.sql();
            printParameters(stream);
            System.out.println(sql);
        } finally {
            em.close();
        }
    }

    @Test
    @RunOrder(200.11)
    public void innerJoinWithOnAndJpaConsumerTest() {

        EntityManager em = emf.createEntityManager();
        try {
            JpaQueryConsumer<Phone> on = o -> o.equal(Phone::getPhoneId, Person::getPersonId);
            JpaQueryConsumer<Phone> consumer = c -> c.equal(Phone::getPhoneId, Person::getPersonId)
                    .equal(Phone::getPhoneId, 100L);
            JpaQueryStream<Person> stream = JpaQueryProvider.select(em, Person.class)
                    .where(q -> q.equal(GeneralInfo::getAddrress, "address").like(Person::getLastName, "lname")
                            .join(Person::getPhones, c -> c.equal(Phone::getPhoneNumber, "100100"))
                            .leftJoin(Person::getPhones, on, consumer));
            String sql = stream.sql();
            printParameters(stream);
            System.out.println(sql);
        } finally {
            em.close();
        }
    }

    @Test
    @RunOrder(200.2)
    public void innerJoinOnMultyTest() {

        EntityManager em = emf.createEntityManager();
        try {
            JpaQueryStream<Person> stream = JpaQueryProvider.select(em, Person.class).where()
                    .equal(GeneralInfo::getAddrress, "address").like(Person::getLastName, "lname")
                    .join(Person::getPhones, c -> c.equal(Phone::getPhoneNumber, "100100")).leftJoin(Person::getPhones,
                            o -> o.equal(Phone::getPhoneId, Person::getPersonId).le(Phone::getOperatorId,
                                    Person::getComparatorId),
                            c -> c.equal(Phone::getPhoneId, Person::getPersonId).equal(Phone::getPhoneId, 100L));
            String sql = stream.sql();
            printParameters(stream);
            System.out.println(sql);
        } finally {
            em.close();
        }
    }

    @Test
    @RunOrder(201)
    public void leftJoinWhereTest() {

        EntityManager em = emf.createEntityManager();
        try {
            JpaQueryStream<Person> stream = JpaQueryProvider.select(em, Person.class)
                    .where(q -> q.like(Person::getLastName, "lname").ge(Person::getPersonId, 1000L)
                            .leftJoin(Person::getPhones, c -> c.equal(Phone::getPhoneNumber, Person::getPersonalNo)));
            String sql = stream.sql();
            System.out.println(sql);
            printParameters(stream);
        } finally {
            em.close();
        }
    }

    @Test
    @RunOrder(201.05)
    public void leftJoinTest() {

        EntityManager em = emf.createEntityManager();
        try {
            JpaQueryStream<Person> stream = JpaQueryProvider.select(em, Person.class).leftJoin(Person::getPhones,
                    c -> c.equal(Phone::getPhoneNumber, Person::getPersonalNo));
            String sql = stream.sql();
            System.out.println(sql);
        } finally {
            em.close();
        }
    }

    @Test
    @RunOrder(201.1)
    public void leftJoinInWhereTest() {

        EntityManager em = emf.createEntityManager();
        try {
            JpaQueryStream<Person> stream = JpaQueryProvider.select(em, Person.class).leftJoin(Person::getPhones,
                    c -> c.where().equal(Phone::getPhoneNumber, Person::getPersonalNo));
            String sql = stream.sql();
            System.out.println(sql);
        } finally {
            em.close();
        }
    }

    @Test
    @RunOrder(201.2)
    public void emptyConditionalLeftJoinTest() {

        EntityManager em = emf.createEntityManager();
        try {
            JpaQueryStream<Person> stream = JpaQueryProvider.select(em, Person.class).leftJoin(Person::getPhones, c -> {
            });
            String sql = stream.sql();
            System.out.println(sql);
        } finally {
            em.close();
        }
    }

    @Test
    @RunOrder(201.3)
    public void unconditionalLeftJoinTest() {

        EntityManager em = emf.createEntityManager();
        try {
            JpaQueryStream<Person> stream = JpaQueryProvider.select(em, Person.class).leftJoin(Person::getPhones);
            String sql = stream.sql();
            System.out.println(sql);
        } finally {
            em.close();
        }
    }

    @Test
    @RunOrder(201.4)
    public void parentAndUnconditionalLeftJoinTest() {

        EntityManager em = emf.createEntityManager();
        try {
            JpaQueryStream<Person> stream = JpaQueryProvider.select(em, Person.class).leftJoin(Person::getPhones)
                    .where().like(Person::getLastName, "lname");
            String sql = stream.sql();
            System.out.println(sql);
        } finally {
            em.close();
        }
    }

    @Test
    @RunOrder(202)
    public void fetchJoinTest() {

        EntityManager em = emf.createEntityManager();
        try {
            JpaQueryStream<Person> stream = JpaQueryProvider.select(em, Person.class).where()
                    .like(Person::getLastName, "lname")
                    .fetchJoin(Person::getPhones, c -> c.equal(Phone::getPhoneNumber, "100100").and()
                            .ge(Phone::getOperatorId, Person::getPersonId));
            String sql = stream.sql();
            System.out.println(sql);
        } finally {
            em.close();
        }
    }

    @Test
    @RunOrder(203)
    public void fetchJoinNotEqualsTest() {

        EntityManager em = emf.createEntityManager();
        try {
            JpaQueryStream<Person> stream = JpaQueryProvider.select(em, Person.class).where()
                    .notEqual(Person::getLastName, "lname").and().like(Person::getAddrress, "address")
                    .fetchJoin(Person::getPhones,
                            c -> c.equal(Phone::getPhoneNumber, "100100")
                                    .notEqual(Phone::getOperatorId, Person::getPersonId).or()
                                    .equal(Phone::getOperatorId, Phone::getPhoneId));
            String sql = stream.sql();
            System.out.println(sql);
        } finally {
            em.close();
        }
    }

    @Test
    @RunOrder(204)
    public void innerJoinOnOuterEntityTest() {

        EntityManager em = emf.createEntityManager();
        try {
            JpaQueryStream<Person> stream = JpaQueryProvider.select(em, Person.class).where()
                    .equal(GeneralInfo::getAddrress, "address").like(Person::getLastName, "lname")
                    .join(Phone.class, c -> c.equal(Phone::getPhoneNumber, "100100")).leftJoin(Phone.class,
                            o -> o.equal(Phone::getPhoneId, Person::getPersonId),
                            c -> c.equal(Phone::getPhoneId, Person::getPersonId).equal(Phone::getPhoneId, 100L));
            String sql = stream.sql();
            printParameters(stream);
            System.out.println(sql);
        } finally {
            em.close();
        }
    }

    @Test
    @RunOrder(205)
    public void innerJoinOnOuterEntityMultyTest() {

        EntityManager em = emf.createEntityManager();
        try {
            JpaQueryStream<Person> stream = JpaQueryProvider.select(em, Person.class).where()
                    .equal(GeneralInfo::getAddrress, "address").like(Person::getLastName, "lname")
                    .leftJoin(Phone.class, c -> c.equal(Phone::getPhoneNumber, "100100")).leftJoin(Person::getPhones,
                            o -> o.equal(Phone::getPhoneId, Person::getPersonId).le(Phone::getOperatorId,
                                    Person::getComparatorId),
                            c -> c.equal(Phone::getPhoneId, Person::getPersonId).equal(Phone::getPhoneId, 100L));
            String sql = stream.sql();
            printParameters(stream);
            System.out.println(sql);
        } finally {
            em.close();
        }
    }
}
