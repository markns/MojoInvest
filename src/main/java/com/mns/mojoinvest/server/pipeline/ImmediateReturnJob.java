package com.mns.mojoinvest.server.pipeline;

import com.google.appengine.tools.pipeline.Job1;
import com.google.appengine.tools.pipeline.Value;

public class ImmediateReturnJob extends Job1<String, String> {

    @Override
    public Value<String> run(String message) {
        return immediate(message);
    }
}
