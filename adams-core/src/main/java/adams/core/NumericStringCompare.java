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
 * NumericStringCompare.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */
package adams.core;

import adams.core.base.BaseRegExp;
import adams.core.option.AbstractOptionHandler;

import java.io.Serializable;
import java.util.Comparator;
import java.util.logging.Level;

/**
 * Compares the numeric portion of two String objects by extracting it using
 * the supplied regexp.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 4584 $
 */
public class NumericStringCompare
  extends AbstractOptionHandler
  implements Comparator<String>, Serializable, QuickInfoSupporter {

  /** for serialization. */
  private static final long serialVersionUID = -6202901070173617221L;

  /** the string to find. */
  protected BaseRegExp m_Find;

  /** the replacement string. */
  protected String m_Replace;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return 
	"Comparator for String objects. Extracts the numeric part using the "
      + "supplied regular expression and interprets it as double.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "find", "find",
	    new BaseRegExp("find"));

    m_OptionManager.add(
	    "replace", "replace",
	    "");
  }

  /**
   * Sets the string to find (regular expression).
   *
   * @param value	the string
   */
  public void setFind(BaseRegExp value) {
    m_Find = value;
    reset();
  }

  /**
   * Returns the string to find (regular expression).
   *
   * @return		the string
   */
  public BaseRegExp getFind() {
    return m_Find;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String findTipText() {
    return "The string to find (a regular expression).";
  }

  /**
   * Sets the string to replace the occurrences with.
   *
   * @param value	the string
   */
  public void setReplace(String value) {
    m_Replace = Utils.unbackQuoteChars(value);
    reset();
  }

  /**
   * Returns the string to replace the occurences with.
   *
   * @return		the string
   */
  public String getReplace() {
    return Utils.backQuoteChars(m_Replace);
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String replaceTipText() {
    return "The string to replace the occurrences with.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String		result;

    result  = QuickInfoHelper.toString(this, "replace", (m_Replace.isEmpty() ? "-none-" : m_Replace), "replace: ");
    result += QuickInfoHelper.toString(this, "find", (m_Find.isEmpty() ? "-none-" : m_Find), ", find: ");

    return result;
  }

  /**
   * Compares its two arguments for order.
   *
   * @param o1		the first object
   * @param o2		the second object
   * @return		-1 if o1&lt;o2, 0 if o1=o2 and 1 if o1&;gt;o2
   */
  public int compare(String o1, String o2) {
    String	s1;
    String	s2;
    Double	d1;
    Double	d2;
    String	msg;

    s1 = o1.replaceAll(m_Find.getValue(), m_Replace);
    s2 = o2.replaceAll(m_Find.getValue(), m_Replace);

    try {
      d1 = Double.parseDouble(s1);
    }
    catch (Exception e) {
      msg = "Failed to parse 1st string as double: " + s1;
      getLogger().log(Level.SEVERE, msg);
      throw new IllegalStateException(msg, e);
    }
    try {
      d2 = Double.parseDouble(s2);
    }
    catch (Exception e) {
      msg = "Failed to parse 2nd string as double: " + s2;
      getLogger().log(Level.SEVERE, msg);
      throw new IllegalStateException(msg, e);
    }

    return d1.compareTo(d2);
  }

  /**
   * Indicates whether some other object is "equal to" this Comparator.
   *
   * @param obj	the object to compare with this Comparator
   * @return		true if the object is a DefaultCompare object as well
   */
  @Override
  public boolean equals(Object obj) {
    return (obj instanceof NumericStringCompare);
  }
}