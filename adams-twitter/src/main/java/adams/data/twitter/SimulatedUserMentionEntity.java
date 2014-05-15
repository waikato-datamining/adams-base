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
 * SimulatedHashtagEntity.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.twitter;

import twitter4j.UserMentionEntity;

/**
 * Represents a {@link UserMentionedEntity} from a simulated tweet.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SimulatedUserMentionEntity
  implements UserMentionEntity {

  /** for serialization. */
  private static final long serialVersionUID = 1720736522571619820L;

  /** the name of the user. */
  protected String m_Name;

  /** the screen name of the user. */
  protected String m_ScreenName;

  /** the ID of the user. */
  protected long m_Id;
  
  /** the start of the hashtag in the tweet. */
  protected int m_Start;
  
  /** the end of the hashtag in the tweet. */
  protected int m_End;

  /**
   * Initializes the entity.
   */
  public SimulatedUserMentionEntity() {
    super();
    initialize();
  }

  /**
   * Initializes the members.
   */
  protected void initialize() {
    m_Name       = null;
    m_ScreenName = null;
    m_Id         = -1;
    m_Start      = -1;
    m_End        = -1;
  }

  /**
   * Sets the name mentioned in the status.
   *
   * @param value the name mentioned in the status
   */
  public void setName(String value) {
    m_Name = value;
  }
  
  /**
   * Returns the name mentioned in the status.
   *
   * @return the name mentioned in the status
   */
  @Override
  public String getName() {
    return m_Name;
  }

  /**
   * Sets the screen name mentioned in the status.
   *
   * @param value the screen name mentioned in the status
   */
  public void setScreenName(String value) {
    m_ScreenName = value;
  }
  
  /**
   * Returns the screen name mentioned in the status.
   *
   * @return the screen name mentioned in the status
   */
  @Override
  public String getScreenName() {
    return m_ScreenName;
  }

  /**
   * Sets the user id mentioned in the status.
   *
   * @param value the user id mentioned in the status
   */
  public void setId(long value) {
    m_Id = value;
  }
  
  /**
   * Returns the user id mentioned in the status.
   *
   * @return the user id mentioned in the status
   */
  @Override
  public long getId() {
    return m_Id;
  }

  /**
   * Sets the index of the start character of the hashtag.
   *
   * @param value the index of the start character of the hashtag
   */
  public void setStart(int value) {
    m_Start = value;
  }
  
  /**
   * Returns the index of the start character of the hashtag.
   *
   * @return the index of the start character of the hashtag
   */
  @Override
  public int getStart() {
    return m_Start;
  }

  /**
   * Sets the index of the end character of the hashtag.
   *
   * @param value the index of the end character of the hashtag
   */
  public void setEnd(int value) {
    m_End = value;
  }
  
  /**
   * Returns the index of the end character of the hashtag.
   *
   * @return the index of the end character of the hashtag
   */
  @Override
  public int getEnd() {
    return m_End;
  }

  /**
   * Returns the screen name mentioned in the status. This method implementation is to meet TweetEntity interface and the behavior is equivalent to {@link #getScreenName()}
   *
   * @return the screen name mentioned in the status
   * @see #getScreenName()
   */
  @Override
  public String getText() {
    return getScreenName();
  }
  
  /**
   * Returns the object as string.
   * 
   * @return		the string description
   */
  @Override
  public String toString() {
    return "usermention: name=" + m_Name + ", screenname=" + m_ScreenName + ", id=" + m_Id + ", start=" + m_Start + ", end=" + m_End;
  }
}
