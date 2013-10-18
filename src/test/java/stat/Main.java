package stat;

import stat.parser.JacksonStatParser;

import java.util.HashMap;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: erlhamna
 * Date: 16.10.13
 * Time: 14:34
 * To change this template use File | Settings | File Templates.
 */
public class Main {

    public static void main(String[] args) throws Exception {
        Stat stat = new JacksonStatParser().parse(Main.class.getResourceAsStream("/oecd-canada.json"));
        for (Dataset set: stat.getDataset("canada")) {
            System.out.println("size = " + set.size());
            Data value = set.getValue(44);
            System.out.println("value = " + value);
            Data value2 = set.getValue(new int[]{0, 0, 7, 0, 2});
            System.out.println("value2 = " + value2);
            Data value3 = set.getValue(new HashMap<String, String>(){
                {
                    put("country", "CA");
                    put("year", "2012");
                    put("concept", "POP");
                    put("age", "34");
                    put("sex", "F");
                }
            });
            System.out.println("value3 = " + value3);
            Data value4 = set.getValue(new HashMap<String, String>(){
                {
                    put("concept", "POP");
                    put("age", "34");
                    put("sex", "F");
                }
            });
            System.out.println("value4 = " + value4);

            List<Data> value5 = set.getSlice(new HashMap<String, String>(){
                {
                    put("concept", "POP");
                    put("age", "34");
              //      put("sex", "F");
                }
            });
            System.out.println("value5 = " + value5);
            List<Data> value6 = set.getSlice(new HashMap<String, String>(){
                {
                    put("concept", "PERCENT");
                    put("age", "34");
              //      put("sex", "F");
                }
            });
            System.out.println("value6 = " + value6);
        }
    }
}
