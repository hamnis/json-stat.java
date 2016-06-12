package net.hamnaberg.jsonstat.v2;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import net.hamnaberg.jsonstat.JsonStatModule;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by hadrien on 07/06/16.
 */
public class DatasetTest {

    private ObjectMapper mapper = new ObjectMapper();

    @BeforeMethod
    public void setUp() throws Exception {
        // TODO
        mapper.registerModule(new JsonStatModule());
        mapper.registerModule(new Jdk8Module().configureAbsentsAsNulls(true));
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.registerModule(new GuavaModule().configureAbsentsAsNulls(false));
    }

    @Test(
            expectedExceptions = IllegalArgumentException.class,
            expectedExceptionsMessageRegExp = "size and property sizes do not match"
    )
    public void testInvalidSize() throws Exception {

        Dataset dataset = new Dataset(ImmutableSet.of(
                "a", "b", "c"
        ), ImmutableList.of(
                1, 2, 3, 4
        ));

    }

    @Test()
    public void testConstructor() throws Exception {

        Dataset dataset = new Dataset(ImmutableSet.of(
                "a", "b", "c"
        ), ImmutableList.of(
                1, 2, 3
        ));

    }

    @Test
    public void testBuilder() throws Exception {

        Dataset.Builder builder = Dataset.create().withLabel("");
        builder.withSource("");
        builder.updatedAt(Instant.now());

        Dimension.Builder dimension = Dimension.create("year")
                .withRole(Dimension.Roles.TIME)
                .withCategories(ImmutableList.of("2003", "2004", "2005"));
        builder.withDimension(dimension);


        builder.withDimension(Dimension.create("month").withRole(Dimension.Roles.TIME)
                .withCategories(ImmutableList.of("may", "june", "july")));

        builder.withDimension(Dimension.create("week").withTimeRole()
                .withLabels(ImmutableList.of("30", "31", "32")));

        builder.withDimension(Dimension.create("population")
                .withIndexedLabels(ImmutableMap.of(
                        "A", "active population",
                        "E", "employment",
                        "U", "unemployment",
                        "I", "inactive population",
                        "T", "population 15 years old and over"
                )));

        // TODO: addDimension("name") returning Dimension.Builder? Super fluent?
        // TODO: How to ensure valid data with the geo builder? Add the type first and extend builders?
        //builder.withDimension(Dimension.create("location")
        //        .withGeoRole());

        builder.withDimension(Dimension.create("arrival").withMetricRole());
        builder.withDimension(Dimension.create("departure").withRole(Dimension.Roles.METRIC));

        // Supplier.
        List<Number> collect = Lists.cartesianProduct(
                ImmutableList.of("2003", "2004", "2005"),
                ImmutableList.of("may", "june", "july"),
                ImmutableList.of("30", "31", "32"),
                ImmutableMap.of(
                        "A", "active population",
                        "E", "employment",
                        "U", "unemployment",
                        "I", "inactive population",
                        "T", "population 15 years old and over"
                ).keySet().asList()
        ).stream().map(dimensions -> {
            System.out.println(dimensions + " -> " + dimensions.hashCode() );
            return dimensions.hashCode();
        }).collect(Collectors.toList());
        System.out.println(collect);

        builder.withValues(collect);

        mapper.writerWithDefaultPrettyPrinter().writeValue(System.out, builder.build());

    }

    @Test
    public void testSerialize() throws Exception {

        // TODO: Find a way to implement the child that is fluent.

        Dataset dataset = new Dataset(ImmutableSet.of(
                "a", "b"
        ), ImmutableList.of(
                1, 1
        ));

        mapper.writeValue(System.out, dataset);

    }

    @Test
    public void testDeserialize() throws Exception {

    }

}