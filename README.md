A Relative Strength based trading strategy for ETFs. Based on the ideas of Michael J. Carr in his book 'Smarter Investing in Any Economy: The Definitive Guide to Relative Strength Investing'

Basic architecture and flow diagram
![MojoInvest](http://i.imgur.com/Qrcwh.png)

### Server To Do
* Return logging statements rather than throw exceptions in pipeline
* Multifactor Relative Strength
* Long/short strategy
* Volatility calculator
* Correlation calculator
* Volume filter
* List of acceptable funds filter
* Spread calculator - transaction costs
* Add "buy now" to result set
* Dividends & Splits - redownload all history?  - http://stockcharts.com/charts/adjusthist.html#s=spy
* How to track events? %change? check if previous days quote changed?
* Should we change data load to Nasdaq so we can always check the dates
* Transaction history DTO (lazy load - put portfolio in memcache)
* Add leverage factor to fund data
* Correct index tracked data
* Maximum time range 10y
* Add moving average filter - don't buy if the etf is trading below it's x-month moving average
* Solutions to the drawbacks of relative strength systems (dynamic hedging, addition of uncorrelated assets) (Cambria paper)
* Relative Strength is obtained by dividing the stocks price with a benchmark index (e.g. S&P 500) over the last 4 quarters, with 40% weight for the most recent quarter and 20% for each of the remaining quarters.

### Client To Do
* http://www.humblesoftware.com/finance/index
* Display "data as of" field
* Display "buy now" data
* Remove investment amount - just index to 100
* Loading static data
* Change drop downs to sliders - get real params
* Multiple select lists for category & provider
* Sorting for category & provider
* Homepage, About and Help text and graphics
* Form validation
* Hide .html suffixes
* Firefox layout
* Date selection screen like on gmail sign up

### Ideas
* Correlation view plot
* Correlation matrix
* Volatility graphs
* Geographical heat map through time - needs to store %return along with ranking values
* Point & Figure charts

### Done
* Complete Nasdaq download
* Consolidate Nasdaq with Yahoo data inc comparison
* Holiday Calendars
* Load nasdaq, nyse data etc for comparison
* Memory usage and performance testing
* Email on pipeline completion

### Notes
* Video: Google I/O 2009 - Using the Visualization API with GWT... - YouTube
* Spinner - http://stackoverflow.com/questions/1309436/automatic-loading-indicator-when-calling-an-async-function/1311604#1311604
* Slider http://nivo.dev7studios.com/features/
* How does the event bus work in gwtp?
* http://www.codinghorror.com/blog/2007/08/yslow-yahoos-problems-are-not-your-problems.html
* http://arcbees.wordpress.com/2010/09/29/how-i-made-my-gwtappengine-application-appear-to-load-quicker/
* http://katemats.com/2012/03/04/what-every-programmer-should-know-about-seo/
* http://www.ewakened.com/
* http://www.traders.com/index.php/sac-magazine/departments/free-articles/853-2012-11-16-etfreplay
* http://www.amazon.com/Profiting-Rotation-Strategies-Turbulent-ebook/dp/B004TGST6I
* http://www.tradingmarkets.com/.site/etfs/commentary/editorial/Beware-of-Improper-Backtesting-in-ETFs-82189.cfm
* http://www.adaptrade.com/Articles/article-stats.htm
* http://quantingdutchman.wordpress.com/strategies/
* http://www.macroaxis.com/
* http://www.investopedia.com/articles/trading/05/030205.asp#axzz1pNPmEpX3
* http://wl4.wealth-lab.com/
* http://www.automated-trading-system.com/backtesting-trading-platform/