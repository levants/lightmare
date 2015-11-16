lightmare-criteria
=========

JPA-QL query generator using lambda expressions

# Overview
==========

Lightmare-criteria is lightweight library to construct and run JPA queries by method reference, more LINQ style, 
instead of raw String composition.

Name implies from similarity with JPA criteria query API.

# Get it!
=========

Get lightmare-criteria from maven central repository

```xml
    <dependency>
      <groupId>com.github.levants</groupId>
      <artifactId>lightmare-criteria</artifactId>
      <version>0.1.4</version>
    </dependency>
```    
or download it from [Central Maven repository](https://oss.sonatype.org/content/repositories/snapshots/com/github/levants/lightmare/)

# Use it!
=========

# Query API
Query can be composed by org.lightmare.criteria.query.QueryProvider.select or org.lightmare.criteria.query.QueryProvider.query 
method call:
```java
  List<Person> persons = QueryProvider.select(em, Person.class).where()
  			.equal(Person::getPrivatNumber, "10010010011")
		    .and().like(Person::getLastName, "lname")
		    .and().startsWith(Person::getFirstName, "fname")
		    .and().equal(Person::getFillName, Person::getLastName)
		    .or().ge(Person::getBirthDate, new Date()).
		    .orderBy(Person::getLastName)
		    .orderByDesc(Person::getBirthDate).toList(); 
```
for brackets there is two ways first is by opening and closing brackets with "openBracket" and "closeBracket" methods call respectively
and second is with "brackets" method call
```java
  Person person = QueryProvider.select(em, Person.class)
  			.where()
  			.equal(Person::getPrivatNumber, "10010010011")
		    .and().like(Person::getLastName, "lname").and()
		    .brackets(c -> c.startsWith(Person::getFirstName, "fname")
		    	.		  .or()
		    			  .ge(Person::getBirthDate, new Date()))
		    .and().startsWith(Person::getFirstName, "fname")
		    .firstOrDefault(new Person()); 
```
operators "where" and "and" are optional, "where" will be set before first query parameter 
and "and" is default boolean operator if other is not called

# Embedded entities

For embedded entity there is method "embedded" with embedded getter method and appropriated query
```java
  Person person = QueryProvider.select(em, Person.class)
  			.set(Person::getMiddName, "newMiddName")
  			.set(Person::getFirstName, "newFName")
  			.where()
  			.equal(Person::getPrivatNumber, "10010010011")
		    .and().like(Person::getLastName, "lname").and()
		    .embedded(Person::getInfo, c -> .equal(PersonInfo::getNote, "This is note")
		    					   .equal(PersonInfo::getCardNumber, Person::getPrivatNumber))
		    .and().startsWith(Person::getFirstName, "fname")
		    .firstOrDefault(new Person()); 
```
# Bulk update and delete

For bulk update there is org.lightmare.criteria.query.QueryProvider.update method:
```java
  int rows = QueryProvider.update(em, Person.class)
  			.set(Person::getMiddName, "newMiddName")
  			.set(Person::getFirstName, "newFName")
  			.where()
  			.equal(Person::getPrivatNumber, "10010010011")
		    .and().like(Person::getLastName, "lname")
		    .openBracket()
		    .and().startsWith(Person::getFirstName, "fname")
		    .or().ge(Person::getBirthDate, new Date())
		    .closeBracket().execute(); 
```
and for bulk delete org.lightmare.criteria.query.QueryProvider.delete method:
```java
  int rows = QueryProvider.delete(em, Person.class)
  			.set(Person::getMiddName, "newMiddName")
  			.set(Person::getFirstName, "newFName")
  			.where()
  			.equal(Person::getPrivatNumber, "10010010011")
		    .and().like(Person::getLastName, "lname")
		    .execute(); 
```	
# Dynamic linking

Query can be linked dynamically:
```java
  QueryStream<Person> stream = QueryProvider.select(em, Person.class);
  			 stream.where().equal(Person::getPrivatNumber, "10010010011");
  			 if(lnameParamter != null){
		     	stream.and().like(Person::getLastName, lnameParamter);
		     }
		     if(fnameParameter != null){
		     	stream.and().startsWith(Person::getFirstName, fnameParameter);
		     }
		     stream.or().greaterOrEquals(Person::getBirthDate, Calendar.getInstance());
  List<Person> persons = stream.toList();
```
# Subqueries

Implementations of sub queries are by calling exits or in functions:
```java
  List<Person> persons = QueryProvider.query(em, Person.class).where()
  			.equal(Person::getPrivatNumber, "10010010011")
		    .and().like(Person::getLastName, "lname")
		    .and().exists(Phone.class, c -> c.where()
		    					   .in(Phone::getOperatorId, Arrays.asList(1L, 2L, 3L))
		                           .and()
		                           .equal(Phone::getPhoneNumber, Person::getPhoneNumber))
		    .orderBy(Person::getLastName)
		    .orderByDesc(Person::getBirthDate).toList(); 
```
# Joins

Joins are similar to sub queries. There are three types of joins:

inner join:
```java
  List<Person> persons = QueryProvider.query(em, Person.class).where()
  			.equal(Person::getPrivatNumber, "10010010011")
		    .and().like(Person::getLastName, "lname")
		    .and().join(Person::getPhones, c -> c.where()
		    						   .in(Phone::getOperatorId, Arrays.asList(1L, 2L, 3L))
		                               .and()
		                               .equal(Phone::getPhoneNumber, Person::getPhoneNumber))
		    .orderBy(Person::getLastName)
		    .orderByDesc(Person::getBirthDate).toList(); 
```
lefts join:
```java
  List<Person> persons = QueryProvider.query(em, Person.class).where()
  			.equal(Person::getPrivatNumber, "10010010011")
		    .and().like(Person::getLastName, "lname")
		    .and().leftJoin(Person::getPhones, c -> c.where()
		    						   .in(Phone::getOperatorId, Arrays.asList(1L, 2L, 3L))
		                               .and()
		                               .equal(Phone::getPhoneNumber, Person::getPhoneNumber))
		    .orderBy(Person::getLastName)
		    .orderByDesc(Person::getBirthDate).toList(); 
```
and fetch join:
```java
  List<Person> persons = QueryProvider.query(em, Person.class).where()
  			.equal(Person::getPrivatNumber, "10010010011")
		    .and().like(Person::getLastName, "lname")
		    .and().fetchJoin(Person::getPhones, c -> c.where()
		    						   .in(Phone::getOperatorId, Arrays.asList(1L, 2L, 3L))
		                               .and()
		                               .equal(Phone::getPhoneNumber, Person::getPhoneNumber))
		    .orderBy(Person::getLastName)
		    .orderByDesc(Person::getBirthDate).toList(); 
```
enjoy :)