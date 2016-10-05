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
 * BruteForcePasswordGenerator.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.core.password;

/**
 * Generates passwords for a brute force attack.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class BruteForcePasswordGenerator
  extends AbstractPasswordGenerator {

  private static final long serialVersionUID = 504757773896722990L;

  /** the maximum length for passwords to test. */
  protected int m_MaxLength;

  /** counter for generating the passwords. */
  protected int[] m_Counter;

  /** the characters to use in the attack. */
  protected char[] m_Chars;

  /** the maximum number of characters. */
  protected int m_Max;

  /** the buffer for the password. */
  protected char[] m_Password;

  /** the current number of characters in the password. */
  protected int m_NumChars;

  /** the next password. */
  protected String m_Next;

  /** whether the next password has already been generated. */
  protected boolean m_NextGenerated;

  /**
   * Initializes the generator.
   *
   * @param chars		the characters to use for each position
   * @param maxLength		the maximum length of the password
   */
  public BruteForcePasswordGenerator(String chars, int maxLength) {
    this(chars, maxLength, null);
  }

  /**
   * Initializes the generator.
   *
   * @param chars		the characters to use for each position
   * @param maxLength		the maximum length of the password
   * @param start 		the starting password, use null to start from scratch
   */
  public BruteForcePasswordGenerator(String chars, int maxLength, String start) {
    m_MaxLength  = maxLength;
    m_Counter    = new int[m_MaxLength];
    m_Chars      = chars.toCharArray();
    m_Max        = m_Chars.length;
    m_Password   = new char[m_Counter.length];
    m_NumChars   = 1;
    m_Next       = null;
    if (start != null) {
      if (start.length() > maxLength)
        throw new IllegalArgumentException(
          "Starting password '" + start + "' is longer than maximum length " + maxLength);
      for (int i = 0; i < start.length(); i++) {
        m_Counter[i] = chars.indexOf(start.charAt(i));
        if (m_Counter[i] == -1)
          throw new IllegalArgumentException(
            "Starting password '" + start + "' contains invalid character '" + start.charAt(i) + "' "
              + "that is not part of the supplied characters '" + chars + "'");
      }
    }
    m_Counter[0]--;
    m_NextGenerated = false;
  }

  /**
   * Checks whether there is another password available.
   *
   * @return		true if another password available
   */
  public boolean hasNext() {
    if (!m_NextGenerated) {
      m_Next = doNext();
      m_NextGenerated = true;
    }
    return (m_Next != null);
  }

  /**
   * Returns the next password.
   *
   * @return		the next password, null if no more available
   */
  public String next() {
    String	result;

    if (!m_NextGenerated)
      m_Next = doNext();

    result          = m_Next;
    m_NextGenerated = false;
    m_Next          = null;

    return result;
  }

  /**
   * Generates the next password.
   *
   * @return		the next password, null if no more available
   */
  protected String doNext() {
    int	i;
    int 	index;

    index = 0;
    while (index < m_Counter.length) {
      m_Counter[index]++;
      if (m_Counter[index] == m_Max) {
	m_Counter[index] = 0;
	index++;
	m_NumChars = Math.max(m_NumChars, index + 1);
	if (m_NumChars > m_Counter.length)
	  return null;
      }
      else {
	break;
      }
    }

    for (i = 0; i < m_NumChars; i++)
      m_Password[i] = m_Chars[m_Counter[i]];

    return new String(m_Password).trim();
  }
}
