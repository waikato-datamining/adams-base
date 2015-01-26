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
 * TwitterConnection.java
 * Copyright (C) 2011-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.standalone;

import twitter4j.TwitterFactory;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import adams.core.base.BasePassword;
import adams.core.net.TwitterHelper;

/**
 <!-- globalinfo-start -->
 * Provides access to various twitter services.<br/>
 * For your own twitter account, you can obtain consumer key and access token for ADAMS (= application trying to access twitter) here:<br/>
 *   https:&#47;&#47;dev.twitter.com&#47;apps&#47;<br/>
 * And make sure that ADAMS has READ and WRITER access.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
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
 * &nbsp;&nbsp;&nbsp;default: TwitterConnection
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
 * <pre>-consumer-key &lt;java.lang.String&gt; (property: consumerKey)
 * &nbsp;&nbsp;&nbsp;The twitter consumer key to use for connecting; leave empty for anonymous 
 * &nbsp;&nbsp;&nbsp;access.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-consumer-secret &lt;adams.core.base.BasePassword&gt; (property: consumerSecret)
 * &nbsp;&nbsp;&nbsp;The consumer secret of the twitter application to use for connecting.
 * &nbsp;&nbsp;&nbsp;default: {}
 * </pre>
 * 
 * <pre>-access-token &lt;java.lang.String&gt; (property: accessToken)
 * &nbsp;&nbsp;&nbsp;The twitter access token to use for connecting.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-acces-token-secret &lt;adams.core.base.BasePassword&gt; (property: accessTokenSecret)
 * &nbsp;&nbsp;&nbsp;The access token secret of the twitter application to use for connecting.
 * &nbsp;&nbsp;&nbsp;default: {}
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class TwitterConnection
  extends AbstractStandalone {

  /** for serialization. */
  private static final long serialVersionUID = -1959430342987913960L;

  /** the twitter consumer key. */
  protected String m_ConsumerKey;

  /** the twitter consumer secret. */
  protected BasePassword m_ConsumerSecret;

  /** the twitter access token. */
  protected String m_AccessToken;

  /** the twitter access token secret. */
  protected BasePassword m_AccessTokenSecret;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Provides access to various twitter services.\n"
	+ "For your own twitter account, you can obtain consumer key and "
	+ "access token for ADAMS (= application trying to access twitter) here:\n"
	+ "  https://dev.twitter.com/apps/\n"
	+ "And make sure that ADAMS has READ and WRITER access.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "consumer-key", "consumerKey",
	    TwitterHelper.getConsumerKey());

    m_OptionManager.add(
	    "consumer-secret", "consumerSecret",
	    TwitterHelper.getConsumerSecret());

    m_OptionManager.add(
	    "access-token", "accessToken",
	    TwitterHelper.getAccessToken());

    m_OptionManager.add(
	    "acces-token-secret", "accessTokenSecret",
	    TwitterHelper.getAccessTokenSecret());
  }

  /**
   * Sets the twitter consumer key to use.
   *
   * @param value	the key
   */
  public void setConsumerKey(String value) {
    m_ConsumerKey = value;
    reset();
  }

  /**
   * Returns the twitter consumer key to use.
   *
   * @return		the key
   */
  public String getConsumerKey() {
    return m_ConsumerKey;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String consumerKeyTipText() {
    return "The twitter consumer key to use for connecting; leave empty for anonymous access.";
  }

  /**
   * Sets the twitter consumer secret to use.
   *
   * @param value	the secret
   */
  public void setConsumerSecret(BasePassword value) {
    m_ConsumerSecret = value;
    reset();
  }

  /**
   * Returns the twitter consumer secret to use.
   *
   * @return		the secret
   */
  public BasePassword getConsumerSecret() {
    return m_ConsumerSecret;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String consumerSecretTipText() {
    return "The consumer secret of the twitter application to use for connecting.";
  }

  /**
   * Sets the twitter acess token to use.
   *
   * @param value	the token
   */
  public void setAccessToken(String value) {
    m_AccessToken = value;
    reset();
  }

  /**
   * Returns the twitter acess token to use.
   *
   * @return		the token
   */
  public String getAccessToken() {
    return m_AccessToken;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String accessTokenTipText() {
    return "The twitter access token to use for connecting.";
  }

  /**
   * Sets the twitter access token secret to use.
   *
   * @param value	the secret
   */
  public void setAccessTokenSecret(BasePassword value) {
    m_AccessTokenSecret = value;
    reset();
  }

  /**
   * Returns the twitter access token secret to use.
   *
   * @return		the secret
   */
  public BasePassword getAccessTokenSecret() {
    return m_AccessTokenSecret;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String accessTokenSecretTipText() {
    return "The access token secret of the twitter application to use for connecting.";
  }

  /**
   * Builds a configuration to use for the twitter4j factories.
   *
   * @return		the configuration
   */
  public Configuration getConfiguration() {
    ConfigurationBuilder	cb;
    
    cb = new ConfigurationBuilder();
    cb.setOAuthConsumerKey(getConsumerKey());
    cb.setOAuthConsumerSecret(getConsumerSecret().getValue());
    cb.setOAuthAccessToken(getAccessToken());
    cb.setOAuthAccessTokenSecret(getAccessTokenSecret().getValue());
    
    return cb.build();
  }
  
  /**
   * Returns the twitter connection object.
   *
   * @return		the connection
   */
  public twitter4j.Twitter getTwitterConnection() {
    return new TwitterFactory(getConfiguration()).getInstance();
  }

  /**
   * Returns the twitter stream connection object.
   *
   * @return		the stream connection
   */
  public twitter4j.TwitterStream getTwitterStreamConnection() {
    return new TwitterStreamFactory(getConfiguration()).getInstance();
  }

  /**
   * Executes the flow item.
   *
   * @return		always null
   */
  @Override
  protected String doExecute() {
    return null;
  }
}
