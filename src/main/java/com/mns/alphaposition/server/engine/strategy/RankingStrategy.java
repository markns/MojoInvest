package com.mns.alphaposition.server.engine.strategy;

import com.mns.alphaposition.server.engine.model.Fund;
import org.joda.time.LocalDate;

import java.util.List;

public interface RankingStrategy<T> {

    List<Fund> rank(LocalDate rebalanceDate, List<Fund> funds, T params);

}
