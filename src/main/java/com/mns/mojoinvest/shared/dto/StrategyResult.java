package com.mns.mojoinvest.shared.dto;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.util.List;

public class StrategyResult implements IsSerializable {

    private List<TransactionDto> transactions;

    public StrategyResult(List<TransactionDto> transactions) {
        this.transactions = transactions;
    }

    public StrategyResult() {
    }

    public List<TransactionDto> getTransactions() {
        return transactions;
    }
}
