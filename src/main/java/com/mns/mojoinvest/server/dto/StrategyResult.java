package com.mns.mojoinvest.server.dto;

import java.util.ArrayList;
import java.util.List;

public class StrategyResult {

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
