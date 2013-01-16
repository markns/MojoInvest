package com.mns.mojoinvest.server.pipeline.quote;

import com.google.appengine.tools.pipeline.Job2;
import com.google.appengine.tools.pipeline.Value;
import com.google.common.base.Joiner;
import com.googlecode.objectify.ObjectifyFactory;
import com.mns.mojoinvest.server.engine.model.BlobstoreEntryRecord;
import com.mns.mojoinvest.server.engine.model.Fund;
import com.mns.mojoinvest.server.engine.model.Quote;
import com.mns.mojoinvest.server.engine.model.dao.FundDao;
import com.mns.mojoinvest.server.engine.model.dao.QuoteDao;
import com.mns.mojoinvest.server.engine.model.dao.QuoteUnavailableException;
import com.mns.mojoinvest.server.engine.model.dao.blobstore.BlobstoreQuoteDao;
import com.mns.mojoinvest.server.engine.model.dao.objectify.MyTypeConverters;
import com.mns.mojoinvest.server.engine.model.dao.objectify.ObjectifyEntryRecordDao;
import com.mns.mojoinvest.server.engine.model.dao.objectify.ObjectifyFundDao;
import com.mns.mojoinvest.server.util.FundUtils;
import com.mns.mojoinvest.server.util.QuoteUtils;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class QuoteFetcherBatchJob extends Job2<String, List<Fund>, LocalDate> {

    private static final Logger log = Logger.getLogger(QuoteFetcherBatchJob.class.getName());

    private transient GoogleQuoteFetcher fetcher;

    private transient QuoteDao quoteDao;

    @Override
    public Value<String> run(List<Fund> funds, LocalDate date) {

        fetcher = new GoogleQuoteFetcher();

        ObjectifyFactory factory = new ObjectifyFactory();
        factory.register(BlobstoreEntryRecord.class);
        factory.register(Fund.class);
        factory.getConversions().add(new MyTypeConverters());
        quoteDao = new BlobstoreQuoteDao(new ObjectifyEntryRecordDao(factory));
        FundDao fundDao = new ObjectifyFundDao(factory);

        List<Value<String>> messages = new ArrayList<Value<String>>();

        addMessageAndLog(messages, "Attempting to retrieve quotes for " + funds.size() +
                " funds: " + FundUtils.symbols(funds));

        for (Fund fund : funds) {
            try {
                List<Quote> quotes = downloadQuotes(date, fund);
                log.fine("Received " + quotes.size() + " quotes for " + fund.getSymbol() + ". Latest quote is " + quotes.get(0));
                if (testForChanges(quotes, messages)) {
                    quotes = downloadAllHistoricQuotes(fund, date);
                }

                addMessageAndLog(messages, "Saving " + quotes.size() + " " +
                        fund.getSymbol() + " quotes");
                if (quotes.size() > 0) {
                    quoteDao.put(quotes);
                    fund.setLatestQuoteDate(quotes.get(0).getDate());
                    fundDao.put(fund);
                }
            } catch (QuoteFetcherException e) {
                addMessageAndLog(messages, e.getMessage() + " - " + e.getCause());
            }
        }

        return immediate(Joiner.on("\n").join(messages));
    }

    private void addMessageAndLog(List<Value<String>> messages, String message) {
        messages.add(immediate(message));
        log.info(message);
    }

    private List<Quote> downloadQuotes(LocalDate date, Fund fund) throws QuoteFetcherException {
        LocalDate fromDate = fund.getLatestQuoteDate();
        if (fromDate != null) {
            fromDate = fromDate.minusMonths(1);
        } else {
            fromDate = fund.getInceptionDate();
        }
        List<Quote> quotes = fetcher.run(fund, fromDate, date);
        QuoteUtils.sortByDateDesc(quotes);
        return quotes;
    }

    private List<Quote> downloadAllHistoricQuotes(Fund fund, LocalDate date) throws QuoteFetcherException {
        List<Quote> quotes = fetcher.run(fund, fund.getInceptionDate(), date);
        QuoteUtils.sortByDateDesc(quotes);
        return quotes;
    }

    private boolean testForChanges(List<Quote> quotes, List<Value<String>> messages) {

        Map<LocalDate, Quote> existingMap = new HashMap<LocalDate, Quote>();
        for (Quote quote : quotes) {
            try {
                Quote existing = quoteDao.get(quote.getSymbol(), quote.getDate());
                existingMap.put(existing.getDate(), existing);
            } catch (QuoteUnavailableException e) { /**/ }
        }

        for (Quote quote : quotes) {
            Quote existing = existingMap.get(quote.getDate());
            if (existing == null) continue;
            if (!quote.equals(existing)) {
                String message;
                messages.add(immediate(message = "New " + quote.getSymbol() + " quotes don't match existing quotes, redownloading\n" +
                        "Existing: " + existing.toDescriptiveString() +
                        "\n     New: " + quote.toDescriptiveString()));
                log.warning(message);
                return true;
            }
        }

        return false;
    }


}
