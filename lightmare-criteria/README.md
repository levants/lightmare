lightmare-criteria
=========

JPA-QL query generator using lambda expressions

# Overview
==========

Lightmare-criteria is lightweight library to construct and run JPA queries by method reference instead of raw String composition.

Name implies from similarity with JPA criteria query style.

# Get it!
=========
```xml
Get lightmare-criteria from maven central repository

    <dependency>
      <groupId>com.github.levants</groupId>
      <artifactId>lightmare-criteria</artifactId>
      <version>0.1.3-SNAPSHOT</version>
    </dependency>
```    
or download it from [Central Maven repository](https://oss.sonatype.org/content/repositories/snapshots/com/github/levants/lightmare/)

# Use it!
=========

# Query API
Query can be composed by org.lightmare.criteria.query.QueryProvider.select method call:
```java
  List<Person> persons = QueryProvider.query(em, Person.class).where()
  			.equals(Person::getPrivatNumber, "10010010011")
		    .and().like(Person::getLastName, "lname")
		    .and().startsWith(Person::getFirstName, "fname")
		    .or().moreOrEqual(Person::getBirthDate, new Date()).
		    .orderBy(Person::getLastName)
		    .orderByDesc(Person::getBirthDate).toList(); 
```	
or for bulk update by org.lightmare.criteria.query.QueryProvider.update method call:
```java
  int rows = QueryProvider.update(em, Person.class)
  			.set(Person::getMiddName, "newMiddName")
  			.set(Person::getFirstName, "newFName")
  			.where()
  			.equal(Person::getPrivatNumber, "10010010011")
		    .and().like(Person::getLastName, "lname")
		    .openBracket()
		    .and().startsWith(Person::getFirstName, "fname")
		    .or().moreOrEqual(Person::getBirthDate, new Date())
		    .closeBracket().execute(); 
```
for brackets there is two ways first is by opening and closing brackets with "openBracket" and "closeBracket" methods call respectively
and second is with "brackets" method call
```java
  int rows = QueryProvider.update(em, Person.class)
  			.set(Person::getMiddName, "newMiddName")
  			.set(Person::getFirstName, "newFName")
  			.where()
  			.equal(Person::getPrivatNumber, "10010010011")
		    .and().like(Person::getLastName, "lname").and()
		    .brackets(c -> c.startsWith(Person::getFirstName, "fname")
		    	.		  .or()
		    			  .moreOrEqual(Person::getBirthDate, new Date()))
		    .and().startsWith(Person::getFirstName, "fname")
		    .execute(); 
```
or for bulk delete by org.lightmare.criteria.query.QueryProvider.delete method call:
```java
  int rows = QueryProvider.delete(em, Person.class)
  			.set(Person::getMiddName, "newMiddName")
  			.set(Person::getFirstName, "newFName")
  			.where()
  			.equal(Person::getPrivatNumber, "10010010011")
		    .and().like(Person::getLastName, "lname")
		    .execute(); 
```	
Query also can be linked dynamically:
```java
  QueryStream<Person> stream = QueryProvider.select(em, Person.class);
  			 stream.where().equal(Person::getPrivatNumber, "10010010011");
		     stream.and().like(Person::getLastName, "lname");
		     stream.and().startsWith(Person::getFirstName, "fname");
		     stream.or().moreOrEquals(Person::getBirthDate, Calendar.getInstance());
  List<Person> persons = stream.toList();
```
# Subqueries

Implementations of sub queries are by calling exits or in functions:
```java
  List<Person> persons = QueryProvider.query(em, Person.class).where()
  			.equals(Person::getPrivatNumber, "10010010011")
		    .and().like(Person::getLastName, "lname")
		    .and().exists(Phone.class, c -> c.where()
		    						   .in(Phone::getOperatorId, Arrays.asList(1L, 2L, 3L))
		                               .and()
		                               .equals(Phone::getPhoneNumber, Person::getPhoneNumber))
		    .orderBy(Person::getLastName)
		    .orderByDesc(Person::getBirthDate).toList(); 
```
# Joins

Joins are similar to sub queries. There are three types of joins:
inner join:
```java
  List<Person> persons = QueryProvider.query(em, Person.class).where()
  			.equals(Person::getPrivatNumber, "10010010011")
		    .and().like(Person::getLastName, "lname")
		    .and().join(Person::getPhones, c -> c.where()
		    						   .in(Phone::getOperatorId, Arrays.asList(1L, 2L, 3L))
		                               .and()
		                               .equals(Phone::getPhoneNumber, Person::getPhoneNumber))
		    .orderBy(Person::getLastName)
		    .orderByDesc(Person::getBirthDate).toList(); 
```
lefts join:
```java
  List<Person> persons = QueryProvider.query(em, Person.class).where()
  			.equals(Person::getPrivatNumber, "10010010011")
		    .and().like(Person::getLastName, "lname")
		    .and().leftJoin(Person::getPhones, c -> c.where()
		    						   .in(Phone::getOperatorId, Arrays.asList(1L, 2L, 3L))
		                               .and()
		                               .equals(Phone::getPhoneNumber, Person::getPhoneNumber))
		    .orderBy(Person::getLastName)
		    .orderByDesc(Person::getBirthDate).toList(); 
```
and fetch join:
```java
  List<Person> persons = QueryProvider.query(em, Person.class).where()
  			.equals(Person::getPrivatNumber, "10010010011")
		    .and().like(Person::getLastName, "lname")
		    .and().fetchJoin(Person::getPhones, c -> c.where()
		    						   .in(Phone::getOperatorId, Arrays.asList(1L, 2L, 3L))
		                               .and()
		                               .equals(Phone::getPhoneNumber, Person::getPhoneNumber))
		    .orderBy(Person::getLastName)
		    .orderByDesc(Person::getBirthDate).toList(); 
```
enjoy :)