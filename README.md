lightmare
=========

Embeddable ejb container (works for stateless session beans)

# Overview
==========

Lightmare is lightweight library to run ejb project in any type application without any ejb containers


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
  builder.setPersistenceProperties(Map<String, String> properties);
```
to set persistence properties at runtime to reate EntityManager with this overriden properties

```java
  builder.setUnitName(String unitName);
```

to scan @UnitName annotated classes with "unitName" parameter in passed files and load them

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
