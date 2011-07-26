package com.mns.alphaposition.server.engine.strategy;

import com.mns.alphaposition.shared.engine.model.Fund;
import com.mns.alphaposition.shared.params.RankingStrategyParams;
import org.joda.time.LocalDate;

import java.util.List;

public interface RankingStrategy<P extends RankingStrategyParams> {

    List<Fund> rank(LocalDate rebalanceDate, List<Fund> funds, P params);

}
