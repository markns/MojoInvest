package com.mns.mojoinvest.server.pipeline.quote;

import com.mns.mojoinvest.server.engine.model.Quote;
import jxl.*;
import org.joda.time.LocalDate;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

public class ISharesExcelParser {

    private static final Logger log = Logger.getLogger(ISharesExcelParser.class.getName());

    public static List<Quote> parse(Workbook workbook) {
        List<Quote> quotes = new ArrayList<Quote>();
        Sheet sheet = workbook.getSheet(0);
        String symbol = parseSymbol(sheet);
        for (int column = 1; column < sheet.getColumns(); column += 5) {

            for (int row = 3; row < sheet.getRows(); row++) {
                LocalDate date = parseDate(sheet, row);
                BigDecimal index = parseBigDecimal(sheet, column, row);
                BigDecimal nav = parseBigDecimal(sheet, column + 1, row);
                BigDecimal trNav = parseBigDecimal(sheet, column + 2, row);
                BigDecimal dividend = parseBigDecimal(sheet, column + 3, row);
                if (nav != null) {
                    Quote quote = new Quote(symbol, date, index, nav, trNav, dividend, false);
                    quotes.add(quote);
                }
            }
        }
        log.info("Parsed " + quotes.size() + " quotes from iShares " + workbook.getSheet(0).getName());
        return quotes;
    }

    private static String parseSymbol(Sheet sheet) {
        //Use column + 1 as column is the name of the tracked index, which can have a different symbol.
        String s = sheet.getCell(0, 0).getContents();
        return s.split(" - ")[0];
    }

    private static LocalDate parseDate(Sheet sheet, int row) {
        Date date = ((DateCell) sheet.getCell(0, row)).getDate();
        return new LocalDate(date);
    }

    private static BigDecimal parseBigDecimal(Sheet sheet, int column, int row) {
        Cell cell = sheet.getCell(column, row);
        if (cell.getType().equals(CellType.NUMBER)) {
            NumberCell numberCell = (NumberCell) cell;
            return new BigDecimal(numberCell.getValue(), MathContext.DECIMAL32);
        }
        return null;
    }
}
