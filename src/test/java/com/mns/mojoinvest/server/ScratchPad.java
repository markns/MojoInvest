package com.mns.mojoinvest.server;

import com.google.common.base.Splitter;
import org.junit.Test;

import java.util.Iterator;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScratchPad {

    @Test
    public void regex() {
        String a = "\"EWA\"";

        Pattern pattern = Pattern.compile("^\"(.+)\"");
        Matcher matcher = pattern.matcher(a);
        matcher.find();
        System.out.println(matcher.group(1));
    }

    Pattern pattern = Pattern.compile("^\"(\\w+)\",\"(\\d{4}-\\d{2}-\\d{2})\"");

    static Splitter splitter = Splitter.on("|");

    @Test
    public void regexVsSplitSpeed() {
        String a = "\"EWA\",\"2011-01-01\",\"blah\"";

        long time = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            Matcher matcher = pattern.matcher(a);
            matcher.find();
            matcher.group(2);
        }
        System.out.println("Regex took " + (System.currentTimeMillis() - time));

        String b = "2011-01-01|\"EWA\",\"2011-01-01\",\"blah\"";

        time = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            Iterator<String> s = splitter.split(b).iterator();
            s.next();
        }
        System.out.println("Split took " + (System.currentTimeMillis() - time));

        time = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            String h = b.split("\\|")[0];

        }
        System.out.println("jplit took " + (System.currentTimeMillis() - time));

        Scanner scanner = new Scanner(b).useDelimiter("\\|");
        time = System.currentTimeMillis();
//        for (int i = 0; i < 10000; i++) {
        System.out.println(scanner.next());

//        }
        System.out.println("scann took " + (System.currentTimeMillis() - time));

    }

    @Test
    public void testTrim() {
        String fdskj = "fdsd\nfdsf\n";
        System.out.println(fdskj);
        System.out.println(fdskj.trim());
    }


}
