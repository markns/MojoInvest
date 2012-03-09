package com.mns.mojoinvest.shared.dto;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.util.ArrayList;
import java.util.List;

public class StrategyResult implements IsSerializable {

    private DataTableDto dataTableDto;
    private ArrayList<TransactionDto> transactions;

    public StrategyResult(DataTableDto dataTableDto, ArrayList<TransactionDto> transactions) {
        this.dataTableDto = dataTableDto;
        this.transactions = transactions;
    }

    public StrategyResult() {
    }

    public DataTableDto getDataTableDto() {
        return dataTableDto;
    }

    public List<TransactionDto> getTransactions() {
        return transactions;
    }
}
