package org.lightmare.criteria.queries;

import java.util.List;

import javax.persistence.EntityManager;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.lightmare.criteria.entities.GeneralInfo;
import org.lightmare.criteria.entities.Person;
import org.lightmare.criteria.entities.Phone;
import org.lightmare.criteria.query.orm.links.SubQuery;
import org.lightmare.criteria.query.providers.jpa.JpaQueryConsumer;
import org.lightmare.criteria.query.providers.jpa.JpaQueryProvider;
import org.lightmare.criteria.query.providers.jpa.JpaQueryStream;
import org.lightmare.criteria.runorder.RunOrder;
import org.lightmare.criteria.runorder.SortedRunner;

@RunWith(SortedRunner.class)
public class SubQueryTest extends QueryTest {

    @RunOrder(100)
    @Test
    public void subQueryTest() {

        EntityManager em = emf.createEntityManager();
        try {
            // ============= Query construction ============== //
            JpaQueryStream<Person> stream = JpaQueryProvider.select(em, Person.class).where().equal(100, SubQuery.any(
                    Phone.class,
                    c -> c.where().equal(Phone::getPhoneNumber, "100100").select(Phone::getPhoneNumber).toList()));
            String sql = stream.sql();
            System.out.println(sql);
        } finally {
            em.close();
        }
    }

    @RunOrder(100.1)
    @Test
    public void subQueryCallTest() {

        EntityManager em = emf.createEntityManager();
        try {
            // ============= Query construction ============== //
            JpaQueryStream<Person> stream = JpaQueryProvider.select(em, Person.class).where()
                    .ge(Person::getPersonalNo,
                            SubQuery.all(Phone.class,
                                    c -> c.where(s -> s.equal(Phone::getPhoneNumber, "100100")
                                            .equal(Phone::getOperatorId, Person::getPersonId))
                                            .select(Phone::getPhoneNumber)));
            String sql = stream.sql();
            System.out.println(sql);
        } finally {
            em.close();
        }
    }

    @RunOrder(100.11)
    @Test
    public void subQueryJpaConsumerCallTest() {

        EntityManager em = emf.createEntityManager();
        try {
            // ============= Query construction ============== //
            JpaQueryConsumer<Phone> consumer = c -> c.where(
                    s -> s.equal(Phone::getPhoneNumber, "100100").equal(Phone::getOperatorId, Person::getPersonId))
                    .select(Phone::getPhoneNumber);
            JpaQueryStream<Person> stream = JpaQueryProvider.select(em, Person.class).where().ge(Person::getPersonalNo,
                    SubQuery.all(Phone.class, consumer));
            String sql = stream.sql();
            System.out.println(sql);
        } finally {
            em.close();
        }
    }

    @RunOrder(100.2)
    @Test
    public void subQuerySelectTest() {

        EntityManager em = emf.createEntityManager();
        try {
            // ============= Query construction ============== //
            JpaQueryStream<Person> stream = JpaQueryProvider.select(em, Person.class).where().in(Person::getLastName,
                    Phone.class,
                    c -> c.where().equal(Phone::getPhoneNumber, "100100").select(Phone::getPhoneNumber).toList());
            String sql = stream.sql();
            System.out.println(sql);
        } finally {
            em.close();
        }
    }

    @RunOrder(100.5)
    @Test
    public void subQueryExistsGetTest() {

        EntityManager em = emf.createEntityManager();
        try {
            // ============= Query construction ============== //
            JpaQueryStream<Person> stream = JpaQueryProvider.select(em, Person.class)
                    .where(q -> q.exists(Phone.class, c -> c.where(s -> s.equal(Phone::getPhoneNumber, "100100")).get())
                            .exists(Phone.class, c -> c.where(s -> s.equal(Phone::getPhoneNumber, "100100")).get()));
            String sql = stream.sql();
            List<Person> persons = stream.toList();
            persons.forEach(System.out::println);
            System.out.println(sql);
            printParameters(stream);
        } finally {
            em.close();
        }
    }

    @RunOrder(100.51)
    @Test
    public void subQueryExistsCallTest() {

        EntityManager em = emf.createEntityManager();
        try {
            // ============= Query construction ============== //
            JpaQueryStream<Person> stream = JpaQueryProvider.select(em, Person.class).where().exists(Phone.class,
                    c -> c.where().equal(Phone::getPhoneNumber, "100100").and().ge(Phone::getOperatorId,
                            Person::getPersonId));
            String sql = stream.sql();
            System.out.println(sql);
        } finally {
            em.close();
        }
    }

    @RunOrder(100.52)
    @Test
    public void subQueryUnconditionalExistsCallTest() {

        EntityManager em = emf.createEntityManager();
        try {
            // ============= Query construction ============== //
            JpaQueryStream<Person> stream = JpaQueryProvider.select(em, Person.class).where().exists(Phone.class);
            String sql = stream.sql();
            System.out.println(sql);
        } finally {
            em.close();
        }
    }

    @RunOrder(100.6)
    @Test
    public void subQueryExistsListTest() {

        EntityManager em = emf.createEntityManager();
        try {
            // ============= Query construction ============== //
            JpaQueryStream<Person> stream = JpaQueryProvider.select(em, Person.class).where().exists(Phone.class,
                    c -> c.where().equal(Phone::getPhoneNumber, "100100").toList());
            String sql = stream.sql();
            System.out.println(sql);
        } finally {
            em.close();
        }
    }

    @RunOrder(101)
    @Test
    public void subQueryCountTest() {

        EntityManager em = emf.createEntityManager();
        try {
            // ============= Query construction ============== //
            JpaQueryStream<Person> stream = JpaQueryProvider.select(em, Person.class).where()
                    .equal(GeneralInfo::getAddrress, "address").in(Person::getLastName, Phone.class,
                            c -> c.where().equal(Phone::getPhoneNumber, "100100").count());
            String sql = stream.sql();
            System.out.println(sql);
        } finally {
            em.close();
        }
    }

    @RunOrder(101.1)
    @Test
    public void subQueryCountWithParentTest() {

        EntityManager em = emf.createEntityManager();
        try {
            // ============= Query construction ============== //
            JpaQueryStream<Person> stream = JpaQueryProvider.select(em, Person.class).where().in(Person::getLastName,
                    Phone.class, c -> c.where().equal(Phone::getPhoneNumber, "100100").and()
                            .le(Phone::getOperatorId, Person::getPersonId).count());
            String sql = stream.sql();
            System.out.println(sql);
        } finally {
            em.close();
        }
    }

    @RunOrder(101.2)
    @Test
    public void subQueryCountWithParentInTest() {

        EntityManager em = emf.createEntityManager();
        try {
            // ============= Query construction ============== //
            JpaQueryStream<Person> stream = JpaQueryProvider.select(em, Person.class).where().in(Person::getLastName,
                    Phone.class, c -> c.where().equal(Phone::getPhoneNumber, "100100").and()
                            .isMember(Phone::getOperatorId, Person::getIdentifiers).count());
            String sql = stream.sql();
            System.out.println(sql);
        } finally {
            em.close();
        }
    }

    @RunOrder(101.3)
    @Test
    public void subQueryMemberWithParentTest() {

        EntityManager em = emf.createEntityManager();
        try {
            // ============= Query construction ============== //
            JpaQueryStream<Person> stream = JpaQueryProvider.select(em, Person.class).where().isMember(100,
                    Person::getIdentifiers);
            String sql = stream.sql();
            System.out.println(sql);
        } finally {
            em.close();
        }
    }

    @RunOrder(102)
    @Test
    public void subQueryAnyTest() {

        EntityManager em = emf.createEntityManager();
        try {
            // ============= Query construction ============== //
            JpaQueryStream<Person> stream = JpaQueryProvider.select(em, Person.class).where()
                    .equal(GeneralInfo::getAddrress, "address").ge(Person::getLastName, SubQuery.any(Phone.class,
                            c -> c.where().equal(Phone::getPhoneNumber, "100100").select(Phone::getPhoneNumber)));
            List<Person> persons = stream.toList();
            persons.forEach(System.out::println);
            String sql = stream.sql();
            System.out.println(sql);
        } finally {
            em.close();
        }
    }

    @RunOrder(103)
    @Test
    public void subQueryAllTest() {

        EntityManager em = emf.createEntityManager();
        try {
            // ============= Query construction ============== //
            JpaQueryStream<Person> stream = JpaQueryProvider.select(em, Person.class).where()
                    .equal(GeneralInfo::getAddrress, "address").ge(Person::getLastName, SubQuery.all(Phone.class,
                            c -> c.where().equal(Phone::getPhoneNumber, "100100").select(Phone::getPhoneNumber)));
            List<Person> persons = stream.toList();
            persons.forEach(System.out::println);
            String sql = stream.sql();
            System.out.println(sql);
        } finally {
            em.close();
        }
    }

    @RunOrder(105)
    @Test
    public void subQueryAllToObjectTest() {

        EntityManager em = emf.createEntityManager();
        try {
            // ============= Query construction ============== //
            JpaQueryStream<Person> stream = JpaQueryProvider.select(em, Person.class).where()
                    .equal(GeneralInfo::getAddrress, "address").ge("100", SubQuery.all(Phone.class,
                            c -> c.where().equal(Phone::getPhoneNumber, "100100").select(Phone::getPhoneNumber)));
            List<Person> persons = stream.toList();
            persons.forEach(System.out::println);
            String sql = stream.sql();
            System.out.println(sql);
        } finally {
            em.close();
        }
    }

    @RunOrder(105.5)
    @Test
    public void subQueryAllToMaxTest() {

        EntityManager em = emf.createEntityManager();
        try {
            // ============= Query construction ============== //
            JpaQueryStream<Person> stream = JpaQueryProvider.select(em, Person.class)
                    .equal(GeneralInfo::getAddrress, "address").ge(100, SubQuery.all(Phone.class,
                            c -> c.equal(Phone::getPhoneNumber, "100100").max(Phone::getPhoneId)));
            List<Person> persons = stream.toList();
            persons.forEach(System.out::println);
            String sql = stream.sql();
            System.out.println(sql);
        } finally {
            em.close();
        }
    }

    @RunOrder(106)
    @Test
    public void subQueryAnyToObjectTest() {

        EntityManager em = emf.createEntityManager();
        try {
            // ============= Query construction ============== //
            JpaQueryStream<Person> stream = JpaQueryProvider.select(em, Person.class).where()
                    .equal(GeneralInfo::getAddrress, "address").ge("100", SubQuery.any(Phone.class,
                            c -> c.where().equal(Phone::getPhoneNumber, "100100").select(Phone::getPhoneNumber)));
            List<Person> persons = stream.toList();
            persons.forEach(System.out::println);
            String sql = stream.sql();
            System.out.println(sql);
        } finally {
            em.close();
        }
    }

    @RunOrder(107)
    @Test
    public void subQuerySomeToObjectTest() {

        EntityManager em = emf.createEntityManager();
        try {
            // ============= Query construction ============== //
            JpaQueryStream<Person> stream = JpaQueryProvider.select(em, Person.class).where()
                    .equal(GeneralInfo::getAddrress, "address").ge("100", SubQuery.some(Phone.class,
                            c -> c.where().equal(Phone::getPhoneNumber, "100100").select(Phone::getPhoneNumber)));
            List<Person> persons = stream.toList();
            persons.forEach(System.out::println);
            String sql = stream.sql();
            System.out.println(sql);
        } finally {
            em.close();
        }
    }

    @RunOrder(108)
    @Test
    public void subQueryAllToFunctionTest() {

        EntityManager em = emf.createEntityManager();
        try {
            // ============= Query construction ============== //
            JpaQueryStream<Person> stream = JpaQueryProvider.select(em, Person.class).where()
                    .equal(GeneralInfo::getAddrress, "address")
                    .geSubQuery(f -> f.abs(Person::getPersonId), SubQuery.all(Phone.class,
                            c -> c.where().equal(Phone::getPhoneNumber, "100100").select(Phone::getOperatorId)));
            List<Person> persons = stream.toList();
            persons.forEach(System.out::println);
            String sql = stream.sql();
            System.out.println(sql);
        } finally {
            em.close();
        }
    }

    @RunOrder(109)
    @Test
    public void subQueryAnyToFunctionTest() {

        EntityManager em = emf.createEntityManager();
        try {
            // ============= Query construction ============== //
            JpaQueryStream<Person> stream = JpaQueryProvider.select(em, Person.class).where()
                    .equal(GeneralInfo::getAddrress, "address")
                    .geSubQuery(f -> f.abs(Person::getPersonId), SubQuery.any(Phone.class,
                            c -> c.where().equal(Phone::getPhoneNumber, "100100").select(Phone::getOperatorId)));
            List<Person> persons = stream.toList();
            persons.forEach(System.out::println);
            String sql = stream.sql();
            System.out.println(sql);
        } finally {
            em.close();
        }
    }

    @RunOrder(110)
    @Test
    public void subQuerySomeToFunctionTest() {

        EntityManager em = emf.createEntityManager();
        try {
            // ============= Query construction ============== //
            JpaQueryStream<Person> stream = JpaQueryProvider.select(em, Person.class).where()
                    .equal(GeneralInfo::getAddrress, "address")
                    .geSubQuery(f -> f.abs(Person::getPersonId), SubQuery.some(Phone.class,
                            c -> c.where().equal(Phone::getPhoneNumber, "100100").select(Phone::getOperatorId)));
            List<Person> persons = stream.toList();
            persons.forEach(System.out::println);
            String sql = stream.sql();
            System.out.println(sql);
        } finally {
            em.close();
        }
    }
}
