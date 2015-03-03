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
 * Amount.java
 * Copyright (C) 2010 University of Waikato, Hamilton, New Zealand
 */

package adams.parser.basetime;

import java.util.Calendar;

/**
 * Helper class for date manipulations (adding, substracting), stores the
 * type and amount.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @see     Calendar#add(int, int)
 */
public class Amount {

  /** the type. */
  protected int m_Type;

  /** the amount. */
  protected int m_Amount;

  /**
   * Initializes the object with amount 1 of the specified type.
   *
   * @param type	the type
   */
  public Amount(int type) {
    this(type, 1);
  }

  /**
   * Initializes the object with specified amount and type.
   *
   * @param type	the type
   * @param amount	the amount
   */
  public Amount(int type, int amount) {
    m_Type   = type;
    m_Amount = amount;
  }

  /**
   * Returns the type.
   *
   * @return		the type
   */
  public int getType() {
    return m_Type;
  }

  /**
   * Returns the amount.
   *
   * @return		the amount
   */
  public int getAmount() {
    return m_Amount;
  }

  /**
   * Returns a string representation of the amount.
   *
   * @return		the string representation
   */
  public String toString() {
    return "Type=" + m_Type + ", Amount=" + m_Amount;
  }
}
