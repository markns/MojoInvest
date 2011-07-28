package com.mns.alphaposition.server.engine.execution;

import com.mns.alphaposition.shared.engine.model.Fund;
import org.joda.time.LocalDate;

import java.math.BigDecimal;

public interface Executor {

    void buy(Fund fund, LocalDate date, BigDecimal allocation);

    void sellAll(Fund fund, LocalDate date);

    BigDecimal getTransactionCost();
}
