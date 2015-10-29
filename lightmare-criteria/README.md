lightmare-criteria
=========

JPA-QL query generator using lambda expressions

# Overview
==========

Lightmare-criteria is lightweight library to construct and run JPA queries by method reference instead of raw String composition.

Name implies from similarity with JPA criteria query style.

# Get it!
=========

Get lightmare-criteria from maven central repository

    <dependency>
      <groupId>com.github.levants</groupId>
      <artifactId>lightmare-criteria</artifactId>
      <version>0.1.3-SNAPSHOT</version>
    </dependency>
    
or download it from [Central Maven repository](https://oss.sonatype.org/content/repositories/snapshots/com/github/levants/lightmare/)

# Use it!
=========

Query can be composed by org.lightmare.criteria.query.QueryProvider.select method call:
```java
  List<Person> persons = QueryProvider.select(em, Person.class).where()
  			.eq(Person::getPrivatNumber, "10010010011")
		    .and().like(Person::getLastName, "fname")
		    .and().startsWith(Person::getFirstName, "lname")
		    .or().moreOrEq(Person::getBirthDate, new Date()).toList(); 
```	
or for bulk update (delete) by org.lightmare.criteria.query.QueryProvider.update (delete) method call:

```java
  int rows = QueryProvider.update(em, Person.class)
  			.set(Person::getMiddName, "newMiddName")
  			.set(Person::getFirstName, "newFName")
  			.where()
  			.eq(Person::getPrivatNumber, "10010010011")
		    .and().like(Person::getLastName, "fname")
		    .and().startsWith(Person::getFirstName, "lname")
		    .or().moreOrEq(Person::getBirthDate, new Date()).execute(); 
```	
Query also can be linked dynamically:
```java
  QueryStream<Person> stream = QueryProvider.select(em, Person.class);
  			 stream.where().eq(Person::getPrivatNumber, "10010010011");
		     stream.and().like(Person::getLastName, "fname");
		     stream.and().startsWith(Person::getFirstName, "lname");
		     stream.or().moreOrEq(Person::getBirthDate, Calendar.getInstance());
  List<Person> persons = stream.toList();
```	  
or if one has entity instance:
```java
  Person entity = ...
  QueryStream<Person> stream = QueryProvider.select(em, Person.class);
  			 stream.where().eq(entity::getPrivatNumber, "10010010011");
		     stream.and().like(entity::getLastName, "fname")
		     stream.and().startsWith(entity::getFirstName, "lname")
		     stream.or().moreOrEq(entity::getBirthDate, Calendar.getInstance());
  List<Person> persons = stream.toList();
```	
enjoy :)