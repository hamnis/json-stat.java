package net.hamnaberg.jsonstat.v2;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import net.hamnaberg.jsonstat.JsonStatModule;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

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
    public void testSerialize() throws Exception {

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