package com.mns.mojoinvest.server.engine.result;

import com.google.visualization.datasource.datatable.DataTable;
import com.mns.mojoinvest.server.engine.transaction.Transaction;
import com.mns.mojoinvest.server.serialization.DataTableSerializer;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.List;
import java.util.Map;

public class StrategyResult {

    private DataTable dataTable;
    private List<Transaction> transactions;
    private Map<String, Object> stats;

    public StrategyResult(DataTable dataTable, List<Transaction> transactions, Map<String, Object> stats) {
        this.dataTable = dataTable;
        this.transactions = transactions;
        this.stats = stats;
    }

    public StrategyResult() {
    }

    @JsonSerialize(using = DataTableSerializer.class)
    public DataTable getDataTable() {
        return dataTable;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public Map<String, Object> getStats() {
        return stats;
    }
}
