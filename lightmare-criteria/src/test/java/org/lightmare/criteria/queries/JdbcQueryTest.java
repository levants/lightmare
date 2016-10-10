package org.lightmare.criteria.queries;

import java.sql.Connection;
import java.util.List;

import javax.persistence.EntityManager;

import org.hibernate.Session;
import org.hibernate.internal.SessionImpl;
import org.junit.Test;
import org.lightmare.criteria.entities.jdbc.JdbcPerson;
import org.lightmare.criteria.query.providers.jdbc.JdbcQueryProvider;
import org.lightmare.criteria.query.providers.jdbc.JdbcQueryStream;
import org.lightmare.criteria.runorder.RunOrder;

public class JdbcQueryTest extends FunctionnalQueryTest {

    @Test
    @RunOrder(600)
    public void testSimpleSelect() {

        EntityManager em = emf.createEntityManager();
        try {
            Session session = em.unwrap(Session.class);
            SessionImpl sessionImpl = ((SessionImpl) session);
            Connection connection = sessionImpl.connection();
            // ============= Query construction ============== //
            JdbcQueryStream<JdbcPerson> stream = JdbcQueryProvider.select(connection, JdbcPerson.class).where()
                    .like(JdbcPerson::getLastName, "lname%").startsWith(JdbcPerson::getFirstName, "fname%");
            List<JdbcPerson> persons = stream.toList(r -> {

                JdbcPerson person = new JdbcPerson();

                person.setPersonalNo(r.getString("PERSONAL_NO"));
                person.setLastName(r.getString("LAST_NAME"));
                person.setFirstName(r.getString("FIRST_NAME"));

                return person;
            });
            String sql = stream.sql();
            System.out.println("===========JDBC-QL==========");
            System.out.println(sql);
            System.out.println("===========RESULTS==========");
            persons.forEach(System.out::println);
        } finally {
            em.close();
        }
    }

    @Test
    @RunOrder(600)
    public void testSimpleSelectLambda() {

        EntityManager em = emf.createEntityManager();
        try {
            Session session = em.unwrap(Session.class);
            SessionImpl sessionImpl = ((SessionImpl) session);
            Connection connection = sessionImpl.connection();
            // ============= Query construction ============== //
            JdbcQueryStream<JdbcPerson> stream = JdbcQueryProvider.select(connection, JdbcPerson.class).where(
                    q -> q.like(JdbcPerson::getLastName, "lname%").startsWith(JdbcPerson::getFirstName, "fname%"));
            List<JdbcPerson> persons = stream.toList(r -> {

                JdbcPerson person = new JdbcPerson();

                person.setPersonalNo(r.getString("PERSONAL_NO"));
                person.setLastName(r.getString("LAST_NAME"));
                person.setFirstName(r.getString("FIRST_NAME"));

                return person;
            });
            String sql = stream.sql();
            System.out.println("===========JDBC-QL==========");
            System.out.println(sql);
            System.out.println("===========RESULTS==========");
            persons.forEach(System.out::println);
        } finally {
            em.close();
        }
    }
}
