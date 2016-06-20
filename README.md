Implementation of JSON Stat in Java - http://json-stat.org


Status
======

[![Build Status](https://travis-ci.org/statisticsnorway/json-stat.java.svg?branch=master)](https://travis-ci.org/statisticsnorway/json-stat.java)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/no.ssb/json-stat-java/badge.svg)](https://maven-badges.herokuapp.com/maven-central/no.ssb/json-stat-java)
[![Javadoc](https://javadoc-emblem.rhcloud.com/doc/no.ssb/json-stat-java/badge.svg)](http://www.javadoc.io/doc/no.ssb/json-stat-java)

Usage
=====

Add json stat dependency into your project

````java
<dependency>
            <groupId>no.ssb</groupId>
            <artifactId>json-stat</artifactId>
            <version>0.1.0</version>
</dependency>
````

Create a new json stat data set

````java 

Dataset.Builder builder = Dataset.create().withLabel("")
        .withDimension(Dimension.create("year")
                .withRole(Dimension.Roles.TIME)
                .withIndexedLabels(ImmutableMap.of("2003", "2003", "2004", "2004", "2005", "2005")))

        .withDimension(Dimension.create("month").withRole(Dimension.Roles.TIME)
                .withIndexedLabels(ImmutableMap.of("may", "may", "june", "june", "july", "july")))

        .withDimension(Dimension.create("week").withTimeRole()
                .withIndexedLabels(ImmutableMap.of("30", "30", "31", "31", "32", "32")))

        .withDimension(Dimension.create("population")
                .withIndexedLabels(ImmutableMap.of(
                        "A", "active population",
                        "E", "employment",
                        "U", "unemployment",
                        "I", "inactive population",
                        "T", "population 15 years old and over"
                )))
        .withDimension(Dimension.create("amount").withMetricRole()
                .withIndexedLabels(ImmutableMap.of("millions", "millions")))

        .withDimension(Dimension.create("percent").withMetricRole()
                .withIndexedLabels(ImmutableMap.of("%", "percent")));


Dataset dataset = builder.withMapper(
        dimensions -> newArrayList(
                dimensions.hashCode(),
                dimensions.hashCode())
);


````