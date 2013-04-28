package com.mns.mojoinvest.server.pipeline.calculator;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.tools.pipeline.Job1;
import com.google.appengine.tools.pipeline.Value;

import java.util.List;

public class CalculatedValueMergeJob extends Job1<String, List<BlobKey>> {

    @Override
    public Value<String> run(List<BlobKey> blobKeys) {
        return null;
    }
}
