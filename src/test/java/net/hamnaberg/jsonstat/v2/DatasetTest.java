package net.hamnaberg.jsonstat.v2;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import net.hamnaberg.jsonstat.JsonStatModule;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Instant;

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

        Dimension.Builder dimension = Dimension.create("dimension")
                .withRole(Dimension.Roles.TIME)
                .withCategories(ImmutableList.of("2003", "2004", "2005"));
        builder.withDimension(dimension);


        builder.withDimension(Dimension.create("firstTime").withRole(Dimension.Roles.TIME)
                .withCategories(ImmutableList.of("2003", "2004", "2005")));

        builder.withDimension(Dimension.create("secondTime").withTimeRole()
                .withLabels(ImmutableList.of("2003", "2004", "2005")));

        builder.withDimension(Dimension.create("thirdTime")
                .withIndexedLabels(ImmutableMap.of(
                        "A", "active population",
                        "E", "employment",
                        "U", "unemployment",
                        "I", "inactive population",
                        "T", "population 15 years old and over"
                )));

        // TODO: addDimension("name") returning Dimension.Builder? Super fluent?
        builder.withDimension(Dimension.create("Location").withGeoRole());
        builder.withDimension(Dimension.create("arrival").withMetricRole());
        builder.withDimension(Dimension.create("departure").withRole(Dimension.Roles.METRIC));

        builder.build();

    }

    @Test
    public void testSerialize() throws Exception {

        // TODO: Find a way to implement the child that is fluent.

        Dataset dataset = new Dataset(ImmutableSet.of(
                "a", "b"
        ), ImmutableList.of(
                1, 1
        ));

        //mapper.writeValue(System.out, dataset);

    }

    @Test
    public void testDeserialize() throws Exception {

    }

}