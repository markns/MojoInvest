package com.mns.mojoinvest.shared.dto;

import com.google.gwt.user.client.rpc.IsSerializable;
import com.google.gwt.visualization.client.AbstractDataTable;
import com.google.gwt.visualization.client.DataTable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class DataTableDto implements IsSerializable {

    private ArrayList<Column> cols;

    private ArrayList<ArrayList<AbstractValue>> rows;

    public DataTableDto() {
        cols = new ArrayList<Column>();
        rows = new ArrayList<ArrayList<AbstractValue>>();
    }

    public void addColumn(Column column) {
        cols.add(column);
    }

    public void addRow(ArrayList<AbstractValue> values) {
        rows.add(values);
    }

    public void addRow(AbstractValue... values) {
        addRow(new ArrayList<AbstractValue>(Arrays.asList(values)));
    }

    public List<Column> getCols() {
        return cols;
//        return null;
    }

    public List<ArrayList<AbstractValue>> getRows() {
        return rows;
    }

    /*
     * Nb. This method must only be executed client side
     */
    public DataTable getDataTable() {

        DataTable data = DataTable.create();

        for (Column col : cols) {
            data.addColumn(col.type, col.label, col.id);
        }

        data.addRows(rows.size());

        for (int i = 0; i < rows.size(); i++) {
            for (int j = 0; j < rows.get(i).size(); j++) {

                AbstractValue value = rows.get(i).get(j);
                if (value instanceof DateValue) {
                    data.setValue(i, j, (Date) value.value);
                } else if (value instanceof DoubleValue) {
                    data.setValue(i, j, (Double) value.value);
//                    data.setProperty();
                } else if (value instanceof IntegerValue) {
                    data.setValue(i, j, (Integer) value.value);
                }

            }
        }

        return data;
    }


    public static class Column implements IsSerializable {

        private AbstractDataTable.ColumnType type;
        private String label;
        private String id;

        public Column(AbstractDataTable.ColumnType type, String label, String id) {
            this.type = type;
            this.label = label;
            this.id = id;
        }

        public Column() {
        }
    }

    public static abstract class AbstractValue<T> implements IsSerializable {

        private T value;
        private String formattedValue;

        protected AbstractValue(T value, String formattedValue) {
            this.value = value;
            this.formattedValue = formattedValue;
        }

        public AbstractValue(T value) {
            this.value = value;
        }

        protected AbstractValue() {
        }

        public T getValue() {
            return value;
        }

        public String getFormattedValue() {
            return formattedValue;
        }
    }

    public static class DateValue extends AbstractValue<Date> {

        public DateValue(Date date, String formattedValue) {
            super(date, formattedValue);
        }

        public DateValue(Date date) {
            super(date);
        }


        public DateValue() {
        }
    }

    public static class DoubleValue extends AbstractValue<Double> {

        public DoubleValue(Double number, String formattedValue) {
            super(number, formattedValue);
        }

        public DoubleValue(Double number) {
            super(number);
        }

        public DoubleValue() {
        }
    }

    public static class IntegerValue extends AbstractValue<Integer> {

        public IntegerValue(Integer number, String formattedValue) {
            super(number, formattedValue);
        }

        public IntegerValue(Integer number) {
            super(number);
        }

        public IntegerValue() {
        }
    }

}
