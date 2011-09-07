package com.mns.alphaposition.server.engine.strategy;

import com.mns.alphaposition.server.engine.model.Fund;
import com.mns.alphaposition.server.engine.params.RankingStrategyParams;
import org.joda.time.LocalDate;

import java.util.List;

public interface RankingStrategy<P extends RankingStrategyParams> {

    List<Fund> rank(LocalDate rebalanceDate, List<Fund> funds, P params);

}
