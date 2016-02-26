package org.lightmare.criteria.queries;

import java.util.Calendar;
import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.lightmare.criteria.db.TestDBUtils;
import org.lightmare.criteria.entities.Person;
import org.lightmare.criteria.query.providers.JpaQueryProvider;
import org.lightmare.criteria.query.providers.JpaQueryStream;
import org.lightmare.criteria.runorder.SortedRunner;
import org.lightmare.criteria.utils.ObjectUtils;

@RunWith(SortedRunner.class)
public class TestEnviromentConfig {

    protected static final String PERSONAL_NO1 = "10100100100";

    protected static final String PERSONAL_NO2 = "10100100111";

    protected static final Long[] IDENTIFIERS = { 1L, 2L, 3L, 4L, 5L };

    protected static EntityManagerFactory emf;

    protected static Date defaultDate = new Date();

    protected static Date getDateValue(int before) {

        Date date;

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(defaultDate);
        calendar.add(Calendar.YEAR, -before);
        date = calendar.getTime();

        return date;
    }

    protected static Date getDateValue() {
        return getDateValue(100);
    }

    @BeforeClass
    public static void config() {

        try {
            emf = ObjectUtils.thisOrDefault(emf, TestDBUtils::create);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    protected static void printParameters(JpaQueryStream<?> stream) {
        stream.getParameters().forEach(c -> System.out.format("name:%s - value:%s\n", c.getName(), c.getValue()));
    }

    protected JpaQueryStream<Person> createQueryStream(final EntityManager em) {

        JpaQueryStream<Person> stream = JpaQueryProvider.select(em, Person.class);

        stream.where().ge(Person::getBirthDate, getDateValue()).and();
        stream.like(Person::getLastName, "lname");
        stream.and().brackets(
                part -> part.like(Person::getFirstName, "fname").or().equal(Person::getPersonalNo, PERSONAL_NO1));
        stream.or().isNull(Person::getInfo);
        stream.orderByDesc(Person::getLastName).orderBy(Person::getBirthDate);
        stream.orderBy(Person::getPersonId);
        stream.in(Person::getPersonId, IDENTIFIERS);

        return stream;
    }

    @Test
    public void queryStreamTest() {

        EntityManager em = emf.createEntityManager();
        try {
            JpaQueryStream<Person> stream = createQueryStream(em);
            String sql = stream.sql();
            System.out.println();
            System.out.println("===============FIRST=TEST===================");
            System.out.println(sql);
            System.out.println("============================================");
            System.out.println();
        } catch (Throwable ex) {
            ex.printStackTrace();
        } finally {
            em.close();
        }
    }
}
