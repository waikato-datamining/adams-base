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

/**
 * FixedTweets.java
 * Copyright (C) 2013-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.io.input;

import twitter4j.Status;
import adams.core.QuickInfoHelper;
import adams.core.base.BaseString;
import adams.core.net.TwitterHelper;
import adams.data.twitter.SimulatedStatus;

/**
 <!-- globalinfo-start -->
 * Dummy replayer that just outputs tweets from manually set status texts.<br>
 * Extracts hashtags ('#...') and usermentions ('&#64;...') automatically.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-tweet &lt;adams.core.base.BaseString&gt; [-tweet ...] (property: tweets)
 * &nbsp;&nbsp;&nbsp;The status texts to generate the tweets from.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class FixedTweets
  extends AbstractTweetReplay {

  /** for serialization. */
  private static final long serialVersionUID = -3432288686771377759L;
  
  /** the texts for the tweets. */
  protected BaseString[] m_Tweets;
  
  /** the current index. */
  protected int m_Index;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Dummy replayer that just outputs tweets from manually set status texts.\n"
	+ "Extracts hashtags ('#...') and usermentions ('@...') automatically.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "tweet", "tweets",
	    new BaseString[]{});
  }

  /**
   * Resets the object.
   */
  @Override
  protected void reset() {
    super.reset();
    
    m_Index = 0;
  }
  
  /**
   * Sets the status text to generate the tweets from.
   *
   * @param value	the status texts
   */
  public void setTweets(BaseString[] value) {
    m_Tweets = value;
    reset();
  }

  /**
   * Returns the status text to generate the tweets from.
   *
   * @return		the status texts
   */
  public BaseString[] getTweets() {
    return m_Tweets;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String tweetsTipText() {
    return "The status texts to generate the tweets from.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "tweets", m_Tweets.length + " tweet" + (m_Tweets.length == 1 ? "" : "s"));
  }
  
  /**
   * Performs the actual setup.
   * 
   * @return		null if everything OK, otherwise error message
   */
  @Override
  protected String doConfigure() {
    m_Index = 0;
    return null;
  }

  /**
   * Checks whether there is another tweet available.
   * 
   * @return		true if tweet available
   */
  @Override
  public boolean hasNext() {
    return (m_Index < m_Tweets.length);
  }

  /**
   * Returns the next tweet.
   * 
   * @return		the next tweet, null if none available
   */
  @Override
  public Status next() {
    SimulatedStatus	result;
    
    if (m_Index >= m_Tweets.length)
      return null;
    
    result = new SimulatedStatus();
    result.setText(m_Tweets[m_Index].getValue());
    result.setHashtagEntities(TwitterHelper.extractHashtags(m_Tweets[m_Index].getValue()));
    result.setUserMentionEntities(TwitterHelper.extractUserMentions(m_Tweets[m_Index].getValue()));
    
    m_Index++;
    
    return result;
  }
}
