package com.mns.alphaposition.server.engine.execution;

import com.mns.alphaposition.server.engine.model.Fund;
import org.joda.time.LocalDate;

import java.math.BigDecimal;

public interface Executor {
    BigDecimal getTransactionCost();

    void buy(Fund fund, LocalDate date, BigDecimal allocation);

    void sellAll(Fund fund, LocalDate date);
}
