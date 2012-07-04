package com.mns.mojoinvest.server;

import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScratchPad {

    @Test
    public void go() {
        String a = "\"EWA\"";

        Pattern pattern = Pattern.compile("^\"(.+)\"");
        Matcher matcher = pattern.matcher(a);
        matcher.find();
        System.out.println(matcher.group(1));
    }

}
