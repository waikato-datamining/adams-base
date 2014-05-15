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
 * SimulatedStatus.java
 * Copyright (C) 2013-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.twitter;

import java.util.Date;

import twitter4j.GeoLocation;
import twitter4j.HashtagEntity;
import twitter4j.MediaEntity;
import twitter4j.Place;
import twitter4j.Scopes;
import twitter4j.Status;
import twitter4j.SymbolEntity;
import twitter4j.URLEntity;
import twitter4j.User;
import twitter4j.UserMentionEntity;

/**
 * For simulating tweets without using Twitter.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SimulatedStatus
  extends AbstractSimulatedTwitterResponse
  implements Status {

  /** for serialization. */
  private static final long serialVersionUID = -1780902710056376673L;

  /** the user mention entities. */
  protected UserMentionEntity[] m_UserMentionEntity;

  /** the URL entities. */
  protected URLEntity[] m_URLEntity;

  /** the hashtag entities. */
  protected HashtagEntity[] m_HashtagEntity;

  /** the media entities. */
  protected MediaEntity[] m_MediaEntity;

  /** the CreatedAt. */
  protected Date m_CreatedAt;

  /** the ID. */
  protected long m_Id;

  /** the tweet text. */
  protected String m_Text;

  /** the source. */
  protected String m_Source;

  /** whether the tweet got truncated. */
  protected boolean m_Truncated;

  /** the in-reply-to status ID. */
  protected long m_InReplyToStatusId;

  /** the in-reply-to user ID. */
  protected long m_InReplyToUserId;

  /** the in-reply-to screen name. */
  protected String m_InReplyToScreenName;

  /** the geo location. */
  protected GeoLocation m_GeoLocation;

  /** the place. */
  protected Place m_Place;

  /** whether tweet got favorited. */
  protected boolean m_Favorited;

  /** the associated user. */
  protected User m_User;

  /** whether it is a retweet. */
  protected boolean m_Retweet;

  /** the retweeted status. */
  protected Status m_RetweetedStatus;

  /** the contributors. */
  protected long[] m_Contributors;

  /** the retweet count. */
  protected int m_RetweetCount;

  /** whether it was retweeted by me. */
  protected boolean m_RetweetedByMe;

  /** the current user's retweet id. */
  protected long m_CurrentUserRetweetId;

  /** whether the tweet is potentially sensitive. */
  protected boolean m_PossiblySensitive;

  protected SymbolEntity[] m_SymbolEntities;

  /** whether retweeted or not. */
  protected boolean m_IsRetweeted;

  /** the favorite count. */
  protected int m_FavoriteCount;

  /** the iso language code. */
  protected String m_IsoLanguageCode;

  /** the language. */
  protected String m_Lang;

  /** the scopes. */
  protected Scopes m_Scopes;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_UserMentionEntity    = new UserMentionEntity[0];
    m_URLEntity            = null;
    m_HashtagEntity        = new HashtagEntity[0];
    m_MediaEntity          = null;
    m_CreatedAt            = null;
    m_Id                   = -1;
    m_Text                 = "";
    m_Source               = "";
    m_Truncated            = false;
    m_InReplyToStatusId    = -1;
    m_InReplyToUserId      = -1;
    m_InReplyToScreenName  = "";
    m_GeoLocation          = null;
    m_Place                = null;
    m_Favorited            = false;
    m_User                 = null;
    m_Retweet              = false;
    m_RetweetedStatus      = null;
    m_Contributors         = null;
    m_RetweetCount         = -1;
    m_RetweetedByMe        = false;
    m_CurrentUserRetweetId = -1;
    m_PossiblySensitive    = false;
    m_SymbolEntities       = new SymbolEntity[0];
    m_IsRetweeted          = false;
    m_FavoriteCount        = 0;
    m_IsoLanguageCode      = "";
    m_Lang                 = "";
    m_Scopes               = null;
  }

  /**
   * Compares this status with the other one.
   * Uses: {@link #m_Id}, {@link #m_Text}, {@link #m_Source}
   *
   * @param o		the status to compare with
   * @return		less than 0, equal to 0, greater than 0 if this
   * 			status is less, equal or larger than the other one
   */
  @Override
  public int compareTo(Status o) {
    int		result;

    result = new Long(getId()).compareTo(o.getId());
    if (result == 0)
      result = getText().compareTo(o.getText());
    if (result == 0)
      result = getSource().compareTo(o.getSource());

    return result;
  }

  /**
   * Sets an array of user mentions in the tweet, or null if no users were mentioned.
   *
   * @param value An array of user mention entities in the tweet.
   */
  public void setUserMentionEntities(UserMentionEntity[] value) {
    m_UserMentionEntity = value;
  }

  /**
   * Returns an array of user mentions in the tweet, or null if no users were mentioned.
   *
   * @return An array of user mention entities in the tweet.
   */
  @Override
  public UserMentionEntity[] getUserMentionEntities() {
    return m_UserMentionEntity;
  }

  /**
   * Sets an array if URLEntity mentioned in the tweet, or null if no URLs were mentioned.
   *
   * @param value An array of URLEntity mentioned in the tweet.
   */
  public void setURLEntities(URLEntity[] value) {
    m_URLEntity = value;
  }

  /**
   * Returns an array if URLEntity mentioned in the tweet, or null if no URLs were mentioned.
   *
   * @return An array of URLEntity mentioned in the tweet.
   */
  @Override
  public URLEntity[] getURLEntities() {
    return m_URLEntity;
  }

  /**
   * Sets an array of MediaEntities if medias are available in the tweet, or null if no media is included in the tweet.
   *
   * @param value an array of MediaEntities.
   */
  public void setHashtagEntities(HashtagEntity[] value) {
    m_HashtagEntity = value;
  }

  /**
   * Returns an array of MediaEntities if medias are available in the tweet, or null if no media is included in the tweet.
   *
   * @return an array of MediaEntities.
   */
  @Override
  public HashtagEntity[] getHashtagEntities() {
    return m_HashtagEntity;
  }

  /**
   * Sets an array of MediaEntities if medias are available in the tweet, or null if no media is included in the tweet.
   *
   * @param value an array of MediaEntities.
   */
  public void setMediaEntities(MediaEntity[] value) {
    m_MediaEntity = value;
  }

  /**
   * Returns an array of MediaEntities if medias are available in the tweet, or null if no media is included in the tweet.
   *
   * @return an array of MediaEntities.
   */
  @Override
  public MediaEntity[] getMediaEntities() {
    return m_MediaEntity;
  }

  /**
   * Sets the created_at
   *
   * @param value created_at
   */
  public void setCreatedAt(Date value) {
    m_CreatedAt = value;
  }

  /**
   * Return the created_at
   *
   * @return created_at
   */
  @Override
  public Date getCreatedAt() {
    return m_CreatedAt;
  }

  /**
   * Sets the id of the status
   *
   * @param value the id
   */
  public void setId(long value) {
    m_Id = value;
  }

  /**
   * Returns the id of the status
   *
   * @return the id
   */
  @Override
  public long getId() {
    return m_Id;
  }

  /**
   * Sets the text of the status
   *
   * @param value the text
   */
  public void setText(String value) {
    m_Text = value;
  }

  /**
   * Returns the text of the status
   *
   * @return the text
   */
  @Override
  public String getText() {
    return m_Text;
  }

  /**
   * Sets the source
   *
   * @param value the source
   */
  public void setSource(String value) {
    m_Source = value;
  }

  /**
   * Returns the source
   *
   * @return the source
   */
  @Override
  public String getSource() {
    return m_Source;
  }

  /**
   * Set if the status is truncated
   *
   * @param value true if truncated
   */
  public void setTruncated(boolean value) {
    m_Truncated = value;
  }

  /**
   * Test if the status is truncated
   *
   * @return true if truncated
   */
  @Override
  public boolean isTruncated() {
    return m_Truncated;
  }

  /**
   * Sets the in_reply_tostatus_id
   *
   * @param value the in_reply_tostatus_id
   */
  public void setInReplyToStatusId(long value) {
    m_InReplyToStatusId = value;
  }

  /**
   * Returns the in_reply_tostatus_id
   *
   * @return the in_reply_tostatus_id
   */
  @Override
  public long getInReplyToStatusId() {
    return m_InReplyToStatusId;
  }

  /**
   * Sets the in_reply_user_id
   *
   * @param value the in_reply_tostatus_id
   */
  public void setInReplyToUserId(long value) {
    m_InReplyToUserId = value;
  }

  /**
   * Returns the in_reply_user_id
   *
   * @return the in_reply_tostatus_id
   */
  @Override
  public long getInReplyToUserId() {
    return m_InReplyToUserId;
  }

  /**
   * Sets the in_reply_to_screen_name
   *
   * @param value the in_in_reply_to_screen_name
   */
  public void setInReplyToScreenName(String value) {
    m_InReplyToScreenName = value;
  }

  /**
   * Returns the in_reply_to_screen_name
   *
   * @return the in_in_reply_to_screen_name
   */
  @Override
  public String getInReplyToScreenName() {
    return m_InReplyToScreenName;
  }

  /**
   * Sets The location that this tweet refers to if available.
   *
   * @param value The location that this tweet refers to if available (can be null)
   */
  public void setGeoLocation(GeoLocation value) {
    m_GeoLocation = value;
  }

  /**
   * Returns The location that this tweet refers to if available.
   *
   * @return returns The location that this tweet refers to if available (can be null)
   */
  @Override
  public GeoLocation getGeoLocation() {
    return m_GeoLocation;
  }

  /**
   * Sets the place attached to this status
   *
   * @param value The place attached to this status
   */
  public void setPlace(Place value) {
    m_Place = value;
  }

  /**
   * Returns the place attached to this status
   *
   * @return The place attached to this status
   */
  @Override
  public Place getPlace() {
    return m_Place;
  }

  /**
   * Sets if the status is favorited
   *
   * @param value true if favorited
   */
  public void setFavorited(boolean value) {
    m_Favorited = value;
  }

  /**
   * Test if the status is favorited
   *
   * @return true if favorited
   */
  @Override
  public boolean isFavorited() {
    return m_Favorited;
  }

  /**
   * Sets the user associated with the status.<br>
   * This can be null if the instance if from User.getStatus().
   *
   * @param value the user
   */
  public void setUser(User value) {
    m_User = value;
  }

  /**
   * Return the user associated with the status.<br>
   * This can be null if the instance if from User.getStatus().
   *
   * @return the user
   */
  @Override
  public User getUser() {
    return m_User;
  }

  /**
   * Sets whether it is a retweet.
   */
  public void setRetweet(boolean value) {
    m_Retweet = value;
  }

  /**
   * Whether it is a retweet.
   */
  @Override
  public boolean isRetweet() {
    return m_Retweet;
  }

  /**
   * The retweeted status.
   */
  @Override
  public Status getRetweetedStatus() {
    return m_RetweetedStatus;
  }

  /**
   * Sets the retweeted status.
   */
  public void setRetweetedStatus(Status value) {
    m_RetweetedStatus = value;
  }

  /**
   * Sets an array of contributors, or null if no contributor is associated with this status.
   */
  public void setContributors(long[] value) {
    m_Contributors = value;
  }

  /**
   * Returns an array of contributors, or null if no contributor is associated with this status.
   */
  @Override
  public long[] getContributors() {
    return m_Contributors;
  }

  /**
   * Sets the number of times this tweet has been retweeted, or -1 when the tweet was
   * created before this feature was enabled.
   *
   * @param value the retweet count.
   */
  public void setRetweetCount(int value) {
    m_RetweetCount = value;
  }

  /**
   * Returns the number of times this tweet has been retweeted, or -1 when the tweet was
   * created before this feature was enabled.
   *
   * @return the retweet count.
   */
  @Override
  public int getRetweetCount() {
    return m_RetweetCount;
  }

  /**
   * Set true if the authenticating user has retweeted this tweet, or false when the tweet was
   * created before this feature was enabled.
   *
   * @param value whether the authenticating user has retweeted this tweet.
   */
  public void setRetweetedByMe(boolean value) {
    m_RetweetedByMe = value;
  }

  /**
   * Returns true if the authenticating user has retweeted this tweet, or false when the tweet was
   * created before this feature was enabled.
   *
   * @return whether the authenticating user has retweeted this tweet.
   */
  @Override
  public boolean isRetweetedByMe() {
    return m_RetweetedByMe;
  }

  /**
   * Sets the authenticating user's retweet's id of this tweet, or -1L when the tweet was created
   * before this feature was enabled.
   *
   * @param value the authenticating user's retweet's id of this tweet
   */
  public void setCurrentUserRetweetId(long value) {
    m_CurrentUserRetweetId = value;
  }

  /**
   * Returns the authenticating user's retweet's id of this tweet, or -1L when the tweet was created
   * before this feature was enabled.
   *
   * @return the authenticating user's retweet's id of this tweet
   */
  @Override
  public long getCurrentUserRetweetId() {
    return m_CurrentUserRetweetId;
  }

  /**
  * Set true if the status contains a link that is identified as sensitive.
  *
  * @param value whether the status contains sensitive links
  */
  public void setPossiblySensitive(boolean value) {
    m_PossiblySensitive = value;
  }

  /**
  * Returns true if the status contains a link that is identified as sensitive.
  *
  * @return whether the status contains sensitive links
  */
  @Override
  public boolean isPossiblySensitive() {
    return m_PossiblySensitive;
  }

  /**
   * Sets an array of SymbolEntities if medias are available in the tweet.
   *
   * @param value an array of SymbolEntities.
   */
  public void setSymbolEntities(SymbolEntity[] value) {
    m_SymbolEntities = value;
  }
  
  /**
   * Returns an array of SymbolEntities if medias are available in the tweet. This method will an empty array if no symbols were mentioned.
   *
   * @return an array of SymbolEntities.
   */
  @Override
  public SymbolEntity[] getSymbolEntities() {
    return m_SymbolEntities;
  }

  /**
   * Sets whether the status is retweeted.
   * 
   * @param value true if retweeted
   */
  public void setIsRetweeted(boolean value) {
    m_IsRetweeted = value;
  }
  
  /**
   * Test if the status is retweeted
   *
   * @return true if retweeted
   */
  @Override
  public boolean isRetweeted() {
    return m_IsRetweeted;
  }

  /**
   * Sets how many times this tweet has been "favorited" by twitter users.
   * 
   * @param value the count
   */
  public void setFavoriteCount(int value) {
    m_FavoriteCount = value;
  }
  
  /**
   * Indicates approximately how many times this Tweet has been "favorited" by Twitter users.
   *
   * @return the favorite count
   */
  @Override
  public int getFavoriteCount() {
    return m_FavoriteCount;
  }

  /**
   * Sets the iso language code set by the Twitter API (best-effort). This field is available only with the search api.
   * It is suggested to use {@link #setLang()}
   * 
   * @param value the language code
   */
  @Deprecated
  public void setIsoLanguageCode(String value) {
    m_IsoLanguageCode = value;
  }
  
  /**
   * Returns the iso language code set by the Twitter API (best-effort). This field is available only with the search api.
   * It is suggested to use {@link #getLang()}
   *
   * @return two-letter iso language code
   * @deprecated use {@link #getLang()} instead
   */
  @Deprecated
  @Override
  public String getIsoLanguageCode() {
    return m_IsoLanguageCode;
  }

  /**
   * Sets the lang of the status text if available.
   * 
   * @param value	the language
   */
  public void setLang(String value) {
    m_Lang = value;
  }
  
  /**
   * Returns the lang of the status text if available.
   *
   * @return two-letter iso language code
   */
  @Override
  public String getLang() {
    return m_Lang;
  }

  /**
   * Sets the targeting scopes applied to a status.
   * 
   * @param value the scopes
   */
  public void setScopes(Scopes value) {
    m_Scopes = value;
  }
  
  /**
   * Returns the targeting scopes applied to a status.
   *
   * @return the targeting scopes applied to a status.
   */
  @Override
  public Scopes getScopes() {
    return m_Scopes;
  };

  /**
   * Returns a short string describing the tweet (ID + text).
   *
   * @return		the string representation
   */
  @Override
  public String toString() {
    return m_Id + ": " + m_Text + ", #tags=" + m_HashtagEntity.length + ", #usermentions=" + m_UserMentionEntity.length;
  }
}
