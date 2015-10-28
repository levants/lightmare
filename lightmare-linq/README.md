lightmare-linq
=========

JPA-QL query generator using lambda expressions

# Overview
==========

Lightmare is lightweight library to run ejb project in any type application without any ejb containers

# Get it!
=========

Get lightmare from maven central repository

    <dependency>
      <groupId>com.github.levants</groupId>
      <artifactId>lightmare-linq</artifactId>
      <version>0.1.3-SNAPSHOT</version>
    </dependency>
    
or download it from [Central Maven repository](https://oss.sonatype.org/content/repositories/snapshots/com/github/levants/lightmare/)

# Use it!
=========

Usage strts with parsing eny directory, jar or ear file to find and register stateless session beans (with scannotation http://scannotation.sourceforge.net/)
and create and cache EntityManagerFactory for each @PersistenceContext annotated field in bean:
```java
  List<Person> persons = QueryStream.select(em, Person.class).where().eq(entity::getPersonalNo, personalNo)
		    .and().like(entity::getLastName, "fname").and().startsWith(entity::getFirstName, "lname").or()
		    .moreOrEq(entity::getBirthDate, date).toList();
```	
