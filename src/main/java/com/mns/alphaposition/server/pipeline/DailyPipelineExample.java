package com.mns.alphaposition.server.pipeline;

import com.google.appengine.tools.pipeline.FutureValue;
import com.google.appengine.tools.pipeline.Job1;
import com.google.appengine.tools.pipeline.Value;
import com.mns.alphaposition.server.engine.model.Quote;

import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class DailyPipelineExample {

    public static class DailyPipeline extends Job1<String, String> {

        @Override
        public Value<String> run(String text) {

            String[] symbols = {"BHH", "DGP"};
            for (String symbol : symbols) {
                FutureValue<Quote> quote = futureCall(new QuoteFetcherJob(), immediate(symbol));
            }
            //return futureCall(new CountCombinerJob(), futureList(countsForEachWord));
            return immediate("done");
        }
    }

    public static class SingleWordCounterJob extends Job1<SortedMap<Character, Integer>, String> {
        @Override
        public Value<SortedMap<Character, Integer>> run(String word) {
            return immediate(countLetters(word));
        }
    }

    public static SortedMap<Character, Integer> countLetters(String text) {
        SortedMap<Character, Integer> charMap = new TreeMap<Character, Integer>();
        for (char c : text.toCharArray()) {
            incrementCount(c, 1, charMap);
        }
        return charMap;
    }

    public static class CountCombinerJob extends
            Job1<SortedMap<Character, Integer>, List<SortedMap<Character, Integer>>> {
        @Override
        public Value<SortedMap<Character, Integer>> run(List<SortedMap<Character, Integer>> listOfMaps) {
            SortedMap<Character, Integer> totalMap = new TreeMap<Character, Integer>();
            for (SortedMap<Character, Integer> charMap : listOfMaps) {
                for (Map.Entry<Character, Integer> pair : charMap.entrySet()) {
                    incrementCount(pair.getKey(), pair.getValue(), totalMap);
                }
            }
            return immediate(totalMap);
        }
    }

    private static void incrementCount(char c, int increment, Map<Character, Integer> charMap) {
        Integer countInteger = charMap.get(c);
        int count = (null == countInteger ? 0 : countInteger) + increment;
        charMap.put(c, count);
    }


}
