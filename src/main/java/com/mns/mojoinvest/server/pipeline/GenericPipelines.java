package com.mns.mojoinvest.server.pipeline;

import com.google.appengine.tools.pipeline.Job1;
import com.google.appengine.tools.pipeline.Value;
import com.google.common.base.Joiner;

import java.util.List;

public class GenericPipelines {


    public static class MergeListJob extends Job1<String, List<String>> {

        @Override
        public Value<String> run(List<String> strings) {
            return immediate(Joiner.on("\n").join(strings));
        }

    }
}
