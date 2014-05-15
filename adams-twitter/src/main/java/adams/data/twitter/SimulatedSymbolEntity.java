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

import twitter4j.SymbolEntity;

/**
 * Represents a {@link SymbolEntity} from a simulated tweet.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 8675 $
 */
public class SimulatedSymbolEntity
  implements SymbolEntity {

  /** for serialization. */
  private static final long serialVersionUID = 2800585470062380124L;

  /** the text of the symbol. */
  protected String m_Text;
  
  /** the start of the symbol in the tweet. */
  protected int m_Start;
  
  /** the end of the symbol in the tweet. */
  protected int m_End;

  /**
   * Initializes the entity.
   */
  public SimulatedSymbolEntity() {
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
   * Sets the text of the symbol without #.
   *
   * @param value the text of the symbol
   */
  public void setText(String value) {
    m_Text = value;
  }
  
  /**
   * Returns the text of the symbol without #.
   *
   * @return the text of the symbol
   */
  @Override
  public String getText() {
    return m_Text;
  }

  /**
   * Sets the index of the start character of the symbol.
   *
   * @param value the index of the start character of the symbol
   */
  public void setStart(int value) {
    m_Start = value;
  }
  
  /**
   * Returns the index of the start character of the symbol.
   *
   * @return the index of the start character of the symbol
   */
  @Override
  public int getStart() {
    return m_Start;
  }

  /**
   * Sets the index of the end character of the symbol.
   *
   * @param value the index of the end character of the symbol
   */
  public void setEnd(int value) {
    m_End = value;
  }
  
  /**
   * Returns the index of the end character of the symbol.
   *
   * @return the index of the end character of the symbol
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
    return "symbol: text=" + m_Text + ", start=" + m_Start + ", end=" + m_End;
  }
}
