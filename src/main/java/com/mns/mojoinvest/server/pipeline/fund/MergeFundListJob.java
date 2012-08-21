package com.mns.mojoinvest.server.pipeline.fund;

import com.google.appengine.tools.pipeline.Job1;
import com.google.appengine.tools.pipeline.Value;
import com.mns.mojoinvest.server.engine.model.Fund;

import java.util.ArrayList;
import java.util.List;

class MergeFundListJob extends Job1<List<Fund>, List<List<Fund>>> {

    @Override
    public Value<List<Fund>> run(List<List<Fund>> lists) {
        List<Fund> funds = new ArrayList<Fund>();
        for (List<Fund> list : lists) {
            funds.addAll(list);
        }
        return immediate(funds);
    }
}
