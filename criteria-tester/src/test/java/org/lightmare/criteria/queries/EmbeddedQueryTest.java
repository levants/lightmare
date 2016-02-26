package org.lightmare.criteria.queries;

import javax.persistence.EntityManager;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.lightmare.criteria.entities.GeneralInfo;
import org.lightmare.criteria.entities.Person;
import org.lightmare.criteria.entities.PersonInfo;
import org.lightmare.criteria.query.providers.JpaQueryProvider;
import org.lightmare.criteria.query.providers.JpaQueryStream;
import org.lightmare.criteria.runorder.RunOrder;
import org.lightmare.criteria.runorder.SortedRunner;

@RunWith(SortedRunner.class)
public class EmbeddedQueryTest extends JoinQueryTest {

    @Test
    @RunOrder(300)
    public void embeddedQueryTest() {

        EntityManager em = emf.createEntityManager();
        try {
            // ============= Query construction ============== //
            JpaQueryStream<Person> stream = JpaQueryProvider.select(em, Person.class).where()
                    .like(Person::getLastName, "lname").like(GeneralInfo::getFullName, Person::getLastName)
                    .embedded(Person::getInfo, c -> c.equal(PersonInfo::getCardNumber, "100100"))
                    .embedded(Person::getInfo, c -> c.equal(PersonInfo::getNote, "note"));
            String sql = stream.sql();
            System.out.println("===========JPA-QL==========");
            System.out.println(sql);
            printParameters(stream);
        } catch (Throwable ex) {
            ex.printStackTrace();
        } finally {
            em.close();
        }
    }

    @Test
    @RunOrder(301)
    public void embeddedWithParentTest() {

        EntityManager em = emf.createEntityManager();
        try {
            // ============= Query construction ============== //
            JpaQueryStream<Person> stream = JpaQueryProvider.select(em, Person.class).where()
                    .like(Person::getLastName, "lname").or()
                    .embedded(Person::getInfo, c -> c.equal(PersonInfo::getCardNumber, Person::getPersonalNo)
                            .equal(PersonInfo::getNote, "100100"));
            String sql = stream.sql();
            System.out.println("===========JPA-QL==========");
            System.out.println(sql);
        } catch (Throwable ex) {
            ex.printStackTrace();
        } finally {
            em.close();
        }
    }

    @Test
    @RunOrder(302)
    public void embeddedWithParentAndSelfTest() {

        EntityManager em = emf.createEntityManager();
        try {
            // ============= Query construction ============== //
            JpaQueryStream<Person> stream = JpaQueryProvider.select(em, Person.class).where()
                    .like(Person::getLastName, "lname")
                    .embedded(Person::getInfo, c -> c.equal(PersonInfo::getCardNumber, Person::getPersonalNo)
                            .equal(PersonInfo::getNote, PersonInfo::getCardNumber));
            Person person = stream.getFirst();
            String sql = stream.sql();
            System.out.println("===========JPA-QL==========");
            System.out.println(sql);
            System.out.println(person);
        } catch (Throwable ex) {
            ex.printStackTrace();
        } finally {
            em.close();
        }
    }
}
