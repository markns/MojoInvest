package com.mns.mojoinvest.server.pipeline;

import com.google.appengine.tools.pipeline.Job0;
import com.google.appengine.tools.pipeline.Value;

public class ImmediateReturnJob extends Job0<String> {

    @Override
    public Value<String> run() {
        return immediate("");
    }
}
