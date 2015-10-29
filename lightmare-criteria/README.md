lightmare-linq
=========

JPA-QL query generator using lambda expressions

# Overview
==========

Lightmare-linq is lightweight library to construct and run JPA queries by method reference instead of raw String composition

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

Query may be composed by org.lightmare.linq.query.QueryProvider select method call:
```java
  List<Person> persons = QueryProvider.select(em, Person.class).where()
  			.eq(Person::getPersonalNo, personalNo)
		    .and().like(Person::getLastName, "fname")
		    .and().startsWith(Person::getFirstName, "lname")
		    .or().moreOrEq(Person::getBirthDate, date).toList();
```	

Query also can be linked dynamically:
```java
  QueryStream<Person> stream = QueryProvider.select(em, Person.class);
  			 stream.where().eq(Person::getPersonalNo, personalNo);
		     stream.and().like(Person::getLastName, "fname")
		     stream.and().startsWith(Person::getFirstName, "lname")
		     stream.or().moreOrEq(Person::getBirthDate, date);
  List<Person> persons = stream.toList();
```	  
or if one has entity instance

```java
  Person entity = ...
  QueryStream<Person> stream = QueryProvider.select(em, Person.class);
  			 stream.where().eq(entity::getPersonalNo, personalNo);
		     stream.and().like(entity::getLastName, "fname")
		     stream.and().startsWith(entity::getFirstName, "lname")
		     stream.or().moreOrEq(entity::getBirthDate, date);
  List<Person> persons = stream.toList();
```	
enjoy :)