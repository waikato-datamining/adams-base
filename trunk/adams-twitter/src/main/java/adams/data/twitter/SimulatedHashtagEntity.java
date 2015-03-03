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

import twitter4j.HashtagEntity;

/**
 * Represents a {@link HashtagEntity} from a simulated tweet.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SimulatedHashtagEntity
  implements HashtagEntity {

  /** for serialization. */
  private static final long serialVersionUID = 2800585470062380124L;

  /** the text of the hashtag. */
  protected String m_Text;
  
  /** the start of the hashtag in the tweet. */
  protected int m_Start;
  
  /** the end of the hashtag in the tweet. */
  protected int m_End;

  /**
   * Initializes the entity.
   */
  public SimulatedHashtagEntity() {
    super();
    initialize();
  }

  /**
   * Initializes the members.
   */
  protected void initialize() {
    m_Text  = null;
    m_Start = -1;
    m_End   = -1;
  }

  /**
   * Sets the text of the hashtag without #.
   *
   * @param value the text of the hashtag
   */
  public void setText(String value) {
    m_Text = value;
  }
  
  /**
   * Returns the text of the hashtag without #.
   *
   * @return the text of the hashtag
   */
  @Override
  public String getText() {
    return m_Text;
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
   * Returns the object as string.
   * 
   * @return		the string description
   */
  @Override
  public String toString() {
    return "hashtag: text=" + m_Text + ", start=" + m_Start + ", end=" + m_End;
  }
}
