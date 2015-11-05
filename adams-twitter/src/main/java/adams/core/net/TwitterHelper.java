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
 * Twitter.java
 * Copyright (C) 2013-2015 University of Waikato, Hamilton, New Zealand
 */

package adams.core.net;

import adams.core.Properties;
import adams.core.Utils;
import adams.core.base.BasePassword;
import adams.data.twitter.SimulatedHashtagEntity;
import adams.data.twitter.SimulatedUserMentionEntity;
import adams.data.twitter.TwitterField;
import adams.env.Environment;
import adams.env.TwitterDefinition;
import adams.flow.core.AbstractActor;
import adams.flow.core.ActorUtils;
import adams.flow.standalone.TwitterConnection;
import twitter4j.HashtagEntity;
import twitter4j.Status;
import twitter4j.SymbolEntity;
import twitter4j.TwitterFactory;
import twitter4j.URLEntity;
import twitter4j.UserMentionEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

/**
 * A helper class for the twitter setup.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class TwitterHelper {

  /** the name of the props file. */
  public final static String FILENAME = "Twitter.props";

  /** the consumer key. */
  public final static String CONSUMER_KEY = "ConsumerKey";

  /** the consumer secret. */
  public final static String CONSUMER_SECRET = "ConsumerSecret";

  /** the access token. */
  public final static String ACCESS_TOKEN = "AccessToken";

  /** the access token secret. */
  public final static String ACCESS_TOKEN_SECRET = "AccessTokenSecret";

  /** the hashtags symbol. */
  public final static String SYMBOL_HASHTAGS = "hashtags";

  /** the user mentions symbol. */
  public final static String SYMBOL_USERMENTIONS = "usermentions";
  
  /** the properties. */
  protected static Properties m_Properties;

  /**
   * Returns the underlying properties.
   *
   * @return		the properties
   */
  public synchronized static Properties getProperties() {
    if (m_Properties == null) {
      try {
	m_Properties = Environment.getInstance().read(TwitterDefinition.KEY);
        System.setProperty("twitter4j.debug", "" + m_Properties.getBoolean("Debug", false));
      }
      catch (Exception e) {
	m_Properties = new Properties();
      }
    }

    return m_Properties;
  }

  /**
   * Writes the specified properties to disk.
   *
   * @return		true if successfully stored
   */
  public synchronized static boolean writeProperties() {
    return writeProperties(getProperties());
  }

  /**
   * Writes the specified properties to disk.
   *
   * @param props	the properties to write to disk
   * @return		true if successfully stored
   */
  public synchronized static boolean writeProperties(Properties props) {
    boolean	result;

    result = Environment.getInstance().write(TwitterDefinition.KEY, props);
    // require reload
    m_Properties = null;

    return result;
  }

  /**
   * Returns the consumer key.
   *
   * @return		the key
   */
  public static String getConsumerKey() {
    return getProperties().getPath(CONSUMER_KEY, "");
  }

  /**
   * Returns the consumer secret.
   *
   * @return		the secret
   */
  public static BasePassword getConsumerSecret() {
    return new BasePassword(getProperties().getPath(CONSUMER_SECRET, ""));
  }

  /**
   * Returns the access token.
   *
   * @return		the token
   */
  public static String getAccessToken() {
    return getProperties().getPath(ACCESS_TOKEN, "");
  }

  /**
   * Returns the access token secret.
   *
   * @return		the secret
   */
  public static BasePassword getAccessTokenSecret() {
    return new BasePassword(getProperties().getPath(ACCESS_TOKEN_SECRET, ""));
  }

  /**
   * Returns the closest TwitterConnection actor, if available.
   *
   * @param actor	the actor start the search from (towards the root)
   * @return		the TwitterConnection actor, or null if none found
   */
  protected static TwitterConnection getTwitterConnectionActor(AbstractActor actor) {
    return (TwitterConnection) ActorUtils.findClosestType(actor, TwitterConnection.class, true);
  }

  /**
   * Returns the twitter connection object.
   *
   * @param actor	the actor to start the search from
   * @return		the connection, default connection object if no
   * 			TwitterConnection actor found
   */
  public static twitter4j.Twitter getTwitterConnection(AbstractActor actor) {
    twitter4j.Twitter	result;
    TwitterConnection	conn;

    conn = getTwitterConnectionActor(actor);
    if (conn == null)
      result = new TwitterFactory().getInstance();
    else
      result = conn.getTwitterConnection();

    return result;
  }

  /**
   * Returns the twitter stream connection object.
   *
   * @return		the stream connection, null if no TwitterConnection
   * 			actor found
   */
  public static twitter4j.TwitterStream getTwitterStreamConnection(AbstractActor actor) {
    twitter4j.TwitterStream	result;
    TwitterConnection		conn;

    result = null;
    conn   = getTwitterConnectionActor(actor);
    if (conn != null)
      result = conn.getTwitterStreamConnection();

    return result;
  }

  /**
   * Turns the status into a hashtable of objects.
   *
   * @param status	the status to process
   * @return		the association between fields and status values
   */
  public static Hashtable<TwitterField,Object> statusToHashtable(Status status) {
    Hashtable<TwitterField,Object>	result;
    List<String>			list;

    result = new Hashtable<TwitterField,Object>();

    result.put(TwitterField.ID, status.getId());
    if (status.getUser() != null) {
      result.put(TwitterField.USER_ID, status.getUser().getId());
      if (status.getUser().getName() != null)
	result.put(TwitterField.USER_NAME, status.getUser().getName());
    }
    result.put(TwitterField.SOURCE, status.getSource());
    result.put(TwitterField.TEXT, status.getText());
    if (status.getCreatedAt() != null)
      result.put(TwitterField.CREATED, status.getCreatedAt());
    result.put(TwitterField.FAVORITED, status.isFavorited());
    result.put(TwitterField.RETWEET, status.isRetweet());
    result.put(TwitterField.IN_REPLY_TO_STATUS_ID, status.getInReplyToStatusId());
    result.put(TwitterField.IN_REPLY_TO_USER_ID, status.getInReplyToUserId());
    if (status.getRetweetedStatus() != null) {
      result.put(TwitterField.RETWEETED_STATUS_ID, status.getRetweetedStatus().getId());
      if (status.getRetweetedStatus().getUser() != null)
	result.put(TwitterField.RETWEETED_STATUS_USER_ID, status.getRetweetedStatus().getUser().getId());
      if (status.getRetweetedStatus().getCreatedAt() != null)
	result.put(TwitterField.RETWEETED_STATUS_CREATED, status.getRetweetedStatus().getCreatedAt());
    }
    result.put(TwitterField.RETWEET_BY_ME, status.isRetweetedByMe());
    result.put(TwitterField.RETWEET_COUNT, status.getRetweetCount());
    result.put(TwitterField.POSSIBLY_SENSITIVE, status.isPossiblySensitive());
    if (status.getGeoLocation() != null) {
      result.put(TwitterField.GEO_LATITUDE, status.getGeoLocation().getLatitude());
      result.put(TwitterField.GEO_LONGITUDE, status.getGeoLocation().getLongitude());
    }
    if (status.getPlace() != null) {
      if (status.getPlace().getPlaceType() != null)
	result.put(TwitterField.PLACE_TYPE, status.getPlace().getPlaceType());
      if (status.getPlace().getURL() != null)
	result.put(TwitterField.PLACE_URL, status.getPlace().getURL());
      if (status.getPlace().getStreetAddress() != null)
	result.put(TwitterField.STREET_ADDRESS, status.getPlace().getStreetAddress());
      if (status.getPlace().getCountryCode() != null)
	result.put(TwitterField.COUNTRY_CODE, status.getPlace().getCountryCode());
      if (status.getPlace().getCountry() != null)
	result.put(TwitterField.COUNTRY, status.getPlace().getCountry());
      if (status.getPlace().getName() != null)
	result.put(TwitterField.PLACE, status.getPlace().getName());
    }
    if (status.getURLEntities() != null) {
      list = new ArrayList<String>();
      for (URLEntity url: status.getURLEntities())
	list.add(url.getExpandedURL());
      if (list.size() > 0)
	result.put(TwitterField.EXPANDED_URLS, Utils.flatten(list, ","));
    }
    if (status.getSymbolEntities() != null) {
      list = new ArrayList<String>();
      for (SymbolEntity sym: status.getSymbolEntities())
	list.add(sym.getText());
      if (list.size() > 0)
	result.put(TwitterField.SYMBOL_ENTITIES, Utils.flatten(list, ","));
    }
    result.put(TwitterField.IS_RETWEETED, status.isRetweeted());
    result.put(TwitterField.FAVORITE_COUNT, status.getFavoriteCount());
    if (status.getLang() != null)
      result.put(TwitterField.STATUS_LANG, status.getLang());
    if (status.getScopes() != null) {
      list = new ArrayList<String>();
      for (String id: status.getScopes().getPlaceIds())
	list.add(id);
      if (list.size() > 0)
	result.put(TwitterField.SCOPES, Utils.flatten(list, ","));
    }

    return result;
  }

  /**
   * Turns the status into a hashmap of objects.
   *
   * @param status	the status to process
   * @param lowerCase	whether to use lowercase for strings
   * @return		the association between field names and status values
   */
  public static HashMap statusToSymbols(Status status, boolean lowerCase) {
    HashMap				result;
    Hashtable<TwitterField,Object>	table;
    Object				value;
    String[]				values;
    int					i;
    
    result = new HashMap();
    table  = statusToHashtable(status);
    
    for (TwitterField field: table.keySet()) {
      value = table.get(field);
      if (lowerCase && value instanceof String)
	value = ((String) value).toLowerCase();
      result.put(field, value);
    }
    
    // add hashtags
    if (status.getHashtagEntities() != null) {
      values = new String[status.getHashtagEntities().length];
      for (i = 0; i < status.getHashtagEntities().length; i++)
	values[i] = status.getHashtagEntities()[i].getText();
    }
    else {
      values = new String[0];
    }
    result.put(SYMBOL_HASHTAGS, values);
    
    // add usermentions
    if (status.getUserMentionEntities() != null) {
      values = new String[status.getUserMentionEntities().length];
      for (i = 0; i < status.getUserMentionEntities().length; i++)
	values[i] = status.getUserMentionEntities()[i].getScreenName();
    }
    else {
      values = new String[0];
    }
    result.put(SYMBOL_USERMENTIONS, values);
    
    return result;
  }  

  /**
   * Extracts any hashtags from the status text.
   * 
   * @param text	the text to process
   * @return		the hashtag array
   */
  public static HashtagEntity[] extractHashtags(String text) {
    List<SimulatedHashtagEntity>	result;
    int					offset;
    int					pos;
    int					i;
    char				chr;
    String				current;
    SimulatedHashtagEntity		entity;
    
    result = new ArrayList<SimulatedHashtagEntity>();
    
    current = "";
    offset  = 0;
    while ((pos = text.indexOf("#")) > -1) {
      offset += pos + 1;
      text    = text.substring(pos + 1, text.length());
      for (i = 0; i < text.length(); i++) {
	chr = text.charAt(i);
	if (!Character.isLetterOrDigit(chr) || (i == text.length() - 1)) {
	  if (!current.isEmpty()) {
	    entity = new SimulatedHashtagEntity();
	    entity.setText(current);
	    entity.setStart(offset);
	    entity.setEnd(offset + i - 1);
	    result.add(entity);
	  }
	  offset  += i + 1;
	  text    = text.substring(i + 1, text.length());
	  current = "";
	  break;
	}
	else {
	  current += chr;
	}
      }
    }
    
    return result.toArray(new SimulatedHashtagEntity[result.size()]);
  }

  /**
   * Extracts any users mentioned in the status text.
   * 
   * @param text	the text to process
   * @return		the user mentions array
   */
  public static UserMentionEntity[] extractUserMentions(String text) {
    List<SimulatedUserMentionEntity>	result;
    int					offset;
    int					pos;
    int					i;
    char				chr;
    String				current;
    SimulatedUserMentionEntity		entity;

    result = new ArrayList<SimulatedUserMentionEntity>();

    current = "";
    offset  = 0;
    while ((pos = text.indexOf("@")) > -1) {
      offset += pos + 1;
      text    = text.substring(pos + 1, text.length());
      for (i = 0; i < text.length(); i++) {
	chr = text.charAt(i);
	if (!Character.isLetterOrDigit(chr) || (i == text.length() - 1)) {
	  if (!current.isEmpty()) {
	    entity = new SimulatedUserMentionEntity();
	    entity.setName(current);
	    entity.setScreenName(current);
	    entity.setId(-1);
	    entity.setStart(offset + pos + 1);
	    entity.setEnd(offset + i - 1);
	    result.add(entity);
	  }
	  offset  += i + 1;
	  text    = text.substring(i + 1, text.length());
	  current = "";
	  break;
	}
	else {
	  current += chr;
	}
      }
    }

    return result.toArray(new SimulatedUserMentionEntity[result.size()]);
  }
}
