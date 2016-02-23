package org.lightmare.criteria.queries;

import java.sql.Connection;
import java.util.List;

import javax.persistence.EntityManager;

import org.hibernate.Session;
import org.hibernate.internal.SessionImpl;
import org.junit.Test;
import org.lightmare.criteria.entities.jdbc.JdbcPerson;
import org.lightmare.criteria.query.JdbcQueryProvider;
import org.lightmare.criteria.query.QueryStream;
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
            QueryStream<JdbcPerson> stream = JdbcQueryProvider.select(connection, JdbcPerson.class)
                    .like(JdbcPerson::getLastName, "lname%").startsWith(JdbcPerson::getFirstName, "fname%");
            List<JdbcPerson> persons = stream.toList(r -> {

                JdbcPerson person = new JdbcPerson();

                try {
                    person.setPersonalNo(r.getString("PERSONAL_NO"));
                    person.setLastName(r.getString("LAST_NAME"));
                    person.setFirstName(r.getString("FIRST_NAME"));
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }

                return person;
            });
            String sql = stream.sql();
            System.out.println("===========JDBC-QL==========");
            System.out.println(sql);
            System.out.println("===========RESULTS==========");
            persons.forEach(System.out::println);
        } catch (Throwable ex) {
            ex.printStackTrace();
        } finally {
            em.close();
        }
    }
}
