/*
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * TwitterQuery.java
 * Copyright (C) 2010-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.source;

import java.util.ArrayList;
import java.util.List;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import adams.core.QuickInfoHelper;
import adams.core.net.TwitterHelper;
import adams.flow.core.Token;

/**
 <!-- globalinfo-start -->
 * Outputs tweet messages.<br/>
 * For more information on twitter queries, see the following web page:<br/>
 * http:&#47;&#47;search.twitter.com&#47;operators
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br/>
 * - generates:<br/>
 * &nbsp;&nbsp;&nbsp;twitter4j.Status<br/>
 * <p/>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 * 
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to 
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 * 
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: TwitterQuery
 * </pre>
 * 
 * <pre>-annotation &lt;adams.core.base.BaseText&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-skip (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded 
 * &nbsp;&nbsp;&nbsp;as it is.
 * </pre>
 * 
 * <pre>-stop-flow-on-error (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * </pre>
 * 
 * <pre>-query &lt;java.lang.String&gt; (property: query)
 * &nbsp;&nbsp;&nbsp;The query for obtaining the tweets.
 * &nbsp;&nbsp;&nbsp;default: search term
 * </pre>
 * 
 * <pre>-results-per-page &lt;int&gt; (property: resultsPerPage)
 * &nbsp;&nbsp;&nbsp;The number of results per page when querying twitter.
 * &nbsp;&nbsp;&nbsp;default: 20
 * &nbsp;&nbsp;&nbsp;minimum: 10
 * &nbsp;&nbsp;&nbsp;maximum: 100
 * </pre>
 * 
 * <pre>-max-tweets &lt;int&gt; (property: maxTweets)
 * &nbsp;&nbsp;&nbsp;The maximum number of tweets to output.
 * &nbsp;&nbsp;&nbsp;default: 100
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class TwitterQuery
  extends AbstractSource {

  /** for serialization. */
  private static final long serialVersionUID = -1959430342987913960L;

  /** the query. */
  protected String m_Query;

  /** the number of results per page. */
  protected int m_ResultsPerPage;

  /** the maximum number of tweets to output. */
  protected int m_MaxTweets;

  /** for storing the messages. */
  protected List<Status> m_Queue;

  /** for accessing the twitter API. */
  protected twitter4j.Twitter m_Twitter;

  /** for querying the twitter API. */
  protected Query m_TwitterQuery;

  /** the result returned by twitter. */
  protected QueryResult	m_QueryResult;

  /** the current count of tweets. */
  protected int	m_Count;

  /** the ID of the last tweet returned. */
  protected Long m_LastId;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Outputs tweet messages.\n"
      + "For more information on twitter queries, see the following web page:\n"
      + "http://search.twitter.com/operators";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "query", "query",
	    "search term");

    m_OptionManager.add(
	    "results-per-page", "resultsPerPage",
	    20, 10, 100);

    m_OptionManager.add(
	    "max-tweets", "maxTweets",
	    100, 1, null);
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Queue = new ArrayList<Status>();
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_Queue.clear();
    m_LastId       = null;
    m_Twitter      = null;
    m_TwitterQuery = null;
    m_QueryResult  = null;
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "query", (m_Query != null ? "\"" + m_Query + "\"" : "<none>"), "query: ");
    result += QuickInfoHelper.toString(this, "maxTweets", m_MaxTweets, ", max tweets: ");

    return result;
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the classes
   */
  public Class[] generates() {
    return new Class[]{Status.class};
  }

  /**
   * Sets the twitter query to use.
   *
   * @param value	the query
   */
  public void setQuery(String value) {
    m_Query = value;
    reset();
  }

  /**
   * Returns the twitter query to use.
   *
   * @return		the query
   */
  public String getQuery() {
    return m_Query;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String queryTipText() {
    return "The query for obtaining the tweets.";
  }

  /**
   * Sets the number of results per page (for each query).
   *
   * @param value	the maximum number
   */
  public void setResultsPerPage(int value) {
    m_ResultsPerPage = value;
    reset();
  }

  /**
   * Returns the number of results per page (for each query).
   *
   * @return		the maximum number
   */
  public int getResultsPerPage() {
    return m_ResultsPerPage;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String resultsPerPageTipText() {
    return "The number of results per page when querying twitter.";
  }

  /**
   * Sets the maximum number of tweets to output.
   *
   * @param value	the maximum number
   */
  public void setMaxTweets(int value) {
    m_MaxTweets = value;
    reset();
  }

  /**
   * Returns the maximum number of tweets to output.
   *
   * @return		the maximum number
   */
  public int getMaxTweets() {
    return m_MaxTweets;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String maxTweetsTipText() {
    return "The maximum number of tweets to output.";
  }

  /**
   * Obtains a bunch of tweets.
   *
   * @throws Exception	if twitter query fails
   */
  protected void performQuery() throws Exception {
    long		maxID;
    List<Status>	tweets;

    if (isLoggingEnabled())
      getLogger().info("--> Querying twitter...");

    if (m_LastId != null) {
      maxID = m_LastId - 1;
      m_TwitterQuery.setMaxId(maxID);
      if (isLoggingEnabled())
	getLogger().info("max ID: " + maxID);
    }

    if (isLoggingEnabled())
      getLogger().info("Sending query...");
    synchronized(m_Twitter) {
      m_QueryResult = m_Twitter.search(m_TwitterQuery);
    }
    if (isLoggingEnabled())
      getLogger().info("Query completed in " + m_QueryResult.getCompletedIn() + "s");

    tweets = m_QueryResult.getTweets();
    for (Status tweet: tweets) {
      if (isLoggingEnabled())
	getLogger().fine("tweet ID: " + tweet.getId());
      m_Queue.add(tweet);
      m_Count++;
      if (m_Count >= m_MaxTweets) {
	m_Twitter = null;
	if (isLoggingEnabled())
	  getLogger().info("count >= max msg");
	break;
      }
      m_LastId = tweet.getId();
    }

    if (isLoggingEnabled()) {
      getLogger().info("# of tweets: " + m_Queue.size());
      getLogger().info("Querying twitter finished!");
    }
  }

  /**
   * Executes the flow item.
   *
   * @return		always null
   */
  @Override
  protected String doExecute() {
    String	result;

    result   = null;
    m_Count  = 0;
    m_LastId = null;
    m_Twitter = TwitterHelper.getTwitterConnection(this);
    m_TwitterQuery = new Query(m_Query);
    m_TwitterQuery.setCount(m_ResultsPerPage);

    try {
      performQuery();
    }
    catch (Exception e) {
      result = handleException("Failed to query twitter:", e);
    }

    return result;
  }

  /**
   * Returns the generated token.
   *
   * @return		the generated token
   */
  public Token output() {
    Token	result;

    result = null;

    if ((m_Queue.size() == 0) && (m_Twitter != null)) {
      try {
	performQuery();
      }
      catch (Exception e) {
	handleException("Failed to query twitter:", e);
      }
    }

    if (m_Queue.size() > 0) {
      result = new Token(m_Queue.get(0));
      m_Queue.remove(0);
    }
    else {
      m_Twitter = null;
    }

    return result;
  }

  /**
   * Checks whether there is pending output to be collected after
   * executing the flow item.
   *
   * @return		true if there is pending output
   */
  public boolean hasPendingOutput() {
    return (m_Twitter != null) || (m_Queue.size() > 0);
  }

  /**
   * Stops the execution. No message set.
   */
  @Override
  public void stopExecution() {
    if (m_Twitter != null) {
      synchronized(m_Twitter) {
	m_Twitter = null;
      }
    }
    if (m_Queue != null)
      m_Queue.clear();

    super.stopExecution();
  }

  /**
   * Cleans up after the execution has finished.
   */
  @Override
  public void wrapUp() {
    super.wrapUp();

    m_Queue.clear();
    m_Twitter      = null;
    m_TwitterQuery = null;
    m_QueryResult  = null;
  }
}
