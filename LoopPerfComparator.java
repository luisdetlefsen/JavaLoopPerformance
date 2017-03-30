package loopperfcomparator;


import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 *
 * @author Luis Detlefsen <lgdetlef@gmail.com>
 */
public class LoopPerfComparator {

    static Map<String, Long> perfTest(Integer threshold) {

        Map<String, Long> results = new HashMap<>();

        List<Integer> intArray = new ArrayList<>();
        IntStream.rangeClosed(0, threshold).forEach(x -> intArray.add(x));

        Instant start = Instant.now();
        Integer sumTotal = 0;

        
        for(Integer i : intArray) {
            sumTotal += i;
        }
        results.put("1#for(Integer i:intArray)|" + sumTotal, Duration.between(start, Instant.now()).toMillis());

        sumTotal = 0;
        start = Instant.now();
        for (int l = 0; l < intArray.size(); l++) {
            sumTotal += l;
        }
        results.put("2#for (Integer l = 0; l < intArray.size(); l++)|" + sumTotal, Duration.between(start, Instant.now()).toMillis());

        start = Instant.now();
        sumTotal = intArray.stream().mapToInt(xx -> xx).sum();
        results.put("3#intArray.stream().mapToInt(xx->xx).sum()|" + sumTotal, Duration.between(start, Instant.now()).toMillis());

        start = Instant.now();
        sumTotal = 0;
        Iterator<Integer> iterator = intArray.iterator();
        while (iterator.hasNext()) {
            sumTotal += iterator.next();
        }
        results.put("4#Iterator<Integer> iterator = intArray.iterator()|" + sumTotal, Duration.between(start, Instant.now()).toMillis());

        start = Instant.now();
        sumTotal = intArray.stream().reduce((x1, x2) -> x1 + x2).get();
        results.put("5#intArray.stream().reduce((x1, x2) -> x1+x2).get()|" + sumTotal, Duration.between(start, Instant.now()).toMillis());

        start = Instant.now();
        sumTotal = intArray.stream().parallel().reduce(0, (x1, x2) -> x1 + x2);
        results.put("6#intArray.stream().parallel().reduce(0,(x1,x2)->x1+x2))|" + sumTotal, Duration.between(start, Instant.now()).toMillis());

        start = Instant.now();
        sumTotal = intArray.stream().parallel().reduce((x1, x2) -> x1 + x2).get();
        results.put("7#intArray.stream().parallel().reduce((x1,x2)->x1+x2)).get()|" + sumTotal, Duration.between(start, Instant.now()).toMillis());

        start = Instant.now();
        sumTotal = intArray.stream().collect(Collectors.summingInt(Integer::valueOf));
        results.put("8#intintArray.stream().collect(Collectors.summingInt(Integer::valueOf))|" + sumTotal, Duration.between(start, Instant.now()).toMillis());

        LongAdder la = new LongAdder();
        start = Instant.now();
        intArray.parallelStream().forEach(la::add);
        results.put("9#LongAdder la = new LongAdder();intArray.parallelStream().forEach(la::add)|" + la.intValue(), Duration.between(start, Instant.now()).toMillis());

        return results;
    }

    public static void printTableRow(String s) {
        int i0 = s.indexOf('#');
        int i1 = s.indexOf('|');
        int i2 = s.indexOf('@');        
        System.out.println("<tr><td>" + s.substring(i0 + 1, i1) + "</td><td>" + s.substring(i2 + 1) + "ms</td></tr>");
    }

    public static void main(String args[]) {
        int threshold = 50_000_000;
        
        
        Map<String, Long> r = perfTest(threshold);
        
        String title = "<h2>" + threshold + " iterations</h2>";
        System.out.println("<html><body>" + title + "<table><tr><th>Loop</th><th>Time taken</th></tr></thead>");

        r.keySet().stream().sorted().forEach(k -> printTableRow(k + "@" + r.get(k).toString()));
        System.out.println("</table></body></html>");                
    }
}
