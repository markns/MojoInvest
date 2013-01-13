package com.mns.mojoinvest.server.pipeline.quote;

import com.google.appengine.tools.pipeline.Job2;
import com.google.appengine.tools.pipeline.Value;
import com.google.common.base.Joiner;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyFactory;
import com.mns.mojoinvest.server.engine.model.BlobstoreEntryRecord;
import com.mns.mojoinvest.server.engine.model.Fund;
import com.mns.mojoinvest.server.engine.model.Quote;
import com.mns.mojoinvest.server.engine.model.dao.QuoteDao;
import com.mns.mojoinvest.server.engine.model.dao.blobstore.BlobstoreQuoteDao;
import com.mns.mojoinvest.server.engine.model.dao.objectify.MyTypeConverters;
import com.mns.mojoinvest.server.engine.model.dao.objectify.ObjectifyEntryRecordDao;
import com.mns.mojoinvest.server.util.QuoteUtils;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class YahooQuoteFetcherBatchJob extends Job2<String, List<Fund>, LocalDate> {

    private static final Logger log = Logger.getLogger(YahooQuoteFetcherBatchJob.class.getName());

    private transient YahooQuoteFetcher fetcher;

    @Override
    public Value<String> run(List<Fund> funds, LocalDate date) {

        fetcher = new YahooQuoteFetcher();

        ObjectifyFactory factory = new ObjectifyFactory();
        factory.register(BlobstoreEntryRecord.class);
        factory.getConversions().add(new MyTypeConverters());
        QuoteDao dao = new BlobstoreQuoteDao(new ObjectifyEntryRecordDao(factory));

        List<Value<String>> messages = new ArrayList<Value<String>>();

        List<Quote> allQuotes = new ArrayList<Quote>();
        addMessageAndLog(messages, "Attempting to retrieve quotes for " + funds.size() + " funds: " + funds);

        for (Fund fund : funds) {
            try {
                List<Quote> quotes = downloadLastWeekOfQuotes(date, fund);
                testReceivedTodaysQuote(date, fund, quotes);

                boolean changed = testForChanges(quotes.subList(1, quotes.size()), messages);
                if (changed) {
                    quotes = downloadAllHistoricQuotes(fund, date);
                }

                allQuotes.addAll(quotes);
            } catch (QuoteFetcherException e) {
                addMessageAndLog(messages, e.getMessage() + " - " + e.getCause());
            }
        }
        addMessageAndLog(messages, "Saving " + allQuotes.size() + " quotes");
        dao.put(allQuotes);

        return immediate(Joiner.on("\n").join(messages));
    }

    private void addMessageAndLog(List<Value<String>> messages, String message) {
        messages.add(immediate(message));
        log.info(message);
    }

    private List<Quote> downloadLastWeekOfQuotes(LocalDate date, Fund fund) throws QuoteFetcherException {
        List<Quote> quotes = fetcher.run(fund, date.minusWeeks(1), date);
        QuoteUtils.sortByDateDesc(quotes);
        return quotes;
    }

    private void testReceivedTodaysQuote(LocalDate date, Fund fund, List<Quote> quotes) throws QuoteFetcherException {
        Quote todays = quotes.get(0);
        if (todays.getDate().equals(date))
            throw new QuoteFetcherException("Didn't get a " + fund + " quote for " + date);
    }

    private boolean testForChanges(List<Quote> quotes, List<Value<String>> messages) {
        List<Key<Quote>> keys = new ArrayList<Key<Quote>>(quotes.size());
        for (Quote quote : quotes) {
            quote.getKey();
            keys.add(quote.getKey());
        }                                              //TODO: needs reimplementing
        List<Quote> existing = new ArrayList<Quote>(); //dao.get(keys));
        QuoteUtils.sortByDateDesc(existing);
        String message;
        if (quotes.size() != existing.size()) {
            messages.add(immediate(message = "Number of existing " + quotes.get(0).getSymbol() +
                    " quotes doesn't match newly retrieved number, redownloading."));
            log.warning(message);
            return true;
        }

        for (int i = 0; i < quotes.size(); i++) {
            //TODO: We might only want to test adjClose here to prevent excessive downloading
            if (!quotes.get(i).equals(existing.get(i))) {
                messages.add(immediate(message = "New " + quotes.get(i).getSymbol() + " quotes don't match existing quotes\n" +
                        "Existing: " + existing.get(i).toDescriptiveString() +
                        "\n     New: " + quotes.get(i).toDescriptiveString() + ", redownloading"));
                log.warning(message);
                return true;
            }
        }
        return false;
    }

    private List<Quote> downloadAllHistoricQuotes(Fund fund, LocalDate date) throws QuoteFetcherException {
        return fetcher.run(fund, fund.getInceptionDate(), date);
    }


}
