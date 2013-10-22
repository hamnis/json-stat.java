package net.hamnaberg.jsonstat.util;

import java.util.ArrayList;
import java.util.List;

public final class CollectionUtils {
    private CollectionUtils() {}
    public static <A> String join(Iterable<A> combination, String sep) {
        StringBuilder sb = new StringBuilder();
        for (A s : combination) {
            if (sb.length() > 0) {
                sb.append(sep);
            }
            sb.append(s);
        }
        return sb.toString();
    }

    public static <A> List<List<A>> product(List<List<A>> lists)
    {
        List<List<A>> results = new ArrayList<>();
        product(results, lists, 0, new ArrayList<A>(lists.size()));
        return results;
    }

    private static <A> void product(List<List<A>> results, List<List<A>> lists, int depth, List<A> current)
    {
        for (int i = 0; i < lists.get(depth).size(); i++)
        {
            current.add(depth, lists.get(depth).get(i));
            if (depth < lists.size() - 1)
                product(results, lists, depth + 1, current);
            else{
                results.add(current);
            }
        }
    }

}
