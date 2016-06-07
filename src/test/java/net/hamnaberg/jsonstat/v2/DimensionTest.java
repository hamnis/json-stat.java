package net.hamnaberg.jsonstat.v2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Created by hadrien on 07/06/16.
 */
public class DimensionTest {

    private ObjectMapper mapper = new ObjectMapper();

    @BeforeMethod
    public void setUp() throws Exception {
        // TODO
        mapper.registerModule(new SimpleModule());
    }

    @Test
    public void testSerialize() throws Exception {

    }

    @Test
    public void testDeserialize() throws Exception {

    }

}