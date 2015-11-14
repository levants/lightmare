package org.lightmare.criteria.queries;

import javax.persistence.EntityManager;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.lightmare.criteria.entities.Person;
import org.lightmare.criteria.entities.PersonInfo;
import org.lightmare.criteria.query.QueryProvider;
import org.lightmare.criteria.query.QueryStream;
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
            QueryStream<Person> stream = QueryProvider.select(em, Person.class).where()
                    .like(Person::getLastName, "lname")
                    .embedded(Person::getInfo, c -> c.equal(PersonInfo::getCardNumber, "100100"));
            String sql = stream.sql();
            System.out.println(sql);
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
            QueryStream<Person> stream = QueryProvider.select(em, Person.class).where()
                    .like(Person::getLastName, "lname")
                    .embedded(Person::getInfo, c -> c.equal(PersonInfo::getCardNumber, Person::getPersonalNo)
                            .equal(PersonInfo::getNote, "100100"));
            String sql = stream.sql();
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
            QueryStream<Person> stream = QueryProvider.select(em, Person.class).where()
                    .like(Person::getLastName, "lname")
                    .embedded(Person::getInfo, c -> c.equal(PersonInfo::getCardNumber, Person::getPersonalNo)
                            .equal(PersonInfo::getNote, PersonInfo::getCardNumber));
            Person person = stream.getFirst();
            String sql = stream.sql();
            System.out.println(sql);
            System.out.println(person);
        } catch (Throwable ex) {
            ex.printStackTrace();
        } finally {
            em.close();
        }
    }
}
