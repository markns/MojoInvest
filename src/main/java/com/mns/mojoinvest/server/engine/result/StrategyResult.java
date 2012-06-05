package com.mns.mojoinvest.server.engine.result;

import com.google.visualization.datasource.datatable.DataTable;
import com.mns.mojoinvest.server.engine.transaction.Transaction;
import com.mns.mojoinvest.server.serialization.DataTableSerializer;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.List;

public class StrategyResult {

    private DataTable dataTableDto;
    private List<Transaction> transactions;

    public StrategyResult(DataTable dataTableDto, List<Transaction> transactions) {
        this.dataTableDto = dataTableDto;
        this.transactions = transactions;
    }

    public StrategyResult() {
    }

    @JsonSerialize(using = DataTableSerializer.class)
    public DataTable getDataTable() {
        return dataTableDto;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }
}
