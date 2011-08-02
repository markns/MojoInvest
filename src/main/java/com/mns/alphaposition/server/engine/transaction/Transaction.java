package com.mns.alphaposition.server.engine.transaction;

import com.mns.alphaposition.shared.engine.model.Fund;
import org.joda.time.LocalDate;

import java.math.BigDecimal;

/**
 * A transaction is a collection of information about an instance of buying or selling a particular security.
 *
 * A transaction feed lists all of the transactions that have been recorded for a particular position.
 * Each transaction entry contains a transaction type (such as buy or sell), a number of units, the price,
 * and so on.
 * 
 * Every portfolio is composed of transactions each of which reference securities, of which are represented
 * by a Ticker Symbol. This is what you see in the Overview, Fundamentals, and Performance views.
 * The symbol for a security may represent a company's Common Stock, such as GOOG for Google. Or it may
 * represent different classes of shares of a company's common stock, such as BRK.A and BRK.B for
 * Berkshire Hathaway, or an Exchange-TradedFund, Mutual fund, or anything else that you can own shares
 * of. Technically, a portfolio can contain a stock Index like the Dow (.DJI) or NASDAQ (.IXIC), but that
 * is just a convenience so you can compare your securities' performance against the broader market; we
 * will not discuss stock indexes further. We start with the fundamental unit of portfolios, the transaction.
 *
 * Transactions
 *
 * A transaction is assumed to have the following values that you can set:
 *
 * Share count: the number of shares referenced by the transaction. This can be zero for a "watchlist"
 * item, that is, a stock that you added to your portfolio just to keep an eye on its performance, not
 * because you own any shares of it.
 * Cost per share: the cost to purchase each share, in the currency of the exchange on which the share is
 * traded. In the case of cash deposits or withdrawals, this is just the amount of the transaction.
 *
 * Commission: the cost to execute the transaction with a broker
 * Type: One of "Buy", "Sell", "Sell Short", "Buy to Cover", "Deposit cash", "Withdraw cash", "Dividend",
 * or "Split". Dividends and splits are computed automatically based on the traded company's history;
 * you cannot set these yourself.
 * Share count, cost per share, and commission are all optional. If you leave any blank, we treat it as zero.
 *
 * A transaction also has certain values that are set automatically based on its traded company:
 * Share price: the trading price of the share at the time computations are performed, in the currency
 * of the company's stock exchange
 * Price change: the percentage change in the trading price of the share since the market open
 * Dividend value: For dividend transactions, this is the amount of the dividend per share of the stock
 * Finally, there are values that are derived from those we have looked at already:
 * Transaction-adjusted share count: this is the number of shares in the transaction, as of the time of
 * the computation, based on the company's split history. For example, if you purchased 100 shares of
 * a stock that then split 2:1 (meaning that you receive two shares in exchange for every individual
 * share you own), this value would be 200.
 * Event-adjusted share count: this is like the transaction-adjusted share count, but instead of being
 * split-adjusted to the present, it is adjusted to the time of a relevant event, such as a split or
 * dividend issue.
 * Cash value: this value depends on the type of the transaction.
 * "Buy" or "Buy to Cover": The amount it cost to make the transaction. This is negative, since purchasing
 * depletes your bank account. cash value = -(share count * cost per share + commission)
 * "Sell" or "Sell Short": The amount you made on the sale. cash value = share count * cost per share - commission
 * "Deposit cash": cash value = cost per share
 * "Withdraw cash": cash value = -cost per share
 * Dividend: cash value = event-adjusted share count * dividend value
 *
 */
public interface Transaction {

    String getRef();

    Fund getFund();

    LocalDate getDate();

    BigDecimal getQuantity();

    BigDecimal getPrice();

    BigDecimal getCashValue();

    BigDecimal getCommission();
}
