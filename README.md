lightmare
=========

Embeddable ejb container (works for stateless session beans) with JPA / Hibernate support

# Overview
==========

Lightmare is lightweight library to run ejb project in any type application without any ejb containers

# Get it!
=========

Get lightmare from maven central repository

    <dependency>
      <groupId>com.github.levants</groupId>
      <artifactId>lightmare</artifactId>
      <version>0.0.55-SNAPSHOT</version>
    </dependency>
    
or download it from [Central Maven repository](https://oss.sonatype.org/content/repositories/snapshots/com/github/levants/lightmare/)

# Use it!
=========

Usage strts with parsing eny directory, jar or ear file to find and register stateless session beans (with scannotation http://scannotation.sourceforge.net/)
and create and cache EntityManagerFactory for each @PersistenceContext annotated field in bean:
```java
  MetaCreator.Builder builder = new MetaCreator.Builder();
  MetaCreator metaCreator = builder.build()
  metaCreator.scanForBeans(files);
```	
where files are String[] or URL[] array of ear, jar or any ejb containd directory pathes or this files (File[]) 

Also builder has more properties which can be set before call build method


```java
  builder.setPersistenceProperties(properties);
```
to set persistence properties at runtime to reate EntityManager with this overriden properties

```java
  builder.setUnitName(name);
```

to scan @UnitName annotated classes with "name" parameter in passed files and load them

```java
  builder.setScanForEntities(true);
```
to scan @Entity annotated classes in passed files and load them

```java
  builder.setXmlFromJar(true);
```
to find and parse persistence.xml from passed jar file

```java
  builder.setDataSourcePath(path);
```
path to standalone.xml file with dataSource tag (jboss 7) to parse and use as NON_JTA_DATASOURCE

```java
  builder.setPersXmlPath(path);
```

path to persistence.xml to use for ORM initialization

```java
  builder.setScanArchives(true);
```
additional propertie to hibernate to continue scannin in depth for @Entity annotated classes default is true exept setPersXmlPath is called before 

```java
  builder.setLibraryPath(path);
```
path to folder with jar libraries to load in parent (server) class loader in addition

Complete example how to use MetaCreator's Builder is here:

```java
  MetaCreator.Builder builder = new MetaCreator.Builder();
  builder.setPersistenceProperties(properties)
        .setUnitName(name)
        .setScanForEntities(true)
        .setXmlFromJar(true)
        .setDataSourcePath(path)
        .setPersXmlPath(path)
        .setScanArchives(true)
        .setLibraryPath(path);
  MetaCreator metaCreator = builder.build();
  metaCreator.scanForBeans(files);
```
To call ejb stateless session bean in non standard way:

```java
  EjbConnector connector = new EjbConnector();
  connector.connectToBean("FooBean",
		    FooBeanRemote.class);
```
or bean can be reached as if it is deployed on JBoss server:

```java
  Context context = new InitialContext(someJndiProperties);
  FooBeanRemote fooBean = (FooBeanRemote)context.lookup("ejb:fooModule//FooBean!FooBeanRemote")
```
