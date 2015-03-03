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
 * Type.java
 * Copyright (C) 2008-2011 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.core.axis;

import adams.core.EnumWithCustomDisplay;
import adams.core.option.AbstractOption;

/**
 * Enumeration for the type of axis.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public enum Type
  implements EnumWithCustomDisplay<Type> {

  /** displaying absolute values (default). */
  ABSOLUTE(AbsoluteAxisModel.class),
  /** displaying percentage values. */
  PERCENTAGE(PercentageAxisModel.class),
  /** displaying log10(absolute) values. */
  LOG10_ABSOLUTE(Log10AbsoluteAxisModel.class),
  /** displaying log10(percentage) values. */
  LOG10_PERCENTAGE(Log10PercentageAxisModel.class),
  /** displaying ln(absolute) values. */
  LOG_ABSOLUTE(LogAbsoluteAxisModel.class),
  /** displaying ln(percentage) values. */
  LOG_PERCENTAGE(LogPercentageAxisModel.class),
  /** displaying dates. */
  DATE(DateAxisModel.class),
  /** displaying dates. */
  TIME(TimeAxisModel.class),
  /** displaying dates. */
  DATETIME(DateTimeAxisModel.class);

  /** the display string of the type. */
  private String m_Display;

  /** the raw string of the type. */
  private String m_Raw;

  /** the corresponding axis model class. */
  private Class m_AxisModelClass;

  /**
   * Initializes the type.
   *
   * @param cls	the axis model class
   */
  private Type(Class cls) {
    m_AxisModelClass = cls;
    m_Display        = getModel().getDisplayName();
    m_Raw            = super.toString();
  }

  /**
   * Returns the display string, used in toString().
   *
   * @return		the display string
   * @see		#toString()
   */
  public String toDisplay() {
    return m_Display;
  }

  /**
   * Returns the raw enum string.
   *
   * @return		the raw enum string
   */
  public String toRaw() {
    return m_Raw;
  }

  /**
   * Returns the corresponding axis model.
   *
   * @return		the model
   */
  public AbstractAxisModel getModel() {
    AbstractAxisModel		result;

    try {
      result = (AbstractAxisModel) m_AxisModelClass.newInstance();
    }
    catch (Exception e) {
      result = null;
      e.printStackTrace();
    }

    return result;
  }

  /**
   * Checks whether the data range can be handled by the model.
   *
   * @param min		the minimum value
   * @param max		the maximum value
   * @return		true if the data can be handled
   */
  public boolean canHandle(double min, double max) {
    boolean		result;
    AbstractAxisModel	model;

    result = false;
    model  = getModel();
    if (model != null)
      result = model.canHandle(min, max);

    return result;
  }

  /**
   * Returns the display string.
   *
   * @return		the display string
   */
  public String toString() {
    return m_Display;
  }

  /**
   * Returns the corresponding type. First tries to parse using the
   * valueOf method of the Enum class, then going over all the enums
   * and checking the display string.
   *
   * @param s		the string to parse
   * @return		the corresponding type or null if not found
   */
  public Type parse(String s) {
    return (Type) valueOf((AbstractOption) null, s);
  }

  /**
   * Returns the enum as string.
   *
   * @param option	the current option
   * @param object	the enum object to convert
   * @return		the generated string
   */
  public static String toString(AbstractOption option, Object object) {
    return ((Type) object).toRaw();
  }

  /**
   * Returns an enum generated from the string.
   *
   * @param option	the current option
   * @param str		the string to convert to an enum
   * @return		the generated enum or null in case of error
   */
  public static Type valueOf(AbstractOption option, String str) {
    Type	result;

    result = null;

    // default parsing
    try {
      result = valueOf(str);
    }
    catch (Exception e) {
      // ignored
    }

    // try display
    if (result == null) {
      for (Type f: values()) {
	if (f.toDisplay().equals(str)) {
	  result = f;
	  break;
	}
      }
    }

    return result;
  }
}